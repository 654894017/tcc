package com.damon;

import cn.hutool.core.util.IdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
public class TestGateway {
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public TestGateway(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public TestEntity get(Long id) {
        return new TestEntity(id);
    }

    public void save(){
        jdbcTemplate.update("insert test2(id,number) values(?,?)", IdUtil.getSnowflakeNextId(),IdUtil.getSnowflakeNextId());
//        if(1==1){
//            throw new RuntimeException("1");
//        }
        jdbcTemplate.update("insert test2(id,number) values(?,?)",IdUtil.getSnowflakeNextId(), IdUtil.getSnowflakeNextId());
    }

}
