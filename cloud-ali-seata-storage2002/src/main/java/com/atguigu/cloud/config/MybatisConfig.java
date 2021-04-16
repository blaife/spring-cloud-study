package com.atguigu.cloud.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Blaife
 * @description TODO
 * @date 2021/4/14 22:32
 */
@Configuration
@MapperScan({"com.atguigu.cloud.dao"})
public class MybatisConfig {
}
