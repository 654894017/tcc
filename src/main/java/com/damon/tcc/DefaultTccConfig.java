package com.damon.tcc;

import cn.hutool.core.util.IdUtil;

import com.damon.tcc.log.ITccLogService;
import com.damon.tcc.log.TccLogService;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

public class DefaultTccConfig {
    @Bean
    public IIDGenerateService idGenerateService(){
        return () -> IdUtil.getSnowflakeNextId();
    }
    @Bean
    public LocalTransactionService localTransactionService(){
        return new LocalTransactionService();
    }
    @Bean
    public ITccLogService tccLogService(DataSource dataSource, String bizType){
        return new TccLogService(dataSource, bizType);
    }

}
