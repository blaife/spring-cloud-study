# Stream 消息驱动

> 屏蔽消息中间件的差异，降低切换成本，统一消息的编程模型。

## Binder 绑定器对象

- 应用程序通过 `inputs` 或 `outputs` 来与 `SpringCloud Stream` 中的 `binder` 对象交互。
- 使用 `binder` 对象来与消息中间件交互。
- 使用 `Spring Integration` 来连接消息代理中间件以实现事件驱动。
- `Stream` 为一些供应商的消息中间件提供了个性化的自动化配置实现，引用了 `发布-订阅`,`消费组`,`分区`三个概念。

## 设计思想
通过定义绑定器 `Binder` 作为中间层，实现应用程序与消息中间件之间的隔离。

## 常用注解
- @Input： 注解标识输入通道。
- @Output： 注解标识输出通道。
- @StreamListener： 监听队列
- @EnableBinding： 指信道chanel和exchange绑定在一起。

## 消息驱动具体实现

### pom依赖
```xml
<!-- Stream - rabbit -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-stream-rabbit</artifactId>
</dependency>
```

### ymal配置
```yaml
server:
  port: 8801

spring:
  application:
    name: cloud-stream-provider
  cloud:
    stream:
      binders: # 在此处配置要绑定的Rabbit的服务信息
        defaultRabbit: # 表示定义的名称，用于binding整合
          type: rabbit # 消息组件类型
          environment: # 设置Rabbitmq的相关环境配置
            spring:
              rabbitmq:
                host: localhost
                port: 5672
                username: guest
                password: guest
      bindings: # 服务的整合处理
        output: # 这个名字是一个通道的名称 生产者
          destination: studyExchange # 表示要使用的Exchange名称定义
          content-type: application/json # 设置消息类型， 本次为JSON， 文本则设置 "text/plain"
          binder: defaultRabbit # 设置要绑定的消息服务的具体设置 报红不影响使用
        input: # 这个名字是一个通道的名称 消费者
          destination: studyExchange # 表示要使用的Exchange名称定义
          content-type: application/json # 设置消息类型， 本次为JSON， 文本则设置 "text/plain"
          binder: defaultRabbit # 设置要绑定的消息服务的具体设置 报红不影响使用
eureka:
  client:
    service-url:
      defaultZone: http://eureka7001.com:7001/eureka/
```

### 业务类
#### 生产者
```java
@RestController
public class SendMessageController {

    @Resource
    private IMessageProvider messageProvider;

    @GetMapping(value = "/sendMessage")
    public String sendMessage() {
        return messageProvider.send();
    }
}

@Slf4j
@EnableBinding(Source.class) // 定义消息的推送管道
public class IMessageProviderImpl implements IMessageProvider {

    @Resource
    private MessageChannel output;

    @Override
    public String send() {
        String serial = UUID.randomUUID().toString();
        output.send(MessageBuilder.withPayload(serial).build());
        log.info("*********serial: " + serial);
        return serial;
    }
}
```
#### 消费者
```java
@Component
@EnableBinding(Sink.class)
public class ReceiveMessageListenerController {

    @Value("${server.port}")
    private String serverPort;

    @StreamListener(Sink.INPUT)
    public void input(Message<String> message) {
        System.out.println("消费者，--------> " + message.getPayload() + "\t port:" + serverPort);
    }
}
```

## 消息分组
```yaml
spring:
  cloud:
    stream:
      bindings:
        input:
          group: atguiguA
```