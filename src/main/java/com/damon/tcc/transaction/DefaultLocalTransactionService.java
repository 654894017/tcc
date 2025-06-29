package com.damon.tcc.transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.function.Supplier;

public class DefaultLocalTransactionService implements ILocalTransactionService {
    private final Logger log = LoggerFactory.getLogger(DefaultLocalTransactionService.class);

    @Override
    @Transactional(rollbackFor = Exception.class)
    public <R> R execute(Supplier<R> supplier) {
        boolean isTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
        if (!isTransactionActive) {
            log.error("Spring local transaction is not effective, please verify if the transaction configuration is correct.");
        }
        return supplier.get();
    }

}
