# 服务调用

## Ribbon

LB(`LoadBalance`) 
    - 集中式： 服务端 nginx
    - 进程内： 客户端 Ribbon

Ribbon 本质上是负载规则 + RestTemplate的调用。

eureka-client 依赖中默认带有Ribbon依赖

配置ReestTemplate
```java
@Configuration
public class ApplicationContextConfig {
    /**
     * Rest请求交互对象
     * LoadBalanced：赋予RestTemplate负载均衡的能力
     * @return RestTemplate
     */
    @Bean
    @LoadBalanced
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

}
```

RestTemplate:
    - postForObject / getForObject  `json串`
    - postForEntity / getForEntity  `ResponseEntity对象` (响应头、响应状态码和响应体)

### 负载规则

#### IRule
- RoundRobinRule : 轮询
- RandomRule : 随机
- RetryRule : 轮询，失败则在指定时间内重试
- WeightedResponseTimeRule : 权重，根据响应速度
- BestAvailableRule : 过滤掉多次访问故障处于跳闸状态的服务，然后选择一个并发量小的服务
- AvailabilityFilteringRule : 过滤掉故障实例，然后选择一个并发量小的服务
- ZoneAvoidanceRule : 默认规则，复合判断server所在区域的性能和server的可用性选择服务器

#### 规则替换
- 定制化不能再@ComponentScan所能扫描到的地方，即新建包，不在@SpringBootApplication下。
- 新建规则类，加入`@ConfigUration`和`@Bean`注解。
- 主启动类添加`@RibbonClient(name = "XXX", configuration = XXX.class)`。 (name中是使用此规则的服务，configuration中是规则类)

#### 关于轮询算法
- 获取所有服务实例 `List<ServiceInstance> instances = discoveryClient.getInstances("serviceID");`
- 原子类 + 自旋乐观锁 + do while 循环 + 对服务总数求余。

## OpenFeign

使编写Java Http 更加容易， 接口 <-----> 接口

### 使用步骤
- pom
    ```xml
    <!--OpenFeign -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-openfeign</artifactId>
        <version>2.2.1.RELEASE</version>
    </dependency>
    ```
- 接口添加注解 `@FeignClient`
- 主启动类添加注解 `@EnableFeignClients`

使编写逻辑更符合习惯 controller ---> service

### 超时控制

OpenFeign底层由Ribbon构成,
```yaml
ribbon:
  ReadTimeout: 5000
  ConnectYimeout: 5000
```

### 日志增强

级别: NONE、BASIC、HEADERS、FULL

```java
@Configuration
public class FeignConfig {

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
```

```yaml
logging:
  level:
    com.atguigu.cloud.service.PaymentFeignService: debug
```