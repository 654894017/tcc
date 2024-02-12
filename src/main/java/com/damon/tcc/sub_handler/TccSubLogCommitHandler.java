package com.damon.tcc.sub_handler;

import com.damon.tcc.BizId;
import com.damon.tcc.exception.TccSubLogInvalidException;
import com.damon.tcc.sub_log.ITccSubLogService;
import com.damon.tcc.sub_log.TccSubLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class TccSubLogCommitHandler<P extends BizId> {
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
            TccSubLog tccSubLog = tccSubLogService.get(parameter.getBizId());
            if (tccSubLog == null) {
                String errorMessage = "找不到对应的业务类型:%s, 业务id: %s, 关联的子事务日志 ";
                throw new TccSubLogInvalidException(String.format(errorMessage, bizType, parameter.getBizId()));
            }
            if (tccSubLog.isCommited()) {
                return;
            }
            tccSubLog.commit();
            tccSubLogService.update(tccSubLog);
            commitPhaseConsumer.accept(parameter);
        } catch (Exception e) {
            log.error("子事务业务类型: {}, 业务id : {}, commit失败", bizType, parameter.getBizId(), e);
            throw e;
        }
    }
}
