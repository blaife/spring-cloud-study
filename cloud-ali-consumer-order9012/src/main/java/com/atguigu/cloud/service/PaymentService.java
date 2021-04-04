package com.atguigu.cloud.service;

import com.atguigu.cloud.entities.CommonResult;
import com.atguigu.cloud.entities.Payment;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author Blaife
 * @description TODO
 * @date 2021/4/4 17:19
 */
@FeignClient(value = "nacos-payment-provider", fallback = PaymentFallbackService.class)
@Service
public interface PaymentService {

    @GetMapping("/paymentSql/{id}")
    public CommonResult<Payment> paymentSql(@PathVariable("id") Long id);
}
