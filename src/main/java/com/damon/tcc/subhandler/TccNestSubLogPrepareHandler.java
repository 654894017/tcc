package com.damon.tcc.subhandler;

import com.damon.tcc.annotation.SubBizId;
import com.damon.tcc.exception.TccPrepareException;
import com.damon.tcc.sublog.ITccSubLogService;
import com.damon.tcc.sublog.TccSubLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiFunction;

/**
 * 嵌套子事务try阶段处理器
 *
 * @param <R>  try本地事务返回结果数据
 * @param <PD> try阶段处理结果数据
 * @param <P>  请求参数
 * @author xianpinglu
 */
public class TccNestSubLogPrepareHandler<R, PD, P extends SubBizId> {
    private final Logger log = LoggerFactory.getLogger(TccNestSubLogPrepareHandler.class);
    private final ITccSubLogService tccSubLogService;
    private final BiFunction<P, PD, R> localTransactionFunction;
    private final String bizType;

    public TccNestSubLogPrepareHandler(ITccSubLogService tccSubLogService, BiFunction<P, PD, R> localTransactionFunction, String bizType) {
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
            log.error("Sub-transaction Business Type: {}, Business ID: {}, Sub-Business ID: {}, try failed",
                    bizType, parameter.getBizId(), parameter.getSubBizId(), e);
            throw new TccPrepareException(e);
        }
    }
}
