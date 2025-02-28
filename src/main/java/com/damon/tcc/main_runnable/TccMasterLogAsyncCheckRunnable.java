package com.damon.tcc.main_runnable;

import com.damon.tcc.annotation.BizId;
import com.damon.tcc.exception.BizIdInvalidException;
import com.damon.tcc.main_log.ITccMainLogService;
import com.damon.tcc.main_log.TccMainLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;
import java.util.function.Function;

public class TccMasterLogAsyncCheckRunnable<O extends BizId> implements Runnable {
    private final Logger log = LoggerFactory.getLogger(TccMasterLogAsyncCheckRunnable.class);
    private final ITccMainLogService tccLogService;
    private final String bizType;
    private final Consumer<O> commitPhaseConsumer;
    private final Consumer<O> cancelPhaseConsumer;
    private Function<Long, O> callbackParameterFunction;
    private TccMainLog tccMainLog;
    private O parameter;

    public TccMasterLogAsyncCheckRunnable(ITccMainLogService tccLogService, String bizType,
                                          Consumer<O> commitPhaseConsumer,
                                          Consumer<O> cancelPhaseConsumer,
                                          O parameter) {
        this.tccLogService = tccLogService;
        this.bizType = bizType;
        this.commitPhaseConsumer = commitPhaseConsumer;
        this.cancelPhaseConsumer = cancelPhaseConsumer;
        this.parameter = parameter;
    }

    public TccMasterLogAsyncCheckRunnable(ITccMainLogService tccLogService, String bizType,
                                          Consumer<O> commitPhaseConsumer,
                                          Consumer<O> cancelPhaseConsumer,
                                          Function<Long, O> callbackParameterFunction,
                                          TccMainLog tccMainLog) {
        this.callbackParameterFunction = callbackParameterFunction;
        this.tccLogService = tccLogService;
        this.bizType = bizType;
        this.commitPhaseConsumer = commitPhaseConsumer;
        this.cancelPhaseConsumer = cancelPhaseConsumer;
        this.tccMainLog = tccMainLog;
    }

    @Override
    public void run() {
        try {
            if (parameter == null) {
                parameter = callbackParameterFunction.apply(tccMainLog.getBizId());
                if (parameter == null) {
                    throw new BizIdInvalidException("Invalid Business ID: " + tccMainLog.getBizId());
                }
            }
            if (tccMainLog == null) {
                tccMainLog = tccLogService.get(parameter.getBizId());
                if (tccMainLog == null) {
                    throw new BizIdInvalidException("Invalid Business ID: " + parameter.getBizId());
                }
            }
            this.check(parameter, tccMainLog);
        } catch (Exception e) {
            handleException(e);
        }
    }

    protected void handleException(Exception e) {
        try {
            log.error("Business Type: {}, Business ID: {}, Asynchronous check failed", bizType, parameter.getBizId(), e);
            TccMainLog mainLog = tccLogService.get(tccMainLog.getBizId());
            if (mainLog == null) {
                log.error("Business Type: {}, Business ID: {}, Invalid BizId", bizType, parameter.getBizId(), e);
                return;
            }
            mainLog.check();
            tccLogService.update(mainLog);
        } catch (Exception exception) {
            log.error("Business Type: {}, Business ID: {}, Failed to update retry count for log", bizType, parameter.getBizId(), exception);
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
    protected void check(O parameter, TccMainLog tccMainLog) {
        if (tccMainLog.isCommited() || tccMainLog.isRollbacked()) {
            log.warn("The corresponding TCC log has already been committed or rolled back. Skipping commit state check. Business Type: {}, Business ID: {}",
                    bizType, parameter.getBizId());
            return;
        }
        if (tccMainLog.isLocalCommited()) {
            tccMainLog.commit();
            commitPhaseConsumer.accept(parameter);
            tccLogService.update(tccMainLog);
            log.info("Business Type: {}, Business ID: {}, Asynchronous retry commit successful", bizType, parameter.getBizId());
        } else {
            tccMainLog.rollback();
            cancelPhaseConsumer.accept(parameter);
            tccLogService.update(tccMainLog);
            log.info("Business Type: {}, Business ID: {}, Asynchronous retry cancel successful", bizType, parameter.getBizId());
        }
    }
}
