package com.atguigu.springcloud.dao;

import com.atguigu.springcloud.entities.Payment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author Blaife
 * @description 支付dao
 * @date 2020/10/5 12:11
 */
@Mapper
public interface PaymentDao {

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
