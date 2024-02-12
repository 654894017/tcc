package com.damon.tcc.main_log;

import java.util.List;

public interface ITccMainLogService {
    void create(TccMainLog tccMainLog);

    void update(TccMainLog tccMainLog);

    List<TccMainLog> queryFailedLogs(Integer checkedCount, Integer pageSize, Integer pageNumber);

    List<TccMainLog> queryDeadLogs(Integer checkedCount, Integer pageSize, Integer pageNumber);

    Integer getFailedLogsTotal(Integer times);

    Integer getDeadLogsTotal(Integer times);

    List<TccMainLog> query(List<Long> bizIds);

    TccMainLog get(Long bizId);
}
