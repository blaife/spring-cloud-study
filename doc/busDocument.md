# Bus 消息总线

分布式自动刷新配置功能

## 基本介绍
- 将分布式系统的节点与轻量级消息系统连接起来的框架
- Spring Cloud Bus配合Spring Cloud Config使用可以实现配置的动态刷新
- Bus支持两种消息代理：RabbitMQ和Kafka

## 具体使用
> 请先配置RabbitMQ环境
> 在 config部分所讲的基础上克隆一份3345，作为客户端2，端口设置为3346

## 设计思想
1. 利用消息总线触发一个客户端/bus/refresh,而刷新所有客户端的配置
2. 利用消息总线触发一个服务端ConfigServer的/bus/refresh端点,而刷新所有客户端的配置

### ***图二的架构显然更加合适，图一不适合的原因如下***
- 打破了微服务的职责单一性，因为微服务本身是业务模块，它本不应该承担配置刷新职责
- 破坏了微服务各节点的对等性
- 有一定的局限性。例如，微服务在迁移时，它的网络地址常常会发生变化，此时如果想要做到自动刷新，那就会增加更多的修改

### config服务端添加pom，修改yml

- pom
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bus-amqp</artifactId>
</dependency>
```
- yml
```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest

management:
  endpoints:
    web:
      exposure:
        include: 'bus-refresh'
```
### config客户端添加pom，修改yml
- pom
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bus-amqp</artifactId>
</dependency>
```
- yml
```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
```

### 测试

1. 启动`7001、3344、3345、3346`
2. 分别查看其配置文件信息
3. 修改配置文件版本
4. 查看`3344、3345、3346`下的配置文件版本，`3344`已修改，`3345、3346`未修改
5. 执行`curl -X POST "http://localhost:3344/actuator/bus-refresh"`
6. 再次查看`3345、3346`下的配置文件版本，均更新

### 刷新定点通知

执行请求修改为： `curl -X POST "http://localhost:3344/actuator/bus-refresh/config-client:3345"`  
此时仅通知`3345`而不通知`3346`