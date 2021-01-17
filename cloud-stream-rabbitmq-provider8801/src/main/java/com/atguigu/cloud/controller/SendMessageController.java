package com.atguigu.cloud.controller;

import com.atguigu.cloud.service.IMessageProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Blaife
 * @description TODO
 * @date 2021/1/6 20:59
 */
@RestController
public class SendMessageController {

    @Resource
    private IMessageProvider messageProvider    ;

    @GetMapping(value = "/sendMessage")
    public String sendMessage() {
        return messageProvider.send();
    }
}
