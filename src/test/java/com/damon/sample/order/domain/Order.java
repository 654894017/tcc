package com.damon.sample.order.domain;

import com.damon.tcc.annotation.BizId;

public class Order implements BizId {

    private Long orderId;
    /**
     * 0 预创建 1 已创建 2 等待支付 3 已完成 4 已关闭
     */
    private Integer status;

    private Long userId;

    private Long deductionPoints;

    public Order(Long orderId, Integer status, Long userId, Long deductionPoints) {
        this.orderId = orderId;
        this.status = status;
        this.userId = userId;
        this.deductionPoints = deductionPoints;
    }

    public Order() {
    }

    @Override
    public Long getBizId() {
        return orderId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
}
