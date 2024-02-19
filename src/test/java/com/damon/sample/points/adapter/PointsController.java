package com.damon.sample.points.adapter;

import com.damon.sample.points.app.PointsDeductionAppService;
import com.damon.sample.points.client.PointsDeductCmd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("points")
public class PointsController {
    @Autowired
    private PointsDeductionAppService pointsDeductionAppService;

    @PostMapping("try_deduction")
    public Boolean deductionTry(@RequestBody PointsDeductCmd cmd) {
        return pointsDeductionAppService.attempt(cmd);
    }

    @PostMapping("commit_deduction")
    public Boolean deductionCommit(@RequestBody PointsDeductCmd cmd) {
        pointsDeductionAppService.commit(cmd);
        return true;
    }


    @PostMapping("cancel_deduction")
    public Boolean deductionCancel(@RequestBody PointsDeductCmd cmd) {
        pointsDeductionAppService.cancel(cmd);
        return true;
    }
}
