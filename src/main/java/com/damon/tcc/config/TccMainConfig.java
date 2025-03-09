package com.damon.tcc.config;

import com.damon.tcc.local_transaction.ILocalTransactionService;
import com.damon.tcc.main_log.ITccMainLogService;
import com.damon.tcc.main_log.TccMainLogService;

import javax.sql.DataSource;

public class TccMainConfig {
    private final ILocalTransactionService localTransactionService;
    private final ITccMainLogService tccLogService;
    private final String bizType;
    private final Integer asyncCommitThreadMinNumber;
    private final Integer asyncCommitThreadMaxNumber;
    private final Integer asyncCheckThreadMinNumber;
    private final Integer asyncCheckThreadMaxNumber;
    private final Integer asyncCommitQueueSize;
    private final Integer asyncCheckQueueSize;
    private final Integer failedCheckTimes;
    private final Integer tccFailedLogPageSize;
    private final DataSource dataSource;

    public TccMainConfig(String bizType, ILocalTransactionService localTransactionService, DataSource dataSource) {
        this(bizType, localTransactionService, dataSource,
                4, 8, 512,
                4, 8, 512,
                5, 100);
    }

    public TccMainConfig(String bizType,
                         ILocalTransactionService localTransactionService,
                         DataSource dataSource,
                         Integer asyncCommitThreadMinNumber,
                         Integer asyncCommitThreadMaxNumber,
                         Integer asyncCommitQueueSize,
                         Integer asyncCheckThreadMinNumber,
                         Integer asyncCheckThreadMaxNumber,
                         Integer asyncCheckQueueSize,
                         Integer failedCheckTimes,
                         Integer tccFailedLogPageSize) {
        this.localTransactionService = localTransactionService;
        this.tccLogService = new TccMainLogService(dataSource, bizType);
        this.bizType = bizType;
        this.asyncCommitThreadMinNumber = asyncCommitThreadMinNumber;
        this.asyncCommitThreadMaxNumber = asyncCommitThreadMaxNumber;
        this.asyncCheckThreadMinNumber = asyncCheckThreadMinNumber;
        this.asyncCheckThreadMaxNumber = asyncCheckThreadMaxNumber;
        this.asyncCommitQueueSize = asyncCommitQueueSize;
        this.asyncCheckQueueSize = asyncCheckQueueSize;
        this.failedCheckTimes = failedCheckTimes;
        this.tccFailedLogPageSize = tccFailedLogPageSize;
        this.dataSource = dataSource;
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

    public ITccMainLogService getTccLogService() {
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

    public DataSource getDataSource() {
        return dataSource;
    }
}
