# 在线商店 (Online Store)

[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://openjdk.java.net/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2022.0.4-blue.svg)](https://spring.io/projects/spring-cloud)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-orange.svg)](https://dev.mysql.com/doc/)
[![Redis](https://img.shields.io/badge/Redis-6.0+-red.svg)](https://redis.io/)

## 📖 项目简介

这是一个基于Spring Cloud微服务架构的现代化在线商店系统，提供用户认证、商品管理、用户管理等核心功能。项目采用前后端分离架构，支持多环境配置，具有良好的扩展性和可维护性。

## 🚀 核心特性

- **微服务架构**：基于Spring Cloud的分布式微服务架构
- **用户认证系统**：支持Token认证和管理员权限控制
- **商品管理**：完整的商品CRUD操作和分页查询
- **用户管理**：用户信息管理和分页查询
- **配置中心**：集成Alibaba Nacos配置管理
- **缓存支持**：Redis缓存优化系统性能
- **国际化支持**：多语言支持(中文/英文)
- **参数验证**：基于AOP的统一参数验证
- **异常处理**：全局异常处理和错误响应
- **健康检查**：Spring Boot Actuator监控端点

## 🛠️ 技术栈

### 后端技术
- **核心框架**：Spring Boot 3.1.5, Spring Cloud 2022.0.4
- **数据库**：MySQL 8.0, MyBatis 3.0.2
- **缓存**：Redis (Jedis 4.3.1)
- **配置中心**：Alibaba Nacos
- **切面编程**：Spring AOP
- **数据验证**：Spring Validation
- **文档工具**：Maven

### 开发工具
- **Java版本**：JDK 17
- **构建工具**：Maven 3.6+
- **代码规范**：Google Java Style

## 🏗️ 项目架构

```
online-store/
├── src/
│   ├── main/
│   │   ├── java/com/example/onlinestore/
│   │   │   ├── annotation/           # 自定义注解
│   │   │   │   ├── RequireAdmin.java # 管理员权限注解
│   │   │   │   └── ValidateParams.java # 参数验证注解
│   │   │   ├── aspect/               # AOP切面
│   │   │   │   ├── AdminAuthAspect.java # 管理员认证切面
│   │   │   │   └── ValidationAspect.java # 参数验证切面
│   │   │   ├── config/               # 配置类
│   │   │   │   ├── RedisConfig.java  # Redis配置
│   │   │   │   ├── MyBatisConfig.java # MyBatis配置
│   │   │   │   └── WebConfig.java    # Web配置
│   │   │   ├── controller/           # 控制器层
│   │   │   │   ├── AuthController.java    # 认证接口
│   │   │   │   ├── ProductController.java # 商品管理接口
│   │   │   │   └── UserController.java    # 用户管理接口
│   │   │   ├── dto/                  # 数据传输对象
│   │   │   ├── model/                # 数据模型
│   │   │   │   ├── Product.java      # 商品实体
│   │   │   │   └── User.java         # 用户实体
│   │   │   ├── service/              # 服务层
│   │   │   │   ├── ProductService.java # 商品服务接口
│   │   │   │   ├── UserService.java    # 用户服务接口
│   │   │   │   └── impl/              # 服务实现
│   │   │   ├── mapper/               # 数据访问层
│   │   │   └── OnlineStoreApplication.java # 启动类
│   │   └── resources/
│   │       ├── application.yml       # 主配置文件
│   │       ├── application-local.yml # 本地配置
│   │       ├── bootstrap.yml         # 引导配置
│   │       ├── db/schema.sql         # 数据库表结构
│   │       ├── i18n/                 # 国际化资源
│   │       └── mapper/               # MyBatis映射文件
│   └── test/                         # 测试代码
├── pom.xml                           # Maven依赖配置
└── README.md                         # 项目文档
```

## 📊 数据库设计

### 用户表 (users)
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    token VARCHAR(100),
    token_expire_time DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 商品表 (products)
商品表包含商品的基本信息，如名称、分类、价格等。

## 🔌 API接口

### 认证接口
- `POST /api/auth/login` - 用户登录

### 商品管理接口
- `POST /api/products` - 创建商品 (需要管理员权限)
- `GET /api/products` - 获取商品列表 (支持分页)

### 用户管理接口
- `GET /api/users` - 获取用户列表 (需要管理员权限, 支持分页)

## 🚀 快速开始

### 环境要求
- JDK 17或更高版本
- Maven 3.6或更高版本
- MySQL 8.0
- Redis 6.0或更高版本

### 安装部署

1. **克隆项目**
```bash
git clone [项目地址]
cd online-store
```

2. **创建数据库**
```sql
CREATE DATABASE online_store DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. **执行数据库脚本**
```bash
mysql -u root -p online_store < src/main/resources/db/schema.sql
```

4. **配置数据库和Redis连接**
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

5. **启动应用**
```bash
mvn clean compile
mvn spring-boot:run
```

应用将在 `http://localhost:8080` 启动。

## 🔧 配置说明

### 环境配置
项目支持多环境配置：
- `application.yml` - 主配置文件
- `application-local.yml` - 本地开发环境
- `bootstrap.yml` - Nacos配置中心引导配置

### 管理员配置
默认管理员账户：
```yaml
admin:
  auth:
    username: admin
    password: password
```

### Nacos配置
如需启用Nacos配置中心，设置环境变量：
```bash
export NACOS_ENABLED=true
```

## 🧪 测试

运行单元测试：
```bash
mvn test
```

运行特定测试类：
```bash
mvn test -Dtest=UserServiceTest
```

## 📝 开发指南

### 代码规范
- 遵循Google Java代码规范
- 使用统一的日志格式
- 所有公共方法需要添加JavaDoc注释
- 异常处理需要记录日志

### 新增功能
1. 在对应的包下创建新的类文件
2. 添加必要的单元测试
3. 更新相关文档

### 数据库变更
1. 在 `src/main/resources/db/` 目录下添加迁移脚本
2. 更新实体类和Mapper文件
3. 添加相应的测试用例

## 🐛 故障排除

### 常见问题

1. **应用启动失败**
   - 检查数据库连接配置
   - 确认MySQL和Redis服务是否正常运行
   - 查看日志输出获取详细错误信息

2. **认证失败**
   - 检查管理员账户配置
   - 确认Token是否过期

3. **数据库连接异常**
   - 检查数据库URL、用户名、密码配置
   - 确认数据库服务状态

## 🤝 贡献指南

欢迎提交Issue和Pull Request！

1. Fork 项目
2. 创建新的功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📄 许可证

该项目基于 MIT 许可证开源 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 📞 联系方式

如有问题或建议，请通过以下方式联系：
- 邮箱：[your-email@example.com]
- 项目Issues：[项目Issues链接]

## 🔗 相关链接

- [Spring Boot 官方文档](https://spring.io/projects/spring-boot)
- [Spring Cloud 官方文档](https://spring.io/projects/spring-cloud)
- [MyBatis 官方文档](https://mybatis.org/mybatis-3/)
- [Redis 官方文档](https://redis.io/documentation) 