package com.damon;

import com.damon.tcc.TccConfig;
import com.damon.tcc.log.ITccLogService;
import com.damon.tcc.log.TccLogService;
import com.damon.tcc.transaction.DefaultLocalTransactionService;
import com.damon.tcc.transaction.ILocalTransactionService;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class TestTccConfig {
    private final String bizType = "order";

    @Bean
    public ILocalTransactionService defaultLocalTransactionService() {
        return new DefaultLocalTransactionService();
    }

    @Bean
    public ITccLogService tccLogService(DataSource dataSource) {
        return new TccLogService(dataSource, bizType);
    }

    @Bean
    public TccConfig tccConfig(ILocalTransactionService localTransactionService, ITccLogService tccLogService) {
        return new TccConfig(bizType, localTransactionService, tccLogService);
    }
    @Bean
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        dataSource.setJdbcUrl("jdbc:mysql://localhost:3307/cqrs?allowPublicKeyRetrieval=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true&allowMultiQueries=true");
        return dataSource;
    }

}
