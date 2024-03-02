package com.damon.tcc.sub_handler;

import com.damon.tcc.annotation.SubBizId;
import com.damon.tcc.exception.TccCancelException;
import com.damon.tcc.sub_log.ITccSubLogService;
import com.damon.tcc.sub_log.TccSubLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class TccSubLogCancelHandler<P extends SubBizId> {
    private final Logger log = LoggerFactory.getLogger(TccSubLogCancelHandler.class);
    private final ITccSubLogService tccSubLogService;
    private final Consumer<P> cancelPhaseConsumer;
    private final String bizType;

    public TccSubLogCancelHandler(ITccSubLogService tccSubLogService, Consumer<P> cancelPhaseConsumer, String bizType) {
        this.tccSubLogService = tccSubLogService;
        this.cancelPhaseConsumer = cancelPhaseConsumer;
        this.bizType = bizType;
    }

    public void execute(P parameter) {
        try {
            TccSubLog tccSubLog = tccSubLogService.get(parameter.getBizId(), parameter.getSubBizId());
            if (tccSubLog == null) {
                tccSubLog = new TccSubLog(parameter.getBizId(), parameter.getSubBizId());
                tccSubLog.cancel();
                tccSubLogService.create(tccSubLog);
                log.warn("找不到对应的业务类型: {}, 业务id: {}, 关联的子事务日志信息，无法进行cancel操作", bizType, parameter.getBizId());
                return;
            }
            if (tccSubLog.isCommited() || tccSubLog.isCanceled()) {
                log.info("业务类型: {}, 业务id: {}, 关联的子事务日志已完成Commit或Cancel处理，不再继续执行 ", bizType, parameter.getBizId());
                return;
            }
            tccSubLog.cancel();
            tccSubLogService.update(tccSubLog);
            cancelPhaseConsumer.accept(parameter);
        } catch (Exception e) {
            log.error("子事务业务类型: {}, 业务id : {}, cancel失败", bizType, parameter.getBizId(), e);
            throw new TccCancelException(e);
        }
    }
}
