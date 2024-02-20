package com.damon.tcc.transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.function.Supplier;

public class DefaultLocalTransactionService implements ILocalTransactionService {
    private final Logger log = LoggerFactory.getLogger(DefaultLocalTransactionService.class);

    @Transactional(rollbackFor = Exception.class)
    public <R> R execute(Supplier<R> supplier) {
        boolean isTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
        if (!isTransactionActive) {
            log.error("spring本地事务不生效，请确认事务配置是否正确");
        }
        return supplier.get();
    }

}
