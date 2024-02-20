package com.damon.sample.points.app;

import com.damon.sample.points.client.IPointsDeductionAppService;
import com.damon.sample.points.client.PointsDeductCmd;
import com.damon.tcc.TccSubConfig;
import com.damon.tcc.TccSubService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class PointsDeductionAppService extends TccSubService<Boolean, PointsDeductCmd> implements IPointsDeductionAppService {
    private final Logger log = LoggerFactory.getLogger(PointsDeductionAppService.class);
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public PointsDeductionAppService(TccSubConfig config) {
        super(config);
        this.jdbcTemplate = new JdbcTemplate(config.getDataSource());
    }
    @Override
    public boolean attempt(PointsDeductCmd parameter) {
        return super.attempt(parameter, cmd -> {
            int result = jdbcTemplate.update("update tcc_demo_user_points set points = points - ? where user_id = ? and points - ? >= 0",
                    cmd.getDeductionPoints(), cmd.getUserId(), cmd.getDeductionPoints());
            boolean transactionActive = TransactionSynchronizationManager.isActualTransactionActive();

            if (result == 0) {
                throw new RuntimeException("用户积分不足 || 用户不存在");
            }

            int result2 = jdbcTemplate.update("insert tcc_demo_points_changing_Log (user_id, change_points, change_type, biz_id, status) values(?,?,?,?,?)",
                    cmd.getUserId(), cmd.getDeductionPoints(), 1, cmd.getOrderId(), 0);

            return true;
        });
    }

    @Override
    public void commit(PointsDeductCmd parameter) {
        super.commit(parameter, cmd -> {
            int result = jdbcTemplate.update("update tcc_demo_points_changing_Log set status = 1 where biz_id = ?", cmd.getBizId());
            if (result == 0) {
                throw new RuntimeException("无效的业务id，无法积分commit");
            }
        });
    }

    @Override
    public void cancel(PointsDeductCmd parameter) {
        super.cancel(parameter, cmd -> {
            int result = jdbcTemplate.update("update tcc_demo_points_changing_Log set status = 2 where biz_id = ?", cmd.getBizId());
            if (result == 0) {
                log.error("无效的业务id : {}，无法进行积分cancel", cmd.getBizId());
                return;
            }

            int result2 = jdbcTemplate.update("update tcc_demo_user_points set points = points + ? where user_id = ?",
                     cmd.getDeductionPoints(), cmd.getUserId()
            );
            if (result2 == 0) {
                throw new RuntimeException("无效的用户id，无法进行积分rollback");
            }
        });
    }


}
