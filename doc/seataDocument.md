# Seata 分布式事务

> Seata 是一款开源的分布式事务解决方案，致力于在微服务架构下提供高性能和简单易用的分布式事务服务。

## 问题的由来

每个服务内部的数据一致性有<font style="color:red">本地</font>事务来保证，但是<font style="color:red">全局的数据一致性</font>无法保证

## Seata 术语
1. Transaction ID XID 全局事务ID
2. TC (Transaction Coordinator) - 事务协调者
    - 维护全局和分支事务的状态，驱动全局事务提交或回滚。
3. TM (Transaction Manager) - 事务管理器
    - 定义全局事务的范围：开始全局事务、提交或回滚全局事务。
4. RM (Resource Manager) - 资源管理器
    - 管理分支事务处理的资源，与TC交谈以注册分支事务和报告分支事务的状态，并驱动分支事务提交或回滚。

## 分布式事务过程

1. TM向TC申请开启一个全局事务，全局事务创建成功后生成一个全局唯一的XID
2. XID在微服务调用链路的上下文中传播
3. RM向TC注册分支事务，将其纳入XID对应全局事务的管辖
4. TM向TC发起针对XID的全局提交或回滚决议
5. TC调度XID下管辖的全部分支事务完成提交或回滚

![分布式事务过程](../img/seata solution.png)

## Seata-Server安装

### 下载地址

http://seata.io/zh-cn/blog/download.html

### 修改配置文件

- file.conf
    ```
    事务组
    service {
      #vgroup->rgroup
      vgroup_mapping.my_test_tx_group = "fsp_tx_grop"
      #only support single node
      default.grouplist = "127.0.0.1:8091"
      #degrade current not support
      enableDegrade = false
      #disable
      disable = false
      #unit ms,s,m,h,d represents milliseconds, seconds, minutes, hours, days, default permanent
      max.commit.retry.timeout = "-1"
      max.rollback.retry.timeout = "-1"
    }
    ```
    ```
    # 事务日志存储
    store {
      ## store mode: file、db
      mode = "db"
    
      ## file store
      file {
        dir = "sessionStore"
    
        # branch session size , if exceeded first try compress lockkey, still exceeded throws exceptions
        max-branch-session-size = 16384
        # globe session size , if exceeded throws exceptions
        max-global-session-size = 512
        # file buffer size , if exceeded allocate new buffer
        file-write-buffer-cache-size = 16384
        # when recover batch read size
        session.reload.read_size = 100
        # async, sync
        flush-disk-mode = async
      }
    
      ## database store
      db {
        ## the implement of javax.sql.DataSource, such as DruidDataSource(druid)/BasicDataSource(dbcp) etc.
        datasource = "dbcp"
        ## mysql/oracle/h2/oceanbase etc.
        db-type = "mysql"
        ## driver-class-name = "com.mysql.jdbc.Driver"
        # mysql 8 
        driver-class-name = "com.mysql.cj.jdbc.Driver"
    
        url = "jdbc:mysql://127.0.0.1:3306/seata?serverTimezone=UTC"
        user = "root"
        password = "blaife"
        min-conn = 1
        max-conn = 3
        global.table = "global_table"
        branch.table = "branch_table"
        lock-table = "lock_table"
        query-limit = 100
      }
    }
    ```

- registry.conf
    ```
    ## 注册信息及地址
    registry {
      # file 、nacos 、eureka、redis、zk、consul、etcd3、sofa
      type = "nacos"
    
      nacos {
        serverAddr = "localhost:8848"
        namespace = ""
        cluster = "default"
      }
    }
    ```
  
### 添加Seata数据库
在`store`对应数据库的位置添加seata数据库  执行`conf/db_store.sql`

### 启动

先启动nacos,再启动seata。  
出现 `extension by class[io.seata.discovery.registry.nacos.NacosRegistryProvider]`表示启动成功。  
可以在Nacos服务列表中看到对应数据。  

## 案例内容

