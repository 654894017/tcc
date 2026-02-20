package com.damon.sample.order.application;

import com.damon.sample.order.application.executer.OrderSubmitExecuter;
import com.damon.sample.order.client.IOrderSubmitAppService;
import org.springframework.stereotype.Service;

@Service
public class OrderApplicationService implements IOrderSubmitAppService {

    private final OrderSubmitExecuter orderSubmitExecuter;

    public OrderApplicationService(OrderSubmitExecuter orderSubmitExecuter) {
        this.orderSubmitExecuter = orderSubmitExecuter;
    }

    @Override
    public Long submitOrder(Long userId, Long points) {
        return orderSubmitExecuter.execute(userId, points);
    }
}
