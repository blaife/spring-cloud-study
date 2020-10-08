package com.atguigu.cloud.controller;

import com.atguigu.cloud.entities.CommonResult;
import com.atguigu.cloud.entities.Payment;
import com.atguigu.cloud.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Blaife
 * @description 支付 controller
 * @date 2020/10/5 12:51
 */
@RestController
@Slf4j
@RequestMapping("/payment")
public class PaymentController {

    @Resource
    private PaymentService paymentService;

    @Resource
    private DiscoveryClient discoveryClient;

    @Value("${server.port}")
    private String serverPort;


    /**
     * 添加方法
     * @param payment 支付实体
     * @return 成功的id号
     */
    @PostMapping(value = "/create")
    public CommonResult<Long> create(@RequestBody Payment payment) {
        int result = paymentService.create(payment);
        log.info("--------插入成功：" + result);
        if (result > 0) {
            return new CommonResult<Long>(200, "插入数据库成功, serverPort: " + serverPort, payment.getId());
        } else {
            return new CommonResult<Long>(444, "插入数据库失败, serverPort: " + serverPort, null);
        }
    }

    /**
     * 根据id获取一条支付记录
     * @param id id
     * @return 支付实体
     */
    @GetMapping(value = "/get/{id}")
    public CommonResult<Payment> getPaymentById(@PathVariable("id") Long id) {
        Payment result = paymentService.getPaymentById(id);
        log.info("--------查询成功：" + result);
        if (result != null) {
            return new CommonResult<Payment>(200, "查询成功, serverPort: " + serverPort, result);
        } else {
            return new CommonResult<Payment>(444, "没有对应记录，查询ID" + id + ", serverPort: " + serverPort, null);
        }
    }

    /**
     * 获取服务具体信息
     * @return DiscoveryClient 对象
     */
    @GetMapping(value = "/discovery")
    public Object discovery() {
        List<String> services = discoveryClient.getServices();
        for (String element : services) {
            log.info("*********:" + element);
        }
        List<ServiceInstance> instances = discoveryClient.getInstances("CLOUD-PAYMENT-SERVICE");
        for (ServiceInstance instance : instances) {
            log.info(instance.getServiceId() + "\t" + instance.getHost() +"\t" + instance.getPort() +"\t" + instance.getUri());
        }
        return this.discoveryClient;
    }
}
