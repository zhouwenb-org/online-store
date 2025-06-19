# Online Store - 在线商店系统

这是一个基于Spring Cloud微服务架构的现代化在线商店后端系统，提供完整的用户管理、商品管理和权限控制功能。

## 🚀 技术栈

### 核心框架
- **JDK 17** - Java运行环境
- **Spring Boot 3.1.5** - 应用框架
- **Spring Cloud 2022.0.4** - 微服务框架
- **Spring Cloud Alibaba 2022.0.0.0** - 阿里云组件集成

### 数据存储
- **MySQL 8.0** - 关系型数据库
- **Redis 6.0+** - 缓存和会话存储
- **MyBatis 3.0.2** - 数据持久层框架

### 配置与注册中心
- **Nacos 2.2.0** - 配置中心和服务发现

### 其他组件
- **Spring Boot Actuator** - 应用监控
- **Spring AOP** - 面向切面编程
- **Bean Validation** - 参数校验
- **Jackson JSR310** - JSON序列化

## 🏗️ 项目架构

```
src/
├── main/
│   ├── java/com/example/onlinestore/
│   │   ├── OnlineStoreApplication.java          # 主启动类
│   │   ├── annotation/                          # 自定义注解
│   │   │   ├── RequireAdmin.java               # 管理员权限注解
│   │   │   └── ValidateParams.java             # 参数校验注解
│   │   ├── aspect/                             # AOP切面
│   │   │   ├── AdminAuthAspect.java            # 管理员权限验证
│   │   │   └── ValidationAspect.java           # 参数校验切面
│   │   ├── config/                             # 配置类
│   │   │   ├── MessageConfig.java              # 国际化配置
│   │   │   ├── MyBatisConfig.java              # MyBatis配置
│   │   │   ├── NacosConfig.java                # Nacos配置
│   │   │   ├── RedisConfig.java                # Redis配置
│   │   │   └── WebConfig.java                  # Web配置
│   │   ├── context/                            # 上下文管理
│   │   │   └── UserContext.java                # 用户上下文
│   │   ├── controller/                         # 控制器层
│   │   │   ├── AuthController.java             # 认证接口
│   │   │   ├── ProductController.java          # 商品管理接口
│   │   │   └── UserController.java             # 用户管理接口
│   │   ├── dto/                                # 数据传输对象
│   │   │   ├── CreateProductRequest.java       # 创建商品请求
│   │   │   ├── LoginRequest.java               # 登录请求
│   │   │   ├── LoginResponse.java              # 登录响应
│   │   │   ├── PageResponse.java               # 分页响应
│   │   │   └── UserVO.java                     # 用户视图对象
│   │   ├── interceptor/                        # 拦截器
│   │   │   └── AuthInterceptor.java            # 认证拦截器
│   │   ├── mapper/                             # 数据访问层
│   │   │   ├── ProductMapper.java              # 商品数据访问
│   │   │   └── UserMapper.java                 # 用户数据访问
│   │   ├── model/                              # 实体模型
│   │   │   ├── Product.java                    # 商品实体
│   │   │   └── User.java                       # 用户实体
│   │   └── service/                            # 业务服务层
│   │       ├── ProductService.java             # 商品服务接口
│   │       ├── UserService.java                # 用户服务接口
│   │       └── impl/                           # 服务实现
│   │           ├── ProductServiceImpl.java     # 商品服务实现
│   │           └── UserServiceImpl.java        # 用户服务实现
│   └── resources/
│       ├── application.yml                     # 主配置文件
│       ├── application-local.yml               # 本地环境配置
│       ├── bootstrap.yml                       # 引导配置
│       ├── db/schema.sql                       # 数据库架构
│       ├── i18n/                              # 国际化资源
│       │   ├── messages.properties             # 默认消息
│       │   └── messages_zh_CN.properties       # 中文消息
│       └── mapper/                             # MyBatis映射文件
│           ├── ProductMapper.xml               # 商品查询映射
│           └── UserMapper.xml                  # 用户查询映射
└── test/                                       # 测试代码
    └── java/com/example/onlinestore/
        ├── aspect/                             # 切面测试
        ├── config/                             # 配置测试
        ├── controller/                         # 控制器测试
        └── service/                            # 服务测试
```

## ✨ 核心特性

