package com.atguigu.cloud.myHandler;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.atguigu.cloud.entities.CommonResult;
import com.atguigu.cloud.entities.Payment;

/**
 * @author Blaife
 * @description 自定义限流处理类
 * @date 2021/3/28 22:14
 */
public class CustomerBlockHandler {
    public static CommonResult handlerException(BlockException exception) {
        return new CommonResult(444, "按客户自定义,global handlerException -------- 1" );
    }

    public static CommonResult handlerException2(BlockException exception) {
        return new CommonResult(444, "按客户自定义,global handlerException -------- 2" );
    }
}
