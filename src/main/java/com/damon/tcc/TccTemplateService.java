package com.damon.tcc;

import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.thread.ThreadUtil;
import com.damon.tcc.log.ITccLogService;
import com.damon.tcc.log.TccLog;
import com.damon.tcc.transaction.ILocalTransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public abstract class TccTemplateService<R, O extends BizId> {
    private final Logger log = LoggerFactory.getLogger(TccTemplateService.class);
    private final ExecutorService executorService;
    private final ITccLogService tccLogService;
    private final ILocalTransactionService localTransactionService;
    private final String bizType;
    private final TccConfig tccConfig;

    public TccTemplateService(TccConfig config) {
        this.tccLogService = config.getTccLogService();
        this.bizType = config.getBizType();
        this.localTransactionService = config.getLocalTransactionService();
        this.executorService = new ThreadPoolExecutor(config.getAsyncThreadMinNumber(), config.getAsyncThreadMaxNumber(),
                120L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(config.getQueueSize()),
                new NamedThreadFactory("tcc-aync-pool-", false), new ThreadPoolExecutor.CallerRunsPolicy()
        );
        this.tccConfig = config;
    }

    protected TccFailedLogIterator queryFailedLogs() {
        Integer failedLogsTotal = tccLogService.getFailedLogsTotal(tccConfig.getFailedCheckTimes());
        Integer totalPage = failedLogsTotal / tccConfig.getTccFailedLogPageSize();
        return new TccFailedLogIterator(totalPage, pageNumber ->
                tccLogService.queryFailedLogs(tccConfig.getFailedCheckTimes(), tccConfig.getTccFailedLogPageSize(), pageNumber)
        );
    }

    protected TccFailedLogIterator queryDeadLogs() {
        Integer deadLogsTotal = tccLogService.getDeadLogsTotal(tccConfig.getFailedCheckTimes());
        Integer totalPage = deadLogsTotal / tccConfig.getTccFailedLogPageSize();
        return new TccFailedLogIterator(totalPage, pageNumber ->
                tccLogService.queryDeadLogs(tccConfig.getFailedCheckTimes(), tccConfig.getTccFailedLogPageSize(), pageNumber)
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
            List<TccLog> tccLogs = iterator.next();
            tccLogs.forEach(tccLog -> {
                O object;
                try {
                    object = callbackParameter(tccLog.getBizId());
                } catch (Exception e) {
                    log.error("获取日志关联业务信息失败, 业务类型: {}, 业务id : {}, ", bizType, tccLog.getBizId(), e);
                    return;
                }
                if (object != null) {
                    this.check(object, tccLog);
                } else {
                    log.error("无效的事务日志信息, 业务类型: {}, 业务id : {}, ", bizType, tccLog.getBizId());
                }
            });
        }
    }

    /**
     * @param object
     * @return
     */
    protected R process(O object) {
        TccLog tccLog = new TccLog(object.getBizId());
        tccLogService.create(tccLog);
        log.info("业务类型: {}, 业务id : {}, 创建事务日志成功", bizType, object.getBizId());
        try {
            tryPhase(object);
            log.info("业务类型: {}, 业务id : {}, 预执行成功", bizType, object.getBizId());
        } catch (Exception exception) {
            executorService.submit(() -> {
                try {
                    cancelPhase(object);
                    tccLogService.rollback(tccLog);
                    log.info("业务类型: {}, 业务id : {}, 异步cancel成功", bizType, object.getBizId());
                } catch (Exception e) {
                    log.error("业务类型: {}, 业务id : {}, 异步cancel失败", bizType, object.getBizId(), e);
                }
            });
            throw exception;
        }
        R result;
        try {
            result = localTransactionService.executeLocalTransaction(() -> {
                tccLogService.commitLocal(tccLog);
                return executeLocalTransactionPhase(object);
            });
        } catch (Exception exception) {
            try {
                check(object);
            } catch (Exception e) {
                log.error("业务类型: {}, 业务id : {}, 执行回调检查失败", bizType, object.getBizId(), e);
            }
            throw exception;
        }
        log.info("业务类型: {}, 业务id : {}, commit本地事务成功", bizType, object.getBizId());
        executorService.submit(() -> {
            try {
                commitPhase(object);
                tccLogService.commit(tccLog);
                log.info("业务类型: {}, 业务id : {}, 异步commit成功", bizType, object.getBizId());
            } catch (Exception e) {
                log.error("业务类型: {}, 业务id : {}, 异步commit失败", bizType, object.getBizId(), e);
            }
        });
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


    protected void check(O object) {
        TccLog tccLog = tccLogService.get(object.getBizId());
        if (tccLog == null) {
            log.warn("找不到对应的tcclog日志信息, 业务类型: {}, 业务id :{}", bizType, object.getBizId());
            return;
        }
        check(object, tccLog);
    }

    /**
     * 检查事务是否成功
     * <p>
     * 1.如果事务状态为：已完成本地事务，则执行commit行为
     * <p>
     * 2.如果事务状态为：创建事务状态，则执行cancel行为
     *
     * @param object
     */
    protected void check(O object, TccLog tccLog) {
        if (tccLog.isCommited() || tccLog.isRollbacked()) {
            log.warn("对应的tcclog日志信息已回滚或已提交, 不执行提交状态检查,业务类型: {}, 业务id :{}", bizType, object.getBizId());
            return;
        }
        try {
            if (tccLog.isLocalCommited()) {
                commitPhase(object);
                tccLogService.commit(tccLog);
                log.info("业务类型: {}, 业务id :{}, 异步重试commit成功", bizType, object.getBizId());
            } else {
                cancelPhase(object);
                tccLogService.rollback(tccLog);
                log.info("业务类型: {}, 业务id :{}, 异步重试cancel成功", bizType, object.getBizId());
            }
        } catch (Exception e) {
            log.error("业务类型: {}, 业务id :{}, 异步check失败", bizType, object.getBizId(), e);
            tccLogService.updateCheckTimes(tccLog);
            ThreadUtil.safeSleep(2000);
        }
    }

}
