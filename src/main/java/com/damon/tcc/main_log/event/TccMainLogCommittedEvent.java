package com.damon.tcc.main_log.event;

import com.damon.tcc.event_source.Event;

public class TccMainLogCommittedEvent implements Event {
    private Integer status;
    private Integer version;
    private Long lastUpdateTime;

    public TccMainLogCommittedEvent(Integer status, int version, Long lastUpdateTime) {
        this.status = status;
        this.version = version;
        this.lastUpdateTime = lastUpdateTime;
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

    public Long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
