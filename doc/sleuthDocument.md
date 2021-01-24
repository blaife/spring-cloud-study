# Sleuth 链路追踪

> 在cloud-consumer-order80和cloud-provider-payment8001上测试

## zipkin
- 下载地址: http://dl.bintray.com/openzipkin/maven/io/zipkin/java/zipkin-server/
- Trace: 类似于树结构的span集合，标识一条调用链路存在的唯一标识
- span: 标识调用链路来源，通俗的理解span就是一次请求信息

## 项目内容

### pom

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-zipkin</artifactId>
</dependency>
```
    
### yaml

```yaml
spring:
  zipkin:
    base-url: http://localhost:9411
  sleuth:
    sampler:
      probability: 1
```

### 业务文件

```java
@GetMapping(value = "/zipkinTest")
public String paymentZipkin() {
    return "hi !!! ";
}

@GetMapping(value = "/zipkinTest")
public String paymentZipkin() {
    String result = restTemplate.getForObject("http://localhost:8001" + "/payment/zipkinTest/", String.class);
    return result;
}
```