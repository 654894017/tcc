package com.damon.sample.points.adapter;

import com.damon.sample.points.application.PointsApplicationService;
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
    private PointsApplicationService pointsApplicationService;

    @PostMapping("try_deduction")
    public void deductionTry(@RequestBody PointsDeductCmd cmd) {
        pointsApplicationService.prepare(cmd);
    }

    @PostMapping("commit_deduction")
    public void deductionCommit(@RequestBody PointsDeductCmd cmd) {
        pointsApplicationService.commit(cmd);
    }

    @PostMapping("cancel_deduction")
    public void deductionCancel(@RequestBody PointsDeductCmd cmd) {
        pointsApplicationService.cancel(cmd);
    }
}
