# Online Store 在线商店

这是一个基于Spring Boot和Spring Cloud的现代化在线商店后端服务，采用微服务架构设计，提供完整的用户管理、商品管理、权限控制等功能。

## 🚀 项目特色

- **现代化架构**：基于Spring Boot 3.x和Spring Cloud 2022的最新技术栈
- **微服务设计**：支持Nacos服务注册与发现、配置中心
- **权限控制**：基于AOP的细粒度权限管理
- **参数验证**：自动参数验证和国际化错误消息
- **高性能**：Redis缓存支持，MyBatis高效数据访问
- **生产就绪**：完善的日志、监控、测试覆盖

## 🛠 技术栈

### 核心框架
- **JDK 17** - Java开发环境
- **Spring Boot 3.1.5** - 应用框架
- **Spring Cloud 2022.0.4** - 微服务框架
- **Spring Cloud Alibaba 2022.0.0.0** - 阿里云微服务解决方案

### 数据存储
- **MySQL 8.0.33** - 关系型数据库
- **MyBatis 3.0.2** - ORM框架
- **Redis (Jedis 4.3.1)** - 缓存数据库

### 服务治理
- **Nacos 2.2.0** - 服务注册中心和配置中心
- **Spring Boot Actuator** - 应用监控

### 开发工具
- **Spring Boot Validation** - 参数验证
- **Spring AOP** - 面向切面编程
- **Jackson JSR310** - JSON序列化
- **SLF4J + Logback** - 日志框架

## 📋 核心功能

### 🔐 认证授权
- 用户登录认证
- Token管理和过期控制
- 管理员权限验证
- 基于AOP的权限拦截

### 👥 用户管理
- 用户信息管理
- 分页查询用户列表
- 用户状态管理

### 📦 商品管理
- 商品创建和编辑
- 商品列表分页查询
- 商品状态管理

### 🌐 系统功能
- 国际化支持 (中文/英文)
- 统一异常处理
- 参数自动验证
- 请求日志记录

## 📁 项目结构

```
online-store/
├── src/
│   ├── main/
│   │   ├── java/com/example/onlinestore/
│   │   │   ├── OnlineStoreApplication.java      # 应用启动类
│   │   │   ├── annotation/                      # 自定义注解
│   │   │   │   ├── RequireAdmin.java           # 管理员权限注解
│   │   │   │   └── ValidateParams.java         # 参数验证注解
│   │   │   ├── aspect/                          # AOP切面
│   │   │   │   ├── AdminAuthAspect.java        # 权限验证切面
│   │   │   │   └── ValidationAspect.java       # 参数验证切面
│   │   │   ├── config/                          # 配置类
│   │   │   │   ├── MessageConfig.java          # 国际化配置
│   │   │   │   ├── MyBatisConfig.java          # MyBatis配置
│   │   │   │   ├── NacosConfig.java            # Nacos配置
│   │   │   │   ├── RedisConfig.java            # Redis配置
│   │   │   │   └── WebConfig.java              # Web配置
│   │   │   ├── controller/                      # 控制器层
│   │   │   │   ├── AuthController.java         # 认证接口
│   │   │   │   ├── UserController.java         # 用户管理接口
│   │   │   │   └── ProductController.java      # 商品管理接口
│   │   │   ├── dto/                            # 数据传输对象
│   │   │   │   ├── LoginRequest.java           # 登录请求
│   │   │   │   ├── LoginResponse.java          # 登录响应
│   │   │   │   ├── PageResponse.java           # 分页响应
│   │   │   │   └── ...                        # 其他DTO
│   │   │   ├── interceptor/                    # 拦截器
│   │   │   │   └── AuthInterceptor.java       # 认证拦截器
│   │   │   ├── mapper/                         # MyBatis映射接口
│   │   │   │   ├── UserMapper.java            # 用户数据访问
│   │   │   │   └── ProductMapper.java         # 商品数据访问
│   │   │   ├── model/                          # 实体类
│   │   │   │   ├── User.java                  # 用户实体
│   │   │   │   └── Product.java               # 商品实体
│   │   │   └── service/                        # 服务层
│   │   │       ├── UserService.java           # 用户服务接口
│   │   │       ├── ProductService.java        # 商品服务接口
│   │   │       └── impl/                      # 服务实现
│   │   └── resources/
│   │       ├── application.yml                 # 应用配置
│   │       ├── application-local.yml           # 本地环境配置
│   │       ├── bootstrap.yml                  # 启动配置
│   │       ├── db/schema.sql                  # 数据库初始化脚本
│   │       ├── i18n/                          # 国际化资源
│   │       │   ├── messages.properties        # 默认消息
│   │       │   └── messages_zh_CN.properties  # 中文消息
│   │       └── mapper/                        # MyBatis XML映射
│   │           ├── UserMapper.xml             # 用户SQL映射
│   │           └── ProductMapper.xml          # 商品SQL映射
│   └── test/                                  # 测试代码
├── pom.xml                                    # Maven配置
└── README.md                                  # 项目说明
```

