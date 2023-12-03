package com.damon;

import com.damon.tcc.main_log.TccMainLog;
import org.junit.Test;

public class TestTccMainLog {
    @Test
    public void test() {
        TccMainLog tccMainLog = new TccMainLog(1L);
        tccMainLog.commitLocal();
        System.out.println(tccMainLog);
        tccMainLog.commit();
        System.out.println(tccMainLog);
        tccMainLog.resetLastVersion();
        System.out.println(tccMainLog);
        tccMainLog.resetLastVersion();
        System.out.println(tccMainLog);
    }
}
