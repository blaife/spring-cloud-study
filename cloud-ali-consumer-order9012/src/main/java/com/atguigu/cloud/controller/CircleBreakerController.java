package com.atguigu.cloud.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.atguigu.cloud.entities.CommonResult;
import com.atguigu.cloud.entities.Payment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * @author Blaife
 * @description TODO
 * @date 2021/4/3 18:52
 */
@RestController
@RequestMapping("/ribbonTest")
public class CircleBreakerController {

    @Resource
    private RestTemplate restTemplate;

    @Value("${service-url.nacos-user-service}")
    private String serverUrl;

    /**
     * getPayment
     * @param id id
     * @return CommonResult
     */
    @GetMapping("/getPayment/{id}")
    // @SentinelResource(value = "getPayment") // 什么都没有配置
    // @SentinelResource(value = "getPayment", fallback = "getPaymentFallback") // fallback只负责业务异常
    // @SentinelResource(value = "getPayment", blockHandler = "getPaymentBlockHandler") // blockHandler只负责Sentinel控制台配置违规
    // @SentinelResource(value = "getPayment", fallback = "getPaymentFallback", blockHandler = "getPaymentBlockHandler") // blockHandler只负责Sentinel控制台配置违规
    @SentinelResource(value = "getPayment", fallback = "getPaymentFallback", blockHandler = "getPaymentBlockHandler",
            exceptionsToIgnore = {IllegalArgumentException.class}) // exceptionsToIgnore
    public CommonResult<Payment> fallback(@PathVariable Long id) {
        CommonResult<Payment> result = restTemplate.getForObject(serverUrl + "/paymentSql/" + id, CommonResult.class, id);
        if (id == 4) {
            throw new IllegalArgumentException("非法参数异常");
        } else {
            assert result != null;
            if (result.getData() == null) {
                throw new NullPointerException("该id没有对应记录，空指针异常");
            }
        }
        return result;
    }

    /**
     * fallback 兜底方法
     * @param id id
     * @param e 异常
     * @return CommonResult
     */
    public CommonResult<Payment> getPaymentFallback(@PathVariable Long id, Throwable e) {
        Payment payment = new Payment(id, null);
        return new CommonResult<>(444, "兜底异常fallback,异常内容：" + e.getMessage(), payment);
    }

    /**
     * blockHandler 兜底方法
     * @param id id
     * @param e 异常
     * @return CommonResult
     */
    public CommonResult<Payment> getPaymentBlockHandler(@PathVariable Long id, BlockException e) {
        Payment payment = new Payment(id, null);
        return new CommonResult<>(445, "兜底异常blockHandler,异常内容：" + e.getMessage(), payment);
    }


}
