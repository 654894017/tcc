package com.damon;

import com.damon.tcc.TccConfig;
import com.damon.tcc.TccTemplateService;
import org.springframework.stereotype.Service;

@Service
public class TestService extends TccTemplateService<Long, TestEntity> {
    private final TestGateway testGateway;

    public TestService(TccConfig config, TestGateway testGateway) {
        super(config);
        this.testGateway = testGateway;
    }

    /**
     * 交由定时器触发
     */
    public void executeFailedCheck() {
        super.executeFailedLogCheck();
    }


    /**
     * 私信消息交由定时器触发或者手动触发
     */
    public void executeDeadCheck() {
        super.executeDeadLogCheck();
    }

    /**
     * 执行业务处理
     *
     * @param id
     * @return
     */
    public Long execute(Long id) {
        try {
            TestEntity testEntity = new TestEntity(id);
            return this.process(testEntity);
        } catch (TestException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected TestEntity callbackParameter(Long bizId) {
        return testGateway.get(bizId);
    }

    @Override
    protected void tryPhase(TestEntity object) {
        //  throw new RuntimeException("tryPhase");
        System.out.println("tryPhase");
    }

    @Override
    protected Long executeLocalTransactionPhase(TestEntity object) {
        // 执行本地事务
        testGateway.save();
        return object.getBizId();
    }

    @Override
    protected void commitPhase(TestEntity object) {
        System.out.println("commitPhase");
    }

    @Override
    protected void cancelPhase(TestEntity object) {
        System.out.println("cancelPhase");
    }

}
