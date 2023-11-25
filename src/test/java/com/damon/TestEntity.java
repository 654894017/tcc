package com.damon;

import com.damon.tcc.BizId;

public class TestEntity implements BizId {
    private Long id;

    public TestEntity(Long id) {
        this.id = id;
    }

    @Override
    public Long getBizId() {
        return id;
    }
}
