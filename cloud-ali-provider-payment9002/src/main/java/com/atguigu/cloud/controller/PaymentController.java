package com.atguigu.cloud.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Blaife
 * @description TODO
 * @date 2021/2/14 16:20
 */
@RestController
@RequestMapping("/nacos/provider")
public class PaymentController {

    @Value("${server.port}")
    private String port;

    @GetMapping(value = "/getId/{id}")
    public String getPayment(@PathVariable("id") Integer id) {
        return "nacos registry, serverport : " + port + "\t" + id;
    }

}
