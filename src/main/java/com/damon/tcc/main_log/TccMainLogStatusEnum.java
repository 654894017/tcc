package com.damon.tcc.main_log;

/**
 * 正常状态流转：  1 -> 3 -> 4
 * <p>
 * 异常状态流转：  3 -> 4  ;   1 -> 2
 */
public enum TccMainLogStatusEnum {
    CREATED(1, "创建事务成功"),
    ROOBACKED(2, "回滚事务成功"),
    LOCAL_COMMITED(3, "完成本地事务成功"),
    COMMITED(4, "提交事务成功");
    private final Integer status;
    private final String message;

    TccMainLogStatusEnum(Integer status, String message) {
        this.status = status;
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

}
