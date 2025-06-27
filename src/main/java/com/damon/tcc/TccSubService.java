package com.damon.tcc;

import com.damon.tcc.annotation.SubBizId;
import com.damon.tcc.config.TccSubConfig;
import com.damon.tcc.subhandler.TccSubLogCancelHandler;
import com.damon.tcc.subhandler.TccSubLogCommitHandler;
import com.damon.tcc.subhandler.TccSubLogPrepareHandler;
import com.damon.tcc.sublog.ITccSubLogService;
import com.damon.tcc.sublog.TccSubLog;
import com.damon.tcc.transaction.ILocalTransactionService;
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
     * @param prepare
     * @return
     */
    protected R prepare(P parameter, Function<P, R> prepare) {
        R result = localTransactionService.execute(() ->
                new TccSubLogPrepareHandler<>(tccSubLogService, prepare::apply, bizType).execute(parameter)
        );
        log.info("Sub-transaction Business Type: {}, Business ID: {}, prepare succeeded", bizType, parameter.getBizId());
        return result;
    }

    /**
     * commit预留的资源
     *
     * @param parameter
     * @param commit
     */
    protected void commit(P parameter, Consumer<P> commit) {
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
        localTransactionService.execute(() -> {
            new TccSubLogCommitHandler<>(tccSubLogService, commit::accept, bizType, tccSubLog).execute(parameter);
            return null;
        });
        log.info("Sub-transaction Business Type: {}, Business ID: {}, commit succeeded", bizType, parameter.getBizId());
    }

    /**
     * cancel预留的资源
     *
     * @param parameter
     * @param cancel
     */
    protected void cancel(P parameter, Consumer<P> cancel) {
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
        localTransactionService.execute(() -> {
            new TccSubLogCancelHandler<>(tccSubLogService, cancel::accept, bizType, tccSubLog).execute(parameter);
            return null;
        });
        log.info("Sub-transaction Business Type: {}, Business ID: {}, asynchronous cancel succeeded", bizType, parameter.getBizId());
    }

}
