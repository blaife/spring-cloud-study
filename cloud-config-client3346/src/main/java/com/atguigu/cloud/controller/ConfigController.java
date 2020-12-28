package com.atguigu.cloud.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Blaife
 * @description TODO
 * @date 2020/12/26 19:13
 */
@RestController
@RefreshScope
public class ConfigController {

    @Value("${word}")
    private String word;

    @Value("${server.port}")
    private String serverPort;

    @GetMapping("/getWord")
    public String getWord() {
        return "serverPort: " + serverPort + "; word: " + word;
    }

}
