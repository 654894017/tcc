package com.damon.tcc;

import com.damon.tcc.sub_log.ITccSubLogService;
import com.damon.tcc.transaction.ILocalTransactionService;

public class TccSubConfig {

    private ITccSubLogService tccSubLogService;
    private ILocalTransactionService localTransactionService;
    private String bizType;

    public TccSubConfig(ITccSubLogService tccSubLogService, ILocalTransactionService localTransactionService, String bizType) {
        this.tccSubLogService = tccSubLogService;
        this.localTransactionService = localTransactionService;
        this.bizType = bizType;
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
}
