package com.damon.sample.points.client;

import com.damon.tcc.annotation.SubBizId;

public class PointsDeductCmd implements SubBizId {

    private Long orderId;

    private Long userId;

    private Long deductionPoints;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDeductionPoints() {
        return deductionPoints;
    }

    public void setDeductionPoints(Long deductionPoints) {
        this.deductionPoints = deductionPoints;
    }

    @Override
    public Long getBizId() {
        return orderId;
    }

    @Override
    public Long getSubBizId() {
        return userId;
    }
}
