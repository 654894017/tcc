package com.damon.sample.order;

import com.damon.sample.order.app.OrderSubmitAppService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OrderApplication.class)
public class TestRun {
    @Autowired
    private OrderSubmitAppService orderSubmitAppService;

    @Test
    public void test() throws InterruptedException {
        orderSubmitAppService.submitOrder(12345678L, 100L);
        Thread.sleep(2000);
    }

    @Test
    public void testTryFailed() throws InterruptedException {
        try {
            orderSubmitAppService.submitOrder(12345679L, 100L);
            Thread.sleep(2000);
        } catch (Exception e) {
            //需要catch，并睡眠不然线程池来不及调用cancel就被关闭了
            Thread.sleep(2000);
        }
    }

    @Test
    public void testFailedLog() throws InterruptedException {
        orderSubmitAppService.executeFailedLogCheck();
        Thread.sleep(2000);
    }

}
