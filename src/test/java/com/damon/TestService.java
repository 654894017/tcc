package com.damon;

import com.damon.tcc.TccConfig;
import com.damon.tcc.TccFailedLogIterator;
import com.damon.tcc.TccTemplateService;
import com.damon.tcc.log.TccLog;

import java.util.List;

public class TestService extends TccTemplateService<Long, Test> {

    public TestService(TccConfig config) {
        super(config);
    }

    public void executeCheck() {
        TccFailedLogIterator iterator = super.queryFailedLogs(5, 100);
        while (iterator.hasNext()) {
            List<TccLog> tccLogList = iterator.next();
            tccLogList.forEach(log->{
                Test test = new Test();
                super.check(test);
            });
        }
    }

    public Long executeSubmit() {
        Test test = new Test();
        try{
            return super.process(test);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void tryPhase(Test object) {

    }

    @Override
    protected Long executeLocalTransactionPhase(Test object) {
        return null;
    }

    @Override
    protected void commitPhase(Test object) {

    }

    @Override
    protected void cancelPhase(Test object) {

    }
}
