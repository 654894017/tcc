package com.damon.tcc;

import com.damon.tcc.log.ITccLogService;
import com.damon.tcc.log.TccLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class TccLogAsyncCommitRunnable<O extends BizId> implements Runnable {
    private final Logger log = LoggerFactory.getLogger(TccLogAsyncCommitRunnable.class);
    private final ITccLogService tccLogService;
    private final TccLog tccLog;
    private final String bizType;
    private final Consumer<O> commitPhaseConsumer;
    private final O object;

    public TccLogAsyncCommitRunnable(ITccLogService tccLogService,
                                     TccLog tccLog,
                                     String bizType,
                                     Consumer<O> commitPhaseConsumer,
                                     O object) {
        this.tccLogService = tccLogService;
        this.tccLog = tccLog;
        this.bizType = bizType;
        this.commitPhaseConsumer = commitPhaseConsumer;
        this.object = object;
    }

    @Override
    public void run() {
        try {
            commitPhaseConsumer.accept(object);
            tccLogService.commit(tccLog);
            log.info("业务类型: {}, 业务id : {}, 异步commit成功", bizType, object.getBizId());
        } catch (Exception e) {
            log.error("业务类型: {}, 业务id : {}, 异步commit失败", bizType, object.getBizId(), e);
        }
    }
}
