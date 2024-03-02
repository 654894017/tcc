package com.damon.tcc.local_transaction;

import java.util.function.Supplier;

public interface ILocalTransactionService {
    <R> R execute(Supplier<R> supplier);
}
