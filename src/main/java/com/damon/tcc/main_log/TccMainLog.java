package com.damon.tcc.main_log;


import com.damon.tcc.event_source.Source;
import com.damon.tcc.main_log.event.*;

public class TccMainLog extends Source {
    private Long bizId;
    private Integer status;
    private int version;
    private int checkedTimes;
    private Long lastUpdateTime;
    private Long createTime;

    public TccMainLog(Long bizId) {
        Long creatTime = System.currentTimeMillis();
        this.applyNewEvent(new TccMainLogCreatedEvent(
                bizId, TccMainLogStatusEnum.CREATED.getStatus(), 0, 0, creatTime, creatTime)
        );
    }

    public TccMainLog() {
    }

    public void rollback() {
        applyNewEvent(new TccMainLogCommittedEvent(TccMainLogStatusEnum.ROOBACKED.getStatus(), version + 1, System.currentTimeMillis()));
    }

    public void commit() {
        applyNewEvent(new TccMainLogCommittedEvent(
                TccMainLogStatusEnum.COMMITED.getStatus(), version + 1, System.currentTimeMillis()
        ));
    }

    public void commitLocal() {
        applyNewEvent(new TccMainLogCommittedEvent(
                TccMainLogStatusEnum.LOCAL_COMMITED.getStatus(), version + 1, System.currentTimeMillis()
        ));
    }

    public void check() {
        applyNewEvent(new TccMainLogCheckedEvent(
                version + 1, checkedTimes + 1, System.currentTimeMillis()
        ));
    }

    private void apply(TccMainLogCheckedEvent event) {
        this.lastUpdateTime = event.getLastUpdateTime();
        this.checkedTimes = event.getCheckedTimes();
        this.version = event.getVersion();
    }

    private void apply(TccMainLogCreatedEvent event) {
        this.bizId = event.getBizId();
        this.status = event.getStatus();
        this.version = event.getVersion();
        this.checkedTimes = event.getCheckedTimes();
        this.createTime = event.getCreateTime();
        this.lastUpdateTime = event.getLastUpdateTime();
    }

    private void apply(TccMainLogLocalCommittedEvent event) {
        this.status = event.getStatus();
        this.lastUpdateTime = event.getLastUpdateTime();
        this.version = event.getVersion();
    }

    private void apply(TccMainLogCommittedEvent event) {
        this.status = event.getStatus();
        this.lastUpdateTime = event.getLastUpdateTime();
        this.version = event.getVersion();
    }

    private void apply(TccMainLogRollbackedEvent event) {
        this.status = event.getStatus();
        this.lastUpdateTime = event.getLastUpdateTime();
        this.version = event.getVersion();
    }

    public boolean isCreated() {
        return this.status == TccMainLogStatusEnum.CREATED.getStatus();
    }

    public boolean isCommited() {
        return this.status == TccMainLogStatusEnum.COMMITED.getStatus();
    }

    public boolean isLocalCommited() {
        return this.status == TccMainLogStatusEnum.LOCAL_COMMITED.getStatus();
    }

    public boolean isRollbacked() {
        return this.status == TccMainLogStatusEnum.ROOBACKED.getStatus();
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

    public void setVersion(int version) {
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

    @Override
    public String toString() {
        return "TccLog{" +
                "bizId=" + bizId +
                ", status=" + status +
                ", version=" + version +
                ", checkedTimes=" + checkedTimes +
                ", lastUpdateTime=" + lastUpdateTime +
                ", createTime=" + createTime +
                '}';
    }

}
