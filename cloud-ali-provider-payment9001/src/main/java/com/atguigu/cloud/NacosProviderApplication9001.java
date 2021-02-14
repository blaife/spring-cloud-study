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
public class NacosProviderApplication9001 {

    public static void main(String[] args) {
        SpringApplication.run(NacosProviderApplication9001.class, args);
    }

}
