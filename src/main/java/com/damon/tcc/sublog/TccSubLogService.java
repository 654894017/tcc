package com.damon.tcc.sublog;

import com.damon.tcc.exception.BizIdConflictException;
import com.damon.tcc.exception.OptimisticLockException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

public class TccSubLogService implements ITccSubLogService {
    private final String INSETR_TCC_SUB_LOG = "insert into tcc_sub_log_%s (biz_id, sub_biz_id, status, last_update_time, create_time, version) values(?, ?, ?, ?, ?, ?)";
    private final String UPDATE_TCC_SUB_LOG = "update tcc_sub_log_%s set status = ?, last_update_time = ?, version = ? where biz_id = ? and sub_biz_id = ? and version = ?";
    private final String GET_TCC_SUB_LOG = "select * from tcc_sub_log_%s where biz_id = ? and sub_biz_id = ?";
    private final String bizType;
    private final JdbcTemplate jdbcTemplate;

    public TccSubLogService(DataSource dataSource, String bizType) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.bizType = bizType;
    }

    @Override
    public void create(TccSubLog tccSubLog) {
        try {
            jdbcTemplate.update(
                    String.format(INSETR_TCC_SUB_LOG, bizType),
                    tccSubLog.getBizId(),
                    tccSubLog.getSubBizId(),
                    tccSubLog.getStatus(),
                    tccSubLog.getLastUpdateTime(),
                    tccSubLog.getCreateTime(),
                    tccSubLog.getVersion()
            );
        } catch (DuplicateKeyException e) {
            throw new BizIdConflictException(e);
        }
    }

    @Override
    public void update(TccSubLog tccSubLog) {
        int i = jdbcTemplate.update(
                String.format(UPDATE_TCC_SUB_LOG, bizType),
                tccSubLog.getStatus(),
                tccSubLog.getLastUpdateTime(),
                tccSubLog.getVersion(),
                tccSubLog.getBizId(),
                tccSubLog.getSubBizId(),
                tccSubLog.getVersion() - 1
        );
        if (i != 1) {
            throw new OptimisticLockException("update tcc sub sub log failed");
        }
    }

    @Override
    public TccSubLog get(Long bizId, Long subBizId) {
        List<TccSubLog> tccSubLogs = jdbcTemplate.query(
                String.format(GET_TCC_SUB_LOG, bizType),
                new BeanPropertyRowMapper<>(TccSubLog.class), bizId, subBizId
        );
        return tccSubLogs.isEmpty() ? null : tccSubLogs.get(0);
    }

}
