package com.damon.tcc;

import cn.hutool.core.thread.NamedThreadFactory;
import com.damon.tcc.main_log.ITccMainLogService;
import com.damon.tcc.main_log.TccMainLog;
import com.damon.tcc.main_runnable.TccMasterLogAsyncCheckRunnable;
import com.damon.tcc.main_runnable.TccMasterLogAsyncCommitRunnable;
import com.damon.tcc.transaction.ILocalTransactionService;
import com.damon.tcc.transaction.TccLocalTransactionSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public abstract class TccMainTemplateService<R, O extends BizId> {
    private final Logger log = LoggerFactory.getLogger(TccMainTemplateService.class);
    private final ExecutorService asyncCommitExecutorService;
    private final ExecutorService asyncCheckExecutorService;
    private final ITccMainLogService tccLogService;
    private final ILocalTransactionService localTransactionService;
    private final String bizType;
    private final TccMainConfig tccMainConfig;
    public TccMainTemplateService(TccMainConfig config) {
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
        Integer totalPage = failedLogsTotal / tccMainConfig.getTccFailedLogPageSize();
        return new TccFailedLogIterator(totalPage, pageNumber ->
                tccLogService.queryFailedLogs(tccMainConfig.getFailedCheckTimes(), tccMainConfig.getTccFailedLogPageSize(), pageNumber)
        );
    }
    protected TccFailedLogIterator queryDeadLogs() {
        Integer deadLogsTotal = tccLogService.getDeadLogsTotal(tccMainConfig.getFailedCheckTimes());
        Integer totalPage = deadLogsTotal / tccMainConfig.getTccFailedLogPageSize();
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
                        new TccMasterLogAsyncCheckRunnable<>(tccLogService, tccLog, bizType,
                                this::callbackParameter, this::commitPhase, this::cancelPhase)
                );
            });
        }
    }
    /**
     * @param parameter
     * @return
     */
    protected R process(O parameter) {
        TccMainLog tccMainLog = new TccMainLog(parameter.getBizId());
        tccLogService.create(tccMainLog);
        log.info("业务类型: {}, 业务id : {}, 创建事务日志成功", bizType, parameter.getBizId());
        try {
            tryPhase(parameter);
            log.info("业务类型: {}, 业务id : {}, 预执行成功", bizType, parameter.getBizId());
        } catch (Exception exception) {
            asyncCommitExecutorService.execute(
                    new TccMasterLogAsyncCheckRunnable<>(tccLogService, tccMainLog, bizType,
                            this::callbackParameter, this::commitPhase, this::cancelPhase, parameter)
            );
            log.error("业务类型: {}, 业务id : {}, 预执行失败", bizType, parameter.getBizId(), exception);
            throw exception;
        }
        R result;
        try {
            result = localTransactionService.execute(
                    new TccLocalTransactionSupplier<>(tccLogService, tccMainLog, this::executeLocalTransactionPhase, parameter)
            );
        } catch (Exception exception) {
            log.error("业务类型: {}, 业务id : {}, 本地事务执行失败", bizType, parameter.getBizId(), exception);
            tccMainLog.resetLastVersion();
            asyncCommitExecutorService.execute(
                    new TccMasterLogAsyncCheckRunnable<>(tccLogService, tccMainLog, bizType,
                            this::callbackParameter, this::commitPhase, this::cancelPhase, parameter)
            );
            throw exception;
        }
        log.info("业务类型: {}, 业务id : {}, 本地事务成功", bizType, parameter.getBizId());
        asyncCommitExecutorService.execute(
                new TccMasterLogAsyncCommitRunnable<>(tccLogService, tccMainLog, bizType, this::commitPhase, parameter)
        );
        return result;
    }
    /**
     * 检查tcc日志是否失败时，需要回调获取业务id对应的实体对象入参
     *
     * @param bizId 实体对象id（业务id）
     * @return
     */
    protected abstract O callbackParameter(Long bizId);
    /**
     * 业务逻辑错误，建议通过返回自定义异常 BusinessException,
     * <p>
     * 服务调用者可以比较好的应对较复杂的业务逻辑
     *
     * @param object
     */
    protected abstract void tryPhase(O object);
    /**
     * 执行本地事务方法和tcc事务日志在一个事务域内处理
     *
     * @param object
     * @return
     */
    protected abstract R executeLocalTransactionPhase(O object);
    protected abstract void commitPhase(O object);
    protected abstract void cancelPhase(O object);

}
