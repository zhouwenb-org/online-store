# Online Store - 在线商店系统

一个基于Spring Boot 3.x和Spring Cloud的现代化在线商店后端系统，支持用户认证、商品管理、权限控制等功能。

## 🚀 项目特性

### 核心功能
- **用户管理**：用户注册、登录、Token认证
- **商品管理**：商品创建、查询、分页展示
- **权限控制**：基于AOP的管理员权限验证
- **国际化支持**：多语言错误消息
- **参数验证**：请求参数自动验证
- **分布式配置**：Nacos配置中心支持

### 技术特性
- **微服务架构**：Spring Cloud生态
- **缓存系统**：Redis缓存支持
- **数据持久化**：MyBatis + MySQL
- **切面编程**：AOP权限验证和参数校验
- **配置管理**：多环境配置支持
- **监控健康检查**：Spring Boot Actuator

## 🛠 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| JDK | 17 | Java运行环境 |
| Spring Boot | 3.1.5 | 应用框架 |
| Spring Cloud | 2022.0.4 | 微服务框架 |
| Spring Cloud Alibaba | 2022.0.0.0 | 阿里云微服务组件 |
| MyBatis | 3.0.2 | ORM框架 |
| MySQL | 8.0.33 | 关系型数据库 |
| Redis | - | 缓存数据库 |
| Jedis | 4.3.1 | Redis客户端 |
| Nacos | 2.2.0 | 配置中心和服务发现 |

## 📁 项目结构

```
online-store/
├── src/
│   ├── main/
│   │   ├── java/com/example/onlinestore/
│   │   │   ├── OnlineStoreApplication.java          # 主启动类
│   │   │   ├── annotation/                          # 自定义注解
│   │   │   │   ├── RequireAdmin.java               # 管理员权限注解
│   │   │   │   └── ValidateParams.java             # 参数验证注解
│   │   │   ├── aspect/                             # 切面类
│   │   │   │   ├── AdminAuthAspect.java            # 管理员权限切面
│   │   │   │   └── ValidationAspect.java           # 参数验证切面
│   │   │   ├── config/                             # 配置类
│   │   │   │   ├── LocalConfigProperties.java     # 本地配置
│   │   │   │   ├── MessageConfig.java              # 国际化配置
│   │   │   │   ├── MyBatisConfig.java              # MyBatis配置
│   │   │   │   ├── NacosConfig.java                # Nacos配置
│   │   │   │   ├── RedisConfig.java                # Redis配置
│   │   │   │   └── WebConfig.java                  # Web配置
│   │   │   ├── controller/                         # 控制器
│   │   │   │   ├── AuthController.java             # 认证控制器
│   │   │   │   ├── ProductController.java          # 商品控制器
│   │   │   │   └── UserController.java             # 用户控制器
│   │   │   ├── dto/                                # 数据传输对象
│   │   │   ├── interceptor/                        # 拦截器
│   │   │   │   └── AuthInterceptor.java           # 认证拦截器
│   │   │   ├── mapper/                             # MyBatis映射器
│   │   │   ├── model/                              # 实体类
│   │   │   │   ├── Product.java                   # 商品实体
│   │   │   │   └── User.java                      # 用户实体
│   │   │   ├── service/                           # 服务层
│   │   │   │   ├── impl/                          # 服务实现
│   │   │   │   ├── ProductService.java            # 商品服务接口
│   │   │   │   └── UserService.java               # 用户服务接口
│   │   │   └── context/                           # 上下文
│   │   │       └── UserContext.java               # 用户上下文
│   │   └── resources/
│   │       ├── application.yml                    # 主配置文件
│   │       ├── application-local.yml              # 本地环境配置
│   │       ├── bootstrap.yml                      # 启动配置
│   │       ├── db/schema.sql                      # 数据库结构
│   │       ├── i18n/                             # 国际化资源
│   │       └── mapper/                           # MyBatis映射文件
│   └── test/                                      # 测试代码
├── pom.xml                                        # Maven配置
└── README.md                                      # 项目说明
```

## 🚀 快速开始

### 环境要求

- **JDK 17+** - Java开发环境
- **Maven 3.6+** - 构建工具
- **MySQL 8.0+** - 数据库
- **Redis 6.0+** - 缓存数据库
- **Nacos 2.2.0+** (可选) - 配置中心

### 数据库初始化

1. **创建数据库**
```sql
CREATE DATABASE online_store DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. **创建用户表**
```sql
USE online_store;
source src/main/resources/db/schema.sql;
```

### 配置文件设置

1. **数据库配置** (`src/main/resources/application.yml`)
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/online_store?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
```

2. **Redis配置**
```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: your_redis_password
      database: 0
```

