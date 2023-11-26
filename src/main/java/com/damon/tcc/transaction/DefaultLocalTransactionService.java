package com.damon.tcc.transaction;

import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

public class DefaultLocalTransactionService implements ILocalTransactionService {
    @Transactional(rollbackFor = Exception.class)
    public <R> R execute(Supplier<R> supplier) {
        return supplier.get();
    }

}
