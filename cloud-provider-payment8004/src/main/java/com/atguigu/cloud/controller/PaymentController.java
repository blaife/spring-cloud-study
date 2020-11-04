package com.atguigu.cloud.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author Blaife
 * @description TODO
 * @date 2020/11/4 23:24
 */
@RestController
@Slf4j
public class PaymentController {

    @Value("${server.port}")
    private String serverPort;

    @GetMapping(value = "/paymentConsul")
    public String paymentConsul() {
        return "SpringCloud with consul: " + serverPort + "\t" + UUID.randomUUID().toString();
    }
}
