package com.damon.sample.order;

import com.damon.sample.order.application.OrderApplicationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = OrderApplication.class)
public class TestRun {
    @Autowired
    private OrderApplicationService orderApplicationService;

    @Test
    public void test() throws InterruptedException {
        orderApplicationService.submitOrder(12345678L, 100L);
        Thread.sleep(2000);
    }

    @Test
    public void testTryFailed() throws InterruptedException {
        try {
            orderApplicationService.submitOrder(12345679L, 100L);
        } catch (Exception e) {
            //需要catch，并睡眠不然线程池来不及调用cancel就被关闭了
            Thread.sleep(2000);
        }
    }

    @Test
    public void testDeadLog() throws InterruptedException {
        orderApplicationService.executeDeadLogCheck();
        Thread.sleep(2000);
    }

    @Test
    public void testFailedLog() throws InterruptedException {
        orderApplicationService.executeFailedLogCheck();
        Thread.sleep(2000);
    }

}
