package com.atguigu.cloud.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Blaife
 * @description 支付实体
 * @date 2020/10/5 12:00
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 流水号
     */
    private String serial;

}
