# Hystrix 断路器 (已停更)

断路器的作用是防止服务雪崩，保证服务的高可用和低耦合。

服务雪崩: 扇出的链路上某个微服务调用不可用，使其上层的服务占用越来越多的系统资源，从而引起系统崩溃。

## 基本概念
- 服务降级(fallback) : 当服务发生故障时，向服务调用方发送一个备选方案，***保证服务调用方的线程不会被长时间不必要的占用***。
    - 程序运行异常
    - 超时
    - 服务熔断触发服务降级
    - 线程池、信号量打满也会导致服务降级
- 服务熔断(break) : 类比保险丝达到最大服务访问后，直接拒绝访问，然后调用服务降级。
- 服务限流(flowlimit) : 秒杀、高并发操作,严禁拥堵,有序进行。

## 具体使用

- pom
    ```xml
     <!-- Hystrix -->
     <dependency>
         <groupId>org.springframework.cloud</groupId>
         <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
     </dependency>
    ```
### 服务降级-消费端
- 主启动类添加注解 `@EnableHystrix`
- 在 servcice 方法上使用 `@HystrixCommand()`。
    ```java
        @HystrixCommand(fallbackMethod = "paymentInfo_TimeOutHandler", commandProperties = {
              @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value = "5000")
        })
        public String paymentInfo_TimeOut(Integer id) { ... }
        public String paymentInfo_TimeOutHandler(Integer id) { ... }                  
    ```
- 如上，就是设置报错后的兜底的方法，并设置了一个超时控制。
- 注意：
    - 无论是超时异常还是运行异常都会进入 `fallbackMethod` 方法。
    - `fallbackMethod` 放在在执行式使用的是 `Hystrix` 中独立的线程。起到隔离的效果。

### 服务降级-客户端
- 主启动类添加注解 `@EnableHystrix
- 在 controller 方法上使用 `@HystrixCommand()`。 (应为此Service中是接口)
    ```java
        @HystrixCommand(fallbackMethod = "paymentInfo_TimeOutHandler", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value = "5000")
        })
        public String paymentInfo_TimeOut(Integer id) { ... }
        public String paymentInfo_TimeOutHandler(Integer id) { ... }                  
    ```
- 添加配置Foreign开启Hystrix
    ```yaml
      feign:
        hystrix:
          enabled: true
    ```

### 全局服务降级 （解决代码膨胀问题）

- 类头部添加注解 `@DefaultProperties(defaultFallback = """)`
- 在方法上只写了注解 `@HystrixCommand` 没有具体配置降级方法时，自动走默认的降级方法。

### 通配服务降级 （解决代码混乱的问题）

- 添加接口实现类（此接口对应生产者，存在 `@FeignClient`）, 并在方法中写入降级处理内容;
- 修改 `@FeignClient` 内容，例如： `@FeignClient(value = "CLOUD-PROVIDER-HYSTRIX-PAYMENT", fallback = PaymentFallbackService.class)`
- ***注意： 在此处理服务降级之后，不要使用 （在类上写GetMapping,与方法上的GetMapping组合成全部路径）的方式，测试失败。出现`There is already 'xxxxxxService' bean method`异常***

### 服务熔断

- 依旧使用注解 `@HystrixCommand` , 配置服务熔断需要新加入一些配置
```java
@HystrixCommand(fallbackMethod = "paymentCircuitBreaker_fallback", commandProperties = {
        @HystrixProperty(name = "circuitBreaker.enabled", value = "true"), //是否开启断路器
        @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"), // 请求次数
        @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "10000"), // 时间窗口期 (跳闸多久后再次尝试开启)
        @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "60") // 失败率达到多少后跳闸
})
```
- 详细进入 `HystrixCommandProperties` 查看。

### 熔断配置整理

- 统计滚动的时间窗口 default 10000 ten seconds  
withMetricsRollingStatisticalWindowInMilliseconds(10000)
- 滚动时间窗口 bucket 数量 default  
withMetricsRollingStatisticalWindowBuckets(10)
- 采样时间间隔 default 500  
withMetricsHealthSnapshotIntervalInMilliseconds(1)
- 熔断器在整个统计时间内是否开启的阀值，默认20。也就是10秒钟内至少请求20次，熔断器才发挥起作用  
withCircuitBreakerRequestVolumeThreshold(20)
- 默认:50。当出错率超过50%后熔断器启动.  
withCircuitBreakerErrorThresholdPercentage(30)
- 熔断器默认工作时间,默认:5秒.熔断器中断请求5秒后会关闭重试,如果请求仍然失败,继续打开熔断器5秒,如此循环  
withCircuitBreakerSleepWindowInMilliseconds(1000)
- 隔离策略  
withExecutionIsolationStrategy(ExecutionIsolationStrategy.SEMAPHORE)
- 信号量隔离时最大并发请求数  
withExecutionIsolationSemaphoreMaxConcurrentRequests(2)
- 命令组名，该命令属于哪一个组，可以帮助我们更好的组织命令。  
withGroupKey(HystrixCommandGroupKey.Factory.asKey(“HelloGroup”))
- 命令名称，每个CommandKey代表一个依赖抽象,相同的依赖要使用相同的CommandKey名称。依赖隔离的根本就是对相同CommandKey的依赖做隔离。  
andCommandKey(HystrixCommandKey.Factory.asKey(“Hello”)
- 所属线程池的名称，同样配置的命令会共享同一线程池，若不配置，会默认使用GroupKey作为线程池名称。  
andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey(“HelloThreadPool”))
- 命令属性，设置包括断路器的配置，隔离策略，降级设置，以及一些监控指标等。  
- 线程池属性，配置包括线程池大小，排队队列的大小等。


### 图形化Dashboard

- 新建项目

- 引入依赖
```xml
<!-- dashboard -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
</dependency>
```
- 主启动类添加注解 `@EnableHystrixDashboard`
- 要监控的服务添加依赖 `spring-boot-starter-actuator` ,以及主启动类添加如下代码。
```java
/**
 * springcloud更新过程中产生的bug （未兼容）
 * 不添加会出现连接失败的异常
 * @return
 */
@Bean
public ServletRegistrationBean getServlet() {
    HystrixMetricsStreamServlet streamServlet = new HystrixMetricsStreamServlet();
    ServletRegistrationBean registrationBean = new ServletRegistrationBean(streamServlet);
    registrationBean.setLoadOnStartup(1);
    registrationBean.addUrlMappings("/hystrix.stream");
    registrationBean.setName("HystrixMetricsStreamServlet");
    return registrationBean;
}
```
- 监控页面 `localhost:9001/hystrix`
- 路径输入 `~/hystrix.stream`
- 注意 要监控的接口需要开启熔断，即 `@HystrixProperty(name = "circuitBreaker.enabled", value = "true")`