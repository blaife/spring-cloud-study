# Config 配置中心

- 配置服务器为各个不同的微服务之间赢得的所有环境提供了一个***中心化的外部配置***

## 配置中心服务端

### pom依赖
```xml
<!-- config -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-server</artifactId>
</dependency>

<!-- eureka-client -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```
### 基本配置
```yaml
server:
  port: 3344

spring:
  application:
    name: cloud-config-center

eureka:
  client:
    service-url:
      defaultZone: http://eureka7001.com:7001/eureka/

```

### git仓库方式
- yml 配置
```yaml
spring:
  cloud:
    config:
      server:
        git:
          uri: git@github.com:XXX/cloud2020-config.git
          search-paths:
            - springcloud-config/config-repo
          force-pull: true
          username: XXXX
          passphrase: XXXX
```
- git文件
在git仓库上建立一个 config-repo 文件夹，新建config-dev.yml、config-test.yml 两个配置，然后上传文件。

- 测试， 例：
`http://localhost:3344/config-dev.yml`

### 本地配置方式

- yml配置
```yaml
spring:
  profiles:
    active: native
```
- 配置文件 `configtest.properties`
```properties
word = hello world - version 1
```
- 测试， 例:
`http://localhost:3344/configtest-1.properties`

## 读取规则
- `/{label}/{application}-{profile}.yml`
- `/{application}-{profile}.yml`
- `/{application}/{profile}[/{label}]`

## 配置中心客户端

### pom依赖
```xml
<!-- config -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>

<!-- eureka-client -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```
### 配置文件
```yaml
server:
  port: 3345
spring:
  application:
    name: config-client
  cloud:
    config:
#      label: master # 因为采用的是本地配置，只需要name就可以了
      name: configtest
#      profile: dev
      uri: http://localhost:3344

eureka:
  client:
    service-url:
      defaultZone: http://eureka7001.com:7001/eureka/
```

### 业务文件
```java
@RestController
public class ConfigController {

    @Value("${word}")
    private String word;

    @GetMapping("/getWord")
    public String getWord() {
        return word;
    }

}
```

### 测试
```
http://localhost:3345/getWord
```

## Config动态刷新

### 引入 `actuator` 依赖
```xml
<!-- 定期监控 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```
### 修改ymal文件
```yaml
# 添加如下内容
management:
  endpoints:
    web:
      exposure:
        include: "*"
```
### 业务类controller添加 `@Refresh` 注解
```java
@RestController
@RefreshScope
public class ConfigController { ... }
```
### 修改配置
修改配置文件，此时重新请求config服务端配置已更新，但请求客户端配置为更新。
### 发送post请求刷新服务
```
cyrl -X -POST "http://localhost:3345/actuator/refresh"
```
再次请求客户端配置，已更新。