package com.damon;

import com.damon.tcc.TccSubConfig;
import com.damon.tcc.TccSubTemplateService;

//@Service
public class TestSubService extends TccSubTemplateService<Long, TestEntity> {
    private final TestGateway testGateway;

    public TestSubService(TccSubConfig config, TestGateway testGateway) {
        super(config);
        this.testGateway = testGateway;
    }

    public Long executeTry() {
        TestEntity entity = new TestEntity(1l);
        return processTry(entity);
    }

    public void executeCommit(TestEntity entity) {
        processCommit(entity);
    }

    public void executeCancel(TestEntity entity) {
        processCancel(entity);
    }

    @Override
    protected Long tryPhase(TestEntity command) {
        return null;
    }

    @Override
    protected void commitPhase(TestEntity command) {

    }

    @Override
    protected void cancelPhase(TestEntity command) {

    }
}
