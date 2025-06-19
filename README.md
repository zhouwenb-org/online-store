# Online Store - 在线商店系统

一个基于Spring Cloud Alibaba微服务架构的现代化在线商店系统，集成了用户管理、商品管理、认证授权等核心功能。

## 🚀 技术栈

### 核心框架
- **JDK 17** - Java运行环境
- **Spring Boot 3.1.5** - 微服务框架
- **Spring Cloud 2022.0.4** - 云原生应用开发
- **Spring Cloud Alibaba 2022.0.0.0** - 阿里巴巴微服务套件

### 数据存储
- **MySQL 8.0** - 主数据库
- **Redis 6.0+** - 缓存和会话存储
- **MyBatis 3.0.2** - ORM框架

### 微服务组件
- **Nacos 2.2.0** - 服务注册与发现、配置管理
- **Jedis 4.3.1** - Redis客户端

### 其他组件
- **Spring AOP** - 面向切面编程
- **Spring Validation** - 参数校验
- **Jackson JSR310** - JSON序列化
- **Spring Actuator** - 监控和管理

## 📁 项目结构

```
online-store/
├── src/
│   ├── main/
│   │   ├── java/com/example/onlinestore/
│   │   │   ├── OnlineStoreApplication.java          # 应用主入口
│   │   │   ├── annotation/                          # 自定义注解
│   │   │   │   ├── RequireAdmin.java               # 管理员权限注解
│   │   │   │   └── ValidateParams.java             # 参数校验注解
│   │   │   ├── aspect/                             # AOP切面
│   │   │   │   ├── AdminAuthAspect.java            # 管理员权限切面
│   │   │   │   └── ValidationAspect.java           # 参数校验切面
│   │   │   ├── config/                             # 配置类
│   │   │   │   ├── LocalConfigProperties.java      # 本地配置
│   │   │   │   ├── MessageConfig.java              # 国际化配置
│   │   │   │   ├── MyBatisConfig.java              # MyBatis配置
│   │   │   │   ├── NacosConfig.java                # Nacos配置
│   │   │   │   ├── RedisConfig.java                # Redis配置
│   │   │   │   └── WebConfig.java                  # Web配置
│   │   │   ├── controller/                         # 控制器层
│   │   │   │   ├── AuthController.java             # 认证控制器
│   │   │   │   ├── ProductController.java          # 商品控制器
│   │   │   │   └── UserController.java             # 用户控制器
│   │   │   ├── dto/                                # 数据传输对象
│   │   │   ├── interceptor/                        # 拦截器
│   │   │   ├── mapper/                             # 数据访问层
│   │   │   ├── model/                              # 实体模型
│   │   │   ├── service/                            # 业务逻辑层
│   │   │   └── context/                            # 上下文管理
│   │   └── resources/
│   │       ├── application.yml                     # 主配置文件
│   │       ├── application-local.yml               # 本地环境配置
│   │       ├── bootstrap.yml                       # 启动配置
│   │       ├── db/schema.sql                       # 数据库结构
│   │       ├── i18n/                              # 国际化资源
│   │       └── mapper/                             # MyBatis映射文件
│   └── test/                                       # 测试代码
├── pom.xml                                         # Maven依赖配置
└── README.md                                       # 项目说明文档
```

## ✨ 核心功能

### 🔐 用户认证与授权
- **用户登录**: Token基础的认证机制
- **管理员权限**: 基于AOP的权限控制
- **Token管理**: 自动过期和刷新机制

### 📦 商品管理
- **商品创建**: 管理员可创建新商品（需要管理员权限）
- **商品列表**: 分页查询商品信息
- **分类管理**: 支持商品分类

### 👥 用户管理
- **用户列表**: 分页查询用户信息（需要管理员权限）
- **用户信息**: 用户基本信息管理

### 🛠 技术特性
- **国际化支持**: 中英文多语言支持
- **参数校验**: 基于注解的自动参数校验
- **异常处理**: 统一异常处理和错误响应
- **日志记录**: 完整的请求和错误日志
- **配置管理**: Nacos动态配置支持
- **缓存机制**: Redis缓存提升性能

