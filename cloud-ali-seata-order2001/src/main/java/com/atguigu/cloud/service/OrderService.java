package com.atguigu.cloud.service;

import com.atguigu.cloud.domain.Order;
import org.springframework.stereotype.Service;

/**
 * @author Blaife
 * @description TODO
 * @date 2021/4/14 22:10
 */
public interface OrderService {
    void create(Order order);
}
