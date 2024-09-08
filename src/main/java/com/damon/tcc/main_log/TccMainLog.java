package com.damon.tcc.main_log;

public class TccMainLog {
    private Long bizId;
    private Integer status;
    private int version;
    private int checkedTimes;
    private Long lastUpdateTime;
    private Long createTime;

    public TccMainLog(Long bizId) {
        Long createTime = System.currentTimeMillis();
        this.checkedTimes = 0;
        this.version = 0;
        this.status = TccMainLogStatusEnum.CREATED.getStatus();
        this.createTime = createTime;
        this.lastUpdateTime = createTime;
        this.bizId = bizId;
    }

    public TccMainLog() {
    }

    public void rollback() {
        this.version = this.version + 1;
        this.status = TccMainLogStatusEnum.ROOBACKED.getStatus();
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public void commit() {
        this.version = this.version + 1;
        this.status = TccMainLogStatusEnum.COMMITED.getStatus();
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public void commitLocal() {
        this.version = this.version + 1;
        this.status = TccMainLogStatusEnum.LOCAL_COMMITED.getStatus();
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public void check() {
        this.version = this.version + 1;
        this.checkedTimes = this.checkedTimes + 1;
        this.lastUpdateTime = System.currentTimeMillis();
    }

    public boolean isCreated() {
        return this.status.equals(TccMainLogStatusEnum.CREATED.getStatus());
    }

    public boolean isCommited() {
        return this.status.equals(TccMainLogStatusEnum.COMMITED.getStatus());
    }

    public boolean isLocalCommited() {
        return this.status.equals(TccMainLogStatusEnum.LOCAL_COMMITED.getStatus());
    }

    public boolean isRollbacked() {
        return this.status.equals(TccMainLogStatusEnum.ROOBACKED.getStatus());
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
