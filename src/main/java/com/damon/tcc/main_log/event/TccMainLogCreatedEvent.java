package com.damon.tcc.main_log.event;

import com.damon.tcc.event_source.Event;

public class TccMainLogCreatedEvent implements Event {
    private Long bizId;
    private Integer status;
    private Integer version;
    private int checkedTimes;
    private Long lastUpdateTime;
    private Long createTime;

    public TccMainLogCreatedEvent(Long bizId, Integer status, Integer version, int checkedTimes, Long lastUpdateTime, Long createTime) {
        this.bizId = bizId;
        this.status = status;
        this.version = version;
        this.checkedTimes = checkedTimes;
        this.lastUpdateTime = lastUpdateTime;
        this.createTime = createTime;
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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public int getCheckedTimes() {
        return checkedTimes;
    }

    public void setCheckedTimes(int checkedTimes) {
        this.checkedTimes = checkedTimes;
    }

    public Long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}
