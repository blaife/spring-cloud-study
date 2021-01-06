package com.atguigu.cloud.service.impl;

import com.atguigu.cloud.service.IMessageProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * @author Blaife
 * @description 不再与controller，dao打交道，就不再需要 @Service
 *      使用Stream的注解 EnableBinding
 * @date 2021/1/6 20:40
 */
@Slf4j
@EnableBinding(Source.class) // 定义消息的推送管道
public class IMessageProviderImpl implements IMessageProvider {

    @Resource
    private MessageChannel output;

    @Override
    public String send() {
        String serial = UUID.randomUUID().toString();
        output.send(MessageBuilder.withPayload(serial).build());
        log.info("*********serial: " + serial);
        return serial;
    }

}
