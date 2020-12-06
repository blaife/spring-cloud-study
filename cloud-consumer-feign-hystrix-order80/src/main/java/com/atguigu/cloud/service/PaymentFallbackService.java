package com.atguigu.cloud.service;

import org.springframework.stereotype.Component;

/**
 * @author Blaife
 * @description fallback
 * @date 2020/11/29 15:25
 */
@Component
public class PaymentFallbackService implements PaymentHystrixService {
    @Override
    public String paymentInfo_OK(Integer id) {
        return "-----PaymentFallbackService fall back paymentInfo_OK, /(ㄒoㄒ)/~~";
    }

    @Override
    public String paymentInfo_TimeOut(Integer id) {
        return "-----PaymentFallbackService fall back paymentInfo_TimeOut, /(ㄒoㄒ)/~~";
    }
}
