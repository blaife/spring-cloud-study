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