## 🔧 环境要求

- **JDK 17+** - Java开发环境
- **Maven 3.6+** - 构建工具
- **MySQL 8.0+** - 数据库
- **Redis 6.0+** - 缓存服务
- **Nacos 2.2.0+** (可选) - 服务注册中心

## 🚀 快速开始

### 1. 环境准备

#### 启动MySQL服务
```bash
# 创建数据库
mysql -u root -p
CREATE DATABASE online_store DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 启动Redis服务
```bash
# 使用Docker启动Redis (推荐)
docker run -d --name redis -p 6379:6379 redis:7-alpine

# 或使用本地Redis服务
redis-server
```

#### 启动Nacos (可选)
```bash
# 使用Docker启动Nacos
docker run -d --name nacos -p 8848:8848 -p 9848:9848 \
  -e MODE=standalone \
  nacos/nacos-server:v2.2.0
```

### 2. 配置应用

#### 修改数据库配置
编辑 `src/main/resources/application.yml`:
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
      password: # 如果有密码请填写
```

#### 配置管理员账号
```yaml
admin:
  auth:
    username: admin
    password: password  # 建议修改为强密码
```

### 3. 运行应用

#### 使用Maven运行
```bash
# 编译项目
mvn clean compile

# 运行应用
mvn spring-boot:run
```

#### 使用IDE运行
直接运行 `OnlineStoreApplication.java` 的main方法

#### 使用Docker运行
```bash
# 构建镜像
mvn clean package
docker build -t online-store .

# 运行容器
docker run -d --name online-store -p 8080:8080 online-store
```

### 4. 验证部署

访问健康检查端点：
```bash
curl http://localhost:8080/actuator/health
```

应该返回：
```json
{"status":"UP"}
```

## 📖 API文档

### 认证接口

#### 用户登录
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "password"
}
```

**响应示例：**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "expireTime": "2024-01-01T12:00:00",
  "user": {
    "id": 1,
    "username": "admin"
  }
}
```

### 用户管理接口

#### 获取用户列表 (需要管理员权限)
```http
GET /api/users?page=1&size=10&username=test
Authorization: Bearer {token}
```

**响应示例：**
```json
{
  "records": [
    {
      "id": 1,
      "username": "testuser",
      "createdAt": "2024-01-01T10:00:00"
    }
  ],
  "total": 1,
  "page": 1,
  "size": 10
}
```

### 商品管理接口

#### 创建商品 (需要管理员权限)
```http
POST /api/products
Content-Type: application/json
Authorization: Bearer {token}

{
  "name": "iPhone 15",
  "description": "最新款iPhone",
  "price": 6999.00,
  "stock": 100
}
```

#### 获取商品列表
```http
GET /api/products?page=1&size=10&name=iPhone
```

**响应示例：**
```json
{
  "records": [
    {
      "id": 1,
      "name": "iPhone 15",
      "description": "最新款iPhone",
      "price": 6999.00,
      "stock": 100,
      "createdAt": "2024-01-01T10:00:00"
    }
  ],
  "total": 1,
  "page": 1,
  "size": 10
}
```

## ⚙️ 配置说明

### 应用配置文件

#### application.yml - 主配置文件
```yaml
server:
  port: 8080                    # 服务端口

spring:
  profiles:
    active: local               # 激活的配置文件
  application:
    name: online-store          # 应用名称
  
  # 数据库配置
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
      database: 0
  
  # Nacos配置 (可选)
  cloud:
    nacos:
      discovery:
        enabled: false          # 是否启用服务发现
        register-enabled: false # 是否注册服务

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

#### application-local.yml - 本地开发配置
用于覆盖本地开发环境的特定配置。

#### bootstrap.yml - 启动配置
用于配置Nacos等需要在应用启动前加载的配置。

### 环境变量配置

支持通过环境变量覆盖配置：

```bash
# 数据库配置
export MYSQL_HOST=localhost
export MYSQL_PORT=3306
export MYSQL_USERNAME=root
export MYSQL_PASSWORD=yourpassword

