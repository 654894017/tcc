package com.damon.tcc.transaction;

import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

public class DefaultLocalTransactionService {
    @Transactional
    public <R> R executeLocalTransaction(Supplier<R> supplier) {
        return supplier.get();
    }

}
