package com.damon.tcc.log;

public enum TccLogStatusEnum {

    CREATED(1, "创建事务成功"), ROOBACKED(2, "回滚事务成功"), LOCAL_COMMITED(3, "完成本地事务成功"), COMMITED(4, "提交事务成功");

    private Integer status;
    private String message;

    TccLogStatusEnum(Integer status, String message) {
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
