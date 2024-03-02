package com.damon.tcc.annotation;

public interface SubBizId extends BizId {
    /**
     * 用于标记子事务需要被更新对象的唯一标识
     * <p>
     * 例如：发起转账交易，需要调用转账服务的扣款接口和收款接口，
     * <p>
     * 如果没有该标记，就无法区分两条tcc子事务日志，那条对应的是收款账户的日志还是扣款账户的tcc子日志。
     *
     * @return
     */
    Long getSubBizId();
}
