package com.atguigu.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author Blaife
 * @description TODO
 * @date 2021/4/3 18:49
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class AliCloudConsumerApplication9012 {

    public static void main(String[] args) {
        SpringApplication.run(AliCloudConsumerApplication9012.class, args);
    }

}
