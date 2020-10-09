package com.atguigu.cloud.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author Blaife
 * @description zookeeper 注册中心测试
 * @date 2020/10/8 21:48
 */
@RestController
@Slf4j
public class PaymentZkController {

    @Value("${server.port}")
    private String serverPort;

    @GetMapping(value = "/paymentZk")
    public String paymentZk() {
        return "SpringCloud with zookeeper: " + serverPort + "\t" + UUID.randomUUID().toString();
    }

}
