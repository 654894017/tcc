package com.damon.tcc.config;

import com.damon.tcc.sublog.ITccSubLogService;
import com.damon.tcc.sublog.TccSubLogService;
import com.damon.tcc.transaction.ILocalTransactionService;

import javax.sql.DataSource;

public class TccSubNestConfig {
    private final ILocalTransactionService localTransactionService;
    private final ITccSubLogService tccSubLogService;
    private final String bizType;
    private final DataSource dataSource;

    public TccSubNestConfig(String bizType, ILocalTransactionService localTransactionService, DataSource dataSource) {
        this.localTransactionService = localTransactionService;
        this.tccSubLogService = new TccSubLogService(dataSource, bizType);
        this.bizType = bizType;
        this.dataSource = dataSource;
    }

    public ILocalTransactionService getLocalTransactionService() {
        return localTransactionService;
    }

    public ITccSubLogService getTccSubLogService() {
        return tccSubLogService;
    }

    public String getBizType() {
        return bizType;
    }

    public DataSource getDataSource() {
        return dataSource;
    }
}
