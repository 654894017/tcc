package com.damon.tcc.main_log;

import com.damon.tcc.exception.OptimisticLockException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

public class TccMainLogService implements ITccMainLogService {
    private final String INSETR_TCC_LOG = "insert into tcc_main_log_%s (biz_id, status, version, checked_times, last_update_time, create_time) values(?, ?, ?, ?, ?, ?)";
    private final String UPDATE_TCC_LOG = "update tcc_main_log_%s set version = ? , status = ?, last_update_time = ? where biz_id = ? and version = ?";
    private final String CHECK_TCC_LOG = "update tcc_main_log_%s set version = ? , last_update_time = ? , checked_times = ? where biz_id = ? and version = ?";
    private final String GET_TCC_LOG = "select * from tcc_main_log_%s where biz_id = ? ";
    private final String GET_TCC_FAILED_LOG_TOTAL = "select count(*) from tcc_main_log_%s where status in (1,3) and checked_times < ?";
    private final String GET_TCC_DEAD_LOG_TOTAL = "select count(*) from tcc_main_log_%s where status in (1,3) and checked_times > ?";
    private final String QUERY_FAILED_TCC_LOG = "select * from tcc_main_log_%s where status in (1,3) and checked_times < ? order by create_time asc limit ?, ?";
    private final String QUERY_DEAD_TCC_LOG = "select * from tcc_main_log_%s where status in (1,3) and checked_times >= ? order by create_time asc limit ?, ?";
    private final String bizType;
    private final JdbcTemplate jdbcTemplate;

    public TccMainLogService(DataSource dataSource, String bizType) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.bizType = bizType;
    }

    //    @Override
//    public void commit(TccMainLog tccMainLog) {
//        tccMainLog.commit();
//        update(tccMainLog);
//    }
//
//    @Override
//    public void commitLocal(TccMainLog tccMainLog) {
//        tccMainLog.commitLocal();
//        update(tccMainLog);
//    }
//
    @Override
    public void create(TccMainLog tccMainLog) {
        int i = jdbcTemplate.update(
                String.format(INSETR_TCC_LOG, bizType),
                tccMainLog.getBizId(), tccMainLog.getStatus(), tccMainLog.getVersion(), tccMainLog.getCheckedTimes(), tccMainLog.getLastUpdateTime(), tccMainLog.getCreateTime());
        if (i != 1) {
            throw new OptimisticLockException("insert tcc main_log failed");
        }
    }

    //
//    @Override
//    public void rollback(TccMainLog tccMainLog) {
//        tccMainLog.rollback();
//        updateStatus(tccMainLog);
//    }
//
//    @Override
//    public void updateCheckTimes(TccMainLog tccMainLog) {
//        tccMainLog.check();
//        int i = jdbcTemplate.update(
//                String.format(CHECK_TCC_LOG, bizType),
//                tccMainLog.getVersion(), tccMainLog.getLastUpdateTime(), tccMainLog.getCheckedTimes(), tccMainLog.getBizId(), tccMainLog.getVersion() -1
//        );
//        if (i != 1) {
//            throw new OptimisticLockException("update tcc main_log failed");
//        }
//    }
    @Override
    public void update(TccMainLog tccMainLog) {
        int i = jdbcTemplate.update(
                String.format(UPDATE_TCC_LOG, bizType),
                tccMainLog.getVersion(), tccMainLog.getStatus(), tccMainLog.getLastUpdateTime(), tccMainLog.getBizId(), tccMainLog.getVersion() - 1
        );
        if (i != 1) {
            throw new OptimisticLockException("update tcc main_log failed, log info : " + tccMainLog);
        }
    }

    @Override
    public List<TccMainLog> queryFailedLogs(Integer checkedCount, Integer pageSize, Integer pageNumber) {
        return jdbcTemplate.query(String.format(QUERY_FAILED_TCC_LOG, bizType),
                new BeanPropertyRowMapper<>(TccMainLog.class),
                checkedCount,
                (pageNumber - 1) * pageSize,
                pageSize
        );
    }

    @Override
    public List<TccMainLog> queryDeadLogs(Integer checkedCount, Integer pageSize, Integer pageNumber) {
        return jdbcTemplate.query(String.format(QUERY_DEAD_TCC_LOG, bizType),
                new BeanPropertyRowMapper<>(TccMainLog.class),
                checkedCount,
                (pageNumber - 1) * pageSize,
                pageSize
        );
    }

    @Override
    public Integer getFailedLogsTotal(Integer times) {
        return jdbcTemplate.queryForObject(String.format(GET_TCC_FAILED_LOG_TOTAL, bizType), Integer.class, times);
    }

    @Override
    public Integer getDeadLogsTotal(Integer times) {
        return jdbcTemplate.queryForObject(String.format(GET_TCC_DEAD_LOG_TOTAL, bizType), Integer.class, times);
    }

    @Override
    public TccMainLog get(Long bizId) {
        TccMainLog tccMainLog = jdbcTemplate.queryForObject(
                String.format(GET_TCC_LOG, bizType),
                new BeanPropertyRowMapper<>(TccMainLog.class), bizId
        );
        return tccMainLog;
    }
}
