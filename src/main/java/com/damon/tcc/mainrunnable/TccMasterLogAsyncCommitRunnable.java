package com.damon.tcc.mainrunnable;

import com.damon.tcc.annotation.BizId;
import com.damon.tcc.exception.TccCommitException;
import com.damon.tcc.mainlog.ITccMainLogService;
import com.damon.tcc.mainlog.TccMainLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class TccMasterLogAsyncCommitRunnable<O extends BizId> implements Runnable {
    private final Logger log = LoggerFactory.getLogger(TccMasterLogAsyncCommitRunnable.class);
    private final ITccMainLogService tccLogService;
    private final TccMainLog tccMainLog;
    private final String bizType;
    private final Consumer<O> commitPhaseConsumer;
    private final O object;

    public TccMasterLogAsyncCommitRunnable(
            ITccMainLogService tccLogService,
            TccMainLog tccMainLog,
            String bizType,
            Consumer<O> commitPhaseConsumer,
            O object
    ) {
        this.tccLogService = tccLogService;
        this.tccMainLog = tccMainLog;
        this.bizType = bizType;
        this.commitPhaseConsumer = commitPhaseConsumer;
        this.object = object;
    }

    @Override
    public void run() {
        try {
            tccMainLog.commit();
            commitPhaseConsumer.accept(object);
            tccLogService.update(tccMainLog);
            log.info("Business Type: {}, Business ID: {}, Asynchronous commit successful", bizType, object.getBizId());
        } catch (Exception e) {
            log.error("Business Type: {}, Business ID: {}, Asynchronous commit failed", bizType, object.getBizId(), e);
            throw new TccCommitException(e);
        }
    }
}
