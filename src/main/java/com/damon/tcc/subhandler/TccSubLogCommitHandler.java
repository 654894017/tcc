package com.damon.tcc.subhandler;

import com.damon.tcc.annotation.SubBizId;
import com.damon.tcc.exception.TccCommitException;
import com.damon.tcc.sublog.ITccSubLogService;
import com.damon.tcc.sublog.TccSubLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class TccSubLogCommitHandler<P extends SubBizId> {
    private final Logger log = LoggerFactory.getLogger(TccSubLogCommitHandler.class);
    private final ITccSubLogService tccSubLogService;
    private final Consumer<P> commitPhaseConsumer;
    private final String bizType;
    private final TccSubLog tccSubLog;

    public TccSubLogCommitHandler(ITccSubLogService tccSubLogService, Consumer<P> commitPhaseConsumer, String bizType, TccSubLog tccSubLog) {
        this.tccSubLogService = tccSubLogService;
        this.commitPhaseConsumer = commitPhaseConsumer;
        this.bizType = bizType;
        this.tccSubLog = tccSubLog;
    }

    public void execute(P parameter) {
        try {
            tccSubLog.commit();
            tccSubLogService.update(tccSubLog);
            commitPhaseConsumer.accept(parameter);
        } catch (Exception e) {
            log.error("Sub-transaction Business Type: {}, Business ID: {}, Sub-Business ID: {}, commit failed",
                    bizType, parameter.getBizId(), parameter.getSubBizId(), e);
            throw new TccCommitException(e);
        }
    }
}
