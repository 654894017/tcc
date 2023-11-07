package com.damon.tcc.log;

import java.util.List;

public interface ITccLogService {
    void commit(TccLog tccLog);

    void commitLocal(TccLog tccLog);

    void create(TccLog tccLog);

    void rollback(TccLog tccLog);

    void updateCheckCount(TccLog tccLog);

    List<TccLog> queryFailedLogs(Integer checkedCount, Integer pageSize, Integer pageNumber);

    List<TccLog> queryDeadLogs(Integer checkedCount, Integer pageSize, Integer pageNumber);

    Integer getFailedLogsTotal(Integer times);

    Integer getDeadLogsTotal(Integer times);


    TccLog get(Long bizId);


}