### 🔐 认证与授权
- **Token认证**: 基于自定义Token的用户认证机制
- **权限控制**: 使用`@RequireAdmin`注解进行管理员权限验证
- **AOP拦截**: 通过切面编程实现权限验证和参数校验

### 📦 商品管理
- **商品CRUD**: 完整的商品增删改查功能
- **分页查询**: 支持分页和条件查询
- **权限控制**: 商品创建需要管理员权限

### 👥 用户管理
- **用户登录**: 支持用户名密码登录
- **用户列表**: 管理员可查看用户列表
- **会话管理**: Token过期时间控制

### 🛠️ 技术特性
- **国际化支持**: 多语言错误消息和提示
- **参数校验**: 基于Bean Validation的自动参数验证
- **异常处理**: 统一的异常处理和错误响应
- **配置管理**: 支持多环境配置和Nacos动态配置
- **缓存集成**: Redis缓存支持
- **服务监控**: Spring Actuator健康检查

## 📋 环境要求

### 必需环境
- **JDK 17+** - Java开发环境
- **Maven 3.6+** - 构建工具
- **MySQL 8.0+** - 数据库
- **Redis 6.0+** - 缓存服务

### 可选环境
- **Nacos Server** - 配置中心（可选，本地开发可禁用）

## 🚀 快速开始

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

-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    token VARCHAR(100),
    token_expire_time DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建商品表
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
      password: your_redis_password  # 如果Redis有密码
```

### 4. 启动应用
```bash
# 使用Maven启动
mvn spring-boot:run

# 或者编译后运行
mvn clean package
java -jar target/online-store-1.0-SNAPSHOT.jar
```

### 5. 验证启动
访问健康检查接口：
```bash
curl http://localhost:8080/actuator/health
```

## 📡 API接口文档

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

**响应示例:**
```json
{
    "token": "generated_token_string",
    "username": "admin",
    "expiresAt": "2024-01-01T12:00:00"
}
```

### 商品接口

#### 创建商品 (需要管理员权限)
```http
POST /api/products
Authorization: Bearer <token>
Content-Type: application/json

{
    "name": "商品名称",
    "category": "商品分类", 
    "price": 99.99
}
```

#### 查询商品列表
```http
GET /api/products?page=1&size=10&name=商品名称&category=分类
```

**响应示例:**
```json
{
    "records": [
        {
            "id": 1,
            "name": "商品名称",
            "category": "分类",
            "price": 99.99,
            "createdAt": "2024-01-01T12:00:00",
            "updatedAt": "2024-01-01T12:00:00"
        }
    ],
    "total": 100,
    "page": 1,
    "size": 10
}
```

### 用户接口

#### 查询用户列表 (需要管理员权限)
```http
GET /api/users?page=1&size=10&username=用户名
Authorization: Bearer <token>
```

## ⚙️ 配置说明

### 应用配置
- `server.port`: 服务端口 (默认: 8080)
- `spring.profiles.active`: 环境配置 (默认: local)

### 数据库配置
- `spring.datasource.*`: MySQL数据库连接配置
- `mybatis.*`: MyBatis配置

### Redis配置
- `spring.data.redis.*`: Redis连接和连接池配置

### Nacos配置
- `spring.cloud.nacos.*`: Nacos服务发现和配置中心

### 管理员配置
- `admin.auth.username`: 管理员用户名
- `admin.auth.password`: 管理员密码

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

## 🐳 Docker部署

### 构建镜像
```bash
docker build -t online-store:latest .
```

### 运行容器
```bash
docker-compose up -d
```

## 📊 监控与健康检查

### 健康检查端点
- `GET /actuator/health` - 应用健康状态
- `GET /actuator/info` - 应用信息
- `GET /actuator/metrics` - 应用指标

### 日志配置
应用使用SLF4J + Logback进行日志记录，支持：
- 分级日志输出
- 文件滚动
- 异步日志

## 🤝 贡献指南

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 📄 许可证

该项目基于 MIT 许可证开源 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 🔗 相关链接

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
- [MyBatis Documentation](https://mybatis.org/mybatis-3/)
- [Nacos Documentation](https://nacos.io/zh-cn/docs/what-is-nacos.html)

## 📞 技术支持

如有问题或建议，请：
1. 查看已有的 [Issues](https://github.com/your-repo/online-store/issues)
2. 创建新的 Issue 描述问题
3. 联系项目维护者

---

**注意**: 本项目仅用于学习和演示目的，生产环境使用前请进行充分的安全评估和性能测试。 