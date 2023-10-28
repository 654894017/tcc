package com.damon.tcc.log;


public class TccLog {

    private Long id;

    private Long bizId;

    private Integer status;

    private int version;

    public TccLog(Long id, Long bizId) {
        this.id = id;
        this.bizId = bizId;
        this.status = TccLogStatusEnum.CREATED.getStatus();
        this.version = 0;
    }

    public TccLog() {
    }

    public void commit() {
        this.status = TccLogStatusEnum.COMMITED.getStatus();
        this.version = this.version + 1;
    }

    public void commitLocal() {
        this.status = TccLogStatusEnum.LOCAL_COMMITED.getStatus();
        this.version = this.version + 1;
    }

    public void rollback() {
        this.status = TccLogStatusEnum.ROOBACKED.getStatus();
        this.version = this.version + 1;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
