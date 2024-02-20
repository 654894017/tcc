package com.damon.sample.order.infra.gateway;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.damon.sample.order.domain.IPointsGateway;
import org.springframework.stereotype.Component;

@Component
public class PointsGateway implements IPointsGateway {
    @Override
    public boolean tryDeductionPoints(Long orderId, Long userId, Long deductionPoints) {
        HttpResponse httpResponse = HttpRequest.post("http://localhost:9898/points/try_deduction")
                .timeout(5000)
                .body("{\"order_id\":" + orderId + ",   \"user_id\":" + userId + ",   \"deduction_points\":" + deductionPoints + "}")
                .execute();
        if (httpResponse.getStatus() != 200) {
            throw new RuntimeException("tryDeductionPoints failed , status : " + httpResponse.body());
        }
        return true;
    }

    @Override
    public boolean commitDeductionPoints(Long orderId, Long userId, Long deductionPoints) {
        HttpResponse httpResponse = HttpRequest.post("http://localhost:9898/points/commit_deduction")
                .timeout(5000)
                .body("{\"order_id\":" + orderId + ",   \"user_id\":" + userId + ",   \"deduction_points\":" + deductionPoints + "}")
                .execute();
        if (httpResponse.getStatus() != 200) {
            throw new RuntimeException("commitDeductionPoints failed , status : " + httpResponse.body());
        }
        return true;
    }

    @Override
    public boolean cancelDeductionPoints(Long orderId, Long userId, Long deductionPoints) {
        HttpResponse httpResponse = HttpRequest.post("http://localhost:9898/points/cancel_deduction")
                .timeout(50000)
                .body("{\"order_id\":" + orderId + ",   \"user_id\":" + userId + ",   \"deduction_points\":" + deductionPoints + "}")
                .execute();

        if (httpResponse.getStatus() != 200) {
            throw new RuntimeException("cancelDeductionPoints failed , status : " + httpResponse.body());
        }
        return true;
    }

}
