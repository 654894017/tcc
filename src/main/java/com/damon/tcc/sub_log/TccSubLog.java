package com.damon.tcc.sub_log;


public class TccSubLog {
    private Long bizId;
    private Integer status;
    private int version;

    private Long lastUpdateTime;
    private Long createTime;

    public TccSubLog(Long bizId) {
        this.bizId = bizId;
        this.status = TccSubLogStatusEnum.TRY.getStatus();
        this.version = 0;
        this.createTime = System.currentTimeMillis();
        this.lastUpdateTime = createTime;
    }

    public TccSubLog() {
    }

    public void commit() {
        this.status = TccSubLogStatusEnum.COMMITTED.getStatus();
        this.version = this.version + 1;
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public void cancel() {
        this.status = TccSubLogStatusEnum.CANCELED.getStatus();
        this.version = this.version + 1;
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public Boolean isCreated() {
        return this.status == TccSubLogStatusEnum.TRY.getStatus();
    }

    public Boolean isCommited() {
        return this.status == TccSubLogStatusEnum.COMMITTED.getStatus();
    }

    public Boolean isCanceled() {
        return this.status == TccSubLogStatusEnum.CANCELED.getStatus();
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

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
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
