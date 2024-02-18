package com.damon.sample.points;

import com.damon.sample.order.Order;
import com.damon.tcc.TccSubConfig;
import com.damon.tcc.TccSubService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class PointsDeductionService extends TccSubService<Boolean, Order> {
    private final Logger log = LoggerFactory.getLogger(PointsDeductionService.class);
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PointsDeductionService(TccSubConfig config) {
        super(config);
        this.jdbcTemplate = new JdbcTemplate(config.getDataSource());
    }

    public Boolean executeTry(Order parameter) {
        return super.processTry(parameter);
    }

    public void executeCommit(Order parameter) {
        super.processCommit(parameter);
    }

    public void executeCancel(Order parameter) {
        super.processCancel(parameter);
    }

    @Override
    protected Boolean tryPhase(Order parameter) {
        int result = jdbcTemplate.update("update tcc_demo_user_points set points = points - ? where user_id = ? and points - ? >= 0",
                parameter.getDeductionPoints(), parameter.getUserId(), parameter.getDeductionPoints());
        boolean transactionActive = TransactionSynchronizationManager.isActualTransactionActive();

        if (result == 0) {
            throw new RuntimeException("用户积分不足 || 用户不存在");
        }

        int result2 = jdbcTemplate.update("insert tcc_demo_points_changing_Log (user_id, change_points, change_type, biz_id, status) values(?,?,?,?,?)",
                parameter.getUserId(), parameter.getDeductionPoints(), 1, parameter.getOrderId(), 0);

        return true;
    }

    @Override
    protected void commitPhase(Order parameter) {
        int result = jdbcTemplate.update("update tcc_demo_points_changing_Log set status = 1 where biz_id = ?", parameter.getBizId());
        if (result == 0) {
            throw new RuntimeException("无效的业务id，无法积分commit");
        }
    }

    @Override
    protected void cancelPhase(Order parameter) {
        int result = jdbcTemplate.update("update tcc_demo_points_changing_Log set status = 2 where biz_id = ?", parameter.getBizId());
        if (result == 0) {
            log.error("无效的业务id : {}，无法进行积分cancel", parameter.getBizId());
            return;
        }

        int result2 = jdbcTemplate.update("update tcc_demo_points set points = points + ? where user_id = ?",
                parameter.getUserId(), parameter.getDeductionPoints()
        );
        if (result2 == 0) {
            throw new RuntimeException("无效的用户id，无法进行积分rollback");
        }
    }

}
