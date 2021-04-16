package com.atguigu.cloud.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * @author Blaife
 * @description TODO
 * @date 2021/4/16 23:17
 */
@Mapper
public interface AccountDao {

    void decrease(@Param("userId") Long userId,@Param("money") BigDecimal money);

}
