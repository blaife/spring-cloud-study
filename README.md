## 服务注册中心
- Eurake
    - Eurake Server: 提供服务注册
    - Eurake Client: 通过注册中心进行访问。
- Zookeeper
- Consul
- Nacos

大部分情况下配置多个，防止服务宕机。

### Eurake 

- 服务治理：管理服务与服务之间的依赖关系，可以实现服务之间的调用、负载均衡、容错等。
- 服务注册与发现：当服务启动的时候，会把自己服务器的的信息，比如服务通讯地址等以别名方式注册到注册中心上去。另一方，以该别名的方式去注册中心上获取实际的服务通讯地址。 

#### service 端

添加一个新的服务，作为Eureka的服务器。

1. 添加依赖
```xml
<!-- eureka-server -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
```
2. 配置参数
```yaml
eureka:
  instance:
    hostname: localhost # eureka 服务实例名称
  client:
    register-with-eureka: false # false 表示不向注册中心注册自己
    fetch-registry: false # false 表示自己就是注册中心
    service-url:
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
```
3. 主启动类添加 `@EnableEurekaServer` 注解表明其为服务器
4. 启动当前Eureka服务，并打开对应网址，正常则显示Eureka服务界面

#### Client 端

对任一服务（非注册中心），进行修改。

1. 添加对应pom依赖
```xml
<!-- eureka-client -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```
2. 配置参数
```yaml
eureka:
  client:
    register-with-eureka: true # 表明是否将自己注册金EurekaService 默认为 true
    fetch-registry: true # 是否从EurekaService抓取已有的注册信息，默认为true。单节点无所谓，集群必须设置为true才能配合ribbon使用负载均衡
    service-url:
      defaultZone: http://localhost:7001/eureka
```
3. 主启动类添加 `@EnableEurekaClient` 注解表明其支持服务发现
4. 打开Eureka服务器进行查看校验

#### Eureka 集群配置
主要作用是 负载均衡 和 故障容错。 
各Eureka之间互相注册，对外暴露一个服务整体。

- server:
```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:7001/eureka/
```
- client:
```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:7001/eureka,http://localhost:7002/eureka
```

#### 服务集群配置

1. http路径填写： `public static final String PAYMENT_URL = "http://CLOUD-PAYMENT-SERVICE";`
2. restTemplate： 使用 `@LoadBalanced` 注解赋予RestTemplate负载均衡的能力

#### 服务信息完善

- eureka 界面显示的服务信息
```yaml
eureka:
  instance:
    instance-id: payment8001 # 设置当前服务显示的名称
    prefer-ip-address: true # 访问路径显示ip地址
```

#### 服务发现 Discovery

1. 主启动类添加 `@EnableDiscoveryClient` 注解
2. 开发接口方法
```java
@Resource
private DiscoveryClient discoveryClient;

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
```

#### 自我保护

- 默认90s没有收到客户端心跳包，就会移除该服务，但有时会因为网络延迟等原因在服务正常时被移除，所以有了自我保护机制，在服务可能存在问题时并不会立即删除该服务，而是会进入自我保护状态。
- 设计思想就是，宁可保留错误的服务信息，也不要移除可能正常的服务。
- 禁止自我保护：
```yaml
# server
eureka:
  server:
    enable-self-preservation: false # 关闭自我保护机制
    eviction-interval-timer-in-ms: 2000 # 时间频率运行并删除过期的实例

# client
eureka:
  instance:
    lease-renewal-interval-in-seconds: 1 # Eureka客户端向服务端发送心跳的时间间隔 默认为30s
    lease-expiration-duration-in-seconds: 2 # Eureka在最后一次接收到心跳的额等待时间 默认为90s
```