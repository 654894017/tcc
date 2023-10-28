package com.damon.tcc.id;

import cn.hutool.core.util.IdUtil;

public class DefaultIDGenerateService implements IIDGenerateService {
    @Override
    public Long nextId() {
        return IdUtil.getSnowflakeNextId();
    }
}
