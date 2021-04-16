package com.atguigu.cloud.service.impl;

import com.atguigu.cloud.dao.AccountDao;
import com.atguigu.cloud.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * @author Blaife
 * @description TODO
 * @date 2021/4/16 22:54
 */
@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

    @Resource
    private AccountDao accountDao;

    @Override
    public void decrease(Long userId, BigDecimal money) {

        System.out.println("account ---------> 扣减余额开始");
        try {
            TimeUnit.SECONDS.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        accountDao.decrease(userId, money);
        System.out.println("account ---------> 扣减余额结束");

    }
}
