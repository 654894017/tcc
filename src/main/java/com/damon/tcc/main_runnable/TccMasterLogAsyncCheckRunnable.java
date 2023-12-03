package com.damon.tcc.main_runnable;

import cn.hutool.core.thread.ThreadUtil;
import com.damon.tcc.BizId;
import com.damon.tcc.main_log.ITccMainLogService;
import com.damon.tcc.main_log.TccMainLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;
import java.util.function.Function;

public class TccMasterLogAsyncCheckRunnable<O extends BizId> implements Runnable {
    private final Logger log = LoggerFactory.getLogger(TccMasterLogAsyncCheckRunnable.class);
    private final ITccMainLogService tccLogService;
    private final TccMainLog tccMainLog;
    private final String bizType;
    private final Function<Long, O> callbackParameterFunction;
    private final Consumer<O> commitPhaseConsumer;
    private final Consumer<O> cancelPhaseConsumer;
    private O parameter;

    public TccMasterLogAsyncCheckRunnable(ITccMainLogService tccLogService, TccMainLog tccMainLog, String bizType,
                                          Function<Long, O> callbackParameterFunction,
                                          Consumer<O> commitPhaseConsumer, Consumer<O> cancelPhaseConsumer) {
        this(tccLogService, tccMainLog, bizType, callbackParameterFunction, commitPhaseConsumer, cancelPhaseConsumer, null);
    }

    public TccMasterLogAsyncCheckRunnable(ITccMainLogService tccLogService, TccMainLog tccMainLog, String bizType,
                                          Function<Long, O> callbackParameterFunction,
                                          Consumer<O> commitPhaseConsumer, Consumer<O> cancelPhaseConsumer, O parameter) {
        this.tccLogService = tccLogService;
        this.tccMainLog = tccMainLog;
        this.bizType = bizType;
        this.callbackParameterFunction = callbackParameterFunction;
        this.commitPhaseConsumer = commitPhaseConsumer;
        this.cancelPhaseConsumer = cancelPhaseConsumer;
        this.parameter = parameter;
    }


    @Override
    public void run() {
        if (parameter == null) {
            try {
                parameter = callbackParameterFunction.apply(tccMainLog.getBizId());
            } catch (Exception e) {
                log.error("获取日志关联业务信息失败, 业务类型: {}, 业务id : {}, ", bizType, tccMainLog.getBizId(), e);
                ThreadUtil.safeSleep(1000);
                return;
            }
        }
        if (parameter != null) {
            try {
                this.check(parameter, tccMainLog);
            } catch (Exception e) {
                log.error("业务类型: {}, 业务id :{}, 异步check失败", bizType, parameter.getBizId(), e);
                tccMainLog.resetLastVersion();
                tccMainLog.check();
                tccLogService.update(tccMainLog);
                ThreadUtil.safeSleep(1000);
            }
        } else {
            //这个情况出现在try成功了，本地事务处理失败的情况下出现，可以忽略它
            log.warn("无效的事务日志信息, 业务类型: {}, 业务id : {}, ", bizType, tccMainLog.getBizId());
            tccMainLog.check();
            tccLogService.update(tccMainLog);
        }
    }

    /**
     * 检查事务是否成功
     * <p>
     * 1.如果事务状态为：已完成本地事务，则执行commit行为
     * <p>
     * 2.如果事务状态为：创建事务状态，则执行cancel行为
     *
     * @param parameter
     */
    private void check(O parameter, TccMainLog tccMainLog) {
        if (tccMainLog.isCommited() || tccMainLog.isRollbacked()) {
            log.warn("对应的tcclog日志信息已回滚或已提交, 不执行提交状态检查,业务类型: {}, 业务id :{}", bizType, parameter.getBizId());
            return;
        }

        if (tccMainLog.isLocalCommited()) {
            tccMainLog.commit();
            commitPhaseConsumer.accept(parameter);
            tccLogService.update(tccMainLog);
            log.info("业务类型: {}, 业务id :{}, 异步重试commit成功", bizType, parameter.getBizId());
        } else {
            tccMainLog.rollback();
            cancelPhaseConsumer.accept(parameter);
            tccLogService.update(tccMainLog);
            log.info("业务类型: {}, 业务id :{}, 异步重试cancel成功", bizType, parameter.getBizId());
        }
    }
}
