package com.damon.tcc.sub_log;

public interface ITccSubLogService {

    void create(TccSubLog tccLog);

    void update(TccSubLog tccSubLog);

    TccSubLog get(Long bizId);

}
