package com.damon.tcc.sub_handler;

import com.damon.tcc.annotation.SubBizId;
import com.damon.tcc.sub_log.ITccSubLogService;
import com.damon.tcc.sub_log.TccSubLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiFunction;

public class TccNestSubLogTryHandler<R, PD, P extends SubBizId> {
    private final Logger log = LoggerFactory.getLogger(TccNestSubLogTryHandler.class);
    private final ITccSubLogService tccSubLogService;
    private final BiFunction<P, PD, R> localTransactionFunction;
    private final String bizType;

    public TccNestSubLogTryHandler(ITccSubLogService tccSubLogService, BiFunction<P, PD, R> localTransactionFunction, String bizType) {
        this.tccSubLogService = tccSubLogService;
        this.localTransactionFunction = localTransactionFunction;
        this.bizType = bizType;
    }

    public R execute(P parameter, PD pd) {
        TccSubLog tccSubLog = new TccSubLog(parameter.getBizId(), parameter.getSubBizId());
        try {
            tccSubLogService.create(tccSubLog);
            return localTransactionFunction.apply(parameter, pd);
        } catch (Exception e) {
            log.error("子事务业务类型: {}, 业务id : {}, try失败", bizType, parameter.getBizId(), e);
            throw e;
        }
    }
}
