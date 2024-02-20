package com.damon.tcc.transaction;

import com.damon.tcc.BizId;
import com.damon.tcc.main_log.ITccMainLogService;
import com.damon.tcc.main_log.TccMainLog;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class TccLocalTransactionSupplier<R, PD, O extends BizId> implements Supplier<R> {
    private final ITccMainLogService tccLogService;
    private final TccMainLog tccMainLog;
    private final BiFunction<O, PD, R> localTransactionPhaseFunction;
    private final O parameter;
    private final PD processData;

    public TccLocalTransactionSupplier(ITccMainLogService tccLogService, TccMainLog tccMainLog, BiFunction<O, PD, R> localTransactionPhaseFunction, O parameter, PD processData) {
        this.tccMainLog = tccMainLog;
        this.tccLogService = tccLogService;
        this.localTransactionPhaseFunction = localTransactionPhaseFunction;
        this.parameter = parameter;
        this.processData = processData;
    }

    @Override
    public R get() {
        tccMainLog.commitLocal();
        tccLogService.update(tccMainLog);
        return localTransactionPhaseFunction.apply(parameter, processData);
    }
}
