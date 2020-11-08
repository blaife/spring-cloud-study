package com.atguigu.cloud.lb;


import org.springframework.cloud.client.ServiceInstance;
import java.util.List;

/**
 * @author Blaife
 * @description 手写负载算法
 * @date 2020/11/8 12:04
 */
public interface LoadBalancer {

    ServiceInstance instance(List<ServiceInstance> serviceInstances);


}
