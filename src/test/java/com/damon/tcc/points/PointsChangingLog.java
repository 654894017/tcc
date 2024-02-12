package com.damon.tcc.points;

public class PointsChangingLog {
    private Long userId;
    private Long changePoints;
    /**
     * 0 增加  1 扣减
     */
    private Integer changeType;
    private Long bizId;
    /**
     * 0 进行中   1 成功   2 失败
     */
    private Integer status;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getChangePoints() {
        return changePoints;
    }

    public void setChangePoints(Long changePoints) {
        this.changePoints = changePoints;
    }

    public Integer getChangeType() {
        return changeType;
    }

    public void setChangeType(Integer changeType) {
        this.changeType = changeType;
    }

    public Long getBizId() {
        return bizId;
    }

    public void setBizId(Long bizId) {
        this.bizId = bizId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
