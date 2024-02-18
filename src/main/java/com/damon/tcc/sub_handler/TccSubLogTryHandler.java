package com.damon.tcc.sub_handler;

import com.damon.tcc.BizId;
import com.damon.tcc.exception.TccTryException;
import com.damon.tcc.sub_log.ITccSubLogService;
import com.damon.tcc.sub_log.TccSubLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class TccSubLogTryHandler<R, P extends BizId> {
    private final Logger log = LoggerFactory.getLogger(TccSubLogTryHandler.class);
    private final ITccSubLogService tccSubLogService;
    private final Function<P, R> tryPhaseFunction;
    private final String bizType;

    public TccSubLogTryHandler(ITccSubLogService tccSubLogService, Function<P, R> tryPhaseFunction, String bizType) {
        this.tccSubLogService = tccSubLogService;
        this.tryPhaseFunction = tryPhaseFunction;
        this.bizType = bizType;
    }

    public R execute(P parameter) {
        TccSubLog tccSubLog = new TccSubLog(parameter.getBizId());
        try {
            tccSubLogService.create(tccSubLog);
            return tryPhaseFunction.apply(parameter);
        } catch (Exception e) {
            log.error("子事务业务类型: {}, 业务id : {}, try失败", bizType, parameter.getBizId(), e);
            throw new TccTryException(e);
        }
    }
}
