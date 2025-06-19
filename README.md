# 在线商店系统 (Online Store)

这是一个基于Spring Cloud微服务架构的现代化在线商店管理系统，提供完整的用户认证、产品管理和权限控制功能。

## 🚀 项目特性

- **微服务架构**: 基于Spring Cloud 2022.0.4构建的分布式系统
- **用户认证系统**: 基于Token的认证机制，支持用户登录和会话管理
- **产品管理**: 完整的商品CRUD操作，支持分页查询和分类管理
- **权限控制**: 基于AOP的管理员权限验证
- **配置中心**: 集成Nacos实现动态配置管理
- **缓存支持**: 集成Redis提供高性能缓存
- **国际化**: 支持多语言消息提示
- **参数验证**: 自动参数校验和错误处理
- **数据持久化**: 使用MyBatis + MySQL实现数据存储

## 🛠 技术栈

### 核心框架
- **Java**: JDK 17
- **Spring Boot**: 3.1.5
- **Spring Cloud**: 2022.0.4
- **Spring Cloud Alibaba**: 2022.0.0.0

### 数据存储
- **MySQL**: 8.0.33 (主数据库)
- **Redis**: 6.0+ (缓存和会话存储)
- **MyBatis**: 3.0.2 (ORM框架)

### 中间件
- **Nacos**: 2.2.0 (配置中心和服务发现)
- **Jedis**: 4.3.1 (Redis客户端)

### 开发工具
- **Maven**: 3.6+ (项目构建)
- **JUnit**: 5.x (单元测试)

## 📁 项目结构

```
online-store/
├── src/
│   ├── main/
│   │   ├── java/com/example/onlinestore/
│   │   │   ├── OnlineStoreApplication.java          # 应用程序入口
│   │   │   ├── annotation/                          # 自定义注解
│   │   │   │   ├── RequireAdmin.java               # 管理员权限注解
│   │   │   │   └── ValidateParams.java             # 参数验证注解
│   │   │   ├── aspect/                              # AOP切面
│   │   │   │   ├── AdminAuthAspect.java            # 管理员权限切面
│   │   │   │   └── ValidationAspect.java           # 参数验证切面
│   │   │   ├── config/                              # 配置类
│   │   │   │   ├── RedisConfig.java                # Redis配置
│   │   │   │   ├── MyBatisConfig.java              # MyBatis配置
│   │   │   │   ├── NacosConfig.java                # Nacos配置
│   │   │   │   └── WebConfig.java                  # Web配置
│   │   │   ├── controller/                          # 控制器层
│   │   │   │   ├── AuthController.java             # 认证控制器
│   │   │   │   ├── ProductController.java          # 产品控制器
│   │   │   │   └── UserController.java             # 用户控制器
│   │   │   ├── dto/                                 # 数据传输对象
│   │   │   │   ├── LoginRequest.java               # 登录请求
│   │   │   │   ├── LoginResponse.java              # 登录响应
│   │   │   │   ├── CreateProductRequest.java       # 创建产品请求
│   │   │   │   └── PageResponse.java               # 分页响应
│   │   │   ├── interceptor/                         # 拦截器
│   │   │   │   └── AuthInterceptor.java            # 认证拦截器
│   │   │   ├── mapper/                              # MyBatis映射器
│   │   │   │   ├── UserMapper.java                 # 用户数据访问
│   │   │   │   └── ProductMapper.java              # 产品数据访问
│   │   │   ├── model/                               # 实体模型
│   │   │   │   ├── User.java                       # 用户实体
│   │   │   │   └── Product.java                    # 产品实体
│   │   │   ├── service/                             # 业务服务层
│   │   │   │   ├── UserService.java                # 用户服务接口
│   │   │   │   ├── ProductService.java             # 产品服务接口
│   │   │   │   └── impl/                           # 服务实现
│   │   │   │       ├── UserServiceImpl.java        # 用户服务实现
│   │   │   │       └── ProductServiceImpl.java     # 产品服务实现
│   │   │   └── context/                             # 上下文
│   │   │       └── UserContext.java                # 用户上下文
│   │   └── resources/
│   │       ├── application.yml                      # 应用配置
│   │       ├── application-local.yml                # 本地环境配置
│   │       ├── bootstrap.yml                        # 启动配置
│   │       ├── db/schema.sql                        # 数据库脚本
│   │       ├── i18n/                                # 国际化资源
│   │       │   ├── messages.properties              # 默认消息
│   │       │   └── messages_zh_CN.properties        # 中文消息
│   │       └── mapper/                              # MyBatis映射文件
│   │           ├── UserMapper.xml                   # 用户SQL映射
│   │           └── ProductMapper.xml                # 产品SQL映射
│   └── test/                                        # 测试代码
│       └── java/com/example/onlinestore/
│           ├── controller/                          # 控制器测试
│           ├── service/                             # 服务测试
│           └── aspect/                              # 切面测试
├── pom.xml                                          # Maven配置
└── README.md                                        # 项目说明
```

