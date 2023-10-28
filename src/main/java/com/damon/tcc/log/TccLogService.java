package com.damon.tcc.log;

import com.damon.tcc.exception.OptimisticLockException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

public class TccLogService implements ITccLogService {
    private final String INSETR_TCC_LOG = "insert into tcc_log_%s (id, biz_id, status, version, last_update_time, create_time) values( ? ,? , ? ,?, ? ,?)";
    private final String UPDATE_TCC_LOG = "update tcc_log_%s set version = ? , status = ?, last_update_time = ? where id = ? , version = ?";
    private final String CHECK_TCC_LOG = "update tcc_log_%s set version = ? , last_update_time = ? , checked_count = ? where id = ? , version = ?";
    private final String GET_TCC_LOG = "select * from tcc_log_%s where biz_id = ? ";
    private final String GET_TCC_FAILED_LOG_TOTAL = "select count(*) from tcc_log_%s where in (1,3) and checked_count < ?";
    private final String QUERY_FAILED_TCC_LOG = "select * from tcc_log_%s where stauts in (1,3) and checked_count < ? order by create_time limit ?, ?";
    private final String bizType;
    private JdbcTemplate jdbcTemplate;

    public TccLogService(DataSource dataSource, String bizType) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.bizType = bizType;
    }

    @Override
    public void commit(TccLog tccLog) {
        tccLog.commit();
        updateStatus(tccLog);
    }

    @Override
    public void commitLocal(TccLog tccLog) {
        tccLog.commitLocal();
        updateStatus(tccLog);
    }

    @Override
    public void create(TccLog tccLog) {
        int i = jdbcTemplate.update(
                String.format(INSETR_TCC_LOG, bizType),
                tccLog.getId(), tccLog.getBizId(), tccLog.getStatus(), tccLog.getVersion(), tccLog.getLastUpdateTime(), tccLog.getCreateTime());
        if (i != 1) {
            throw new OptimisticLockException("insert tcc log failed");
        }
    }

    @Override
    public void rollback(TccLog tccLog) {
        tccLog.rollback();
        updateStatus(tccLog);
    }

    @Override
    public void updateCheckCount(TccLog tccLog) {
        tccLog.check();
        int i = jdbcTemplate.update(
                String.format(CHECK_TCC_LOG, bizType),
                tccLog.getVersion(), tccLog.getLastUpdateTime(), tccLog.getCheckedCount(), tccLog.getId(), tccLog.getVersion() - 1
        );
        if (i != 1) {
            throw new OptimisticLockException("update tcc log failed");
        }
    }

    private void updateStatus(TccLog tccLog) {
        int i = jdbcTemplate.update(
                String.format(UPDATE_TCC_LOG, bizType),
                tccLog.getVersion(), tccLog.getStatus(), tccLog.getLastUpdateTime(), tccLog.getId(), tccLog.getVersion() - 1
        );
        if (i != 1) {
            throw new OptimisticLockException("update tcc log failed");
        }
    }

    @Override
    public List<TccLog> queryFailedLogs(Integer checkedCount, Integer pageSize, Integer pageNumber) {
        return jdbcTemplate.query(String.format(QUERY_FAILED_TCC_LOG, bizType),
                new BeanPropertyRowMapper<>(TccLog.class),
                checkedCount,
                pageNumber * pageSize,
                (pageNumber + 1) * pageSize
        );
    }

    @Override
    public Integer getFailedLogsTotal() {
        return jdbcTemplate.queryForObject(String.format(GET_TCC_FAILED_LOG_TOTAL, bizType), Integer.class);
    }


    @Override
    public TccLog get(Long bizId) {
        TccLog tccLog = jdbcTemplate.queryForObject(
                String.format(GET_TCC_LOG, bizType),
                new BeanPropertyRowMapper<>(TccLog.class), bizId
        );
        return tccLog;
    }
}
