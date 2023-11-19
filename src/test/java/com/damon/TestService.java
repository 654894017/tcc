package com.damon;

import com.damon.tcc.TccConfig;
import com.damon.tcc.TccTemplateService;

public class TestService extends TccTemplateService<Long, Test> {
    private final TestGateway testGateway;

    public TestService(TccConfig config, TestGateway testGateway) {
        super(config);
        this.testGateway = testGateway;
    }

    public void executeFailedCheck() {
        super.executeFailedLogCheck();
    }

    public void executeDeadCheck() {
        super.executeDeadLogCheck();
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
    protected Test callbackParameter(Long bizId) {
        return testGateway.get(bizId);
    }

    @Override
    protected void tryPhase(Test object) {
        throw new TestException();
    }

    @Override
    protected Long executeLocalTransactionPhase(Test object) {
        // 执行本地事务
        return object.getBizId();
    }

    @Override
    protected void commitPhase(Test object) {
    }

    @Override
    protected void cancelPhase(Test object) {

    }

}
