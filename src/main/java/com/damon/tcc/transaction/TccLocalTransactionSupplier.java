package com.damon.tcc.transaction;

import com.damon.tcc.BizId;
import com.damon.tcc.main_log.ITccMainLogService;
import com.damon.tcc.main_log.TccMainLog;

import java.util.function.Function;
import java.util.function.Supplier;

public class TccLocalTransactionSupplier<R, O extends BizId> implements Supplier<R> {
    private final ITccMainLogService tccLogService;
    private final TccMainLog tccMainLog;
    private final Function<O, R> localTransactionPhaseFunction;
    private final O parameter;

    public TccLocalTransactionSupplier(ITccMainLogService tccLogService, TccMainLog tccMainLog, Function<O, R> localTransactionPhaseFunction, O parameter) {
        this.tccMainLog = tccMainLog;
        this.tccLogService = tccLogService;
        this.localTransactionPhaseFunction = localTransactionPhaseFunction;
        this.parameter = parameter;
    }

    @Override
    public R get() {
        tccMainLog.commitLocal();
        tccLogService.update(tccMainLog);
        return localTransactionPhaseFunction.apply(parameter);
    }
}
