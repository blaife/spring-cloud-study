package com.atguigu.cloud.service.impl;

import com.atguigu.cloud.dao.OrderDao;
import com.atguigu.cloud.domain.Order;
import com.atguigu.cloud.service.AccountService;
import com.atguigu.cloud.service.OrderService;
import com.atguigu.cloud.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Blaife
 * @description TODO
 * @date 2021/4/14 22:11
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderDao orderDao;

    @Resource
    private AccountService accountService;

    @Resource
    private StorageService storageService;

    @Override
    public void create(Order order) {
        // 1.创建订单
        log.info("-----------> 开始新建订单");
        orderDao.create(order);

        // 2. 扣减库存
        log.info("-----------> 订单微服务开始调用库存服务，做扣减 start");
        storageService.decrease(order.getProductId(), order.getCount());
        log.info("-----------> 订单微服务开始调用库存服务，做扣减 end");

        // 3. 扣减账户余额
        log.info("-----------> 订单微服务开始调用账户服务，做扣减 start");
        accountService.decrease(order.getUserId(), order.getMoney());
        log.info("-----------> 订单微服务开始调用账户服务，做扣减 end");

        // 4. 修改订单状态
        log.info("-----------> 修改订单状态开始");
        orderDao.update(order.getUserId(), 0);
        log.info("-----------> 修改订单状态结束");

        log.info("-----------> 下订单结束了");

    }
}
