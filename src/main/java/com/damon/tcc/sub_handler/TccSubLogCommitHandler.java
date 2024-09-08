package com.damon.tcc.sub_handler;

import com.damon.tcc.annotation.SubBizId;
import com.damon.tcc.sub_log.ITccSubLogService;
import com.damon.tcc.sub_log.TccSubLog;
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
            log.error("子事务业务类型: {}, 业务id : {}, 子业务id : {}, commit失败", bizType, parameter.getBizId(), parameter.getSubBizId(), e);
            throw e;
        }
    }
}
