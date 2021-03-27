# Sentinel 熔断与限流

## 简介：

Hystrix的阿里版

Hystrix的不足之处：
1. 需要手动搭建平台
2. 没有web界面进行更加细粒度化的配置

Sentinel的初衷： ***约定>配置>编码(使用配置和注解来代替编码)***

Sentinel分为两个部分：
- 核心库：运行于所有java环境，对Dubbo、SpringCloud框架有较好的支持。
- 控制台：基于 SpringBoot ，打包后可以直接运行。

## 控制台的下载与安装

- github.com/alibaba/Sentinel/relases
- 下载jar包，并运行，Sentinel控制台默认使用8080端口。

## 初始化监控 （新建项目8401）

- pom
```xml
<!-- sentinel -->
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
</dependency>
```
- yaml内容
```yaml
spring:
  cloud:
    sentinel:
      transport:
        dashboard: localhost:8080
        # 默认8719端口，加入被占用会自动从8719开始依次+1扫描，直至找到未被占用的端口。
        port: 8719
```
- sentinel内容懒加载  
    只有在执行了请求之后才能够在sentinel可视化页面看到数据监控情况。

## 流控规则简介

- 资源名：唯一名称，默认请求路径
- 针对来源：Sentinel可以针对调用者进行先烈，填写微服务名，默认default（不区分来源）
- 阈值类型/单机阈值：
    - QPS：每秒钟的请求数量，当调用该api的QPS达到阈值的时候进行限流。
    - 流程数：当调用该API的线程数达到阈值的时候进行限流
- 是否集群：不需要集群
- 流控模式：
    - 直接：api达到限流条件时直接限流。
    - 关联：当关联的资源达到阈值时，就限流自己。
    - 链路：只记录指定链路上的流量（指定资源从入口资源进来的流量，如果达到阈值，就进行限流）api级别的针对来源
- 流控效果：
    - 快速失效：直接失败，抛出异常
    - Warm UP：根据codeFactor（冷加载因子，默认为3）的值，从阈值/codFactor，经过预热时长，才达到设定的QPS阈值。
    - 排队等待：匀速排队，让请求以匀速的速度通过，阈值类型必须设置为QPS，否则无效。

## 降级策略

- 平均响应时间（DEGRADE_GRADE_RT）：当1s内持续进入5个请求，对应时刻的平均响应时间（秒级）军查过阈值（count，以ms为单位），那么在接下来的时间窗口（DegradeRule 中的 timeWindow，以ms为单位）之内， 对这个方法都会自动地熔断（抛出DegradeException）。注意Sentinel默认统计的RT的上县是4900ms，超出此阈值都会算作4900ms，若需要变更此上线可以通过启动配置项 `-Dcsp.sentinel.statistic.max.rt=xxx` 来配置。
- 异常比例（DEGRADE_GRADE_EXCEPTION_PATIO）：当资源的每秒请求量 >= 5， 并且每秒异常总数占通过两的比值超过阈值（DegradeRule 中的 count）之后，资源进入降级状态，即在接下来的时间窗口（DegradeRule 中的 timeWindow， 以s为单位）之内，对这个方法的调用都会自动地返回。异常比率的阈值范围是[0.0, 1.0]， 代表 0% ~ 100%；
- 异常数（DEGRADE_GRADE_EXCEPTION_COUNT）：当资源近1分钟的异常数据超过阈值之后会进行熔断。注意由于统计时间窗口是分钟级别的，若 timeWindow 小于 60s， 则接数熔断状态后仍可能再进入熔断状态。

注意：
异常降级仅针对业务异常，对Sentinel限流降级本身的异常（BlockException）不生效。
为了统计异常比例或异常数，需要通过Tracer.trace(ex)记录业务异常。示例：

## 热点规则

```java
/* 热点key使用试例 */
@GetMapping("/testHotkey")
    @SentinelResource(value = "testHotkey", blockHandler = "deal_testHotkey")
    public String testHotkey(@RequestParam(value = "p1", required = false) String p1,
                             @RequestParam(value = "p2", required = false) String p2) {
        return "--------------testHotkey";
    }

    public String deal_testHotkey(String p1, String p2, BlockException blockException) {
        return "--------------deal_testHotkey";
        // sentinel默认提示： Blocked by Sentinel (flow limiting);
    }
```

- 资源名：与@SentinelResource中的value属性值相对
- 限流模式：仅支持QPS
- 参数索引：从0开始
- 单机阈值：超出后禁用
- 统计窗口时长：统计区域
- 是否集群：
- 参数例外项
    - 参数类型：仅支持八种基本数据类型和String
    - 参数值：参数内容
    - 限流阈值：超出后禁用