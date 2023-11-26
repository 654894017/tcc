package com.damon.tcc.transaction;

import com.damon.tcc.BizId;
import com.damon.tcc.log.ITccLogService;
import com.damon.tcc.log.TccLog;

import java.util.function.Function;
import java.util.function.Supplier;

public class TccLocalTransactionSupplier<R, O extends BizId> implements Supplier<R> {
    private final ITccLogService tccLogService;
    private final TccLog tccLog;
    private final Function<O, R> localTransactionPhaseFunction;
    private final O object;

    public TccLocalTransactionSupplier(ITccLogService tccLogService, TccLog tccLog, O object, Function<O, R> localTransactionPhaseFunction) {
        this.tccLog = tccLog;
        this.tccLogService = tccLogService;
        this.localTransactionPhaseFunction = localTransactionPhaseFunction;
        this.object = object;
    }

    @Override
    public R get() {
        tccLogService.commitLocal(tccLog);
        return localTransactionPhaseFunction.apply(object);
    }
}
