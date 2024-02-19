package com.damon.sample.points.infra.config;

import com.damon.tcc.TccSubConfig;
import com.damon.tcc.sub_log.TccSubLogService;
import com.damon.tcc.transaction.DefaultLocalTransactionService;
import com.damon.tcc.transaction.ILocalTransactionService;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class TccPointsSubConfig {
    private final String bizType = "order";

    @Bean("pointsDefaultLocalTransactionService")
    public DefaultLocalTransactionService pointsDefaultLocalTransactionService() {
        return new DefaultLocalTransactionService();
    }

    @Bean
    public TccSubConfig tccSubConfig(@Qualifier("pointsDefaultLocalTransactionService") ILocalTransactionService localTransactionService,
                                     DataSource datasource) {
        return new TccSubConfig(new TccSubLogService(datasource, bizType), localTransactionService, datasource, bizType);
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
