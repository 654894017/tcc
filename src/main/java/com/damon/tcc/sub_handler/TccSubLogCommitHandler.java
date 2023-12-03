package com.damon.tcc.sub_handler;

import com.damon.tcc.BizId;
import com.damon.tcc.exception.OptimisticLockException;
import com.damon.tcc.sub_log.ITccSubLogService;
import com.damon.tcc.sub_log.TccSubLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class TccSubLogCommitHandler<P extends BizId> {
    private final Logger log = LoggerFactory.getLogger(TccSubLogCommitHandler.class);
    private final ITccSubLogService tccSubLogService;
    private final Consumer<P> commitPhaseConsumer;
    private final String bizType;

    public TccSubLogCommitHandler(ITccSubLogService tccSubLogService, Consumer<P> commitPhaseConsumer, String bizType) {
        this.tccSubLogService = tccSubLogService;
        this.commitPhaseConsumer = commitPhaseConsumer;
        this.bizType = bizType;
    }

    public void execute(P parameter) {
        TccSubLog tccSubLog = tccSubLogService.get(parameter.getBizId());
        tccSubLog.commit();
        try{
            tccSubLogService.update(tccSubLog);
            commitPhaseConsumer.accept(parameter);
        } catch (OptimisticLockException e) {
            log.error("子事务业务类型: {}, 业务id : {}, commit失败", bizType, parameter.getBizId(), e);
            throw e;
        }
    }
}
