package com.atguigu.springcloud.controller;

import com.atguigu.springcloud.entities.CommonResult;
import com.atguigu.springcloud.entities.Payment;
import com.atguigu.springcloud.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;

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


    @PostMapping(value = "/create")
    public CommonResult create(Payment payment) {
        int result = paymentService.create(payment);
        log.info("--------插入成功：" + result);
        if (result > 0) {
            return new CommonResult(200, "插入数据库成功", result);
        } else {
            return new CommonResult<String>(444, "插入数据库失败");
        }
    }

    @GetMapping(value = "/get/{id}")
    public CommonResult getPaymentById(@PathVariable("id") Long id) {
        Payment result = paymentService.getPaymentById(id);
        log.info("--------查询成功：" + result);
        if (result != null) {
            return new CommonResult(200, "查询成功", result);
        } else {
            return new CommonResult<String>(444, "没有对应记录，查询ID" + id);
        }
    }

}
