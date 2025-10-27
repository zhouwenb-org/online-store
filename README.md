# Online Store 在线商店系统

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2022.0.4-blue.svg)](https://spring.io/projects/spring-cloud)
[![MyBatis](https://img.shields.io/badge/MyBatis-3.0.2-red.svg)](https://mybatis.org/mybatis-3/)

一个基于Spring Boot + Spring Cloud的现代化在线商店系统，提供完整的用户管理、商品管理、权限控制等功能。

## 🚀 功能特性

### 核心功能
- 🔐 **用户认证与授权** - 支持用户登录、Token管理、管理员权限控制
- 📦 **商品管理** - 商品创建、查询、分页列表等功能
- 👥 **用户管理** - 用户注册、信息查询、分页管理
- 🌍 **国际化支持** - 支持多语言（中文、英文）
- 📊 **分页查询** - 统一的分页查询响应格式
- ⚡ **缓存支持** - 基于Redis的高性能缓存

### 技术特性
- 🛡️ **AOP切面编程** - 参数验证、权限控制、日志记录
- 🔍 **参数验证** - 基于Bean Validation的统一参数校验
- 🚦 **统一异常处理** - 标准化的错误响应格式
- 📈 **健康检查** - Spring Boot Actuator监控端点
- 🔧 **配置中心** - 支持Nacos动态配置管理
- 🏗️ **微服务架构** - 支持服务发现和注册

## 🛠️ 技术栈

### 后端框架
- **JDK 17** - Java开发环境
- **Spring Boot 3.1.5** - 应用框架
- **Spring Cloud 2022.0.4** - 微服务框架
- **Spring AOP** - 面向切面编程
- **Bean Validation** - 参数校验

### 数据层
- **MyBatis 3.0.2** - ORM框架
- **MySQL 8.0.33** - 关系数据库
- **Redis (Jedis 4.3.1)** - 缓存数据库

### 中间件
- **Nacos 2.2.0** - 配置中心与服务发现
- **Spring Cloud Alibaba** - 阿里云微服务组件

### 构建工具
- **Maven 3.6+** - 项目构建管理
- **Spring Boot Maven Plugin** - 应用打包插件

## 📁 项目结构

```
online-store/
├── src/
│   ├── main/
│   │   ├── java/com/example/onlinestore/
│   │   │   ├── OnlineStoreApplication.java     # 应用启动类
│   │   │   ├── annotation/                     # 自定义注解
│   │   │   │   ├── RequireAdmin.java          # 管理员权限注解
│   │   │   │   └── ValidateParams.java        # 参数验证注解
│   │   │   ├── aspect/                        # AOP切面
│   │   │   │   ├── AdminAuthAspect.java       # 管理员权限切面
│   │   │   │   └── ValidationAspect.java      # 参数验证切面
│   │   │   ├── config/                        # 配置类
│   │   │   │   ├── MyBatisConfig.java         # MyBatis配置
│   │   │   │   ├── RedisConfig.java           # Redis配置
│   │   │   │   ├── NacosConfig.java           # Nacos配置
│   │   │   │   └── ...                        # 其他配置
│   │   │   ├── controller/                    # REST控制器
│   │   │   │   ├── AuthController.java        # 认证控制器
│   │   │   │   ├── UserController.java        # 用户控制器
│   │   │   │   └── ProductController.java     # 商品控制器
│   │   │   ├── dto/                          # 数据传输对象
│   │   │   │   ├── LoginRequest.java          # 登录请求
│   │   │   │   ├── PageResponse.java          # 分页响应
│   │   │   │   └── ...                        # 其他DTO
│   │   │   ├── interceptor/                   # 拦截器
│   │   │   │   └── AuthInterceptor.java       # 认证拦截器
│   │   │   ├── mapper/                        # MyBatis Mapper
│   │   │   │   ├── UserMapper.java            # 用户Mapper
│   │   │   │   └── ProductMapper.java         # 商品Mapper
│   │   │   ├── model/                         # 实体类
│   │   │   │   ├── User.java                  # 用户实体
│   │   │   │   └── Product.java               # 商品实体
│   │   │   ├── service/                       # 服务层
│   │   │   │   ├── UserService.java           # 用户服务接口
│   │   │   │   ├── ProductService.java        # 商品服务接口
│   │   │   │   └── impl/                      # 服务实现类
│   │   │   └── context/                       # 上下文工具
│   │   │       └── UserContext.java           # 用户上下文
│   │   └── resources/
│   │       ├── application.yml                # 主配置文件
│   │       ├── application-local.yml          # 本地环境配置
│   │       ├── bootstrap.yml                  # 引导配置
│   │       ├── db/                           # 数据库脚本
│   │       │   └── schema.sql                # 数据库结构
│   │       ├── i18n/                         # 国际化资源
│   │       │   ├── messages.properties        # 英文资源
│   │       │   └── messages_zh_CN.properties  # 中文资源
│   │       └── mapper/                       # MyBatis映射文件
│   │           ├── UserMapper.xml             # 用户映射
│   │           └── ProductMapper.xml          # 商品映射
│   └── test/                                 # 测试代码
│       └── java/com/example/onlinestore/
│           ├── aspect/                       # 切面测试
│           ├── config/                       # 配置测试
│           ├── controller/                   # 控制器测试
│           └── service/                      # 服务测试
├── pom.xml                                   # Maven配置文件
└── README.md                                 # 项目说明文档
```

## 🔧 环境要求

### 必需软件
- **JDK 17** 或更高版本
- **Maven 3.6** 或更高版本  
- **MySQL 8.0** 或更高版本
- **Redis 6.0** 或更高版本

### 可选软件
- **Nacos Server** - 配置中心（可选，默认关闭）
- **Docker** - 容器化部署
- **IntelliJ IDEA** 或 **VS Code** - 开发IDE

## 🚦 快速开始

### 1. 克隆项目

```bash
git clone <repository-url>
cd online-store
```

### 2. 数据库准备

```sql
# 创建数据库
CREATE DATABASE online_store DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 导入表结构
USE online_store;
SOURCE src/main/resources/db/schema.sql;
```

### 3. 配置文件修改

修改 `src/main/resources/application-local.yml` 文件：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/online_store?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_mysql_username
    password: your_mysql_password
  data:
    redis:
      host: localhost
      port: 6379
      password: your_redis_password  # 如果没有密码可以留空
```

### 4. 启动应用

```bash
# 方式1：Maven命令启动
mvn clean spring-boot:run

# 方式2：IDE启动
# 运行 OnlineStoreApplication.java 的 main 方法

# 方式3：打包后启动
mvn clean package
java -jar target/online-store-1.0-SNAPSHOT.jar
```

### 5. 验证启动

访问健康检查端点：
```bash
curl http://localhost:8080/actuator/health
```

应该返回：
```json
{
  "status": "UP"
}
```

## 📖 API文档

### 认证相关

#### 用户登录
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "password"
}
```

#### 用户登出
```http
POST /api/auth/logout
Authorization: Bearer <token>
```

### 用户管理

#### 查询用户列表
```http
GET /api/users?page=1&size=10&username=admin
Authorization: Bearer <token>
```

### 商品管理

#### 创建商品（需要管理员权限）
```http
POST /api/products
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "商品名称",
  "description": "商品描述",
  "price": 99.99
}
```

#### 查询商品列表
```http
GET /api/products?page=1&size=10&name=商品
```

### 响应格式

成功响应：
```json
{
  "id": 1,
  "name": "商品名称",
  "price": 99.99,
  "createdAt": "2023-12-01T10:00:00"
}
```

分页响应：
```json
{
  "data": [...],
  "page": 1,
  "size": 10,
  "total": 100,
  "totalPages": 10
}
```

错误响应：
```json
{
  "message": "错误信息",
  "timestamp": "2023-12-01T10:00:00"
}
```

## 🧪 运行测试

```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=UserControllerTest

# 运行测试并生成覆盖率报告
mvn test jacoco:report
```

## 📋 配置说明

### 应用配置

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `server.port` | 应用端口 | 8080 |
| `spring.profiles.active` | 激活的配置文件 | local |
| `spring.application.name` | 应用名称 | online-store |

### 数据库配置

| 配置项 | 说明 | 示例 |
|--------|------|------|
| `spring.datasource.url` | 数据库连接URL | `jdbc:mysql://localhost:3306/online_store` |
| `spring.datasource.username` | 数据库用户名 | `root` |
| `spring.datasource.password` | 数据库密码 | `password` |

### Redis配置

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `spring.data.redis.host` | Redis主机 | localhost |
| `spring.data.redis.port` | Redis端口 | 6379 |
| `spring.data.redis.database` | Redis数据库 | 0 |

### 管理员配置

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `admin.auth.username` | 管理员用户名 | admin |
| `admin.auth.password` | 管理员密码 | password |

## 🐳 Docker部署

### 1. 构建Docker镜像

```bash
# 创建Dockerfile
cat > Dockerfile << EOF
FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY target/online-store-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EOF

# 构建镜像
mvn clean package
docker build -t online-store:latest .
```

### 2. Docker Compose部署

```yaml
version: '3.8'
services:
  app:
    image: online-store:latest
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=local
    depends_on:
      - mysql
      - redis
  
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: password
      MYSQL_DATABASE: online_store
    ports:
      - "3306:3306"
  
  redis:
    image: redis:6.2
    ports:
      - "6379:6379"
```

## 🔍 故障排除

### 常见问题

1. **启动失败：数据库连接错误**
   - 检查MySQL是否启动
   - 确认数据库连接配置正确
   - 验证数据库用户权限

2. **Redis连接失败**
   - 检查Redis服务是否启动
   - 确认Redis连接配置
   - 检查防火墙设置

3. **Token认证失败**
   - 检查请求头是否包含正确的Authorization
   - 验证Token是否已过期
   - 确认用户是否有相应权限

### 日志查看

```bash
# 查看应用日志
tail -f logs/application.log

# 查看错误日志
grep ERROR logs/application.log
```

## 🤝 开发指南

### 代码规范
- 使用Java Code Style规范
- 类名使用PascalCase
- 方法名和变量名使用camelCase
- 常量使用UPPER_SNAKE_CASE

### 提交规范
```
feat: 新功能
fix: 修复Bug
docs: 文档更新
style: 代码格式调整
refactor: 代码重构
test: 测试相关
chore: 构建过程或辅助工具的变动
```

### 分支管理
- `main` - 主分支，稳定版本
- `develop` - 开发分支
- `feature/*` - 功能分支
- `hotfix/*` - 热修复分支

## 📄 许可证

本项目采用 MIT 许可证。详情请查看 [LICENSE](LICENSE) 文件。

## 👥 贡献者

感谢所有为本项目做出贡献的开发者！

## 📞 联系我们

如果您有任何问题或建议，请通过以下方式联系我们：

- 📧 Email: support@example.com
- 🐛 Issues: [GitHub Issues](https://github.com/your-repo/online-store/issues)
- 📖 Wiki: [项目Wiki](https://github.com/your-repo/online-store/wiki)

---

⭐ 如果这个项目对您有帮助，请给我们一个Star！ 