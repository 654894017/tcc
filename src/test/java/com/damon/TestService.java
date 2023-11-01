package com.damon;

import com.damon.tcc.TccConfig;
import com.damon.tcc.TccTemplateService;

public class TestService extends TccTemplateService<Long, Test> {
    private final TestRepository testRepository;

    public TestService(TccConfig config, TestRepository testRepository) {
        super(config);
        this.testRepository = testRepository;
    }

    public void executeCheck() {
        super.executeCheck(bizId -> testRepository.get(bizId));
    }

    public Long execute(Long id) {
        try {
            Test test = new Test(id);
            return super.process(test);
        } catch (TestException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void tryPhase(Test object) {
        throw new TestException();
    }

    @Override
    protected Long executeLocalTransactionPhase(Test object) {
        return object.getBizId();
    }

    @Override
    protected void commitPhase(Test object) {

    }

    @Override
    protected void cancelPhase(Test object) {

    }
}
