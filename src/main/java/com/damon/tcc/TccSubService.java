package com.damon.tcc;

import com.damon.tcc.annotation.SubBizId;
import com.damon.tcc.config.TccSubConfig;
import com.damon.tcc.exception.TccCancelException;
import com.damon.tcc.exception.TccCommitException;
import com.damon.tcc.exception.TccTryException;
import com.damon.tcc.local_transaction.ILocalTransactionService;
import com.damon.tcc.sub_handler.TccSubLogCancelHandler;
import com.damon.tcc.sub_handler.TccSubLogCommitHandler;
import com.damon.tcc.sub_handler.TccSubLogTryHandler;
import com.damon.tcc.sub_log.ITccSubLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * tcc子事务服务
 *
 * @param <R>
 * @param <P>
 */
public abstract class TccSubService<R, P extends SubBizId> {
    private final Logger log = LoggerFactory.getLogger(TccSubService.class);
    private final String bizType;
    private final ITccSubLogService tccSubLogService;
    private final ILocalTransactionService localTransactionService;

    protected TccSubService(TccSubConfig tccSubConfig) {
        this.tccSubLogService = tccSubConfig.getTccSubLogService();
        this.localTransactionService = tccSubConfig.getLocalTransactionService();
        this.bizType = tccSubConfig.getBizType();
    }

    /**
     * try执行业务，进行资源预留
     *
     * @param parameter
     * @param attempt
     * @return
     * @throws TccTryException
     */
    protected R attempt(P parameter, Function<P, R> attempt) throws TccTryException {
        R result = localTransactionService.execute(() ->
                new TccSubLogTryHandler<>(tccSubLogService, attempt::apply, bizType).execute(parameter)
        );
        log.info("子事务业务类型: {}, 业务id : {}, try 成功", bizType, parameter.getBizId());
        return result;
    }

    /**
     * commit预留的资源
     *
     * @param parameter
     * @param commit
     * @throws TccCommitException
     */
    protected void commit(P parameter, Consumer<P> commit) throws TccCommitException {
        localTransactionService.execute(() -> {
            new TccSubLogCommitHandler<>(tccSubLogService, commit::accept, bizType).execute(parameter);
            return null;
        });
        log.info("子事务业务类型: {}, 业务id : {}, 异步commit成功", bizType, parameter.getBizId());
    }

    /**
     * cancel预留的资源
     *
     * @param parameter
     * @param cancel
     * @throws TccCancelException
     */
    protected void cancel(P parameter, Consumer<P> cancel) throws TccCancelException {
        localTransactionService.execute(() -> {
            new TccSubLogCancelHandler<>(tccSubLogService, cancel::accept, bizType).execute(parameter);
            return null;
        });
        log.info("子事务业务类型: {}, 业务id : {}, 异步cancel成功", bizType, parameter.getBizId());
    }

}
