package com.damon.tcc;

import cn.hutool.core.thread.NamedThreadFactory;
import com.damon.tcc.annotation.BizId;
import com.damon.tcc.config.TccMainConfig;
import com.damon.tcc.local_transaction.ILocalTransactionService;
import com.damon.tcc.local_transaction.TccLocalTransactionSupplier;
import com.damon.tcc.main_log.ITccMainLogService;
import com.damon.tcc.main_log.TccMainLog;
import com.damon.tcc.main_runnable.TccMasterLogAsyncCheckRunnable;
import com.damon.tcc.main_runnable.TccMasterLogAsyncCommitRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public abstract class TccMainService<R, PD, P extends BizId> {
    private final Logger log = LoggerFactory.getLogger(TccMainService.class);
    private final ExecutorService asyncCommitExecutorService;
    private final ExecutorService asyncCheckExecutorService;
    private final ITccMainLogService tccLogService;
    private final ILocalTransactionService localTransactionService;
    private final String bizType;
    private final TccMainConfig tccMainConfig;

    public TccMainService(TccMainConfig config) {
        this.tccLogService = config.getTccLogService();
        this.bizType = config.getBizType();
        this.localTransactionService = config.getLocalTransactionService();
        this.asyncCommitExecutorService = new ThreadPoolExecutor(config.getAsyncCommitThreadMinNumber(), config.getAsyncCommitThreadMaxNumber(),
                120L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(config.getAsyncCommitQueueSize()),
                new NamedThreadFactory(config.getBizType() + "-tcc-async-commit-pool-", false), new ThreadPoolExecutor.CallerRunsPolicy()
        );
        this.asyncCheckExecutorService = new ThreadPoolExecutor(config.getAsyncCheckThreadMinNumber(), config.getAsyncCheckThreadMaxNumber(),
                60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(config.getAsyncCheckQueueSize()),
                new NamedThreadFactory(config.getBizType() + "-tcc-async-check-pool-", false), new ThreadPoolExecutor.CallerRunsPolicy()
        );
        this.tccMainConfig = config;
    }

    protected TccFailedLogIterator queryFailedLogs() {
        Integer failedLogsTotal = tccLogService.getFailedLogsTotal(tccMainConfig.getFailedCheckTimes());
        Integer totalPage = (failedLogsTotal + tccMainConfig.getTccFailedLogPageSize() - 1) / tccMainConfig.getTccFailedLogPageSize();
        return new TccFailedLogIterator(totalPage, pageNumber ->
                tccLogService.queryFailedLogs(tccMainConfig.getFailedCheckTimes(), tccMainConfig.getTccFailedLogPageSize(), pageNumber)
        );
    }

    protected TccFailedLogIterator queryDeadLogs() {
        Integer deadLogsTotal = tccLogService.getDeadLogsTotal(tccMainConfig.getFailedCheckTimes());
        Integer totalPage = (deadLogsTotal + tccMainConfig.getTccFailedLogPageSize() - 1) / tccMainConfig.getTccFailedLogPageSize();
        return new TccFailedLogIterator(totalPage, pageNumber ->
                tccLogService.queryDeadLogs(tccMainConfig.getFailedCheckTimes(), tccMainConfig.getTccFailedLogPageSize(), pageNumber)
        );
    }

    /**
     * 执行一次死信日志检查
     */
    protected void executeDeadLogCheck() {
        TccFailedLogIterator iterator = queryDeadLogs();
        check(iterator);
    }

    /**
     * 执行事务状态检查，供上游业务系统调用
     */
    protected void executeFailedLogCheck() {
        TccFailedLogIterator iterator = queryFailedLogs();
        check(iterator);
    }

    protected void check(TccFailedLogIterator iterator) {
        while (iterator.hasNext()) {
            List<TccMainLog> tccMainLogs = iterator.next();
            tccMainLogs.forEach(tccLog -> {
                asyncCheckExecutorService.execute(
                        new TccMasterLogAsyncCheckRunnable<>(tccLogService, bizType, this::commit, this::cancel, this::callbackParameter, tccLog)
                );
            });
        }
    }

    /**
     * @param parameter
     * @return
     */
    protected R process(P parameter) {
        TccMainLog tccMainLog = new TccMainLog(parameter.getBizId());
        tccLogService.create(tccMainLog);
        log.info("业务类型: {}, 业务id : {}, 创建事务日志成功", bizType, parameter.getBizId());
        PD processData = this.executeTry(parameter);
        log.info("业务类型: {}, 业务id : {}, 预执行成功", bizType, parameter.getBizId());
        R result = this.executeLocalTransaction(parameter, tccMainLog, processData);
        log.info("业务类型: {}, 业务id : {}, 本地事务成功", bizType, parameter.getBizId());
        this.executeCommit(parameter, tccMainLog);
        return result;
    }

    private PD executeTry(P parameter) {
        try {
            return attempt(parameter);
        } catch (Exception exception) {
            log.error("业务类型: {}, 业务id : {}, 预执行失败", bizType, parameter.getBizId(), exception);
            asyncCommitExecutorService.execute(
                    new TccMasterLogAsyncCheckRunnable<>(tccLogService, bizType, this::commit, this::cancel, parameter)
            );
            throw exception;
        }
    }

    private void executeCommit(P parameter, TccMainLog tccMainLog) {
        asyncCommitExecutorService.execute(
                new TccMasterLogAsyncCommitRunnable<>(tccLogService, tccMainLog, bizType, this::commit, parameter)
        );
    }

    private R executeLocalTransaction(P parameter, TccMainLog tccMainLog, PD processData) {
        try {
            return localTransactionService.execute(
                    new TccLocalTransactionSupplier<>(tccLogService, tccMainLog, this::executeLocalTransaction, parameter, processData)
            );
        } catch (Exception exception) {
            log.error("业务类型: {}, 业务id : {}, 本地事务执行失败", bizType, parameter.getBizId(), exception);
            asyncCommitExecutorService.execute(
                    new TccMasterLogAsyncCheckRunnable<>(tccLogService, bizType, this::commit, this::cancel, parameter)
            );
            throw exception;
        }
    }

    /**
     * 检查tcc日志是否失败时，需要回调获取业务id对应的实体对象入参
     *
     * @param bizId 实体对象id（业务id）
     * @return
     */
    protected abstract P callbackParameter(Long bizId);

    /**
     * 业务逻辑错误，建议通过返回自定义异常 BusinessException,
     * <p>
     * 服务调用者可以比较好的应对较复杂的业务逻辑
     *
     * @param object
     * @return 返回的结果作为本地事务方法的入参(processData)
     */
    protected abstract PD attempt(P object);

    /**
     * 执行本地事务方法和tcc事务日志在一个事务域内处理
     *
     * @param object
     * @param processData attempt 方法返回的结果
     * @return
     */
    protected abstract R executeLocalTransaction(P object, PD processData);

    protected abstract void commit(P object);

    protected abstract void cancel(P object);

}