- [业务数据库准备](../sql)
- order微服务  
    - pom 依赖
        ```xml
        <dependencies>
            <!-- nacos -->
            <dependency>
                <groupId>com.alibaba.cloud</groupId>
                <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
            </dependency>
    
            <!-- seata -->
            <dependency>
                <groupId>com.alibaba.cloud</groupId>
                <artifactId>spring-cloud-alibaba-seata</artifactId>
                <exclusions>
                    <exclusion>
                        <artifactId>seata-all</artifactId>
                        <groupId>io.seata</groupId>
                    </exclusion>
                </exclusions>
            </dependency>
    
            <dependency>
                <groupId>io.seata</groupId>
                <artifactId>seata-all</artifactId>
                <version>0.9.0</version>
            </dependency>
    
            <!-- openFeign -->
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-starter-openfeign</artifactId>
            </dependency>
    
            <!-- web -->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-web</artifactId>
            </dependency>
    
            <!-- mybatis -->
            <dependency>
                <groupId>org.mybatis.spring.boot</groupId>
                <artifactId>mybatis-spring-boot-starter</artifactId>
            </dependency>
    
            <!-- druid -->
            <dependency>
                <groupId>com.alibaba</groupId>
                <artifactId>druid</artifactId>
            </dependency>
    
            <!-- mysql -->
            <dependency>
                <groupId>mysql</groupId>
                <artifactId>mysql-connector-java</artifactId>
            </dependency>
    
            <!-- jdbc -->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-jdbc</artifactId>
            </dependency>
    
            <!-- 定期监控 -->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-actuator</artifactId>
            </dependency>
    
            <!-- 热部署 -->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-devtools</artifactId>
                <scope>runtime</scope>
                <optional>true</optional>
            </dependency>
    
            <!-- lombok -->
            <dependency>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <optional>true</optional>
            </dependency>
    
            <!-- 单元测试 -->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-test</artifactId>
                <scope>test</scope>
            </dependency>
    
            <!-- hutool -->
            <dependency>
                <groupId>cn.hutool</groupId>
                <artifactId>hutool-captcha</artifactId>
                <version>5.2.0</version>
            </dependency>
        </dependencies>
        ```
    - yaml 配置
        ```yaml
            server:
              port: 2001
            spring:
              application:
                name: seata-order-server
              cloud:
                nacos:
                  discovery:
                    server-addr: localhost:8848
                alibaba:
                  seata:
                    # 自定义事务组与file.conf中设置的对应
                    tx-service-group: fsp_tx_grop
              datasource:
                driver-class-name: com.mysql.jdbc.Driver
                url: jdbc:mysql://localhost:3306/seata_order?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=GMT%2B8
                username: root
                password: blaife
            feign:
              hystrix:
                enabled: false
            logging:
              level:
                io:
                  seata: info
            mybatis:
              mapperLocations: classpath:mapper/*.xml
        ```
    - main 主启动类
        ```java
        // 去除掉原本的数据源配置内容，在config内容中会重新配置
        @SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
        @EnableFeignClients
        @EnableDiscoveryClient
        public class AliSeataOrderApplication2001 {
            public static void main(String[] args) {
                SpringApplication.run(AliSeataOrderApplication2001.class, args);
            }
        }
        ```
    - conf seata 配置
        即 `file.conf` 和 `registry.conf`
        ```
        ## file.conf 服务内容
        vgroup_mapping.fsp_tx_grop = "default"
        ```
    - domain 实体
        - CommonResult
        ```java
        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public class CommonResult<T> {
            private Integer code;
            private String message;
            private T data;
        
            public CommonResult(Integer code, String message) {
                this(code, message, null);
            }
        }
        ```
        - Order
        ```java
        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public class Order {
            private Long id;
            private Long userId;
            private Long productId;
            private Integer count;
            private BigDecimal money;
            private Integer status;
        }
        ```
    - dao 接口实现
        ```java
        @Mapper
        public interface OrderDao {
        
            /**
             * 新建订单
             * @param order
             */
            void create(Order order);
        
            /**
             * 修改订单
             * @param userId
             * @param status
             */
            void update(@Param("userId") Long userId, @Param("status") int status);
        
        }
        ```
        ```xml
        <?xml version="1.0" encoding="UTF-8" ?>
        <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
        <mapper namespace="com.atguigu.cloud.dao.OrderDao">
        
            <resultMap id="BaseResultMap" type="com.atguigu.cloud.domain.Order">
                <id column="id" property="id" jdbcType="BIGINT" />
                <result column="user_id" property="userId" jdbcType="BIGINT" />
                <result column="product_id" property="productId" jdbcType="BIGINT" />
                <result column="count" property="count" jdbcType="INTEGER" />
                <result column="money" property="money" jdbcType="DECIMAL" />
                <result column="status" property="status" jdbcType="INTEGER" />
            </resultMap>
        
            <insert id="create" parameterType="com.atguigu.cloud.domain.Order" useGeneratedKeys="true" keyProperty="id">
                INSERT INTO `seata_order`.`t_order`(`user_id`, `product_id`, `count`, `money`, `status`)
                VALUES (#{userId}, #{productId}, #{count}, #{money}, 0);
            </insert>
        
            <update id="update">
                UPDATE `seata_order`.`t_order`
                SET
                    `status` = 1
                WHERE
                    `user_id` = #{userId}
                AND `status` = #{status}
        
            </update>
        </mapper>
        ```
    - server 接口实现
        ```java
        // OrderService
        /**
         * @author Blaife
         * @description TODO
         * @date 2021/4/14 22:10
         */
        public interface OrderService {
            void create(Order order);
        }
      
        // StorageService
        @FeignClient(value = "seata-storage-server")
        public interface StorageService {
            @PostMapping(value = "/storage/decrease")
            CommonResult decrease(@RequestParam("productId") Long productId, @RequestParam("count") Integer count);
        }
      
        // AccountService
        @FeignClient(value = "seata-account-server")
        public interface AccountService {
            @PostMapping(value = "/account/decrease")
            CommonResult decrease(@RequestParam("userId") Long userId, @RequestParam("money") BigDecimal money);
        }
      
        // OrderServiceImpl
        @Service
        @Slf4j
        public class OrderServiceImpl implements OrderService {
        
            @Resource
            private OrderDao orderDao;
        
            @Resource
            private AccountService accountService;
        
            @Resource
            private StorageService storageService;
        
            @Override
            @GlobalTransactional(name = "fsp-create-order", rollbackFor = Exception.class)
            public void create(Order order) {
                // 1.创建订单
                log.info("-----------> 开始新建订单");
                orderDao.create(order);
        
                // 2. 扣减库存
                log.info("-----------> 订单微服务开始调用库存服务，做扣减 start");
                storageService.decrease(order.getProductId(), order.getCount());
                log.info("-----------> 订单微服务开始调用库存服务，做扣减 end");
        
                // 3. 扣减账户余额
                log.info("-----------> 订单微服务开始调用账户服务，做扣减 start");
                accountService.decrease(order.getUserId(), order.getMoney());
                log.info("-----------> 订单微服务开始调用账户服务，做扣减 end");
        
                // 4. 修改订单状态
                log.info("-----------> 修改订单状态开始");
                orderDao.update(order.getUserId(), 0);
                log.info("-----------> 修改订单状态结束");
        
                log.info("-----------> 下订单结束了");
        
            }
        }
        ```
    - controller 实现
        ```java
        @RestController
        @Slf4j
        @RequestMapping("/order")
        public class OrderController {
        
            @Resource
            private OrderService orderService;
        
            @GetMapping("/create")
            public CommonResult create(Order order) {
                orderService.create(order);
                return new CommonResult(200, "订单创建成功");
            }
        
        }
        ```
    - config 配置
        ```java
        // MybatisConfig
        @Configuration
        @MapperScan({"com.atguigu.cloud.dao"})
        public class MybatisConfig {
        }
      
        // DataSourceProxyConfig
        @Configuration
        public class DataSourceProxyConfig {
        
            @Value("${mybatis.mapperLocations}")
            private String mapperLocations;
        
            @Bean
            @ConfigurationProperties(prefix = "spring.datasource")
            public DataSource druidDataSource() {
                return new DruidDataSource();
            }
        
            @Bean
            public DataSourceProxy dataSourceProxy(DataSource druidDataSource) {
                return new DataSourceProxy(druidDataSource);
            }
        
            @Bean
            public SqlSessionFactory sqlSessionFactoryBean(DataSourceProxy dataSourceProxy) throws Exception {
                SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
                bean.setDataSource(dataSourceProxy);
                ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
                bean.setMapperLocations(resolver.getResources(mapperLocations));
                return bean.getObject();
            }
        }
        ```
