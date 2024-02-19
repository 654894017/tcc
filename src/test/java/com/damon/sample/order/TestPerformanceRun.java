package com.damon.sample.order;

import com.damon.sample.order.app.OrderSubmitAppService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OrderApplication.class)
public class TestPerformanceRun {
    @Autowired
    private OrderSubmitAppService orderSubmitAppService;

    @Test
    public void test() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        for (int i = 0; i < 20; i++) {
            executorService.submit(() -> {
                for (int j = 0; j < 100000; j++) {
                    orderSubmitAppService.submitOrder(12345678L, 100L);
                }
            });
        }

        Thread.sleep(20000000);
    }

    @Test
    public void testTryFailed() throws InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(100);
        for (int i = 0; i < 100; i++) {
            executorService.submit(() -> {
                for (int j = 0; j < 10000; j++) {
                    orderSubmitAppService.submitOrder(12345679L, 100L);
                }
            });
        }


        Thread.sleep(20000000);
    }

    @Test
    public void testFailedLog() throws InterruptedException {
        orderSubmitAppService.executeFailedLogCheck();
        Thread.sleep(222222);
    }

}
