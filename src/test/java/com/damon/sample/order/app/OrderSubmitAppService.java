package com.damon.sample.order.app;

import cn.hutool.core.util.IdUtil;
import com.damon.sample.order.client.IOrderSubmitAppService;
import com.damon.sample.order.domain.IPointsGateway;
import com.damon.sample.order.domain.Order;
import com.damon.tcc.TccMainConfig;
import com.damon.tcc.TccMainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderSubmitAppService extends TccMainService<Long, Order> implements IOrderSubmitAppService {
    private final JdbcTemplate jdbcTemplate;
    private final IPointsGateway pointsGateway;

    @Autowired
    public OrderSubmitAppService(TccMainConfig config,
                                 IPointsGateway pointsGateway) {
        super(config);
        this.jdbcTemplate = new JdbcTemplate(config.getDataSource());
        this.pointsGateway = pointsGateway;
    }

    public void executeFailedLogCheck() {
        super.executeFailedLogCheck();
    }

    public void executeDeadLogCheck() {
        super.executeDeadLogCheck();
    }

    @Override
    protected Order callbackParameter(Long bizId) {
        return jdbcTemplate.queryForObject("select * from tcc_demo_order where order_id = ? ", new BeanPropertyRowMapper<>(Order.class), bizId);
    }

    @Override
    public Long submitOrder(Long userId, Long points) {
        Long orderId = IdUtil.getSnowflakeNextId();
        jdbcTemplate.update("insert into tcc_demo_order(order_id, user_id, status, deduction_points) values (?, ?, ? ,? )", orderId, userId, 0, points);
        Order order = new Order(orderId, 0, userId, points);
        return super.process(order);
    }
    @Override
    protected void attempt(Order order) {
        pointsGateway.tryDeductionPoints(order.getOrderId(), order.getUserId(), order.getDeductionPoints());
    }
    @Override
    protected Long executeLocalTransaction(Order object) {
        int result = jdbcTemplate.update("update tcc_demo_order set status = ?  where order_id = ? ", 1, object.getOrderId());
        if (result == 0) {
            throw new RuntimeException("无效的订单id : " + object.getOrderId());
        }
        return object.getOrderId();
    }
    @Override
    protected void commit(Order order) {
        pointsGateway.commitDeductionPoints(order.getOrderId(), order.getUserId(), order.getDeductionPoints());
    }
    @Override
    protected void cancel(Order order) {
        pointsGateway.cancelDeductionPoints(order.getOrderId(), order.getUserId(), order.getDeductionPoints());
    }
}
