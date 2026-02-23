package com.damon.sample.order.client;

public interface IOrderApplicationService {
    Long submitOrder(Long userId, Long points);

    void executeFailedLogCheck();

    void executeDeadLogCheck();
}
