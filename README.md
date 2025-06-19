# 在线商店 (Online Store)

一个基于 **Spring Boot 3.x** 和 **Spring Cloud** 的现代化企业级在线商店系统，集成了微服务架构、分布式配置、缓存、认证授权等企业级特性。

## 🚀 技术栈

### 核心框架
- **JDK 17** - 现代Java开发平台
- **Spring Boot 3.1.5** - 应用程序框架
- **Spring Cloud 2022.0.4** - 微服务生态
- **Spring Cloud Alibaba 2022.0.0.0** - 阿里巴巴微服务组件

### 数据与缓存
- **MyBatis 3.0.2** - 持久层框架
- **MySQL 8.0.33** - 关系型数据库
- **Redis (Jedis 4.3.1)** - 缓存与会话存储

### 配置与服务发现
- **Nacos 2.2.0** - 配置中心与服务发现
- **Spring Cloud Bootstrap** - 配置加载支持

### 其他特性
- **Spring Boot Actuator** - 应用监控
- **Spring AOP** - 面向切面编程
- **Bean Validation** - 参数校验
- **Jackson JSR310** - 时间序列化
- **Spring Boot Test** - 单元测试支持

## 🏗️ 项目架构

```
online-store/
├── src/main/java/com/example/onlinestore/
│   ├── OnlineStoreApplication.java         # 应用启动类
│   ├── annotation/                         # 自定义注解
│   │   ├── RequireAdmin.java               # 管理员权限注解
│   │   └── ValidateParams.java             # 参数验证注解
│   ├── aspect/                             # AOP切面
│   │   ├── AdminAuthAspect.java            # 管理员认证切面
│   │   └── ValidationAspect.java           # 参数验证切面
│   ├── config/                             # 配置类
│   │   ├── LocalConfigProperties.java      # 本地配置属性
│   │   ├── MessageConfig.java              # 国际化配置
│   │   ├── MyBatisConfig.java              # MyBatis配置
│   │   ├── NacosConfig.java                # Nacos配置
│   │   ├── RedisConfig.java                # Redis配置
│   │   ├── RestTemplateConfig.java         # HTTP客户端配置
│   │   ├── ValidationConfig.java           # 验证配置
│   │   └── WebConfig.java                  # Web配置
│   ├── context/                            # 上下文管理
│   │   └── UserContext.java                # 用户上下文
│   ├── controller/                         # 控制器层
│   │   ├── AuthController.java             # 认证控制器
│   │   ├── ProductController.java          # 产品控制器
│   │   └── UserController.java             # 用户控制器
│   ├── dto/                                # 数据传输对象
│   │   ├── CreateProductRequest.java       # 创建产品请求
│   │   ├── ErrorResponse.java              # 错误响应
│   │   ├── LoginRequest.java               # 登录请求
│   │   ├── LoginResponse.java              # 登录响应
│   │   ├── PageResponse.java               # 分页响应
│   │   ├── ProductPageRequest.java         # 产品分页请求
│   │   ├── UserPageRequest.java            # 用户分页请求
│   │   └── UserVO.java                     # 用户视图对象
│   ├── interceptor/                        # 拦截器
│   │   └── AuthInterceptor.java            # 认证拦截器
│   ├── mapper/                             # MyBatis映射器
│   │   ├── ProductMapper.java              # 产品映射器
│   │   └── UserMapper.java                 # 用户映射器
│   ├── model/                              # 实体模型
│   │   ├── Product.java                    # 产品实体
│   │   └── User.java                       # 用户实体
│   └── service/                            # 服务层
│       ├── ProductService.java             # 产品服务接口
│       ├── UserService.java                # 用户服务接口
│       └── impl/                           # 服务实现
│           ├── ProductServiceImpl.java     # 产品服务实现
│           └── UserServiceImpl.java        # 用户服务实现
├── src/main/resources/
│   ├── application.yml                     # 主配置文件
│   ├── application-local.yml              # 本地环境配置
│   ├── bootstrap.yml                       # 引导配置
│   ├── db/schema.sql                       # 数据库表结构
│   ├── i18n/                               # 国际化资源
│   │   ├── messages.properties             # 默认消息
│   │   └── messages_zh_CN.properties       # 中文消息
│   └── mapper/                             # MyBatis XML映射
│       ├── ProductMapper.xml               # 产品映射XML
│       └── UserMapper.xml                  # 用户映射XML
└── src/test/                               # 测试代码
    └── java/com/example/onlinestore/
        ├── aspect/                         # 切面测试
        ├── config/                         # 配置测试
        ├── controller/                     # 控制器测试
        └── service/                        # 服务测试
```

## ✨ 核心功能特性

### 🔐 认证与授权
- **基于Token的认证机制** - 安全的API访问控制
- **管理员权限控制** - 使用AOP切面实现细粒度权限管理
- **用户上下文管理** - ThreadLocal实现用户会话管理
- **认证拦截器** - 自动处理请求认证

### 📦 产品管理
- **产品CRUD操作** - 完整的产品生命周期管理
- **分页查询支持** - 高性能的数据分页
- **分类管理** - 产品分类组织
- **价格管理** - 支持精确的货币计算

### 👥 用户管理
- **用户注册登录** - 完整的用户生命周期
- **Token管理** - 安全的会话管理
- **用户信息维护** - 用户资料管理

### 🎯 企业级特性
- **参数验证** - 基于Bean Validation的自动参数校验
- **异常处理** - 统一的异常处理机制
- **国际化支持** - 多语言消息支持
- **配置管理** - 支持多环境配置
- **缓存集成** - Redis缓存提升性能
- **监控支持** - Spring Boot Actuator健康检查

