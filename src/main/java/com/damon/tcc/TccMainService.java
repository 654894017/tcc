package com.damon.tcc;

import cn.hutool.core.thread.NamedThreadFactory;
import com.damon.tcc.annotation.BizId;
import com.damon.tcc.config.TccMainConfig;
import com.damon.tcc.exception.TccLocalTransactionException;
import com.damon.tcc.exception.TccPrepareException;
import com.damon.tcc.mainlog.ITccMainLogService;
import com.damon.tcc.mainlog.TccMainLog;
import com.damon.tcc.mainrunnable.TccMasterLogAsyncCheckRunnable;
import com.damon.tcc.mainrunnable.TccMasterLogAsyncCommitRunnable;
import com.damon.tcc.transaction.ILocalTransactionService;
import com.damon.tcc.transaction.TccLocalTransactionSupplier;
import com.damon.tcc.utils.ShardTailNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;


public abstract class TccMainService<R, D, C extends BizId> {
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
        this.asyncCommitExecutorService = new ThreadPoolExecutor(
                config.getAsyncCommitThreadMinNumber(),
                config.getAsyncCommitThreadMaxNumber(),
                120L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(config.getAsyncCommitQueueSize()),
                new NamedThreadFactory(config.getBizType() + "-tcc-async-commit-pool-", false),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        this.asyncCheckExecutorService = new ThreadPoolExecutor(
                config.getAsyncCheckThreadMinNumber(),
                config.getAsyncCheckThreadMaxNumber(),
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(config.getAsyncCheckQueueSize()),
                new NamedThreadFactory(config.getBizType() + "-tcc-async-check-pool-", false),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        this.tccMainConfig = config;
    }

    protected TccFailedLogIterator queryFailedLogs(String tailNumber) {
        Integer failedLogsTotal = tccLogService.getFailedLogsTotal(tailNumber, tccMainConfig.getFailedCheckTimes());
        log.info("Query failed logs total: {}, tailNumber: {}", failedLogsTotal, tailNumber);
        Integer totalPage = (failedLogsTotal + tccMainConfig.getTccFailedLogPageSize() - 1) / tccMainConfig.getTccFailedLogPageSize();
        return new TccFailedLogIterator(totalPage, pageNumber ->
                tccLogService.queryFailedLogs(
                        tailNumber,
                        tccMainConfig.getFailedCheckTimes(),
                        tccMainConfig.getTccFailedLogPageSize(),
                        pageNumber
                )
        );
    }

    protected TccFailedLogIterator queryDeadLogs(String tailNumber) {
        Integer deadLogsTotal = tccLogService.getDeadLogsTotal(tailNumber, tccMainConfig.getFailedCheckTimes());
        log.info("Query dead logs total: {}, tailNumber: {}", deadLogsTotal, tailNumber);
        Integer totalPage = (deadLogsTotal + tccMainConfig.getTccFailedLogPageSize() - 1) / tccMainConfig.getTccFailedLogPageSize();
        return new TccFailedLogIterator(totalPage, pageNumber ->
                tccLogService.queryDeadLogs(
                        tailNumber,
                        tccMainConfig.getFailedCheckTimes(),
                        tccMainConfig.getTccFailedLogPageSize(),
                        pageNumber
                )
        );
    }

    private void executeLogCheck(Integer shardTotal, Integer shardIndex, Function<String, TccFailedLogIterator> queryFunc) {
        List<String> tailNumbers = new ShardTailNumber(shardTotal, shardIndex, tccMainConfig.getTailLength()).generateTailNumbers();
        tailNumbers.forEach(tailNumber -> {
            TccFailedLogIterator iterator = queryFunc.apply(tailNumber);
            check(iterator);
        });
    }

    protected void executeDeadLogCheck() {
        this.executeDeadLogCheck(1, 0);
    }

    /**
     * 执行一次死信日志检查
     */
    protected void executeDeadLogCheck(Integer shardTotal, Integer shardIndex) {
        executeLogCheck(shardTotal, shardIndex, this::queryDeadLogs);
    }

    protected void executeFailedLogCheck() {
        this.executeFailedLogCheck(1, 0);
    }

    /**
     * 执行事务状态检查，供上游业务系统调用
     */
    protected void executeFailedLogCheck(Integer shardTotal, Integer shardIndex) {
        executeLogCheck(shardTotal, shardIndex, this::queryFailedLogs);
    }

    protected void check(TccFailedLogIterator iterator) {
        while (iterator.hasNext()) {
            List<TccMainLog> tccMainLogs = iterator.next();
            tccMainLogs.forEach(tccLog -> {
                asyncCheckExecutorService.execute(
                        new TccMasterLogAsyncCheckRunnable<>(
                                tccLogService,
                                bizType,
                                this::commit,
                                this::cancel,
                                this::callbackParameter,
                                tccLog
                        )
                );
            });
        }
    }

    /**
     * 执行业务
     * <p>
     * 该方法不会抛出TccCommitException和TccCancelException, 因为commit和cancel方法都是异步执行.
     *
     * @param parameter
     * @return
     * @throws TccPrepareException
     * @throws TccLocalTransactionException
     */
    protected R process(C parameter) throws TccPrepareException, TccLocalTransactionException {
        TccMainLog tccMainLog = createTccMainLog(parameter);
        log.info("Business Type: {}, Business ID: {}, Transaction log created successfully", bizType, parameter.getBizId());
        D processData = this.executePrepare(parameter);
        log.info("Business Type: {}, Business ID: {}, Pre-execution successful", bizType, parameter.getBizId());
        R result = this.executeLocalTransaction(parameter, tccMainLog, processData);
        log.info("Business Type: {}, Business ID: {}, Local transaction successful", bizType, parameter.getBizId());
        this.executeCommit(parameter, tccMainLog);
        return result;
    }

    private TccMainLog createTccMainLog(C parameter) {
        TccMainLog tccMainLog = new TccMainLog(parameter.getBizId());
        tccLogService.create(tccMainLog);
        return tccMainLog;
    }

    private D executePrepare(C parameter) {
        try {
            return prepare(parameter);
        } catch (Exception exception) {
            log.error("Business Type: {}, Business ID: {}, Pre-execution failed", bizType, parameter.getBizId(), exception);
            asyncCommitExecutorService.execute(
                    new TccMasterLogAsyncCheckRunnable<>(
                            tccLogService,
                            bizType,
                            this::commit,
                            this::cancel,
                            parameter
                    )
            );
            throw new TccPrepareException(exception);
        }
    }

    private void executeCommit(C parameter, TccMainLog tccMainLog) {
        asyncCommitExecutorService.execute(
                new TccMasterLogAsyncCommitRunnable<>(
                        tccLogService,
                        tccMainLog,
                        bizType,
                        this::commit,
                        parameter
                )
        );
    }

    private R executeLocalTransaction(C parameter, TccMainLog tccMainLog, D processData) {
        try {
            return localTransactionService.execute(
                    new TccLocalTransactionSupplier<>(
                            tccLogService,
                            tccMainLog,
                            this::executeLocalTransaction,
                            parameter,
                            processData
                    )
            );
        } catch (Exception exception) {
            log.error("Business Type: {}, Business ID: {}, Local transaction execution failed", bizType, parameter.getBizId(), exception);
            asyncCommitExecutorService.execute(
                    new TccMasterLogAsyncCheckRunnable<>(
                            tccLogService,
                            bizType,
                            this::commit,
                            this::cancel,
                            parameter
                    )
            );
            throw new TccLocalTransactionException(exception);
        }
    }

    /**
     * 检查tcc日志是否失败时，需要回调获取业务id对应的实体对象入参
     *
     * @param bizId 实体对象id（业务id）
     * @return
     */
    protected abstract C callbackParameter(Long bizId);

    /**
     * 业务逻辑错误，建议通过返回自定义异常 BusinessException,
     * <p>
     * 服务调用者可以比较好的应对较复杂的业务逻辑
     *
     * @param object
     * @return 返回的结果作为本地事务方法的入参(prepareExecuteResult)
     */
    protected abstract D prepare(C object);

    /**
     * 执行本地事务方法和tcc事务日志在一个事务域内处理
     *
     * @param object
     * @param prepareExecuteResult prepare 方法返回的结果
     * @return
     */
    protected abstract R executeLocalTransaction(C object, D prepareExecuteResult);

    protected abstract void commit(C object);

    protected abstract void cancel(C object);

}
