package com.damon.sample.points.infra.config;

import cn.hutool.core.util.IdUtil;
import com.damon.tcc.config.TccSubConfig;
import com.damon.tcc.local_transaction.DefaultLocalTransactionService;
import com.damon.tcc.local_transaction.ILocalTransactionService;
import com.damon.tcc.sub_log.TccSubLogService;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class TccPointsSubConfig {
    private final String bizType = "order";

    @Bean
    public static DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        dataSource.setMaximumPoolSize(25);
        dataSource.setMinimumIdle(25);
        dataSource.setJdbcUrl("jdbc:mysql://localhost:3307/cqrs?allowPublicKeyRetrieval=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true&allowMultiQueries=true");
        return dataSource;
    }

    public static void main(String[] args) throws InterruptedException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource());
        ExecutorService service = Executors.newFixedThreadPool(20);
        CountDownLatch countDownLatch = new CountDownLatch(20 * 100000);
        long start = System.currentTimeMillis();
        for (int i = 0; i < 20; i++) {
            service.submit(() -> {
                for (int j = 0; j < 100000; j++) {
                    jdbcTemplate.update("INSERT INTO `tcc_demo_points_changing_log` (`biz_id`, `user_id`, `change_points`, `change_type`, `status`) VALUES (" + IdUtil.getSnowflakeNextId() + ", 12345678, 100, 1, 1)");
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        System.out.println(System.currentTimeMillis() - start);
        System.out.println(1);
    }

    @Bean("pointsDefaultLocalTransactionService")
    public DefaultLocalTransactionService pointsDefaultLocalTransactionService() {
        return new DefaultLocalTransactionService();
    }

    @Bean
    public TccSubConfig tccSubConfig(@Qualifier("pointsDefaultLocalTransactionService") ILocalTransactionService localTransactionService,
                                     DataSource datasource) {
        return new TccSubConfig(new TccSubLogService(datasource, bizType), localTransactionService, datasource, bizType);
    }


}
