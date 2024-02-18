package com.damon.sample.order;

import com.damon.tcc.TccMainConfig;
import com.damon.tcc.transaction.DefaultLocalTransactionService;
import com.damon.tcc.transaction.ILocalTransactionService;
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

}