3. **管理员账户配置**
```yaml
admin:
  auth:
    username: admin
    password: your_admin_password
```

### 启动应用

1. **使用Maven启动**
```bash
mvn clean spring-boot:run
```

2. **使用IDE启动**
   - 导入项目到IDE
   - 运行 `OnlineStoreApplication.main()` 方法

3. **打包运行**
```bash
mvn clean package
java -jar target/online-store-1.0-SNAPSHOT.jar
```

应用启动后将在 `http://localhost:8080` 运行

## 📚 API 文档

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
  "username": "admin",
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenExpireTime": "2024-01-01T12:00:00"
}
```

### 商品管理接口

#### 创建商品（需要管理员权限）
```http
POST /api/products
Authorization: Bearer your_token
Content-Type: application/json

{
  "name": "商品名称",
  "category": "商品分类",
  "price": 99.99
}
```

#### 查询商品列表
```http
GET /api/products?page=1&size=10&category=electronics
```

**响应示例：**
```json
{
  "content": [
    {
      "id": 1,
      "name": "商品名称",
      "category": "electronics",
      "price": 99.99,
      "createdAt": "2024-01-01T12:00:00",
      "updatedAt": "2024-01-01T12:00:00"
    }
  ],
  "totalElements": 100,
  "totalPages": 10,
  "size": 10,
  "number": 0
}
```

### 用户管理接口

#### 获取用户列表（需要管理员权限）
```http
GET /api/users?page=1&size=10
Authorization: Bearer your_token
```

## 🔧 配置说明

### 环境配置

项目支持多环境配置，通过 `spring.profiles.active` 切换：

- **local**: 本地开发环境
- **dev**: 开发环境
- **prod**: 生产环境

### Nacos配置

如需使用Nacos配置中心，设置以下环境变量：

```bash
export NACOS_ENABLED=true
export NACOS_SERVER_ADDR=your_nacos_server:8848
export NACOS_NAMESPACE=your_namespace
```

### Redis配置

支持连接池配置：

```yaml
spring:
  data:
    redis:
      jedis:
        pool:
          max-active: 8      # 最大连接数
          max-idle: 8        # 最大空闲连接
          min-idle: 0        # 最小空闲连接
          max-wait: -1ms     # 最大等待时间
```

## 🎯 核心特性详解

### 权限控制系统

使用AOP切面实现权限控制：

```java
@RequireAdmin
@PostMapping("/admin-only")
public ResponseEntity<?> adminOnlyEndpoint() {
    // 只有管理员可以访问
}
```

### 参数验证

自动参数验证：

```java
@ValidateParams
@PostMapping("/validate")
public ResponseEntity<?> validateEndpoint(@Valid @RequestBody MyRequest request) {
    // 自动验证请求参数
}
```

### 国际化支持

支持多语言错误消息：

- 中文：`src/main/resources/i18n/messages_zh_CN.properties`
- 英文：`src/main/resources/i18n/messages.properties`

### 用户上下文

线程安全的用户上下文：

```java
User currentUser = UserContext.getCurrentUser();
```

## 🧪 测试

### 运行单元测试

```bash
mvn test
```

### 测试覆盖率

项目包含以下测试：

- **控制器测试**: `AuthControllerTest`, `UserControllerTest`
- **服务层测试**: `UserServiceTest`
- **切面测试**: `AdminAuthAspectTest`, `ValidationAspectTest`
- **配置测试**: `MessageSourceTest`

## 📊 监控和健康检查

访问以下端点进行应用监控：

- **健康检查**: `GET /actuator/health`
- **应用信息**: `GET /actuator/info`
- **配置信息**: `GET /actuator/configprops`

## 🔍 故障排除

### 常见问题

1. **数据库连接失败**
   - 检查MySQL服务是否启动
   - 验证数据库连接配置
   - 确认数据库用户权限

2. **Redis连接失败**
   - 检查Redis服务状态
   - 验证Redis连接配置
   - 检查防火墙设置

3. **权限验证失败**
   - 确认Token是否有效
   - 检查管理员用户名配置
   - 验证请求头格式

### 日志级别配置

```yaml
logging:
  level:
    com.example.onlinestore: DEBUG
    org.springframework.web: INFO
    org.mybatis: DEBUG
```

## 🤝 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 📝 许可证

本项目采用 MIT 许可证。详见 [LICENSE](LICENSE) 文件。

## 👥 联系方式

- 项目主页: [GitHub Repository](#)
- 问题反馈: [Issues](#)
- 邮件联系: [your-email@example.com](#)

---

**最后更新**: 2024年1月
**版本**: 1.0-SNAPSHOT 