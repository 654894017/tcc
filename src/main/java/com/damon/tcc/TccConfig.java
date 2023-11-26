package com.damon.tcc;

import com.damon.tcc.log.ITccLogService;
import com.damon.tcc.transaction.ILocalTransactionService;

public class TccConfig {
    private ILocalTransactionService localTransactionService;
    private ITccLogService tccLogService;
    private String bizType;
    private Integer asyncCommitThreadMinNumber;
    private Integer asyncCommitThreadMaxNumber;
    private Integer asyncCheckThreadMinNumber;
    private Integer asyncCheckThreadMaxNumber;
    private Integer asyncCommitQueueSize;
    private Integer asyncCheckQueueSize;
    private Integer failedCheckTimes;
    private Integer tccFailedLogPageSize;

    public TccConfig(String bizType, ILocalTransactionService localTransactionService, ITccLogService tccLogService) {
        this(bizType, localTransactionService, tccLogService,
                4, 8, 512,
                4,8, 512,
                5, 100);
    }

    public TccConfig(String bizType, ILocalTransactionService localTransactionService, ITccLogService tccLogService,
                     Integer asyncCommitThreadMinNumber,
                     Integer asyncCommitThreadMaxNumber,
                     Integer asyncCommitQueueSize,
                     Integer asyncCheckThreadMinNumber,
                     Integer asyncCheckThreadMaxNumber,
                     Integer asyncCheckQueueSize,
                     Integer failedCheckTimes,
                     Integer tccFailedLogPageSize) {
        this.localTransactionService = localTransactionService;
        this.tccLogService = tccLogService;
        this.bizType = bizType;
        this.asyncCommitThreadMinNumber = asyncCommitThreadMinNumber;
        this.asyncCommitThreadMaxNumber = asyncCommitThreadMaxNumber;
        this.asyncCheckThreadMinNumber = asyncCheckThreadMinNumber;
        this.asyncCheckThreadMaxNumber = asyncCheckThreadMaxNumber;
        this.asyncCommitQueueSize = asyncCommitQueueSize;
        this.asyncCheckQueueSize = asyncCheckQueueSize;
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

    public Integer getAsyncCommitThreadMinNumber() {
        return asyncCommitThreadMinNumber;
    }

    public Integer getAsyncCommitThreadMaxNumber() {
        return asyncCommitThreadMaxNumber;
    }

    public Integer getAsyncCheckThreadMinNumber() {
        return asyncCheckThreadMinNumber;
    }

    public Integer getAsyncCheckThreadMaxNumber() {
        return asyncCheckThreadMaxNumber;
    }

    public Integer getAsyncCommitQueueSize() {
        return asyncCommitQueueSize;
    }

    public Integer getAsyncCheckQueueSize() {
        return asyncCheckQueueSize;
    }
}
