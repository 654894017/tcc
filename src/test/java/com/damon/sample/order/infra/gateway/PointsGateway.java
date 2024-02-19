package com.damon.sample.order.infra.gateway;

import cn.hutool.http.HttpUtil;
import com.damon.sample.order.domain.IPointsGateway;
import org.springframework.stereotype.Component;

@Component
public class PointsGateway implements IPointsGateway {
    @Override
    public boolean tryDeductionPoints(Long orderId, Long userId, Long deductionPoints) {

        String response = HttpUtil.post("http://localhost:9898/points/try_deduction", "{\"order_id\":" + orderId + ",   \"user_id\":" + userId + ",   \"deduction_points\":" + deductionPoints + "}", 5000);
        return true;
    }

    @Override
    public boolean commitDeductionPoints(Long orderId, Long userId, Long deductionPoints) {

        String response = HttpUtil.post("http://localhost:9898/points/commit_deduction", "{\"order_id\":" + orderId + ",   \"user_id\":" + userId + ",   \"deduction_points\":" + deductionPoints + "}", 5000);
        return true;
    }

    @Override
    public boolean cancelDeductionPoints(Long orderId, Long userId, Long deductionPoints) {

        String response = HttpUtil.post("http://localhost:9898/points/cancel_deduction", "{\"order_id\":" + orderId + ",   \"user_id\":" + userId + ",   \"deduction_points\":" + deductionPoints + "}", 5000);
        return true;
    }

}
