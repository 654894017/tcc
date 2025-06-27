package com.damon.tcc.mainlog;

import com.damon.tcc.exception.BizIdConflictException;
import com.damon.tcc.exception.OptimisticLockException;
import com.damon.tcc.utils.RandomNumber;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

public class TccMainLogService implements ITccMainLogService {
    private final String INSETR_TCC_LOG = "insert into tcc_main_log_%s (biz_id, random_factor, status, version, checked_times, last_update_time, create_time) values(?, ?, ?, ?, ?, ?, ?)";
    private final String UPDATE_TCC_LOG = "update tcc_main_log_%s set version = ? , status = ?, last_update_time = ?, checked_times = ? where biz_id = ? and version = ?";
    private final String GET_TCC_LOG = "select * from tcc_main_log_%s where biz_id = ? ";
    private final String GET_TCC_FAILED_LOG_TOTAL = "select count(*) from tcc_main_log_%s where status in (1,3) and checked_times < ? and random_factor like ? ";
    private final String GET_TCC_DEAD_LOG_TOTAL = "select count(*) from tcc_main_log_%s where status in (1,3) and checked_times >= ? and random_factor like ?";
    private final String QUERY_FAILED_TCC_LOG = "select * from tcc_main_log_%s where status in (1,3) and checked_times < ? and random_factor like ? order by create_time asc limit ?, ?";
    private final String QUERY_DEAD_TCC_LOG = "select * from tcc_main_log_%s where status in (1,3) and checked_times >= ? and random_factor like ? order by create_time asc limit ?, ?";
    private final String bizType;
    private final JdbcTemplate jdbcTemplate;
    private final RandomNumber randomNumber;

    public TccMainLogService(DataSource dataSource, String bizType) {
        this(dataSource, bizType, 4);
    }


    public TccMainLogService(DataSource dataSource, String bizType, Integer randomLength) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.bizType = bizType;
        this.randomNumber = new RandomNumber(randomLength);
    }

    @Override
    public void create(TccMainLog tccMainLog) {
        try {
            jdbcTemplate.update(
                    String.format(INSETR_TCC_LOG, bizType),
                    tccMainLog.getBizId(),
                    randomNumber.generate(),
                    tccMainLog.getStatus(),
                    tccMainLog.getVersion(),
                    tccMainLog.getCheckedTimes(),
                    tccMainLog.getLastUpdateTime(),
                    tccMainLog.getCreateTime()
            );
        } catch (DuplicateKeyException e) {
            throw new BizIdConflictException(e);
        }
    }

    @Override
    public void update(TccMainLog tccMainLog) {
        int i = jdbcTemplate.update(
                String.format(UPDATE_TCC_LOG, bizType),
                tccMainLog.getVersion(),
                tccMainLog.getStatus(),
                tccMainLog.getLastUpdateTime(),
                tccMainLog.getCheckedTimes(),
                tccMainLog.getBizId(),
                tccMainLog.getVersion() - 1
        );
        if (i != 1) {
            throw new OptimisticLockException("update tcc main_log failed, log info : " + tccMainLog);
        }
    }

    @Override
    public List<TccMainLog> queryFailedLogs(String tailNumber, Integer checkedCount, Integer pageSize, Integer pageNumber) {
        return jdbcTemplate.query(String.format(QUERY_FAILED_TCC_LOG, bizType),
                new BeanPropertyRowMapper<>(TccMainLog.class),
                checkedCount,
                tailNumber + "%",
                (pageNumber - 1) * pageSize,
                pageSize
        );
    }

    @Override
    public List<TccMainLog> queryDeadLogs(String tailNumber, Integer checkedCount, Integer pageSize, Integer pageNumber) {
        return jdbcTemplate.query(String.format(QUERY_DEAD_TCC_LOG, bizType),
                new BeanPropertyRowMapper<>(TccMainLog.class),
                checkedCount,
                tailNumber + "%",
                (pageNumber - 1) * pageSize,
                pageSize
        );
    }

    @Override
    public Integer getFailedLogsTotal(String tailNumber, Integer times) {
        return jdbcTemplate.queryForObject(
                String.format(GET_TCC_FAILED_LOG_TOTAL, bizType),
                Integer.class,
                times,
                tailNumber
        );
    }

    @Override
    public Integer getDeadLogsTotal(String tailNumber, Integer times) {
        return jdbcTemplate.queryForObject(String.format(GET_TCC_DEAD_LOG_TOTAL, bizType),
                Integer.class,
                times,
                tailNumber
        );
    }

    @Override
    public TccMainLog get(Long bizId) {
        return jdbcTemplate.queryForObject(String.format(GET_TCC_LOG, bizType),
                new BeanPropertyRowMapper<>(TccMainLog.class), bizId
        );
    }
}
