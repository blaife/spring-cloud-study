package com.atguigu.cloud.controller;

import com.atguigu.cloud.entities.CommonResult;
import com.atguigu.cloud.entities.Payment;
import com.atguigu.cloud.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author Blaife
 * @description 支付 controller
 * @date 2020/10/5 12:51
 */
@RestController
@Slf4j
@RequestMapping("/payment")
public class PaymentController {

    @Resource
    private PaymentService paymentService;

    @Value("${server.port}")
    private String serverPort;


    @PostMapping(value = "/create")
    public CommonResult<Long> create(@RequestBody Payment payment) {
        int result = paymentService.create(payment);
        log.info("--------插入成功：" + result);
        if (result > 0) {
            return new CommonResult<Long>(200, "插入数据库成功, serverPort: " + serverPort, payment.getId());
        } else {
            return new CommonResult<Long>(444, "插入数据库失败, serverPort: " + serverPort, null);
        }
    }

    @GetMapping(value = "/get/{id}")
    public CommonResult<Payment> getPaymentById(@PathVariable("id") Long id) {
        Payment result = paymentService.getPaymentById(id);
        log.info("--------查询成功：" + result);
        if (result != null) {
            return new CommonResult<Payment>(200, "查询成功, serverPort: " + serverPort, result);
        } else {
            return new CommonResult<Payment>(444, "没有对应记录，查询ID" + id + ", serverPort: " + serverPort, null);
        }
    }

    /**
     * 获取 LB (LoadBalance)
     * @return
     */
    @GetMapping(value = "/lb")
    public String getPaymentLB() {
        return serverPort;
    }

    /**
     * 测试feign超时控制
     * @return
     */
    @GetMapping(value = "/paymentFeignTimeout")
    public String paymentFeignTimeout() {
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return serverPort;
    }

}
