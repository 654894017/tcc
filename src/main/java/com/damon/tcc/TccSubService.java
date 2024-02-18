package com.damon.tcc;

import com.damon.tcc.exception.TccCancelException;
import com.damon.tcc.exception.TccCommitException;
import com.damon.tcc.exception.TccTryException;
import com.damon.tcc.sub_handler.TccSubLogCancelHandler;
import com.damon.tcc.sub_handler.TccSubLogCommitHandler;
import com.damon.tcc.sub_handler.TccSubLogTryHandler;
import com.damon.tcc.sub_log.ITccSubLogService;
import com.damon.tcc.transaction.ILocalTransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 使用示例
 *
 * @param <R>
 * @param <P>
 */
public abstract class TccSubService<R, P extends BizId> {
    private final Logger log = LoggerFactory.getLogger(TccSubService.class);
    private final String bizType;
    private final ITccSubLogService tccSubLogService;
    private final ILocalTransactionService localTransactionService;

    protected TccSubService(TccSubConfig tccSubConfig) {
        this.tccSubLogService = tccSubConfig.getTccSubLogService();
        this.localTransactionService = tccSubConfig.getLocalTransactionService();
        this.bizType = tccSubConfig.getBizType();
    }

    protected R processTry(P parameter) throws TccTryException {
        R result = localTransactionService.execute(() ->
                new TccSubLogTryHandler<>(tccSubLogService, this::tryPhase, bizType).execute(parameter)
        );
        log.info("子事务业务类型: {}, 业务id : {}, try 成功", bizType, parameter.getBizId());
        return result;
    }

    protected void processCommit(P parameter) throws TccCommitException {
        localTransactionService.execute(() -> {
            new TccSubLogCommitHandler<>(tccSubLogService, this::commitPhase, bizType).execute(parameter);
            return null;
        });
        log.info("子事务业务类型: {}, 业务id : {}, 异步commit成功", bizType, parameter.getBizId());
    }

    protected void processCancel(P parameter) throws TccCancelException {
        localTransactionService.execute(() -> {
            new TccSubLogCancelHandler<>(tccSubLogService, this::cancelPhase, bizType).execute(parameter);
            return null;
        });
        log.info("子事务业务类型: {}, 业务id : {}, 异步cancel成功", bizType, parameter.getBizId());
    }

    protected abstract R tryPhase(P parameter);

    protected abstract void commitPhase(P parameter);

    protected abstract void cancelPhase(P parameter);

}
