package com.atguigu.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author Blaife
 * @description TODO
 * @date 2021/3/13 16:39
 */
@SpringBootApplication
@EnableDiscoveryClient
public class SentinelServerApplication8401 {
    public static void main(String[] args) {
        SpringApplication.run(SentinelServerApplication8401.class, args);
    }
}