- 库存微服务
    - pom 依赖
        - 如上
    - yaml 配置
        - 如上
    - main 主启动类
        ```java
        @SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
        @EnableDiscoveryClient
        @EnableFeignClients
        public class AliSeataStorageApplication2002 {
        
            public static void main(String[] args) {
                SpringApplication.run(AliSeataStorageApplication2002.class, args);
            }
        
        }
        ```
    - conf seata 配置
        - 如上
    - domain 实体
        - CommonResult 如上
        - Storage 
        ```java
        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public class Storage {
            private Long id;
            private Long productId;
            private Integer total;
            private Integer used;
            private Integer  residue;
        }
        ```
    - dao 接口实现
        ```java
        @Mapper
        public interface StorageDao {
        
            void decrease(@Param("productId") Long productId, @Param("count") Integer count);
        }
        ```
        ```xml
        <?xml version="1.0" encoding="UTF-8" ?>
        <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
        <mapper namespace="com.atguigu.cloud.dao.StorageDao">
        
            <resultMap id="BaseResultMap" type="com.atguigu.cloud.domain.Storage">
                <id column="id" property="id" jdbcType="BIGINT" />
                <result column="product_id" property="productId" jdbcType="BIGINT" />
                <result column="total" property="total" jdbcType="INTEGER" />
                <result column="used" property="used" jdbcType="INTEGER" />
                <result column="residue" property="residue" jdbcType="INTEGER" />
            </resultMap>
        
            <update id="decrease">
                update
                    t_storage
                set
                    used = used + #{count},
                    residue = residue - #{count}
                where
                      product_id = #{productId};
            </update>
        </mapper>
        ```
    - server 接口实现
        ```java
        // StorageService
        public interface StorageService {
            void decrease(Long productId, Integer count);
        }
        
        // StorageServiceImpl
        @Service
        @Slf4j
        public class StorageServiceImpl implements StorageService {
        
            @Resource
            private StorageDao storageDao;
        
            @Override
            public void decrease(Long productId, Integer count) {
                System.out.println("storage ---------> 扣减库存开始");
                storageDao.decrease(productId, count);
                System.out.println("storage ---------> 扣减库存结束");
            }
        }
        ```
    - controller 实现
        ```java
        @RestController
        @RequestMapping("/storage")
        public class StorageController {
        
            @Resource
            private StorageService storageService;
        
            @RequestMapping("/decrease")
            public CommonResult decrease(Long productId, Integer count) {
                storageService.decrease(productId, count);
                return new CommonResult(200,"扣减库存成功!");
            }
        }
        ```
    - config 配置  
        - 如上
