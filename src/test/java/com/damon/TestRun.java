package com.damon;

import cn.hutool.core.util.IdUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class TestRun {
    @Autowired
    private TestMainService testMainService;

    @Test
    public void testAddPoints() {
        Long start = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            Long id = testMainService.execute(IdUtil.getSnowflakeNextId());
        }
        Long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

}
