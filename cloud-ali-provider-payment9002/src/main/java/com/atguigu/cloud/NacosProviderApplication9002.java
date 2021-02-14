package com.atguigu.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Blaife
 * @description TODO
 * @date 2021/2/14 16:12
 */
@SpringBootApplication
@EnableDiscoveryClient
public class NacosProviderApplication9002 {

    public static void main(String[] args) {
        SpringApplication.run(NacosProviderApplication9002.class, args);
    }

}
