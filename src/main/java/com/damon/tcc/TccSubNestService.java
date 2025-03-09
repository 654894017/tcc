package com.damon.tcc;

import com.damon.tcc.annotation.SubBizId;
import com.damon.tcc.config.TccSubNestConfig;
import com.damon.tcc.exception.TccCancelException;
import com.damon.tcc.exception.TccCommitException;
import com.damon.tcc.exception.TccPrepareException;
import com.damon.tcc.local_transaction.ILocalTransactionService;
import com.damon.tcc.sub_handler.TccNestSubLogPrepareHandler;
import com.damon.tcc.sub_handler.TccSubLogCancelHandler;
import com.damon.tcc.sub_handler.TccSubLogCommitHandler;
import com.damon.tcc.sub_log.ITccSubLogService;
import com.damon.tcc.sub_log.TccSubLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * tcc嵌套子事务服务，如果存在子事务嵌套的场景使用该类
 *
 * @param <R>
 * @param <P>
 */
public abstract class TccSubNestService<R, P extends SubBizId> {
    private final Logger log = LoggerFactory.getLogger(TccSubNestService.class);
    private final String bizType;
    private final ITccSubLogService tccSubLogService;
    private final ILocalTransactionService localTransactionService;

    public TccSubNestService(TccSubNestConfig config) {
        this.tccSubLogService = config.getTccSubLogService();
        this.localTransactionService = config.getLocalTransactionService();
        this.bizType = config.getBizType();
    }

    /**
     * @param parameter
     * @param prepare
     * @param executeLocalTransactionFunction
     * @param <PD>
     * @return
     */
    protected <PD> R prepare(P parameter, Function<P, PD> prepare, BiFunction<P, PD, R> executeLocalTransactionFunction) {
        try {
            PD pd = prepare.apply(parameter);
            R result = localTransactionService.execute(() ->
                    new TccNestSubLogPrepareHandler<>(tccSubLogService, executeLocalTransactionFunction, bizType).execute(parameter, pd)
            );
            log.info("Sub-transaction Business Type: {}, Business ID: {}, try succeeded", bizType, parameter.getBizId());
            return result;
        } catch (Exception e) {
            log.error("Sub-transaction Business Type: {}, Business ID: {}, try failed", bizType, parameter.getBizId(), e);
            throw new TccPrepareException(e);
        }
    }

    /**
     * @param parameter
     * @param commitConsumer                  调用外部服务先执行commit动作
     * @param executeLocalTransactionConsumer 外部服务调用成功后需要执行本地事务
     */
    protected void commit(P parameter, Consumer<P> commitConsumer, Consumer<P> executeLocalTransactionConsumer) {
        try {
            TccSubLog tccSubLog = tccSubLogService.get(parameter.getBizId(), parameter.getSubBizId());
            if (tccSubLog == null) {
                log.warn("Cannot find the corresponding business type: {}, Business ID: {}, associated sub-transaction log, commit operation cannot be performed",
                        bizType, parameter.getBizId());
                return;
            }
            if (tccSubLog.isCommited() || tccSubLog.isCanceled()) {
                log.info("Business Type: {}, Business ID: {}, the associated sub-transaction log has already completed Commit or Cancel processing, skipping Commit operation",
                        bizType, parameter.getBizId());
                return;
            }
            commitConsumer.accept(parameter);
            localTransactionService.execute(() -> {
                new TccSubLogCommitHandler<>(tccSubLogService, executeLocalTransactionConsumer, bizType, tccSubLog).execute(parameter);
                return null;
            });
            log.info("Sub-transaction Business Type: {}, Business ID: {}, asynchronous commit succeeded", bizType, parameter.getBizId());
        } catch (Exception e) {
            log.error("Sub-transaction Business Type: {}, Business ID: {}, commit failed", bizType, parameter.getBizId(), e);
            throw new TccCommitException(e);
        }
    }

    /**
     * @param parameter
     * @param cancel                          调用外部服务先执行cancel动作
     * @param executeLocalTransactionConsumer 外部服务调用成功后需要执行本地事务
     */
    protected void cancel(P parameter, Consumer<P> cancel, Consumer<P> executeLocalTransactionConsumer) {
        try {
            TccSubLog tccSubLog = tccSubLogService.get(parameter.getBizId(), parameter.getSubBizId());
            if (tccSubLog == null) {
                TccSubLog subLog = new TccSubLog(parameter.getBizId(), parameter.getSubBizId());
                subLog.cancel();
                tccSubLogService.create(subLog);
                log.warn("Cannot find the corresponding business type: {}, Business ID: {}, associated sub-transaction log, cancel operation cannot be performed",
                        bizType, parameter.getBizId());
                return;
            }
            if (tccSubLog.isCommited() || tccSubLog.isCanceled()) {
                log.info("Business Type: {}, Business ID: {}, the associated sub-transaction log has already completed Commit or Cancel processing, skipping Cancel operation",
                        bizType, parameter.getBizId());
                return;
            }
            cancel.accept(parameter);
            localTransactionService.execute(() -> {
                new TccSubLogCancelHandler<>(tccSubLogService, executeLocalTransactionConsumer, bizType, tccSubLog).execute(parameter);
                return null;
            });
            log.info("Sub-transaction Business Type: {}, Business ID: {}, asynchronous cancel succeeded", bizType, parameter.getBizId());
        } catch (Exception e) {
            log.error("Sub-transaction Business Type: {}, Business ID: {}, cancel failed", bizType, parameter.getBizId(), e);
            throw new TccCancelException(e);
        }
    }

}
