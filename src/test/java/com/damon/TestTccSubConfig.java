package com.damon;

import com.damon.tcc.TccSubConfig;
import com.damon.tcc.sub_log.ITccSubLogService;
import com.damon.tcc.sub_log.TccSubLogService;
import com.damon.tcc.transaction.DefaultLocalTransactionService;
import com.damon.tcc.transaction.ILocalTransactionService;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

//@Configuration
public class TestTccSubConfig {
    private final String bizType = "order";

    // @Bean
    public ILocalTransactionService defaultLocalTransactionService() {
        return new DefaultLocalTransactionService();
    }

    ///@Bean
    public ITccSubLogService tccLogService(DataSource dataSource) {
        return new TccSubLogService(dataSource, bizType);
    }

    // @Bean
    public TccSubConfig tccConfig(ILocalTransactionService localTransactionService, ITccSubLogService tccSubLogService) {
        return new TccSubConfig(tccSubLogService, localTransactionService, bizType);
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
