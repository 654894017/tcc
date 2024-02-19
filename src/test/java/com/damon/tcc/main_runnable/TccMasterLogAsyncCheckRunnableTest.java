package com.damon.tcc.main_runnable;

import com.damon.sample.order.domain.Order;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TccMasterLogAsyncCheckRunnableTest {

    @Before
    public void setUp() throws Exception {
        TccMasterLogAsyncCheckRunnable checkRunnable = new TccMasterLogAsyncCheckRunnable<Order>(null, null, null, null, null);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void run() {
    }

    @Test
    public void handleException() {
    }

    @Test
    public void check() {
    }
}