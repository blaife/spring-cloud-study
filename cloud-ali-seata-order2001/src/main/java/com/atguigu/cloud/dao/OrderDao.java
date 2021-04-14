package com.atguigu.cloud.dao;

import com.atguigu.cloud.domain.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author Blaife
 * @description TODO
 * @date 2021/4/14 19:59
 */
@Mapper
public interface OrderDao {

    /**
     * 新建订单
     * @param order
     */
    void create(Order order);

    /**
     * 修改订单
     * @param userId
     * @param status
     */
    void update(@Param("userId") Long userId, @Param("status") int status);


}
