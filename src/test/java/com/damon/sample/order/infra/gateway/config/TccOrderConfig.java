package com.damon.sample.order.infra.gateway.config;

import com.damon.tcc.config.TccMainConfig;
import com.damon.tcc.local_transaction.DefaultLocalTransactionService;
import com.damon.tcc.local_transaction.ILocalTransactionService;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class TccOrderConfig {
    private final String bizType = "order";

    @Bean("orderDefaultLocalTransactionService")
    public ILocalTransactionService orderDefaultLocalTransactionService() {
        return new DefaultLocalTransactionService();
    }

    @Bean
    public TccMainConfig tccConfig(@Qualifier("orderDefaultLocalTransactionService") ILocalTransactionService localTransactionService,
                                   DataSource dataSource) {
        return new TccMainConfig(bizType, localTransactionService, dataSource, 100,
                100, 1024 * 10, 100,
                100, 1024 * 10, 5, 100);
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
