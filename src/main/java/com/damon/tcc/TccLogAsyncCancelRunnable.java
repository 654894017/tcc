package com.damon.tcc;

import com.damon.tcc.log.ITccLogService;
import com.damon.tcc.log.TccLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class TccLogAsyncCancelRunnable<O extends BizId> implements Runnable {
    private final Logger log = LoggerFactory.getLogger(TccLogAsyncCancelRunnable.class);
    private final ITccLogService tccLogService;
    private final TccLog tccLog;
    private final String bizType;
    private final Consumer<O> cancelPhaseConsumer;
    private final O object;
    public TccLogAsyncCancelRunnable(ITccLogService tccLogService,
                                     TccLog tccLog,
                                     String bizType,
                                     Consumer<O> cancelPhaseConsumer,
                                     O object) {
        this.tccLogService = tccLogService;
        this.tccLog = tccLog;
        this.bizType = bizType;
        this.cancelPhaseConsumer = cancelPhaseConsumer;
        this.object = object;
    }

    @Override
    public void run() {
        try {
            cancelPhaseConsumer.accept(object);
            tccLogService.rollback(tccLog);
            log.info("业务类型: {}, 业务id : {}, 异步cancel成功", bizType, object.getBizId());
        } catch (Exception e) {
            log.error("业务类型: {}, 业务id : {}, 异步cancel失败", bizType, object.getBizId(), e);
        }
    }
}
