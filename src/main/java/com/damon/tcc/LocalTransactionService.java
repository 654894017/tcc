package com.damon.tcc;

import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

public class LocalTransactionService {
    @Transactional
    public <R> R executeLocalTransaction(Supplier<R> supplier) {
        return supplier.get();
    }

}
