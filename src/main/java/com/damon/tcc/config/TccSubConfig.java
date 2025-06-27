package com.damon.tcc.config;

import com.damon.tcc.sublog.ITccSubLogService;
import com.damon.tcc.transaction.ILocalTransactionService;

import javax.sql.DataSource;

public class TccSubConfig {

    private final DataSource dataSource;
    private ITccSubLogService tccSubLogService;
    private ILocalTransactionService localTransactionService;
    private String bizType;

    public TccSubConfig(ITccSubLogService tccSubLogService, ILocalTransactionService localTransactionService, DataSource dataSource, String bizType) {
        this.tccSubLogService = tccSubLogService;
        this.localTransactionService = localTransactionService;
        this.bizType = bizType;
        this.dataSource = dataSource;
    }

    public ITccSubLogService getTccSubLogService() {
        return tccSubLogService;
    }

    public void setTccSubLogService(ITccSubLogService tccSubLogService) {
        this.tccSubLogService = tccSubLogService;
    }

    public ILocalTransactionService getLocalTransactionService() {
        return localTransactionService;
    }

    public void setLocalTransactionService(ILocalTransactionService localTransactionService) {
        this.localTransactionService = localTransactionService;
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    public DataSource getDataSource() {
        return dataSource;
    }
}
