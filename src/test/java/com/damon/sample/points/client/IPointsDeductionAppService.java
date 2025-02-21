package com.damon.sample.points.client;

public interface IPointsDeductionAppService {
    boolean prepare(PointsDeductCmd parameter);

    void commit(PointsDeductCmd parameter);

    void cancel(PointsDeductCmd parameter);
}
