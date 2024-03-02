package com.damon.tcc.annotation;

public interface BizId {
    /**
     * 全局事务日志id
     * <p>
     * 例如： 以订单下单为例，那么这个id就是订单id，
     * <p>
     * 这个id会贯穿这个流程，包括其它服务的子事务标识。
     *
     * @return
     */
    Long getBizId();
}
