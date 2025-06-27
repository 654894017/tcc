package com.damon.tcc.transaction;

import java.util.function.Supplier;

public interface ILocalTransactionService {
    /**
     * 执行本地事务
     *
     * @param supplier 事务执行逻辑
     * @param <R>      返回值类型
     * @return 事务执行结果
     */
    <R> R execute(Supplier<R> supplier);
}
