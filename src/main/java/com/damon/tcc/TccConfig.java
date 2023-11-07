package com.damon.tcc;

import com.damon.tcc.log.ITccLogService;
import com.damon.tcc.transaction.ILocalTransactionService;

public class TccConfig {
    private ILocalTransactionService localTransactionService;
    private ITccLogService tccLogService;
    private String bizType;
    private Integer asyncThreadMinNumber;
    private Integer asyncThreadMaxNumber;
    private Integer queueSize;
    private Integer failedCheckTimes;
    private Integer tccFailedLogPageSize;

    public TccConfig(String bizType, ILocalTransactionService localTransactionService, ITccLogService tccLogService) {
        this(bizType, localTransactionService, tccLogService,
                4, 8, 512,
                5, 100);
    }

    public TccConfig(String bizType, ILocalTransactionService localTransactionService, ITccLogService tccLogService, Integer asyncThreadMinNumber,
                     Integer asyncThreadMaxNumber, Integer queueSize,
                     Integer failedCheckTimes, Integer tccFailedLogPageSize) {
        this.localTransactionService = localTransactionService;
        this.tccLogService = tccLogService;
        this.bizType = bizType;
        this.asyncThreadMinNumber = asyncThreadMinNumber;
        this.asyncThreadMaxNumber = asyncThreadMaxNumber;
        this.queueSize = queueSize;
        this.failedCheckTimes = failedCheckTimes;
        this.tccFailedLogPageSize = tccFailedLogPageSize;
    }

    public Integer getFailedCheckTimes() {
        return failedCheckTimes;
    }

    public Integer getTccFailedLogPageSize() {
        return tccFailedLogPageSize;
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
