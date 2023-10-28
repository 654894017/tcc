package com.damon.tcc.transaction;

import java.util.function.Supplier;

public interface ILocalTransactionService {
    <R> R executeLocalTransaction(Supplier<R> supplier);
}
