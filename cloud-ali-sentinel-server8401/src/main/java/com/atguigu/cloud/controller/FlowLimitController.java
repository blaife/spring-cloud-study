package com.atguigu.cloud.controller;

import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author Blaife
 * @description TODO
 * @date 2021/3/13 16:41
 */
@RestController
@RequestMapping("/sentinel")
public class FlowLimitController {

    @GetMapping("/testA")
    public String testA() {
        try {
            TimeUnit.MILLISECONDS.sleep(800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return Thread.currentThread().getName() + ";A------------";
    }
    @GetMapping("/testB")
    public String testB() {
        return "B------------";
    }
}
