package com.damon.sample.order.domain;

public interface IPointsGateway {
    boolean tryDeductionPoints(Long orderId, Long userId, Long deductionPoints);

    boolean commitDeductionPoints(Long orderId, Long userId, Long deductionPoints);

    boolean cancelDeductionPoints(Long orderId, Long userId, Long deductionPoints);
}
