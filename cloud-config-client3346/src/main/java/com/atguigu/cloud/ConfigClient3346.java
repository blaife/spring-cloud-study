package com.atguigu.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @author Blaife
 * @description TODO
 * @date 2020/12/26 19:07
 */
@SpringBootApplication
@EnableEurekaClient
public class ConfigClient3346 {
    public static void main(String[] args) {
        SpringApplication.run(ConfigClient3346.class, args);
    }
}
