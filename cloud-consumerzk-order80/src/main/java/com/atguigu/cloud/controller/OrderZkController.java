package com.atguigu.cloud.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * @author Blaife
 * @description TODO
 * @date 2020/10/9 22:29
 */
@RestController
@Slf4j
public class OrderZkController {

    private static final String INVOKE_URL = "http://cloud-provider-payment";

    @Resource
    private RestTemplate restTemplate;

    @GetMapping(value = "/paymentInfo")
    public String paymentInfo() {
        return restTemplate.getForObject(INVOKE_URL + "/paymentZk", String.class);
    }

}
