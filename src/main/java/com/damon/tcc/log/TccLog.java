package com.damon.tcc.log;


public class TccLog {
    private Long bizId;
    private Integer status;
    private int version;
    private int checkedTimes;
    private Long lastUpdateTime;
    private Long createTime;

    public TccLog(Long bizId) {
        this.bizId = bizId;
        this.status = TccLogStatusEnum.CREATED.getStatus();
        this.version = 0;
        this.checkedTimes = 0;
        this.createTime = System.currentTimeMillis();
        this.lastUpdateTime = createTime;
    }

    public TccLog() {
    }

    public void commit() {
        this.status = TccLogStatusEnum.COMMITED.getStatus();
        this.version = this.version + 1;
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public void commitLocal() {
        this.status = TccLogStatusEnum.LOCAL_COMMITED.getStatus();
        this.version = this.version + 1;
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public void rollback() {
        this.status = TccLogStatusEnum.ROOBACKED.getStatus();
        this.version = this.version + 1;
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public void check() {
        this.version = this.version + 1;
        this.lastUpdateTime = System.currentTimeMillis();
        this.checkedTimes = this.checkedTimes + 1;
    }

    public boolean isCreated() {
        return this.status == TccLogStatusEnum.CREATED.getStatus();
    }

    public boolean isCommited() {
        return this.status == TccLogStatusEnum.COMMITED.getStatus();
    }

    public boolean isLocalCommited() {
        return this.status == TccLogStatusEnum.LOCAL_COMMITED.getStatus();
    }

    public boolean isRollbacked() {
        return this.status == TccLogStatusEnum.ROOBACKED.getStatus();
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
