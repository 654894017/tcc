package com.damon.tcc.config;

import com.damon.tcc.sublog.ITccSubLogService;
import com.damon.tcc.sublog.TccSubLogService;
import com.damon.tcc.transaction.ILocalTransactionService;

import javax.sql.DataSource;

public class TccSubConfig {

    private final DataSource dataSource;
    private ITccSubLogService tccSubLogService;
    private ILocalTransactionService localTransactionService;
    private String bizType;

    public TccSubConfig(DataSource dataSource, String bizType, ILocalTransactionService localTransactionService) {
        this.tccSubLogService = new TccSubLogService(dataSource, bizType);
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
