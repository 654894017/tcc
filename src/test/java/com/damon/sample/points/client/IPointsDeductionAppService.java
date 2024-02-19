package com.damon.sample.points.client;

public interface IPointsDeductionAppService {
    boolean attempt(PointsDeductCmd parameter);

    void commit(PointsDeductCmd parameter);

    void cancel(PointsDeductCmd parameter);
}
