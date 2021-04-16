package com.atguigu.cloud.controller;

import com.atguigu.cloud.domain.CommonResult;
import com.atguigu.cloud.service.AccountService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @author Blaife
 * @description TODO
 * @date 2021/4/16 23:28
 */
@RestController
@RequestMapping("/account")
public class AccountController {

    @Resource
    private AccountService accountService;

    @RequestMapping("/decrease")
    public CommonResult decrease(Long userId, BigDecimal money) {
        accountService.decrease(userId, money);
        return new CommonResult(200,"扣减余额成功!");
    }

}
