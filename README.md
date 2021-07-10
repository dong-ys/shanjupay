# 闪聚支付

闪聚支付采用当前流行的前后端分离架构开发，由用户层、UI层、微服务层、数据层等部分组成，为PC、H5等客 

户端用户提供服务。

## 核心技术栈

| 软件名称              | 描述               | 版本            |
| --------------------- | ------------------ | --------------- |
| Jdk                   | Java环境           | 1.8             |
| Spring Boot           | 开发框架           | 2.1.3           |
| Spring Cloud Alibaba  | 微服务框架         | 2.1.0           |
| Spring Cloud Security | 用户认证           |                 |
| Spring Cloud OAuth2.0 | 用户认证           |                 |
| Nacos                 | 注册中心、配置中心 |                 |
| Dubbo                 | RPC框架            |                 |
| Redis                 | 缓存中间件         | 3.2.8 或 高版本 |
| MySQL                 | 数据库             | 5.7.X           |
| RocketMQ              | 消息中间件         | 4.5.0           |
| MyBatis-Plus          | 持久层框架         | 3.1.0           |
| Swagger               | 接口规范           |                 |

## 项目架构

```
shanjupay
├── config -- nacos配置文件,初始化sql语句
├── sailing -- 发送验证码服务
├── shanjupay-common -- 核心依赖包
├── shanjupay-gateway -- Saas平台,网关层
├── shanjupay-merchant-application -- 应用层,提供http接口
├── shanjupay-merchant -- 商户服务
├── shanjupay-payment-agent -- 支付服务
├── shanjupay-trasaction -- 交易服务
├── shanjupay-uaa -- Saas平台,用户认证授权
└── shanjupay-user -- Saas平台,用户认证授权
```
