package com.damon.tcc.sub_handler;

import com.damon.tcc.BizId;
import com.damon.tcc.exception.OptimisticLockException;
import com.damon.tcc.exception.TccSubLogInvalidException;
import com.damon.tcc.main_runnable.TccMasterLogAsyncCancelRunnable;
import com.damon.tcc.sub_log.ITccSubLogService;
import com.damon.tcc.sub_log.TccSubLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class TccSubLogCancelHandler<P extends BizId> {
    private final Logger log = LoggerFactory.getLogger(TccMasterLogAsyncCancelRunnable.class);
    private final ITccSubLogService tccSubLogService;
    private final Consumer<P> cancelPhaseConsumer;
    private final String bizType;

    public TccSubLogCancelHandler(ITccSubLogService tccSubLogService, Consumer<P> cancelPhaseConsumer, String bizType) {
        this.tccSubLogService = tccSubLogService;
        this.cancelPhaseConsumer = cancelPhaseConsumer;
        this.bizType = bizType;
    }

    public void execute(P parameter) {
        TccSubLog tccSubLog = tccSubLogService.get(parameter.getBizId());
        if (tccSubLog == null) {
            String errorMessage = "找不到对应的业务类型:%s, 业务id: %s, 关联的子事务日志 ";
            throw new TccSubLogInvalidException(String.format(errorMessage, bizType, parameter.getBizId()));
        }
        if (tccSubLog.isCommited() || tccSubLog.isCanceled()) {
            return;
        }

        tccSubLog.cancel();
        try {
            tccSubLogService.update(tccSubLog);
            cancelPhaseConsumer.accept(parameter);
        } catch (OptimisticLockException e) {
            log.error("子事务业务类型: {}, 业务id : {}, cancel失败", bizType, parameter.getBizId(), e);
            throw e;
        }
    }
}
