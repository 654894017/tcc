package com.damon.sample.order;

import com.damon.sample.order.app.OrderSubmitAppService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = OrderApplication.class)
public class TestPerformanceRun {
    @Autowired
    private OrderSubmitAppService orderSubmitAppService;

    @Test
    public void test() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(200 * 800);
        ExecutorService executorService = Executors.newFixedThreadPool(200);
        long start = System.currentTimeMillis();
        for (int i = 0; i < 200; i++) {
            executorService.submit(() -> {
                for (int j = 0; j < 800; j++) {
                    try{
                        orderSubmitAppService.submitOrder(12345678L, 100L);
                    }finally {
                        countDownLatch.countDown();
                    }
                }
            });
        }
        countDownLatch.await();
        System.out.println("耗时：" + (System.currentTimeMillis() - start));
    }
}
