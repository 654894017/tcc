package com.damon.tcc.main_log.event;

import com.damon.tcc.event_source.Event;

public class TccMainLogCheckedEvent implements Event {
    private Integer version;
    private int checkedTimes;
    private Long lastUpdateTime;

    public TccMainLogCheckedEvent(int version, int checkedTimes, Long lastUpdateTime) {
        this.version = version;
        this.checkedTimes = checkedTimes;
        this.lastUpdateTime = lastUpdateTime;
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
}
