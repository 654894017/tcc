package com.damon.tcc;

import com.damon.tcc.log.ITccLogService;

public class TccConfig {

    private IIDGenerateService idGenerateService;

    private LocalTransactionService localTransactionService;

    private ITccLogService tccLogService;

    private String bizType;
    private Integer asyncThreadMinNumber;
    private Integer asyncThreadMaxNumber;
    private Integer queueSize;

    public TccConfig(String bizType, IIDGenerateService idGenerateService, LocalTransactionService localTransactionService, ITccLogService tccLogService) {
        this(idGenerateService, localTransactionService, tccLogService, bizType, 64, 256, 512);
    }

    public TccConfig(IIDGenerateService idGenerateService, LocalTransactionService localTransactionService,
                     ITccLogService tccLogService, String bizType, Integer asyncThreadMinNumber,
                     Integer asyncThreadMaxNumber, Integer queueSize) {
        this.idGenerateService = idGenerateService;
        this.localTransactionService = localTransactionService;
        this.tccLogService = tccLogService;
        this.bizType = bizType;
        this.asyncThreadMinNumber = asyncThreadMinNumber;
        this.asyncThreadMaxNumber = asyncThreadMaxNumber;
        this.queueSize = queueSize;
    }

    public IIDGenerateService getIdGenerateService() {
        return idGenerateService;
    }

    public LocalTransactionService getLocalTransactionService() {
        return localTransactionService;
    }

    public ITccLogService getTccLogService() {
        return tccLogService;
    }

    public String getBizType() {
        return bizType;
    }

    public Integer getAsyncThreadMinNumber() {
        return asyncThreadMinNumber;
    }

    public Integer getAsyncThreadMaxNumber() {
        return asyncThreadMaxNumber;
    }

    public Integer getQueueSize() {
        return queueSize;
    }
}