## 🔧 系统要求

### 开发环境
- **JDK**: 17或更高版本
- **Maven**: 3.6或更高版本
- **IDE**: IntelliJ IDEA 或 Eclipse

### 运行环境
- **MySQL**: 8.0或更高版本
- **Redis**: 6.0或更高版本
- **Nacos**: 2.2.0或更高版本（可选）

## 🚀 快速开始

### 1. 环境准备

#### 安装MySQL
```bash
# 使用Docker运行MySQL
docker run -d \
  --name mysql-online-store \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=online_store \
  mysql:8.0
```

#### 安装Redis
```bash
# 使用Docker运行Redis
docker run -d \
  --name redis-online-store \
  -p 6379:6379 \
  redis:7-alpine
```

### 2. 数据库初始化

```sql
-- 创建数据库
CREATE DATABASE online_store DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE online_store;

-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    token VARCHAR(100),
    token_expire_time DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建产品表（如需要）
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50),
    price DECIMAL(10,2) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 3. 配置修改

编辑 `src/main/resources/application-local.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/online_store?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_mysql_password
  data:
    redis:
      host: localhost
      port: 6379
      password: your_redis_password  # 如果设置了密码
```

### 4. 运行应用

```bash
# 克隆项目
git clone <repository-url>
cd online-store

# 编译项目
mvn clean compile

# 运行测试
mvn test

# 启动应用
mvn spring-boot:run
```

应用启动后访问：http://localhost:8080

## 📚 API接口

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
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "admin",
  "expireTime": "2024-01-01T12:00:00"
}
```

### 产品管理接口

#### 创建产品（需要管理员权限）
```http
POST /api/products
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "商品名称",
  "category": "电子产品",
  "price": 999.99
}
```

#### 查询产品列表
```http
GET /api/products?page=1&size=10&category=电子产品
```

### 用户管理接口

#### 查询用户列表（需要管理员权限）
```http
GET /api/users?page=1&size=10
Authorization: Bearer <token>
```

## 🔐 权限说明

### 管理员账户
- **用户名**: admin
- **密码**: password

### 权限级别
- **普通用户**: 可以查看产品列表
- **管理员**: 可以创建产品、管理用户

## 🧪 测试

### 运行单元测试
```bash
mvn test
```

### 测试覆盖范围
- 控制器层测试
- 服务层测试  
- AOP切面测试
- 配置类测试

## 🐳 Docker部署

### 构建Docker镜像
```bash
# 构建应用
mvn clean package -DskipTests

# 构建Docker镜像
docker build -t online-store:latest .
```

### 使用Docker Compose部署
```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: online_store
    ports:
      - "3306:3306"
    
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
      
  app:
    image: online-store:latest
    ports:
      - "8080:8080"
    depends_on:
      - mysql
      - redis
    environment:
      SPRING_PROFILES_ACTIVE: local
```

## 🔧 配置说明

### Nacos配置中心
如需使用Nacos，设置环境变量：
```bash
export NACOS_ENABLED=true
export NACOS_SERVER_ADDR=localhost:8848
```

### Redis集群
支持Redis集群配置，在application.yml中配置cluster节点。

## 🤝 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 🆘 常见问题

### Q: 启动时数据库连接失败？
A: 请检查MySQL服务是否启动，数据库配置是否正确。

### Q: Redis连接超时？
A: 请确认Redis服务正常运行，检查防火墙设置。

### Q: Nacos服务注册失败？
A: 确认Nacos服务已启动，检查网络连接和配置。

## 📞 联系方式

如有问题或建议，请通过以下方式联系：
- 创建 Issue
- 发送邮件到 developer@example.com 