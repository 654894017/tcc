package com.damon.sample.order.app;

import cn.hutool.core.util.IdUtil;
import com.damon.sample.order.client.IOrderSubmitAppService;
import com.damon.sample.order.domain.IPointsGateway;
import com.damon.sample.order.domain.Order;
import com.damon.tcc.TccMainService;
import com.damon.tcc.config.TccMainConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class OrderSubmitAppService extends TccMainService<Long, Map<String, Boolean>, Order> implements IOrderSubmitAppService {
    private final JdbcTemplate jdbcTemplate;
    private final IPointsGateway pointsGateway;

    @Autowired
    public OrderSubmitAppService(TccMainConfig config, IPointsGateway pointsGateway) {
        super(config);
        this.jdbcTemplate = new JdbcTemplate(config.getDataSource());
        this.pointsGateway = pointsGateway;
    }

    /**
     * 检查失败的日志，用于纠正事务是否需要回顾还是提交
     */
    public void executeFailedLogCheck() {
        super.executeFailedLogCheck();
    }

    /**
     * 检查死亡的日志，用于纠正事务是否需要回顾还是提交
     */
    public void executeDeadLogCheck() {
        super.executeDeadLogCheck();
    }

    /**
     * 执行失败日志检查的时候需要回查请求参数（因为事务日志未记录方法请求参数，所以需要回查一下）
     *
     * @param bizId 实体对象id（业务id）
     * @return
     */
    @Override
    protected Order callbackParameter(Long bizId) {
        return jdbcTemplate.queryForObject("select * from tcc_demo_order where order_id = ? ", new BeanPropertyRowMapper<>(Order.class), bizId);
    }

    /**
     * 创建订单 （1 预先创建订单  2 执行try动作）
     *
     * @param userId
     * @param points
     * @return
     */
    @Override
    public Long submitOrder(Long userId, Long points) {
        Long orderId = IdUtil.getSnowflakeNextId();
        jdbcTemplate.update("insert into tcc_demo_order(order_id, user_id, status, deduction_points) values (?, ?, ? ,? )", orderId, userId, 0, points);
        Order order = new Order(orderId, 0, userId, points);
        try {
            return super.process(order);
        } catch (RuntimeException e) {
            // try  localTransaction  commit  cancel 错误可以通过自定义异常抛出
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("系统异常");
        }
    }

    /**
     * try执行用户积分扣除
     *
     * @param order
     * @return
     */
    @Override
    protected Map<String, Boolean> attempt(Order order) {
        Boolean result = pointsGateway.tryDeductionPoints(order.getOrderId(), order.getUserId(), order.getDeductionPoints());
        Map<String, Boolean> map = new HashMap<>();
        map.put("flag", result);
        return map;
    }

    @Override
    protected Long executeLocalTransaction(Order object, Map<String, Boolean> map) {
        int result = jdbcTemplate.update("update tcc_demo_order set status = ?  where order_id = ? ", 1, object.getOrderId());
        if (result == 0) {
            throw new RuntimeException("无效的订单id : " + object.getOrderId());
        }
        return object.getOrderId();
    }

    /**
     * commit积分
     *
     * @param order
     */
    @Override
    protected void commit(Order order) {
        pointsGateway.commitDeductionPoints(order.getOrderId(), order.getUserId(), order.getDeductionPoints());
    }

    /**
     * cancel回滚积分
     *
     * @param order
     */
    @Override
    protected void cancel(Order order) {
        pointsGateway.cancelDeductionPoints(order.getOrderId(), order.getUserId(), order.getDeductionPoints());
    }
}
