package com.damon.tcc;

import com.damon.tcc.sub_handler.TccSubLogCancelHandler;
import com.damon.tcc.sub_handler.TccSubLogCommitHandler;
import com.damon.tcc.sub_handler.TccSubLogTryHandler;
import com.damon.tcc.sub_log.ITccSubLogService;
import com.damon.tcc.transaction.ILocalTransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class TccSubTemplateService<R, P extends BizId> {
    private final Logger log = LoggerFactory.getLogger(TccSubTemplateService.class);
    private final String bizType;
    private final ITccSubLogService tccSubLogService;
    private final ILocalTransactionService localTransactionService;

    protected TccSubTemplateService(TccSubConfig tccSubConfig) {
        this.tccSubLogService = tccSubConfig.getTccSubLogService();
        this.localTransactionService = tccSubConfig.getLocalTransactionService();
        this.bizType = tccSubConfig.getBizType();
    }

    protected R processTry(P parameter) {
        R result = localTransactionService.execute(() ->
                new TccSubLogTryHandler<>(tccSubLogService, this::tryPhase, bizType).execute(parameter)
        );
        log.info("子事务业务类型: {}, 业务id : {}, try 成功", bizType, parameter.getBizId());
        return result;
    }

    protected void processCommit(P parameter) {
        localTransactionService.execute(() -> {
            new TccSubLogCommitHandler<>(tccSubLogService, this::commitPhase, bizType).execute(parameter);
            return null;
        });
        log.info("子事务业务类型: {}, 业务id : {}, 异步commit成功", bizType, parameter.getBizId());
    }

    protected void processCancel(P parameter) {
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