### 🏛️ 架构特性
- **分层架构** - Controller/Service/Mapper清晰分层
- **依赖注入** - Spring IoC容器管理
- **面向切面编程** - 横切关注点分离
- **配置外部化** - 支持Nacos配置中心

## 📋 环境要求

| 组件 | 版本要求 | 说明 |
|------|----------|------|
| JDK | 17+ | 支持现代Java特性 |
| Maven | 3.6+ | 项目构建工具 |
| MySQL | 8.0+ | 主数据库 |
| Redis | 6.0+ | 缓存和会话存储 |
| Nacos | 2.2.0+ | 配置中心(可选) |

## 🔧 快速开始

### 1. 环境准备

```bash
# 确保Java 17已安装
java -version

# 确保Maven已安装
mvn -version
```

### 2. 数据库初始化

```sql
-- 创建数据库
CREATE DATABASE online_store DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE online_store;

-- 创建用户表(schema.sql中的内容)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    token VARCHAR(100),
    token_expire_time DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 3. 配置文件修改

修改 `src/main/resources/application.yml`:

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
      password: your_redis_password
```

### 4. 启动应用

```bash
# 开发模式启动
mvn spring-boot:run

# 或者编译后启动
mvn clean package
java -jar target/online-store-1.0-SNAPSHOT.jar
```

### 5. 验证部署

```bash
# 健康检查
curl http://localhost:8080/actuator/health

# 预期响应
{"status":"UP"}
```

## 📡 API 文档

### 认证相关 API

#### 用户登录
```http
POST /api/auth/login
Content-Type: application/json

{
    "username": "admin",
    "password": "password"
}
```

**响应示例:**
```json
{
    "token": "abcd1234-efgh-5678-ijkl-9012mnop3456",
    "username": "admin",
    "expireTime": "2024-12-01T10:30:00"
}
```

### 产品管理 API

#### 创建产品 (需要管理员权限)
```http
POST /api/products
X-Token: your-auth-token
Content-Type: application/json

{
    "name": "iPhone 15",
    "category": "Electronics",
    "price": 999.99
}
```

#### 查询产品列表
```http
GET /api/products?page=1&size=10&category=Electronics&name=iPhone
X-Token: your-auth-token
```

**响应示例:**
```json
{
    "data": [
        {
            "id": 1,
            "name": "iPhone 15",
            "category": "Electronics",
            "price": 999.99,
            "createdAt": "2024-12-01T09:00:00",
            "updatedAt": "2024-12-01T09:00:00"
        }
    ],
    "total": 1,
    "page": 1,
    "size": 10,
    "totalPages": 1
}
```

### 用户管理 API

#### 查询用户列表 (需要管理员权限)
```http
GET /api/users?page=1&size=10
X-Token: your-auth-token
```

## ⚙️ 配置说明

### 应用配置 (application.yml)

```yaml
# 服务端口
server:
  port: 8080

# Spring配置
spring:
  profiles:
    active: local  # 激活的环境配置
  
  # 数据源配置
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/online_store
    username: root
    password: 
  
  # Redis配置
  data:
    redis:
      host: localhost
      port: 6379
      password:
      database: 0

# MyBatis配置
mybatis:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.example.onlinestore.model
  configuration:
    map-underscore-to-camel-case: true

# 管理员配置
admin:
  auth:
    username: admin
    password: password
```

### 多环境配置

项目支持多环境配置，可以通过以下方式切换环境：

```bash
# 本地环境
java -jar app.jar --spring.profiles.active=local

# 开发环境
java -jar app.jar --spring.profiles.active=dev

# 生产环境
java -jar app.jar --spring.profiles.active=prod
```

### Nacos配置中心集成

如需使用Nacos配置中心，修改 `bootstrap.yml`:

```yaml
spring:
  cloud:
    nacos:
      config:
        server-addr: localhost:8848
        file-extension: yml
        namespace: your-namespace-id
      discovery:
        server-addr: localhost:8848
        namespace: your-namespace-id
```

## 🧪 测试

### 运行单元测试
```bash
mvn test
```

### 运行集成测试
```bash
mvn verify
```

### 测试覆盖率报告
```bash
mvn jacoco:report
```

## 🔍 监控与健康检查

### Actuator 端点

- **健康检查**: `GET /actuator/health`
- **应用信息**: `GET /actuator/info`
- **指标监控**: `GET /actuator/metrics`
- **环境信息**: `GET /actuator/env`

### 日志级别

可通过配置文件或Actuator动态调整日志级别：

```bash
# 查看日志级别
curl http://localhost:8080/actuator/loggers/com.example.onlinestore

# 动态调整日志级别
curl -X POST http://localhost:8080/actuator/loggers/com.example.onlinestore \
  -H 'Content-Type: application/json' \
  -d '{"configuredLevel": "DEBUG"}'
```

## 🚀 部署指南

### Docker 部署

创建 `Dockerfile`:

```dockerfile
FROM openjdk:17-jre-slim
COPY target/online-store-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

构建和运行:

```bash
# 构建镜像
docker build -t online-store:latest .

# 运行容器
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/online_store \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=password \
  online-store:latest
```

### 生产环境建议

1. **JVM 调优**:
```bash
java -Xms512m -Xmx2g -XX:+UseG1GC -jar app.jar
```

2. **外部化配置**:
```bash
java -jar app.jar --spring.config.location=classpath:/,/opt/config/
```

3. **日志配置**:
```bash
java -jar app.jar --logging.config=/opt/config/logback-spring.xml
```

## 🤝 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 📞 联系方式

- 项目维护者: [Your Name]
- 邮箱: your.email@example.com
- 项目链接: [https://github.com/yourusername/online-store](https://github.com/yourusername/online-store)

---

⭐ 如果这个项目对您有帮助，请给我们一个 Star！ 