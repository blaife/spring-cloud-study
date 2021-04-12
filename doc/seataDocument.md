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

[业务数据库准备](../sql)

