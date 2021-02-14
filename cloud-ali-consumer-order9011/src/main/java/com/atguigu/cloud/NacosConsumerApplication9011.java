package com.atguigu.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author Blaife
 * @description TODO
 * @date 2021/2/14 16:12
 */
@SpringBootApplication
@EnableDiscoveryClient
public class NacosConsumerApplication9011 {

    public static void main(String[] args) {
        SpringApplication.run(NacosConsumerApplication9011.class, args);
    }

}
