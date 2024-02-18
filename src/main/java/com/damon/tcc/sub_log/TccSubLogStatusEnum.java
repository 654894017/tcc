package com.damon.tcc.sub_log;


public enum TccSubLogStatusEnum {
    TRY(1, "创建事务成功"),
    COMMITTED(2, "提交事务成功"),
    CANCELED(3, "回滚事务成功");
    private final Integer status;
    private final String message;

    TccSubLogStatusEnum(Integer status, String message) {
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
