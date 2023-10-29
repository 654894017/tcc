package com.damon;

import com.damon.tcc.BizId;

public class Test implements BizId {
    private Long id;
    @Override
    public Long getBizId() {
        return id;
    }

    public Test(Long id) {
        this.id = id;
    }
}