## 🔧 环境要求

### 必需环境
- **JDK 17+** - Java开发工具包
- **Maven 3.6+** - 项目构建工具
- **MySQL 8.0+** - 关系型数据库
- **Redis 6.0+** - 内存数据库

### 可选环境
- **Nacos Server** - 配置中心和服务发现（可选，默认关闭）

## 🚀 快速开始

### 1. 环境准备

确保已安装并启动以下服务：

```bash
# 启动MySQL服务
sudo systemctl start mysql

# 启动Redis服务
sudo systemctl start redis
```

### 2. 数据库初始化

```sql
-- 创建数据库
CREATE DATABASE online_store DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE online_store;

-- 导入数据库结构
SOURCE src/main/resources/db/schema.sql;
```

### 3. 配置修改

修改 `src/main/resources/application-local.yml` 中的数据库和Redis连接信息：

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
      password: your_redis_password  # 如果Redis设置了密码
```

### 4. 运行应用

```bash
# 方式1：使用Maven运行
mvn spring-boot:run

# 方式2：打包后运行
mvn clean package
java -jar target/online-store-1.0-SNAPSHOT.jar

# 方式3：指定环境运行
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

### 5. 验证部署

应用启动后，访问以下地址验证：

```bash
# 健康检查
curl http://localhost:8080/actuator/health

# API测试
curl http://localhost:8080/api/products
```

## 📡 API接口

### 认证接口
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "password"
}
```

### 商品管理
```http
# 获取商品列表
GET /api/products?page=1&size=10

# 创建商品（需要管理员权限）
POST /api/products
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "商品名称",
  "category": "分类",
  "price": 99.99
}
```

### 用户管理
```http
# 获取用户列表（需要管理员权限）
GET /api/users?page=1&size=10
Authorization: Bearer {token}
```

## 🔍 开发指南

### 管理员配置

默认管理员账号在 `application.yml` 中配置：

```yaml
admin:
  auth:
    username: admin
    password: password
```

### 自定义注解使用

```java
@RestController
public class ExampleController {
    
    @RequireAdmin  // 需要管理员权限
    @ValidateParams  // 自动参数校验
    @PostMapping("/admin-only")
    public ResponseEntity<?> adminOnlyAction(@Valid @RequestBody Request request) {
        // 业务逻辑
    }
}
```

### 国际化支持

在 `src/main/resources/i18n/` 目录下添加语言文件：

```properties
# messages_zh_CN.properties
error.system.internal=系统内部错误

# messages.properties (默认英文)
error.system.internal=Internal system error
```

## 🧪 测试

```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=UserServiceTest

# 生成测试报告
mvn surefire-report:report
```

## 📊 监控

应用集成了Spring Boot Actuator，提供以下监控端点：

- `/actuator/health` - 健康检查
- `/actuator/info` - 应用信息
- `/actuator/metrics` - 应用指标

## 🔄 部署

### Docker部署

```dockerfile
FROM openjdk:17-jdk-slim
COPY target/online-store-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### 生产环境配置

创建 `application-prod.yml` 配置文件，配置生产环境数据库和Redis连接。

## 🤝 贡献指南

1. Fork项目仓库
2. 创建特性分支 (`git checkout -b feature/amazing-feature`)
3. 提交更改 (`git commit -m 'Add some amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 创建Pull Request

## 📝 更新日志

### v1.0.0
- 初始版本发布
- 用户认证与授权功能
- 商品管理功能
- 用户管理功能
- 国际化支持
- Spring Cloud Alibaba集成

## 📄 许可证

本项目采用MIT许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 📞 联系方式

如有问题或建议，请通过以下方式联系：

- 项目Issues: [GitHub Issues](https://github.com/your-repo/online-store/issues)
- 邮箱: your-email@example.com

---

⭐ 如果这个项目对您有帮助，请给我们一个Star！ 