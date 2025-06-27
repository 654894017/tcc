package com.damon.tcc.sublog;

public interface ITccSubLogService {

    void create(TccSubLog tccLog);

    void update(TccSubLog tccSubLog);

    TccSubLog get(Long bizId, Long subBizId);

}