- 账户微服务
    - pom 依赖
        - 如上
    - yaml 配置
        - 如上
    - main 主启动类
        ```java
        @SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
        @EnableFeignClients
        @EnableDiscoveryClient
        public class AliSeataAccountApplication2003 {
            public static void main(String[] args) {
                SpringApplication.run(AliSeataAccountApplication2003.class, args);
            }
        }
        ```
    - conf seata 配置
        - 如上
    - domain 实体
        - CommonResult 如上
        - Account 
        ```java
        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public class Account {
            private Long id;
            private Long userId;
            private BigDecimal total;
            private BigDecimal used;
            private BigDecimal residue;
        }
        ```
    - dao 接口实现
        ```java
        @Mapper
        public interface AccountDao {
            void decrease(@Param("userId") Long userId,@Param("money") BigDecimal money);
        }
        ```
        ```xml
        <?xml version="1.0" encoding="UTF-8" ?>
        <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
        <mapper namespace="com.atguigu.cloud.dao.AccountDao">
        
            <resultMap id="BaseResultMap" type="com.atguigu.cloud.domain.Account">
                <id column="id" property="id" jdbcType="BIGINT" />
                <result column="user_id" property="userId" jdbcType="BIGINT" />
                <result column="total" property="total" jdbcType="INTEGER" />
                <result column="used" property="used" jdbcType="INTEGER" />
                <result column="residue" property="residue" jdbcType="INTEGER" />
            </resultMap>
        
            <update id="decrease">
                update
                    t_account
                set
                    used = used + #{money},
        	    residue = residue - #{money}
                where
                    user_id= #{userId};
            </update>
        </mapper>
        ```
    - server 接口实现
        ```java
        // AccountService
        public interface AccountService {
            void decrease(Long userId, BigDecimal count);
        }
      
        // AccountServiceImpl
        @Service
        @Slf4j
        public class AccountServiceImpl implements AccountService {
        
            @Resource
            private AccountDao accountDao;
        
            @Override
            public void decrease(Long userId, BigDecimal money) {
                System.out.println("account ---------> 扣减余额开始");
                try {
                    TimeUnit.SECONDS.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                accountDao.decrease(userId, money);
                System.out.println("account ---------> 扣减余额结束");
            }
        }
        ```
    - controller 实现
        ```java
        @RestController
        @RequestMapping("/account")
        public class AccountController {
        
            @Resource
            private AccountService accountService;
        
            @RequestMapping("/decrease")
            public CommonResult decrease(Long userId, BigDecimal money) {
                accountService.decrease(userId, money);
                return new CommonResult(200,"扣减余额成功!");
            }
        
        }
        ```
    - config 配置  
        - 如上
        
## 原理简介