# Redis配置
export REDIS_HOST=localhost
export REDIS_PORT=6379
export REDIS_PASSWORD=

# Nacos配置
export NACOS_ENABLED=true
export NACOS_SERVER_ADDR=localhost:8848

# 运行应用
mvn spring-boot:run
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

### 测试覆盖率
```bash
mvn jacoco:report
```

### API测试
推荐使用Postman或curl进行API测试：

```bash
# 设置变量
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}' \
  | jq -r '.token')

# 测试用户列表
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/users

# 测试商品列表
curl http://localhost:8080/api/products
```

## 📊 监控和日志

### 应用监控
- **健康检查**: `GET /actuator/health`
- **应用信息**: `GET /actuator/info`
- **指标数据**: `GET /actuator/metrics`

### 日志配置
应用使用SLF4J + Logback进行日志记录：

- **日志级别**: DEBUG (开发) / INFO (生产)
- **日志格式**: 包含时间戳、级别、类名、消息
- **日志输出**: 控制台 + 文件 (可配置)

## 🚢 部署指南

### Docker部署

#### 1. 创建Dockerfile
```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/online-store-1.0-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### 2. 构建和运行
```bash
# 构建应用
mvn clean package -DskipTests

# 构建镜像
docker build -t online-store:latest .

# 运行容器
docker run -d \
  --name online-store \
  -p 8080:8080 \
  -e MYSQL_HOST=host.docker.internal \
  -e REDIS_HOST=host.docker.internal \
  online-store:latest
```

### Docker Compose部署

创建 `docker-compose.yml`:
```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: online_store
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"

  online-store:
    build: .
    ports:
      - "8080:8080"
    environment:
      MYSQL_HOST: mysql
      MYSQL_USERNAME: root
      MYSQL_PASSWORD: rootpassword
      REDIS_HOST: redis
    depends_on:
      - mysql
      - redis

volumes:
  mysql_data:
```

运行：
```bash
docker-compose up -d
```

### 生产环境部署

#### 1. 配置优化
```yaml
# application-prod.yml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
  data:
    redis:
      jedis:
        pool:
          max-active: 20
          max-idle: 10

logging:
  level:
    com.example.onlinestore: INFO
    org.springframework: WARN
  file:
    name: /var/log/online-store/application.log
```

#### 2. JVM优化
```bash
java -jar \
  -Xms1024m -Xmx2048m \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=100 \
  -Dspring.profiles.active=prod \
  online-store-1.0-SNAPSHOT.jar
```

## 🤝 开发指南

### 代码规范
- 遵循阿里巴巴Java开发手册
- 使用统一的代码格式化规则
- 必须编写单元测试，覆盖率不低于80%
- 所有公共方法必须有完整的JavaDoc注释

### 提交规范
```bash
# 功能开发
git commit -m "feat: 添加用户注册功能"

# Bug修复
git commit -m "fix: 修复登录token过期问题"

# 文档更新
git commit -m "docs: 更新API文档"
```

### 分支管理
- `main`: 主分支，用于生产发布
- `dev`: 开发分支，用于功能集成
- `feature/*`: 功能分支，用于新功能开发
- `hotfix/*`: 热修复分支，用于紧急修复

## 📝 更新日志

### v1.0.0 (2024-01-01)
- ✨ 初始版本发布
- 🔐 实现用户认证和权限管理
- 📦 实现商品管理功能
- 🌐 支持国际化
- 🔧 集成Nacos配置中心
- 📊 添加应用监控

## 📄 许可证

本项目使用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 🆘 帮助和支持

### 常见问题

**Q: 启动时报连接数据库失败？**
A: 请检查MySQL服务是否启动，数据库配置是否正确。

**Q: Redis连接失败？**
A: 请检查Redis服务是否启动，端口是否正确。

**Q: 登录提示用户名或密码错误？**
A: 默认管理员账号为 admin/password，请检查配置文件。

### 技术支持
- 📧 邮箱: support@example.com
- 🐛 问题反馈: [GitHub Issues](https://github.com/example/online-store/issues)
- 📖 文档: [项目Wiki](https://github.com/example/online-store/wiki)

---

**Made with ❤️ by Online Store Team** 