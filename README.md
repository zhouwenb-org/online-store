# Online Store - 在线商店系统

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2022.0.4-blue.svg)](https://spring.io/projects/spring-cloud)
[![MyBatis](https://img.shields.io/badge/MyBatis-3.0.2-red.svg)](https://mybatis.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)

## 📖 项目简介

这是一个基于Spring Cloud生态的现代化在线商店系统，采用微服务架构设计。系统提供完整的电商基础功能，包括用户认证、商品管理、订单处理等核心业务模块。

## ✨ 功能特性

### 🔐 用户认证系统
- Token-based认证机制
- 管理员权限控制
- 国际化支持（中文/英文）

### 📦 商品管理
- 商品CRUD操作
- 分类管理
- 分页查询
- 价格管理

### 🛡️ 安全特性
- 基于AOP的权限控制
- 参数验证
- 异常统一处理
- 请求拦截器

### 🔧 技术特性
- 分布式配置管理（Nacos）
- 缓存支持（Redis）
- 数据持久化（MyBatis + MySQL）
- 健康检查（Actuator）

## 🏗️ 技术栈

### 核心框架
- **Java 17** - 编程语言
- **Spring Boot 3.1.5** - 应用框架
- **Spring Cloud 2022.0.4** - 微服务框架
- **Spring Cloud Alibaba 2022.0.0.0** - 阿里云组件

### 数据层
- **MyBatis 3.0.2** - ORM框架
- **MySQL 8.0.33** - 关系型数据库
- **Redis** - 缓存数据库

### 服务发现与配置
- **Nacos 2.2.0** - 配置中心与服务注册

### 其他组件
- **Jackson** - JSON处理
- **Spring AOP** - 面向切面编程
- **Spring Validation** - 参数验证
- **Jedis 4.3.1** - Redis客户端

## 🏛️ 项目架构

```
online-store/
├── src/
│   ├── main/
│   │   ├── java/com/example/onlinestore/
│   │   │   ├── OnlineStoreApplication.java         # 应用启动类
│   │   │   ├── annotation/                         # 自定义注解
│   │   │   │   ├── RequireAdmin.java              # 管理员权限注解
│   │   │   │   └── ValidateParams.java            # 参数验证注解
│   │   │   ├── aspect/                            # 切面类
│   │   │   │   ├── AdminAuthAspect.java           # 管理员认证切面
│   │   │   │   └── ValidationAspect.java          # 参数验证切面
│   │   │   ├── config/                            # 配置类
│   │   │   │   ├── MyBatisConfig.java             # MyBatis配置
│   │   │   │   ├── RedisConfig.java               # Redis配置
│   │   │   │   ├── NacosConfig.java               # Nacos配置
│   │   │   │   └── WebConfig.java                 # Web配置
│   │   │   ├── controller/                        # 控制器层
│   │   │   │   ├── AuthController.java            # 认证控制器
│   │   │   │   ├── ProductController.java         # 商品控制器
│   │   │   │   └── UserController.java            # 用户控制器
│   │   │   ├── dto/                               # 数据传输对象
│   │   │   ├── interceptor/                       # 拦截器
│   │   │   ├── mapper/                            # 数据访问层
│   │   │   ├── model/                             # 实体类
│   │   │   └── service/                           # 业务逻辑层
│   │   └── resources/
│   │       ├── application.yml                    # 应用配置
│   │       ├── application-local.yml              # 本地环境配置
│   │       ├── bootstrap.yml                      # 启动配置
│   │       ├── db/schema.sql                      # 数据库脚本
│   │       ├── i18n/                             # 国际化资源
│   │       └── mapper/                            # MyBatis映射文件
│   └── test/                                      # 测试代码
├── pom.xml                                        # Maven依赖配置
└── README.md                                      # 项目说明文档
```

## 📡 API 接口

### 认证接口
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "用户名",
  "password": "密码"
}
```

### 商品接口
```http
# 创建商品（需要管理员权限）
POST /api/products
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "商品名称",
  "category": "商品分类",
  "price": 99.99
}

# 查询商品列表
GET /api/products?page=1&size=10&category=电子产品
```

### 用户接口
```http
# 查询用户列表（需要管理员权限）
GET /api/users?page=1&size=10
Authorization: Bearer {token}
```

## 🚀 环境要求

- **JDK 17** 或更高版本
- **Maven 3.6+**
- **MySQL 8.0+**
- **Redis 6.0+**
- **Nacos 2.2.0** （可选，用于分布式配置）

## 📦 安装和运行

### 1. 克隆项目
```bash
git clone <repository-url>
cd online-store
```

### 2. 数据库准备
```sql
-- 创建数据库
CREATE DATABASE online_store DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE online_store;

-- 执行建表脚本
source src/main/resources/db/schema.sql
```

### 3. Redis 启动
```bash
# Linux/Mac
redis-server

# Docker
docker run -d -p 6379:6379 redis:7-alpine
```

### 4. 配置文件修改
修改 `src/main/resources/application.yml` 中的数据库连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/online_store?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
  data:
    redis:
      host: localhost
      port: 6379
      password: # 如果Redis设置了密码
```

### 5. 启动应用
```bash
# 使用Maven启动
mvn spring-boot:run

# 或者编译后启动
mvn clean package
java -jar target/online-store-1.0-SNAPSHOT.jar
```

### 6. 验证运行
访问 http://localhost:8080/actuator/health 检查应用健康状态

## ⚙️ 配置说明

### 环境配置
- `application.yml` - 主配置文件
- `application-local.yml` - 本地开发环境配置
- `bootstrap.yml` - 启动时配置（用于Nacos）

### 关键配置项
```yaml
# 管理员认证配置
admin:
  auth:
    username: admin
    password: password

# Nacos配置（可选）
spring:
  cloud:
    nacos:
      discovery:
        enabled: false  # 本地开发时设为false
```

## 🧪 测试

### 运行单元测试
```bash
mvn test
```

### 运行特定测试类
```bash
mvn test -Dtest=UserServiceTest
```

### 测试覆盖的模块
- 控制器层测试
- 服务层测试
- AOP切面测试
- 配置类测试

## 📋 开发规范

### 代码规范
- 使用驼峰命名法
- 类名使用大驼峰，方法名使用小驼峰
- 常量使用全大写下划线分隔
- 包名使用小写字母

### 日志规范
- DEBUG: 详细调试信息
- INFO: 一般性信息
- WARN: 警告信息
- ERROR: 错误信息

### 异常处理
- 使用统一的异常处理机制
- 业务异常返回4xx状态码
- 系统异常返回5xx状态码

## 🤝 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 📞 联系我们

如有问题或建议，请提交 Issue 或联系开发团队。

---

⭐ 如果这个项目对您有帮助，请给它一个星标！ 