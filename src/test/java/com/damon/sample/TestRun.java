package com.damon.sample;

import com.damon.Application;
import com.damon.sample.order.OrderSubmitService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class TestRun {
    @Autowired
    private OrderSubmitService orderSubmitService;

    @Test
    public void test() throws InterruptedException {
        orderSubmitService.execute(12345678L, 100L);
        Thread.sleep(2000);
    }

    @Test
    public void testTryFailed() throws InterruptedException {
        //不存在的用户id
        orderSubmitService.execute(12345679L, 100L);
        Thread.sleep(2000);
    }

    @Test
    public void testFailedLog() throws InterruptedException {
        orderSubmitService.executeFailedLogCheck();
        Thread.sleep(2000);
    }

}
