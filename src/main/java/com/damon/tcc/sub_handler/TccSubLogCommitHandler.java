package com.damon.tcc.sub_handler;

import com.damon.tcc.annotation.SubBizId;
import com.damon.tcc.exception.TccCommitException;
import com.damon.tcc.sub_log.ITccSubLogService;
import com.damon.tcc.sub_log.TccSubLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class TccSubLogCommitHandler<P extends SubBizId> {
    private final Logger log = LoggerFactory.getLogger(TccSubLogCommitHandler.class);
    private final ITccSubLogService tccSubLogService;
    private final Consumer<P> commitPhaseConsumer;
    private final String bizType;

    public TccSubLogCommitHandler(ITccSubLogService tccSubLogService, Consumer<P> commitPhaseConsumer, String bizType) {
        this.tccSubLogService = tccSubLogService;
        this.commitPhaseConsumer = commitPhaseConsumer;
        this.bizType = bizType;
    }

    public void execute(P parameter) {
        try {
            TccSubLog tccSubLog = tccSubLogService.get(parameter.getBizId(), parameter.getSubBizId());
            if (tccSubLog == null) {
                log.warn("找不到对应的业务类型: {}, 业务id: {}, 关联的子事务日志，无法进行commit操作", bizType, parameter.getBizId());
                return;
            }
            if (tccSubLog.isCommited() || tccSubLog.isCanceled()) {
                log.info("业务类型: {}, 业务id: {}, 关联的子事务日志已完成Commit或Cancel处理，不再继续执行 ", bizType, parameter.getBizId());
                return;
            }
            tccSubLog.commit();
            tccSubLogService.update(tccSubLog);
            commitPhaseConsumer.accept(parameter);
        } catch (Exception e) {
            log.error("子事务业务类型: {}, 业务id : {}, commit失败", bizType, parameter.getBizId(), e);
            throw new TccCommitException(e);
        }
    }
}
