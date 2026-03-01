package com.damon.sample.points.application;

import com.damon.sample.points.application.executer.PointsDeductionExecuter;
import com.damon.sample.points.client.IPointsDeductionAppService;
import com.damon.sample.points.client.PointsDeductCmd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PointsApplicationService implements IPointsDeductionAppService {
    private final Logger log = LoggerFactory.getLogger(PointsApplicationService.class);

    private final PointsDeductionExecuter pointsDeductionExecuter;

    public PointsApplicationService(PointsDeductionExecuter pointsDeductionExecuter) {
        this.pointsDeductionExecuter = pointsDeductionExecuter;
    }

    /**
     * try执行积分扣减
     *
     * @param parameter
     * @return
     */
    @Override
    public boolean prepare(PointsDeductCmd parameter) {
        return pointsDeductionExecuter.prepare(parameter);
    }

    /**
     * commit提交积分扣减
     *
     * @param parameter
     */
    @Override
    public void commit(PointsDeductCmd parameter) {
        pointsDeductionExecuter.commit(parameter);
    }

    /**
     * cancel回顾积分扣减
     *
     * @param parameter
     */
    @Override
    public void cancel(PointsDeductCmd parameter) {
        pointsDeductionExecuter.cancel(parameter);
    }


}
