package com.atguigu.cloud.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author Blaife
 * @description 连接工具
 * @date 2020/10/5 17:08
 */
@Configuration
public class ApplicationContextConfig {

    /**
     * Rest请求交互对象
     *      LoadBalanced：赋予RestTemplate负载均衡的能力
     * @return RestTemplate
     */
    @Bean
    // @LoadBalanced
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

}
