package com.damon.tcc;

import com.damon.tcc.id.IIDGenerateService;
import com.damon.tcc.log.ITccLogService;
import com.damon.tcc.transaction.ILocalTransactionService;

public class TccConfig {
    private IIDGenerateService idGenerateService;
    private ILocalTransactionService localTransactionService;
    private ITccLogService tccLogService;
    private String bizType;
    private Integer asyncThreadMinNumber;
    private Integer asyncThreadMaxNumber;
    private Integer queueSize;

    public TccConfig(String bizType, IIDGenerateService idGenerateService, ILocalTransactionService localTransactionService, ITccLogService tccLogService) {
        this(idGenerateService, localTransactionService, tccLogService, bizType, 64, 256, 512);
    }

    public TccConfig(IIDGenerateService idGenerateService, ILocalTransactionService localTransactionService,
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

    public ILocalTransactionService getLocalTransactionService() {
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
