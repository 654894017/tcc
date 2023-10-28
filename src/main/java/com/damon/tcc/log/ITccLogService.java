package com.damon.tcc.log;

public interface ITccLogService {
    void commit(TccLog tccLog);

    void commitLocal(TccLog tccLog);

    void create(TccLog tccLog);

    void rollback(TccLog tccLog);

    TccLog get(Long bizId);
}
