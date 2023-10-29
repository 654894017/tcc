package com.damon;

import com.damon.tcc.TccConfig;
import com.damon.tcc.TccTemplateService;

public class TestService extends TccTemplateService<Long, Test> {

    public TestService(TccConfig config) {
        super(config);
    }


    public void executeCheck() {
       super.executeCheck(bizId -> {
           return new Test(bizId);
       });
    }

    public Long execute(Long id) {
        Test test = new Test(id);
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
