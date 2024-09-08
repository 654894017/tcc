package com.damon.tcc.sub_handler;

import com.damon.tcc.annotation.SubBizId;
import com.damon.tcc.sub_log.ITccSubLogService;
import com.damon.tcc.sub_log.TccSubLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class TccSubLogCancelHandler<P extends SubBizId> {
    private final Logger log = LoggerFactory.getLogger(TccSubLogCancelHandler.class);
    private final ITccSubLogService tccSubLogService;
    private final Consumer<P> cancelPhaseConsumer;
    private final String bizType;
    private final TccSubLog tccSubLog;

    public TccSubLogCancelHandler(ITccSubLogService tccSubLogService, Consumer<P> cancelPhaseConsumer, String bizType, TccSubLog tccSubLog) {
        this.tccSubLogService = tccSubLogService;
        this.cancelPhaseConsumer = cancelPhaseConsumer;
        this.bizType = bizType;
        this.tccSubLog = tccSubLog;
    }

    public void execute(P parameter) {
        try {
            tccSubLog.cancel();
            tccSubLogService.update(tccSubLog);
            cancelPhaseConsumer.accept(parameter);
        } catch (Exception e) {
            log.error("子事务业务类型: {}, 业务id : {}, 子业务id : {}, cancel失败", bizType, parameter.getBizId(), parameter.getSubBizId(), e);
            throw e;
        }
    }
}
