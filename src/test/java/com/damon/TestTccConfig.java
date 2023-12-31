package com.damon;

import com.damon.tcc.TccMainConfig;
import com.damon.tcc.main_log.ITccMainLogService;
import com.damon.tcc.main_log.TccMainLogService;
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
    public ITccMainLogService tccLogService(DataSource dataSource) {
        return new TccMainLogService(dataSource, bizType);
    }

    @Bean
    public TccMainConfig tccConfig(ILocalTransactionService localTransactionService, ITccMainLogService tccLogService) {
        return new TccMainConfig(bizType, localTransactionService, tccLogService);
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
