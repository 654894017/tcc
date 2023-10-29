package com.damon.tcc;

import cn.hutool.core.thread.NamedThreadFactory;
import com.damon.tcc.exception.BusinessException;
import com.damon.tcc.exception.TccException;
import com.damon.tcc.id.IIDGenerateService;
import com.damon.tcc.log.ITccLogService;
import com.damon.tcc.log.TccLog;
import com.damon.tcc.transaction.ILocalTransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public abstract class TccTemplateService<R, O extends BizId> {
    private final Logger log = LoggerFactory.getLogger(TccTemplateService.class);
    private final ExecutorService executorService;
    private final ITccLogService tccLogService;
    private final IIDGenerateService idGenerateService;
    private final ILocalTransactionService localTransactionService;
    private final String bizType;

    public TccTemplateService(TccConfig config) {
        this.tccLogService = config.getTccLogService();
        this.idGenerateService = config.getIdGenerateService();
        this.bizType = config.getBizType();
        this.localTransactionService = config.getLocalTransactionService();
        this.executorService = new ThreadPoolExecutor(config.getAsyncThreadMinNumber(), config.getAsyncThreadMaxNumber(),
                120L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(config.getQueueSize()),
                new NamedThreadFactory("tcc-aync-pool-", false), new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    public abstract void checkTrasactionStatus();

    /**
     *
     *
     *
     * @param object
     * @return
     * @throws BusinessException
     * @throws TccException
     */
    public R process(O object) {
        TccLog tccLog = new TccLog(idGenerateService.nextId(), object.getBizId());
        tccLogService.create(tccLog);
        log.info("业务类型: {}, 业务id : {}, 创建事务日志成功", bizType, object.getBizId());
        try {
            tryPhase(object);
            log.info("业务类型: {}, 业务id : {}, 预执行成功", bizType, object.getBizId());
        } catch (BusinessException businessException) {
            executorService.submit(() -> {
                try {
                    cancelPhase(object);
                    tccLogService.rollback(tccLog);
                    log.info("业务类型: {}, 业务id : {}, 异步cancel成功", bizType, object.getBizId());
                } catch (Exception e) {
                    log.error("业务类型: {}, 业务id : {}, 异步cancel失败", bizType, object.getBizId(), e);
                }
            });
            throw businessException;
        } catch (Exception e) {
            throw new TccException(e);
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
            throw new TccException(exception);
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
     * 业务逻辑错误，建议通过返回自定义异常 BusinessException,
     *
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

    protected TccFailedLogIterator queryFailedLogs(Integer checkedCount, Integer pageSize) {
        Integer failedLogsTotal = tccLogService.getFailedLogsTotal();
        Integer totalPage = failedLogsTotal / pageSize;
        return new TccFailedLogIterator(totalPage, pageNumber ->
                tccLogService.queryFailedLogs(checkedCount, pageSize, pageNumber)
        );
    }

    public void check(O object) {
        TccLog tccLog = tccLogService.get(object.getBizId());
        if (tccLog == null) {
            log.warn("找不到对应的tcclog日志信息, 业务类型: {}, 业务id :{}", bizType, object.getBizId());
            return;
        }

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
            tccLogService.updateCheckCount(tccLog);
        }
    }

}
