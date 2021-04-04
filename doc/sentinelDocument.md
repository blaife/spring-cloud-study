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

## 系统规则

从应用级别的入口流量进行控制

系统规则支持以下的模式：

- ***Load自适应***（仅对Linux/Unix-like机器生效） ：系统的load1作为启发指标，进行自适应系统保护。当load1超过设定的启发值，且系统当前的并发线程数超过估算的系统容量时才会触发系统保护（BBR阶段）。系统容量有系统的maxQps * minRt 估算得出。设定参考值一般是CPU cores * 2.5.
- ***CPU usage*** （1.5.0+ 版本）：当系统CPU使用率超过阈值即触发系统保护（取值范围0.0-1.0），比较灵敏。
- ***平均RT*** ：当单台机器上所有入口流量的平均RT达到阈值即触发系统保护，单位是毫秒。
- ***并发线程数***：当单台机器上所有入口流量的并发线程数达到阈值即触发系统保护。
- ***入口QPS***：当单台机器上所有入口流量的QPS达到阈值即触发系统保护。

## @SentinelResource配置

### 配置方式

- 按资源名称限流
- 按Url地址限流 （***测试过程中发现其不走blockHandler中的方法***）

### 问题： blockHandler

1. 系统默认的未体现业务需求
2. 自定义处理方法与业务代码耦合，不直观
3. 每个业务代码都添加兜底方法，代码膨胀加剧
4. 全局统一方法未实现

解决方案：创建自定义限流处理逻辑

```java
package com.atguigu.cloud.myHandler;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.atguigu.cloud.entities.CommonResult;
import com.atguigu.cloud.entities.Payment;

/**
 * @author Blaife
 * @description 自定义限流处理类
 * @date 2021/3/28 22:14
 */
public class CustomerBlockHandler {
    public static CommonResult handlerException(BlockException exception) {
        return new CommonResult(444, "按客户自定义,global handlerException -------- 1" );
    }

    public static CommonResult handlerException2(BlockException exception) {
        return new CommonResult(444, "按客户自定义,global handlerException -------- 2" );
    }
}
```

```java
    @GetMapping("/rateLimit/customewrBlockHandler")
    @SentinelResource(value = "customewrBlockHandler",
            blockHandlerClass = CustomerBlockHandler.class,
            blockHandler = "handlerException2")
    public CommonResult customewrBlockHandler() {
        return new CommonResult(200, "按客户自定义测试OK", new Payment(2020L, "serial002"));
    }
```

## 关于@SentinelResource

***注意：注解方式埋点不支持private方法***

@SentinelResource注解最主要的两个用法：限流控制和熔断降级的具体使用案例介绍完了。另外，该注解还有一些其他更精细化的配置，比如忽略某些异常的配置、默认降级函数等等，具体可见如下说明：

- value：资源名称，必需项（不能为空）
- entryType：entry类型，可选项（默认为 EntryType.OUT）
- fallback：fallback函数名称，可选项，用于在抛出异常的时候提供 fallback处理逻辑。fallback函数可以针对所有类型的异常（除了exceptionsToIgnore里面排除掉的异常类型）进行处理。fallback函数签名和位置要求： 返回值类型必须与原函数返回值类型一致；方法参数列表需要和原函数一致，或者可以额外多一个 Throwable类型的参数用于接收对应的异常。
- fallback函数默认需要和原方法在同一个类中。若希望使用其他类的函数，则可以指定 fallbackClass为对应的类的 Class 对象，注意对应的函数必需为 static函数，否则无法解析。 defaultFallback（since 1.6.0）：默认的 fallback函数名称，可选项，通常用于通用的 fallback逻辑（即可以用于很多服务或方法）。默认 fallback函数可以针对所有类型的异常（除了exceptionsToIgnore里面排除掉的异常类型）进行处理。若同时配置了 fallback和 defaultFallback，则只有 fallback会生效。defaultFallback函数签名要求：返回值类型必须与原函数返回值类型一致；
- 方法参数列表需要为空，或者可以额外多一个 Throwable 类型的参数用于接收对应的异常。
- defaultFallback函数默认需要和原方法在同一个类中。若希望使用其他类的函数，则可以指定 fallbackClass为对应的类的 Class 对象，注意对应的函数必需为 static 函数，否则无法解析。
- exceptionsToIgnore（since 1.6.0）：用于指定哪些异常被排除掉，不会计入异常统计中，也不会进入 fallback 逻辑中，而是会原样抛出。

## Sentinel的三个核心API

- SphU：定义资源
- Trace：定义统计
- ContextUtil；定义上下文

## Sentinel 服务熔断

- fallback：可以处理运行时异常
- blockHandler：只负责Sentinel控制台的配置违规
- blockHandler优先
- exceptionToIgnore：异常忽略

***问题：Ribbon和Feign的区别，以及使用场景***
***问题：熔断和降级的区别是什么***

## Sentinel持久化

可以持久化入mysql，redis，nacos，文件等各种媒介

### Nacos

持久化入Nacos
