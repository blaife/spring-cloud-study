package com.atguigu.cloud.controller;

import com.atguigu.cloud.domain.CommonResult;
import com.atguigu.cloud.domain.Order;
import com.atguigu.cloud.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Blaife
 * @description TODO
 * @date 2021/4/14 22:28
 */
@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {

    @Resource
    private OrderService orderService;

    @GetMapping("/create")
    public CommonResult create(Order order) {
        orderService.create(order);
        return new CommonResult(200, "订单创建成功");
    }

}
