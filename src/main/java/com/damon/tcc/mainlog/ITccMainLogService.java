package com.damon.tcc.mainlog;

import java.util.List;

public interface ITccMainLogService {
    void create(TccMainLog tccMainLog);

    void update(TccMainLog tccMainLog);

    List<TccMainLog> queryFailedLogs(String tailNumber, Integer checkedCount, Integer pageSize, Integer pageNumber);

    List<TccMainLog> queryDeadLogs(String tailNumber, Integer checkedCount, Integer pageSize, Integer pageNumber);

    Integer getFailedLogsTotal(String tailNumber, Integer times);

    Integer getDeadLogsTotal(String tailNumber, Integer times);

    TccMainLog get(Long bizId);
}
