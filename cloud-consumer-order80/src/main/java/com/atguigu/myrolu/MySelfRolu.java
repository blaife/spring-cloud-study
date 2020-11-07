package com.atguigu.myrolu;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Blaife
 * @description 自定义Ribbon规则
 * @date 2020/11/7 17:37
 */
@Configuration
public class MySelfRolu {

    @Bean
    public IRule myRule () {
        return new RandomRule();
    }

}
