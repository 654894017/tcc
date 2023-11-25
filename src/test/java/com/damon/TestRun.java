package com.damon;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class TestRun {
    @Autowired
    private TestService testService;

    @Test
    public void testAddPoints() {
        testService.execute(1L);
    }

}
