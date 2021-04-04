package com.atguigu.cloud.service;

import com.atguigu.cloud.entities.CommonResult;
import com.atguigu.cloud.entities.Payment;
import org.springframework.stereotype.Component;

/**
 * @author Blaife
 * @description TODO
 * @date 2021/4/4 17:22
 */

@Component
public class PaymentFallbackService implements PaymentService {
    @Override
    public CommonResult<Payment> paymentSql(Long id) {
        return new CommonResult<>(444, "服务降级返回，---PaymentFallbackService", new Payment(id, "errorSerial"));
    }
}
