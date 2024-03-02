package com.damon.tcc;

import com.damon.tcc.annotation.SubBizId;
import com.damon.tcc.config.TccSubNestConfig;
import com.damon.tcc.exception.TccCancelException;
import com.damon.tcc.exception.TccCommitException;
import com.damon.tcc.exception.TccTryException;
import com.damon.tcc.function.TwoParameterFunction;
import com.damon.tcc.local_transaction.ILocalTransactionService;
import com.damon.tcc.sub_handler.TccNestSubLogTryHandler;
import com.damon.tcc.sub_handler.TccSubLogCancelHandler;
import com.damon.tcc.sub_handler.TccSubLogCommitHandler;
import com.damon.tcc.sub_log.ITccSubLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
     * @param attemptFunction                 调用外部服务先执行try动作
     * @param executeLocalTransactionFunction 外部服务调用成功后需要执行本地事务
     * @param <PD>
     * @return
     */
    protected <PD> R attempt(P parameter, Function<P, PD> attemptFunction, TwoParameterFunction<P, PD, R> executeLocalTransactionFunction) throws TccTryException {
        try {
            PD pd = attemptFunction.apply(parameter);
            R result = localTransactionService.execute(() ->
                    new TccNestSubLogTryHandler<>(tccSubLogService, executeLocalTransactionFunction::apply, bizType).execute(parameter, pd)
            );
            log.info("子事务业务类型: {}, 业务id : {}, try 成功", bizType, parameter.getBizId());
            return result;
        } catch (Exception e) {
            log.error("子事务业务类型: {}, 业务id : {}, try失败", bizType, parameter.getBizId(), e);
            throw new TccTryException(e);
        }

    }

    /**
     * @param parameter
     * @param commitConsumer                  调用外部服务先执行commit动作
     * @param executeLocalTransactionConsumer 外部服务调用成功后需要执行本地事务
     */
    protected void commit(P parameter, Consumer<P> commitConsumer, Consumer<P> executeLocalTransactionConsumer) throws TccCommitException {
        try {
            commitConsumer.accept(parameter);
            localTransactionService.execute(() -> {
                new TccSubLogCommitHandler<>(tccSubLogService, executeLocalTransactionConsumer::accept, bizType).execute(parameter);
                return null;
            });
            log.info("子事务业务类型: {}, 业务id : {}, 异步commit成功", bizType, parameter.getBizId());
        } catch (Exception e) {
            log.error("子事务业务类型: {}, 业务id : {}, commit失败", bizType, parameter.getBizId(), e);
            throw new TccCommitException(e);
        }
    }

    /**
     * @param parameter
     * @param cancelConsumer                  调用外部服务先执行cancel动作
     * @param executeLocalTransactionConsumer 外部服务调用成功后需要执行本地事务
     */
    protected void cancel(P parameter, Consumer<P> cancelConsumer, Consumer<P> executeLocalTransactionConsumer) throws TccCancelException {
        try {
            cancelConsumer.accept(parameter);
            localTransactionService.execute(() -> {
                new TccSubLogCancelHandler<>(tccSubLogService, executeLocalTransactionConsumer::accept, bizType).execute(parameter);
                return null;
            });
            log.info("子事务业务类型: {}, 业务id : {}, 异步cancel成功", bizType, parameter.getBizId());
        } catch (Exception e) {
            log.error("子事务业务类型: {}, 业务id : {}, cancel失败", bizType, parameter.getBizId(), e);
            throw new TccCancelException(e);
        }
    }

}
