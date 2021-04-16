package com.atguigu.cloud.service;

import java.math.BigDecimal;

/**
 * @author Blaife
 * @description TODO
 * @date 2021/4/16 23:25
 */
public interface AccountService {

    void decrease(Long userId, BigDecimal count);

}
