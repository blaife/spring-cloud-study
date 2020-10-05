package com.atguigu.springcloud.service;

import com.atguigu.springcloud.entities.Payment;
import org.apache.ibatis.annotations.Param;

/**
 * @author Blaife
 * @description 支付service
 * @date 2020/10/5 12:47
 */
public interface PaymentService {

    /**
     * 创建
     * @param payment 支付实体
     * @return id
     */
    public int create(Payment payment);

    /**
     * 根据id查询
     * @param id id
     * @return 支付实体
     */
    public Payment getPaymentById(@Param("id") Long id);

}
