package com.damon;

import com.damon.tcc.BizId;

public class Test implements BizId {
    @Override
    public Long getBizId() {
        return 1l;
    }
}
