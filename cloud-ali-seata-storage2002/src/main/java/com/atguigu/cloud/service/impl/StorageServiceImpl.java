package com.atguigu.cloud.service.impl;

import com.atguigu.cloud.dao.StorageDao;
import com.atguigu.cloud.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Blaife
 * @description TODO
 * @date 2021/4/16 22:54
 */
@Service
@Slf4j
public class StorageServiceImpl implements StorageService {

    @Resource
    private StorageDao storageDao;

    @Override
    public void decrease(Long productId, Integer count) {

        System.out.println("storage ---------> 扣减库存开始");
        storageDao.decrease(productId, count);
        System.out.println("storage ---------> 扣减库存结束");

    }
}
