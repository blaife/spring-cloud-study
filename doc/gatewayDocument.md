# 路由

目前主流的路由框架是 `Zuul` 和 `GateWay`。  
由于Zuul团队成员的意见存在分歧，导致核心成员流失。Zuul2一直处于未完成的状态。  
就不再讨论Zuul, 只学习GateWay。

## GateWay

### GateWay是什么？
`GateWay` 是基于 `WebFlux` 框架实现的，而 `WebFlux` 框架底层则使用了高性能的 `Reactor`模式通讯框架 `Netty` 。（异步非阻塞响应式框架）。

### GateWay能干什么？
- 反向代理
- 鉴权
- 流量控制
- 日志监控
- ...

### GateWay在项目中的位置
WEB -> Nginx -> 网关 -> 微服务

### GateWay的异步非阻塞模型
`Struts2` 和 `SpringMVC` 都是在 `Servlet API` 和 `Servlet` 容器中运行的。（ `Servlet3.1` 之后才有了异步非阻塞支持）  
WebFlux是一个非阻塞异步的框架（非阻塞+函数式编程）。不依赖于 `Servlet API` ，并基于Reactor来实现响应式编程。

### GateWay三大核心概念
- Route-路由：由ID,目标URI，一系列断言和过滤器组成。
- Predicate-断言：参考 java8 `java.util.function.Predicate`。
- Filter-过滤：在请求被路由前或后对请求进行修改。

### GateWay简单服务搭建

1. 新建项目 `cloud-gatewawy-gateway9527`
2. 添加pom依赖 `spring-cloud-starter-gateway` 及其他基本包
    ```xml
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-gateway</artifactId>
    </dependency>
    ```
    - ***注意***：不能引入 `web` 依赖， 会存在冲突。
3. 配置文件 端口，服务名，Eureka注册
4. 主启动类
5. yml新增网关配置
    ``` yaml
    spring:
      cloud:
        gateway:
          routes:
            - id: payment_routh # 路由id，每当有固定规则，但要求唯一，建议配合服务名
              uri: http://localhost:8001 # 匹配后提供服务的路由地址
              predicates:
                - Path=/payment/get/** # 断言，路径相匹配的进行路由
    
            - id: payment_routh2
              uri: http://localhost:8001
              predicates:
                - Path=/payment/lb/**
    ```

### GateWay路由配置的两种方式
- yml方式：
    ```yaml
    spring:
      cloud:
        gateway:
          routes:
            - id: payment_routh # 路由id，每当有固定规则，但要求唯一，建议配合服务名
              uri: http://localhost:8001 # 匹配后提供服务的路由地址
              predicates:
                - Path=/payment/get/** # 断言，路径相匹配的进行路由
    
            - id: payment_routh2
              uri: http://localhost:8001
              predicates:
                - Path=/payment/lb/**
    ```
- 硬编码：
    ``` java
    @Bean
    public RouteLocator baiduRouteLocator_guonei(RouteLocatorBuilder routeLocatorBuilder) {
        RouteLocatorBuilder.Builder routes = routeLocatorBuilder.routes();
        routes.route("path_route_baidu_guonei", r -> r.path("/guonei")
                .uri("http://news.baidu.com/guonei")).build();
        return routes.build();
    }
    ```

### GateWay配置动态路由

- 实际上是实现一次负载均衡，通过服务发现组件实现
- yaml配置入下：
    ```yaml
    spring:
      application:
      cloud:
        gateway:
          discovery:
            locator:
              enabled: true # 开启动态路由
          routes:
            - id: payment_routh
              # uri: http://localhost:8001
              uri: lb://cloud-payment-service # 匹配后提供的服务路由地址
              predicates:
                - Path=/payment/get/**
    ```

### GateWay 常用的 `Predicate` 的使用

- After: 在某个时间后才能匹配
    - `-After=2020-12-20T18:12:50.254+08:00[Asia/Shanghai]`
    - 如何获取这个时间串呢
        ```java
            public class Test {
                public static void main(String[] args) {
                    ZonedDateTime zdt = ZonedDateTime.now();
                    System.out.println(zdt);
                }
            }
        ```
- Before: 在某个时间后才能匹配
    - `- Before=2020-12-21T18:12:50.254+08:00[Asia/Shanghai]`
- Between: 在某个时间段中才匹配
    - `- Before=2020-12-20T18:12:50.254+08:00[Asia/Shanghai],2020-12-21T18:12:50.254+08:00[Asia/Shanghai]`
- Cookie: 需要两个参数，一个CookieName，一个正则表达式
    - `- Cookie=username,blaif`
- Header: 需要两个参数，一个是属性名称，一个是正则表达式
    - `- Header=X-Request-Id,\d+`
- Host: 接受一组参数，一组匹配的域名列表，它通过参数中的主机地址作为匹配规则
    - `- Host=**.somehost.org,**.anotherhost.org`
- Method: 接收请求类型
    - `- Method=GET`
- Path: 请求路径
    - `- Path=/payment/lb/**`
- Query: 请求参数
    - `- Query=username, \d+`
- ReadBodyPredicateFactory:
- RemoteAddr:
- Weight:
- CloudFoundryRouteService:

### GateWay中的Filter

- 按生命周期:
    - pro 之前
    - post 之后
- 种类
    - GateWayFilter 单一 ***（30余种）***
    - GlobalFilter 全局 ***（10余种）***

#### 自定义过滤器
```java
@Component
@Slf4j
public class MyLogGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("**************************come MyLogGlobalFilter:" + new Date());
        String uname = exchange.getRequest().getQueryParams().getFirst("uname");
        if (uname == null) {
            log.info("**************************用户名为null, 非法用户， ┭┮﹏┭┮");
            exchange.getResponse().setStatusCode(HttpStatus.NOT_ACCEPTABLE);
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
```