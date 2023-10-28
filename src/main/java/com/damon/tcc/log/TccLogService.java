package com.damon.tcc.log;

import com.damon.tcc.exception.OptimisticLockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class TccLogService implements ITccLogService {
    private final String INSETR_TCC_LOG = "insert into tcc_log_%s (id, biz_id, status, version) values( ? ,? , ? ,?)";
    private final String UPDATE_TCC_LOG = "update tcc_log_%s set version = ? , status = ? where id = ? , version = ?";
    private final String GET_TCC_LOG = "select * from tcc_log_%s where biz_id = ? ";
    private final String bizType;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public TccLogService(DataSource dataSource, String bizType) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.bizType = bizType;
    }

    @Override
    public void commit(TccLog tccLog) {
        tccLog.commit();
        update(tccLog);
    }

    @Override
    public void commitLocal(TccLog tccLog) {
        tccLog.commitLocal();
        update(tccLog);
    }

    @Override
    public void create(TccLog tccLog) {
        int i = jdbcTemplate.update(
                String.format(INSETR_TCC_LOG, bizType),
                tccLog.getId(), tccLog.getBizId(), tccLog.getStatus(), tccLog.getVersion());
        if (i != 1) {
            throw new OptimisticLockException("insert tcc log failed");
        }
    }

    @Override
    public void rollback(TccLog tccLog) {
        tccLog.rollback();
        update(tccLog);
    }

    private void update(TccLog tccLog) {
        int i = jdbcTemplate.update(
                String.format(UPDATE_TCC_LOG, bizType),
                tccLog.getVersion(), tccLog.getStatus(), tccLog.getId(), tccLog.getVersion() - 1
        );
        if (i != 1) {
            throw new OptimisticLockException("update tcc log failed");
        }
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
