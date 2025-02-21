package com.damon.tcc;

import com.damon.tcc.annotation.SubBizId;
import com.damon.tcc.config.TccSubConfig;
import com.damon.tcc.local_transaction.ILocalTransactionService;
import com.damon.tcc.sub_handler.TccSubLogCancelHandler;
import com.damon.tcc.sub_handler.TccSubLogCommitHandler;
import com.damon.tcc.sub_handler.TccSubLogTryHandler;
import com.damon.tcc.sub_log.ITccSubLogService;
import com.damon.tcc.sub_log.TccSubLog;
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
                new TccSubLogTryHandler<>(tccSubLogService, prepare::apply, bizType).execute(parameter)
        );
        log.info("子事务业务类型: {}, 业务id : {}, try 成功", bizType, parameter.getBizId());
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
            log.warn("找不到对应的业务类型: {}, 业务id: {}, 关联的子事务日志，无法进行commit操作", bizType, parameter.getBizId());
            return;
        }
        if (tccSubLog.isCommited() || tccSubLog.isCanceled()) {
            log.info("业务类型: {}, 业务id: {}, 关联的子事务日志已完成Commit或Cancel处理，不再继续执行Commit操作 ", bizType, parameter.getBizId());
            return;
        }
        localTransactionService.execute(() -> {
            new TccSubLogCommitHandler<>(tccSubLogService, commit::accept, bizType, tccSubLog).execute(parameter);
            return null;
        });
        log.info("子事务业务类型: {}, 业务id : {}, 异步commit成功", bizType, parameter.getBizId());
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
            log.warn("找不到对应的业务类型: {}, 业务id: {}, 关联的子事务日志信息，无法进行cancel操作", bizType, parameter.getBizId());
            return;
        }
        if (tccSubLog.isCommited() || tccSubLog.isCanceled()) {
            log.info("业务类型: {}, 业务id: {}, 关联的子事务日志已完成Commit或Cancel处理，不再继续执行Cancel操作 ", bizType, parameter.getBizId());
            return;
        }
        localTransactionService.execute(() -> {
            new TccSubLogCancelHandler<>(tccSubLogService, cancel::accept, bizType, tccSubLog).execute(parameter);
            return null;
        });
        log.info("子事务业务类型: {}, 业务id : {}, 异步cancel成功", bizType, parameter.getBizId());
    }

}
