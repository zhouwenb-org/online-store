# 在线商店后端服务

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.5-green.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2022.0.4-blue.svg)](https://spring.io/projects/spring-cloud)
[![MyBatis](https://img.shields.io/badge/MyBatis-3.0.2-red.svg)](https://mybatis.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-6.0+-red.svg)](https://redis.io/)

一个基于Spring Cloud微服务架构的现代化在线商店后端服务，采用最新的Spring生态技术栈构建，支持高并发、高可用的电商场景。

## 🚀 特性

- **🔐 安全认证**：基于Token的用户认证机制，支持管理员权限控制
- **📦 产品管理**：完整的商品CRUD操作，支持分页查询和分类管理
- **👥 用户管理**：用户注册登录、信息管理、权限控制
- **🌐 国际化支持**：内置中英文双语支持，易于扩展其他语言
- **📊 分页查询**：统一的分页响应格式，支持自定义页码和页面大小
- **⚡ 缓存机制**：Redis缓存支持，提升系统性能
- **🔍 配置中心**：Nacos配置管理，支持动态配置刷新
- **🛡️ AOP增强**：切面编程实现权限验证和参数校验
- **🧪 测试覆盖**：完整的单元测试和集成测试

## 🏗️ 技术栈

### 核心框架
- **JDK 17** - 最新LTS版本，性能优化
- **Spring Boot 3.1.5** - 应用框架核心
- **Spring Cloud 2022.0.4** - 微服务生态
- **Spring Cloud Alibaba 2022.0.0.0** - 阿里云微服务组件

### 数据层
- **MyBatis 3.0.2** - ORM框架
- **MySQL 8.0** - 关系型数据库
- **Redis 6.0+** - 缓存数据库
- **Jedis 4.3.1** - Redis Java客户端

### 基础设施
- **Nacos** - 配置中心与服务发现
- **Maven** - 项目构建管理

## 📁 项目结构

```
src/
├── main/
│   ├── java/com/example/onlinestore/
│   │   ├── annotation/           # 自定义注解
│   │   │   ├── RequireAdmin.java       # 管理员权限注解
│   │   │   └── ValidateParams.java     # 参数验证注解
│   │   ├── aspect/              # AOP切面
│   │   │   ├── AdminAuthAspect.java    # 管理员权限切面
│   │   │   └── ValidationAspect.java   # 参数验证切面
│   │   ├── config/              # 配置类
│   │   │   ├── MessageConfig.java      # 国际化配置
│   │   │   ├── MyBatisConfig.java      # MyBatis配置
│   │   │   ├── RedisConfig.java        # Redis配置
│   │   │   └── WebConfig.java          # Web配置
│   │   ├── controller/          # 控制器层
│   │   │   ├── AuthController.java     # 认证控制器
│   │   │   ├── ProductController.java  # 产品控制器
│   │   │   └── UserController.java     # 用户控制器
│   │   ├── dto/                # 数据传输对象
│   │   │   ├── CreateProductRequest.java
│   │   │   ├── LoginRequest.java
│   │   │   ├── LoginResponse.java
│   │   │   ├── PageResponse.java
│   │   │   └── UserVO.java
│   │   ├── model/              # 实体模型
│   │   │   ├── Product.java           # 产品实体
│   │   │   └── User.java              # 用户实体
│   │   ├── service/            # 服务层
│   │   │   ├── impl/
│   │   │   ├── ProductService.java
│   │   │   └── UserService.java
│   │   ├── mapper/             # 数据访问层
│   │   └── OnlineStoreApplication.java # 启动类
│   └── resources/
│       ├── application.yml            # 主配置文件
│       ├── application-local.yml      # 本地环境配置
│       ├── bootstrap.yml             # 引导配置
│       ├── db/schema.sql             # 数据库表结构
│       ├── i18n/                     # 国际化资源
│       └── mapper/                   # MyBatis映射文件
└── test/                      # 测试代码
```

## 🔧 环境要求

- **JDK 17+** 
- **Maven 3.6+**
- **MySQL 8.0+**
- **Redis 6.0+**
- **Nacos 2.2.0+** (可选，用于配置中心)

## 🚀 快速开始

### 1. 克隆项目

```bash
git clone <repository-url>
cd online-store
```

### 2. 数据库配置

#### 创建数据库
```sql
CREATE DATABASE online_store DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 导入表结构
```bash
mysql -u root -p online_store < src/main/resources/db/schema.sql
```

### 3. 配置修改

编辑 `src/main/resources/application.yml`：

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

### 4. 运行项目

```bash
# 使用Maven运行
mvn spring-boot:run

# 或者打包后运行
mvn clean package
java -jar target/online-store-1.0-SNAPSHOT.jar
```

### 5. 验证运行

访问 http://localhost:8080，项目启动成功！

## 📚 API文档

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

### 产品管理接口

#### 创建产品 (需要管理员权限)
```http
POST /api/products
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "iPhone 15",
  "category": "手机",
  "price": 7999.00
}
```

#### 获取产品列表
```http
GET /api/products?page=1&size=10&name=iPhone
```

### 用户管理接口

#### 获取用户列表 (需要管理员权限)
```http
GET /api/users?page=1&size=10
Authorization: Bearer <token>
```

## 🔒 权限系统

项目采用基于注解的权限控制：

- `@RequireAdmin`：要求管理员权限
- `@ValidateParams`：自动参数验证

默认管理员账户：
- 用户名：`admin`
- 密码：`password`

## 🌐 国际化支持

支持中英文切换，通过请求头 `Accept-Language` 控制：

- `zh-CN`：中文
- `en`：英文

错误信息和提示文本都支持国际化。

## 🧪 测试

运行单元测试：

```bash
mvn test
```

运行集成测试：

```bash
mvn verify
```

## 📈 性能优化

- **Redis缓存**：用户信息和热点数据缓存
- **分页查询**：大数据量分页展示
- **连接池**：数据库和Redis连接池优化
- **异步处理**：支持异步任务处理

## 🔧 配置说明

### Nacos配置

如需使用Nacos配置中心，修改环境变量：

```bash
export NACOS_ENABLED=true
export SPRING_PROFILES_ACTIVE=nacos
```

### 环境配置

- `local`：本地开发环境
- `dev`：开发环境  
- `test`：测试环境
- `prod`：生产环境

## 🚀 部署

### Docker部署

```bash
# 构建镜像
docker build -t online-store:latest .

# 运行容器
docker run -d \
  --name online-store \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  online-store:latest
```

### 传统部署

```bash
# 打包
mvn clean package -Pprod

# 部署
java -jar -Dspring.profiles.active=prod target/online-store-1.0-SNAPSHOT.jar
```

## 🤝 贡献指南

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 📄 许可证

本项目采用 MIT 许可证。详情请见 [LICENSE](LICENSE) 文件。

## 📞 联系方式

如有问题或建议，请通过以下方式联系：

- 提交 [Issue](../../issues)
- 发送邮件至：[your-email@example.com]

---

⭐ 如果这个项目对您有帮助，请给它一个星标！ 