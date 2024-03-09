package com.damon.tcc;

import com.damon.tcc.annotation.SubBizId;
import com.damon.tcc.config.TccSubNestConfig;
import com.damon.tcc.local_transaction.ILocalTransactionService;
import com.damon.tcc.sub_handler.TccNestSubLogTryHandler;
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
     * @param attemptFunction
     * @param executeLocalTransactionFunction
     * @param <PD>
     * @return
     */
    protected <PD> R attempt(P parameter, Function<P, PD> attemptFunction, BiFunction<P, PD, R> executeLocalTransactionFunction) {
        try {
            PD pd = attemptFunction.apply(parameter);
            R result = localTransactionService.execute(() ->
                    new TccNestSubLogTryHandler<>(tccSubLogService, executeLocalTransactionFunction, bizType).execute(parameter, pd)
            );
            log.info("子事务业务类型: {}, 业务id : {}, try 成功", bizType, parameter.getBizId());
            return result;
        } catch (Exception e) {
            log.error("子事务业务类型: {}, 业务id : {}, try失败", bizType, parameter.getBizId(), e);
            throw e;
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
                log.warn("找不到对应的业务类型: {}, 业务id: {}, 关联的子事务日志，无法进行commit操作", bizType, parameter.getBizId());
                return;
            }
            if (tccSubLog.isCommited() || tccSubLog.isCanceled()) {
                log.info("业务类型: {}, 业务id: {}, 关联的子事务日志已完成Commit或Cancel处理，不再继续执行Commit操作 ", bizType, parameter.getBizId());
                return;
            }
            commitConsumer.accept(parameter);
            localTransactionService.execute(() -> {
                new TccSubLogCommitHandler<>(tccSubLogService, executeLocalTransactionConsumer, bizType, tccSubLog).execute(parameter);
                return null;
            });
            log.info("子事务业务类型: {}, 业务id : {}, 异步commit成功", bizType, parameter.getBizId());
        } catch (Exception e) {
            log.error("子事务业务类型: {}, 业务id : {}, commit失败", bizType, parameter.getBizId(), e);
            throw e;
        }
    }

    /**
     * @param parameter
     * @param cancelConsumer                  调用外部服务先执行cancel动作
     * @param executeLocalTransactionConsumer 外部服务调用成功后需要执行本地事务
     */
    protected void cancel(P parameter, Consumer<P> cancelConsumer, Consumer<P> executeLocalTransactionConsumer) {
        try {
            TccSubLog tccSubLog = tccSubLogService.get(parameter.getBizId(), parameter.getSubBizId());
            if (tccSubLog == null) {
                TccSubLog subLog = new TccSubLog(parameter.getBizId(), parameter.getSubBizId());
                subLog.cancel();
                tccSubLogService.create(subLog);
                log.warn("找不到对应的业务类型: {}, 业务id: {}, 关联的子事务日志信息，无法进行cancel操作", bizType, parameter.getBizId());
                return;
            }
            if (tccSubLog.isCommited() || tccSubLog.isCanceled()) {
                log.info("业务类型: {}, 业务id: {}, 关联的子事务日志已完成Commit或Cancel处理，不再继续执行Cancelc操作 ", bizType, parameter.getBizId());
                return;
            }
            cancelConsumer.accept(parameter);
            localTransactionService.execute(() -> {
                new TccSubLogCancelHandler<>(tccSubLogService, executeLocalTransactionConsumer, bizType, tccSubLog).execute(parameter);
                return null;
            });
            log.info("子事务业务类型: {}, 业务id : {}, 异步cancel成功", bizType, parameter.getBizId());
        } catch (Exception e) {
            log.error("子事务业务类型: {}, 业务id : {}, cancel失败", bizType, parameter.getBizId(), e);
            throw e;
        }
    }

}
