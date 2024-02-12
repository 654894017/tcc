package com.damon.tcc;

import com.damon.tcc.main_log.ITccMainLogService;
import com.damon.tcc.main_log.TccMainLogService;
import com.damon.tcc.transaction.ILocalTransactionService;

import javax.sql.DataSource;

public class TccMainConfig {
    private ILocalTransactionService localTransactionService;
    private ITccMainLogService tccLogService;
    private String bizType;
    private Integer asyncCommitThreadMinNumber;
    private Integer asyncCommitThreadMaxNumber;
    private Integer asyncCheckThreadMinNumber;
    private Integer asyncCheckThreadMaxNumber;
    private Integer asyncCommitQueueSize;
    private Integer asyncCheckQueueSize;
    private Integer failedCheckTimes;
    private Integer tccFailedLogPageSize;
    private DataSource dataSource;

    public TccMainConfig(String bizType, ILocalTransactionService localTransactionService, DataSource dataSource) {
        this(bizType, localTransactionService, dataSource,
                4, 8, 512,
                4, 8, 512,
                5, 100);
    }

    public TccMainConfig(String bizType, ILocalTransactionService localTransactionService, DataSource dataSource,
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
