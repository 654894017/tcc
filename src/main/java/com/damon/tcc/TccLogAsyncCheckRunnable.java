package com.damon.tcc;

import cn.hutool.core.thread.ThreadUtil;
import com.damon.tcc.log.ITccLogService;
import com.damon.tcc.log.TccLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;
import java.util.function.Function;

public class TccLogAsyncCheckRunnable<O extends BizId> implements Runnable {
    private final Logger log = LoggerFactory.getLogger(TccLogAsyncCheckRunnable.class);
    private final ITccLogService tccLogService;
    private final TccLog tccLog;
    private final String bizType;
    private final Function<Long, O> callbackParameterFunction;
    private final Consumer<O> commitPhaseConsumer;
    private final Consumer<O> cancelPhaseConsumer;

    public TccLogAsyncCheckRunnable(ITccLogService tccLogService, TccLog tccLog, String bizType,
                                    Function<Long, O> callbackParameterFunction,
                                    Consumer<O> commitPhaseConsumer, Consumer<O> cancelPhaseConsumer) {
        this.tccLogService = tccLogService;
        this.tccLog = tccLog;
        this.bizType = bizType;
        this.callbackParameterFunction = callbackParameterFunction;
        this.commitPhaseConsumer = commitPhaseConsumer;
        this.cancelPhaseConsumer = cancelPhaseConsumer;
    }

    @Override
    public void run() {
        O object;
        try {
            object = callbackParameterFunction.apply(tccLog.getBizId());
        } catch (Exception e) {
            log.error("获取日志关联业务信息失败, 业务类型: {}, 业务id : {}, ", bizType, tccLog.getBizId(), e);
            ThreadUtil.safeSleep(1000);
            return;
        }
        if (object != null) {
            try {
                this.check(object, tccLog);
            } catch (Exception e) {
                log.error("业务类型: {}, 业务id :{}, 异步check失败", bizType, object.getBizId(), e);
                tccLogService.updateCheckTimes(tccLog);
                ThreadUtil.safeSleep(1000);
            }
        } else {
            //这个情况出现在try成功了，本地事务处理失败的情况下出现，可以忽略它
            log.warn("无效的事务日志信息, 业务类型: {}, 业务id : {}, ", bizType, tccLog.getBizId());
            tccLogService.updateCheckTimes(tccLog);
        }
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
    private void check(O object, TccLog tccLog) {
        if (tccLog.isCommited() || tccLog.isRollbacked()) {
            log.warn("对应的tcclog日志信息已回滚或已提交, 不执行提交状态检查,业务类型: {}, 业务id :{}", bizType, object.getBizId());
            return;
        }

        if (tccLog.isLocalCommited()) {
            commitPhaseConsumer.accept(object);
            tccLogService.commit(tccLog);
            log.info("业务类型: {}, 业务id :{}, 异步重试commit成功", bizType, object.getBizId());
        } else {
            cancelPhaseConsumer.accept(object);
            tccLogService.rollback(tccLog);
            log.info("业务类型: {}, 业务id :{}, 异步重试cancel成功", bizType, object.getBizId());
        }
    }
}
