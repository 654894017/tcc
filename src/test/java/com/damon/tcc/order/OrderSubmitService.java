package com.damon.tcc.order;

import cn.hutool.core.util.IdUtil;
import com.damon.tcc.TccMainConfig;
import com.damon.tcc.TccMainTemplateService;
import com.damon.tcc.points.PointsDeductionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderSubmitService extends TccMainTemplateService<Long, Order> {
    private final JdbcTemplate jdbcTemplate;
    private final PointsDeductionService pointsDeductionService;

    @Autowired
    public OrderSubmitService(TccMainConfig config,
                              PointsDeductionService pointsDeductionService) {
        super(config);
        this.jdbcTemplate = new JdbcTemplate(config.getDataSource());
        this.pointsDeductionService = pointsDeductionService;
    }

    public void executeFailedLogCheck() {
        super.executeFailedLogCheck();
    }

    public void executeDeadLogCheck() {
        super.executeDeadLogCheck();
    }

    public Long execute(Long userId, Long points) {
        Long orderId = IdUtil.getSnowflakeNextId();
        jdbcTemplate.update("insert into tcc_demo_order(order_id, user_id, status, deduction_points) values (?, ?, ? ,? )", orderId, userId, 0, points);
        Order order = new Order(orderId, 0, userId, points);
        return super.process(order);
    }

    @Override
    protected Order callbackParameter(Long bizId) {
        Order order = jdbcTemplate.queryForObject("select * from tcc_demo_order where order_id = ? ", new BeanPropertyRowMapper<>(Order.class), bizId);
        return order;
    }

    @Override
    protected void tryPhase(Order order) {
        pointsDeductionService.executeTry(order);
    }

    @Override
    protected Long executeLocalTransactionPhase(Order object) {
        int result = jdbcTemplate.update("update tcc_demo_order set status = ?  where order_id = ? ", 1, object.getOrderId());
        if (result == 0) {
            throw new RuntimeException("无效的订单id : " + object.getOrderId());
        }
        return object.getOrderId();
    }

    @Override
    protected void commitPhase(Order object) {
        pointsDeductionService.executeCommit(object);
    }

    @Override
    protected void cancelPhase(Order object) {
        pointsDeductionService.executeCancel(object);
    }
}
