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

## 🏗 系统架构

### 架构概览

本项目采用经典的分层架构设计，结合Spring Boot的最佳实践，提供高可用、高性能的微服务解决方案。

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   前端应用       │    │   移动端APP     │    │   第三方系统     │
│   (Vue/React)   │    │   (iOS/Android) │    │   (Partner API) │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                        │                        │
         └────────────┬───────────┴────────────────────────┘
                      │
              ┌───────▼───────┐
              │   负载均衡器    │
              │   (Nginx)     │
              └───────┬───────┘
                      │
         ┌────────────┼────────────┐
         │            │            │
  ┌──────▼──────┐ ┌──▼──┐ ┌────▼────┐
  │ Online Store│ │ ... │ │ Service │
  │  Service    │ │     │ │   N     │
  └──────┬──────┘ └─────┘ └─────────┘
         │
    ┌────┼────┐
    │    │    │
┌───▼──┐ │ ┌──▼───┐
│MySQL │ │ │Redis │
│数据库 │ │ │缓存  │
└──────┘ │ └──────┘
         │
    ┌────▼────┐
    │ Nacos   │
    │配置中心  │
    └─────────┘
```

### 核心组件

1. **Web层** - 处理HTTP请求，参数验证，权限控制
2. **Service层** - 业务逻辑处理，事务管理
3. **Mapper层** - 数据访问层，MyBatis映射
4. **Model层** - 实体类和数据传输对象

### 数据流图

```
用户请求 → 拦截器 → Controller → AOP切面 → Service → Mapper → Database
    ↓         ↓         ↓        ↓       ↓       ↓        ↓
  认证检查   权限验证   参数验证   业务逻辑  缓存处理  SQL执行   数据存储
```

## 📋 核心功能

### 🔐 认证授权
- **Token认证**: 基于JWT的无状态认证机制
- **权限控制**: 基于AOP的细粒度权限管理
- **会话管理**: Token自动续期和过期控制
- **安全拦截**: 统一的认证和授权拦截器

### 👥 用户管理
- **用户CRUD**: 完整的用户生命周期管理
- **分页查询**: 高性能的用户列表分页查询
- **搜索过滤**: 支持用户名模糊搜索
- **状态管理**: 用户启用/禁用状态控制

### 📦 商品管理
- **商品CRUD**: 商品信息的完整管理
- **库存管理**: 商品库存的实时更新
- **分类管理**: 商品分类和标签系统
- **价格管理**: 支持多种价格策略

### 🌐 系统功能
- **国际化**: 完整的多语言支持 (中文/英文)
- **异常处理**: 统一的异常处理和错误码管理
- **参数验证**: 自动参数验证和错误消息国际化
- **日志管理**: 结构化日志记录和性能监控
- **缓存机制**: Redis缓存提升系统性能
- **配置管理**: 支持多环境配置和动态配置更新

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

## 🗄 数据库设计

### 数据库架构

项目使用MySQL 8.0作为主数据库，Redis作为缓存层，实现读写分离和高性能查询。

### 核心表结构

#### 用户表 (users)
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    email VARCHAR(100) UNIQUE COMMENT '邮箱',
    password VARCHAR(255) COMMENT '密码',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(255) COMMENT '头像URL',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    role ENUM('USER', 'ADMIN') DEFAULT 'USER' COMMENT '角色',
    last_login_time DATETIME COMMENT '最后登录时间',
    login_count INT DEFAULT 0 COMMENT '登录次数',
    token VARCHAR(255) COMMENT '当前token',
    token_expire_time DATETIME COMMENT 'token过期时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at DATETIME COMMENT '删除时间',
    
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

#### 商品表 (products)
```sql
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '商品ID',
    name VARCHAR(200) NOT NULL COMMENT '商品名称',
    description TEXT COMMENT '商品描述',
    category_id BIGINT COMMENT '分类ID',
    brand VARCHAR(100) COMMENT '品牌',
    sku VARCHAR(100) UNIQUE COMMENT 'SKU编码',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    cost_price DECIMAL(10,2) COMMENT '成本价',
    market_price DECIMAL(10,2) COMMENT '市场价',
    stock INT DEFAULT 0 COMMENT '库存数量',
    min_stock INT DEFAULT 0 COMMENT '最小库存预警',
    sales_count INT DEFAULT 0 COMMENT '销售数量',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    weight DECIMAL(8,2) COMMENT '重量(kg)',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-下架, 1-上架',
    is_featured BOOLEAN DEFAULT FALSE COMMENT '是否推荐',
    images JSON COMMENT '商品图片',
    specifications JSON COMMENT '商品规格',
    tags VARCHAR(500) COMMENT '标签',
    seo_title VARCHAR(200) COMMENT 'SEO标题',
    seo_keywords VARCHAR(500) COMMENT 'SEO关键词',
    seo_description TEXT COMMENT 'SEO描述',
    sort_order INT DEFAULT 0 COMMENT '排序',
    created_by BIGINT COMMENT '创建人',
    updated_by BIGINT COMMENT '更新人',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted_at DATETIME COMMENT '删除时间',
    
    INDEX idx_name (name),
    INDEX idx_category (category_id),
    INDEX idx_brand (brand),
    INDEX idx_sku (sku),
    INDEX idx_price (price),
    INDEX idx_stock (stock),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    FULLTEXT idx_search (name, description, tags)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';
```

#### 商品分类表 (product_categories)
```sql
CREATE TABLE product_categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID',
    name VARCHAR(100) NOT NULL COMMENT '分类名称',
    slug VARCHAR(100) UNIQUE COMMENT '分类别名',
    description TEXT COMMENT '分类描述',
    image VARCHAR(255) COMMENT '分类图片',
    icon VARCHAR(100) COMMENT '分类图标',
    level TINYINT DEFAULT 1 COMMENT '分类层级',
    path VARCHAR(500) COMMENT '分类路径',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    sort_order INT DEFAULT 0 COMMENT '排序',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_parent (parent_id),
    INDEX idx_slug (slug),
    INDEX idx_level (level),
    INDEX idx_status (status),
    INDEX idx_sort (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';
```

#### 系统日志表 (system_logs)
```sql
CREATE TABLE system_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    user_id BIGINT COMMENT '用户ID',
    action VARCHAR(100) NOT NULL COMMENT '操作类型',
    module VARCHAR(50) COMMENT '模块名称',
    method VARCHAR(10) COMMENT 'HTTP方法',
    url VARCHAR(500) COMMENT '请求URL',
    params TEXT COMMENT '请求参数',
    response TEXT COMMENT '响应结果',
    ip VARCHAR(45) COMMENT 'IP地址',
    user_agent VARCHAR(500) COMMENT '用户代理',
    execution_time INT COMMENT '执行时间(ms)',
    status TINYINT COMMENT '状态: 0-失败, 1-成功',
    error_message TEXT COMMENT '错误信息',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    INDEX idx_user (user_id),
    INDEX idx_action (action),
    INDEX idx_module (module),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    INDEX idx_ip (ip)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统日志表';
```

### 数据库索引策略

1. **主键索引**: 所有表都使用自增主键
2. **唯一索引**: 用户名、邮箱、SKU等唯一字段
3. **普通索引**: 查询频繁的字段如状态、时间等
4. **复合索引**: 多字段组合查询的优化
5. **全文索引**: 商品搜索功能的支持

### Redis缓存设计

```
缓存键设计模式:
- 用户缓存: user:{user_id}
- 商品缓存: product:{product_id}
- 商品列表: product:list:{page}:{size}:{filters_hash}
- 分类缓存: category:{category_id}
- 分类树: category:tree
- 配置缓存: config:{key}
- 会话缓存: session:{token}

TTL设置:
- 用户信息: 30分钟
- 商品信息: 1小时  
- 商品列表: 15分钟
- 分类信息: 4小时
- 配置信息: 24小时
- 会话信息: 2小时
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

## 🔒 安全配置

### 安全最佳实践

#### 1. 认证和授权安全

**Token安全配置**
```yaml
# application-prod.yml
security:
  jwt:
    secret: ${JWT_SECRET:your-very-long-and-complex-secret-key-here}
    expiration: ${JWT_EXPIRATION:7200} # 2小时
    refresh-expiration: ${JWT_REFRESH_EXPIRATION:604800} # 7天
    
admin:
  auth:
    username: ${ADMIN_USERNAME:admin}
    password: ${ADMIN_PASSWORD} # 通过环境变量设置强密码
    max-login-attempts: 5
    lockout-duration: 1800 # 30分钟
    session-timeout: 3600 # 1小时
```

**密码安全策略**
```java
// 密码加密配置
@Configuration
public class PasswordConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // 强度12
    }
    
    // 密码复杂度验证
    public boolean isValidPassword(String password) {
        return password.length() >= 8 &&
               password.matches(".*[A-Z].*") &&
               password.matches(".*[a-z].*") &&
               password.matches(".*[0-9].*") &&
               password.matches(".*[!@#$%^&*()].*");
    }
}
```

#### 2. 数据库安全

**连接安全配置**
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/online_store?useSSL=true&requireSSL=true&serverTimezone=Asia/Shanghai
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    hikari:
      connection-test-query: SELECT 1
      validation-timeout: 3000
      leak-detection-threshold: 60000
      maximum-pool-size: 20
      minimum-idle: 5
```

**SQL注入防护**
- 使用MyBatis的参数化查询
- 禁用动态SQL拼接
- 输入参数严格验证

#### 3. Redis安全配置

```yaml
spring:
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD} # 设置强密码
      ssl: true # 启用SSL
      timeout: 2000ms
      jedis:
        pool:
          max-active: 8
          max-idle: 8
          min-idle: 2
          max-wait: -1ms
```

#### 4. 网络安全

**HTTPS配置**
```yaml
server:
  port: 8443
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: ${SSL_KEYSTORE_PASSWORD}
    key-store-type: PKCS12
    key-alias: tomcat
    protocol: TLS
    enabled-protocols: TLSv1.2,TLSv1.3
```

**CORS配置**
```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("https://*.yourdomain.com") // 限制允许的域名
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
```

#### 5. 请求安全

**请求限制配置**
```yaml
security:
  rate-limit:
    enabled: true
    requests-per-minute: 60
    burst-capacity: 100
    
  request-size:
    max-request-size: 10MB
    max-file-size: 5MB
```

**XSS和CSRF防护**
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
            .headers(headers -> headers
                .frameOptions().deny()
                .contentTypeOptions().and()
                .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                    .maxAgeInSeconds(31536000)
                    .includeSubdomains(true)
                )
            );
        return http.build();
    }
}
```

### 生产环境安全检查清单

#### 部署前检查
- [ ] 修改所有默认密码
- [ ] 启用HTTPS/SSL
- [ ] 配置防火墙规则
- [ ] 设置数据库访问白名单
- [ ] 启用Redis AUTH
- [ ] 配置日志审计
- [ ] 设置文件上传限制
- [ ] 启用请求频率限制

#### 运行时监控
- [ ] 监控异常登录尝试
- [ ] 监控SQL慢查询
- [ ] 监控Redis连接数
- [ ] 监控API响应时间
- [ ] 监控系统资源使用
- [ ] 定期备份数据
- [ ] 定期更新依赖包

#### 数据保护
```yaml
# 敏感数据加密配置
jasypt:
  encryptor:
    password: ${JASYPT_ENCRYPTOR_PASSWORD}
    algorithm: PBEWITHHMACSHA512ANDAES_256
    iv-generator-classname: org.jasypt.iv.RandomIvGenerator
```

#### 日志安全
```xml
<!-- logback-spring.xml -->
<configuration>
    <property name="LOG_FILE" value="/var/log/online-store/application"/>
    
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_FILE}.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>${LOG_FILE}.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <maxFileSize>100MB</maxFileSize>
            <maxHistory>30</maxHistory>
            <totalSizeCap>3GB</totalSizeCap>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <!-- 不记录敏感信息 -->
    <logger name="org.springframework.security" level="WARN"/>
    <logger name="org.apache.http.wire" level="WARN"/>
</configuration>
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

## ⚡ 性能优化

### JVM调优

#### 生产环境JVM参数
```bash
# 启动脚本示例
java -jar \
  -server \
  -Xms2g -Xmx4g \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:G1HeapRegionSize=16m \
  -XX:+UseStringDeduplication \
  -XX:+PrintGCDetails \
  -XX:+PrintGCTimeStamps \
  -XX:+PrintGCApplicationStoppedTime \
  -Xloggc:/var/log/online-store/gc.log \
  -XX:+UseGCLogFileRotation \
  -XX:NumberOfGCLogFiles=5 \
  -XX:GCLogFileSize=10M \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:HeapDumpPath=/var/log/online-store/heapdump/ \
  -Djava.awt.headless=true \
  -Dfile.encoding=UTF-8 \
  -Dspring.profiles.active=prod \
  online-store-1.0-SNAPSHOT.jar
```

#### JVM监控和分析
```yaml
# application-prod.yml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus,httptrace
  endpoint:
    health:
      show-details: when-authorized
  metrics:
    export:
      prometheus:
        enabled: true
    distribution:
      percentiles-histogram:
        http.server.requests: true
      percentiles:
        http.server.requests: 0.5,0.95,0.99
```

### 数据库性能优化

#### MySQL配置优化
```sql
-- my.cnf 优化配置
[mysqld]
# 内存设置
innodb_buffer_pool_size = 2G  # 系统内存的70-80%
innodb_log_file_size = 256M
innodb_log_buffer_size = 16M
innodb_flush_log_at_trx_commit = 2

# 连接设置
max_connections = 500
max_connect_errors = 10000
connect_timeout = 10
wait_timeout = 28800

# 查询缓存
query_cache_size = 128M
query_cache_type = 1

# 慢查询日志
slow_query_log = 1
long_query_time = 2
slow_query_log_file = /var/log/mysql/slow.log

# 索引优化
key_buffer_size = 256M
read_buffer_size = 2M
read_rnd_buffer_size = 16M
sort_buffer_size = 8M
```

#### 数据库连接池优化
```yaml
spring:
  datasource:
    hikari:
      # 连接池大小（公式：CPU核数 * 2 + 磁盘数）
      maximum-pool-size: 20
      minimum-idle: 5
      # 连接超时
      connection-timeout: 30000
      # 空闲超时
      idle-timeout: 600000
      # 最大生命周期
      max-lifetime: 1800000
      # 连接测试查询
      connection-test-query: SELECT 1
      # 验证超时
      validation-timeout: 3000
      # 泄漏检测阈值
      leak-detection-threshold: 60000
```

#### SQL优化策略
```java
// 分页查询优化
@Service
public class ProductService {
    
    // 使用索引进行分页
    public PageResponse<Product> listProducts(ProductPageRequest request) {
        // 避免使用 OFFSET，使用游标分页
        Long lastId = request.getLastId();
        List<Product> products = productMapper.selectByLastId(lastId, request.getSize());
        
        // 缓存热点数据
        String cacheKey = "product:list:" + request.hashCode();
        return redisTemplate.opsForValue().get(cacheKey);
    }
    
    // 批量操作优化
    @Transactional
    public void batchCreateProducts(List<CreateProductRequest> requests) {
        // 分批处理，避免长事务
        int batchSize = 100;
        for (int i = 0; i < requests.size(); i += batchSize) {
            List<CreateProductRequest> batch = requests.subList(i, 
                Math.min(i + batchSize, requests.size()));
            productMapper.batchInsert(batch);
        }
    }
}
```

### Redis性能优化

#### Redis配置优化
```yaml
spring:
  data:
    redis:
      # 连接池配置
      jedis:
        pool:
          max-active: 20
          max-idle: 10
          min-idle: 5
          max-wait: 3000ms
      # 序列化配置
      serialization:
        key-serializer: org.springframework.data.redis.serializer.StringRedisSerializer
        value-serializer: org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
        hash-key-serializer: org.springframework.data.redis.serializer.StringRedisSerializer
        hash-value-serializer: org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
```

#### 缓存策略优化
```java
@Service
public class CacheService {
    
    // 多级缓存策略
    @Cacheable(value = "products", key = "#id", unless = "#result == null")
    public Product getProduct(Long id) {
        return productMapper.selectById(id);
    }
    
    // 缓存穿透防护
    public Product getProductSafe(Long id) {
        String cacheKey = "product:" + id;
        Product product = redisTemplate.opsForValue().get(cacheKey);
        
        if (product == null) {
            // 使用布隆过滤器防止缓存穿透
            if (!bloomFilter.mightContain(id)) {
                return null;
            }
            
            product = productMapper.selectById(id);
            if (product != null) {
                redisTemplate.opsForValue().set(cacheKey, product, Duration.ofHours(1));
            } else {
                // 缓存空值，防止缓存穿透
                redisTemplate.opsForValue().set(cacheKey, new NullProduct(), Duration.ofMinutes(5));
            }
        }
        
        return product instanceof NullProduct ? null : product;
    }
    
    // 缓存预热
    @PostConstruct
    public void warmUpCache() {
        List<Product> hotProducts = productMapper.selectHotProducts();
        for (Product product : hotProducts) {
            String cacheKey = "product:" + product.getId();
            redisTemplate.opsForValue().set(cacheKey, product, Duration.ofHours(2));
        }
    }
}
```

### 应用层优化

#### 线程池配置
```java
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {
    
    @Override
    @Bean(name = "taskExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // 核心线程数 = CPU核数
        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors());
        // 最大线程数 = CPU核数 * 2
        executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors() * 2);
        // 队列容量
        executor.setQueueCapacity(200);
        // 线程名前缀
        executor.setThreadNamePrefix("Async-");
        // 拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待任务完成后关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(300);
        
        executor.initialize();
        return executor;
    }
}
```

#### HTTP客户端优化
```java
@Configuration
public class RestTemplateConfig {
    
    @Bean
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory factory = 
            new HttpComponentsClientHttpRequestFactory();
        
        // 连接超时
        factory.setConnectTimeout(5000);
        // 读取超时
        factory.setReadTimeout(10000);
        // 连接请求超时
        factory.setConnectionRequestTimeout(3000);
        
        // HTTP客户端配置
        CloseableHttpClient httpClient = HttpClients.custom()
            .setMaxConnTotal(200)
            .setMaxConnPerRoute(20)
            .setKeepAliveStrategy((response, context) -> 30 * 1000) // 30秒
            .build();
            
        factory.setHttpClient(httpClient);
        
        return new RestTemplate(factory);
    }
}
```

### 监控和诊断

#### 性能监控指标
```yaml
# application.yml
management:
  metrics:
    tags:
      application: online-store
    enable:
      jvm: true
      system: true
      web: true
      jdbc: true
      redis: true
  
  endpoint:
    metrics:
      enabled: true
    prometheus:
      enabled: true
```

#### 自定义性能监控
```java
@Component
public class PerformanceMonitor {
    
    private final MeterRegistry meterRegistry;
    private final Timer.Sample sample;
    
    // 方法执行时间监控
    @EventListener
    public void handleMethodExecution(MethodExecutionEvent event) {
        Timer.Sample.start(meterRegistry)
            .stop(Timer.builder("method.execution.time")
                .tag("class", event.getClassName())
                .tag("method", event.getMethodName())
                .register(meterRegistry));
    }
    
    // 数据库连接池监控
    @Scheduled(fixedRate = 30000)
    public void monitorConnectionPool() {
        HikariDataSource datasource = (HikariDataSource) dataSource;
        Gauge.builder("hikari.connections.active")
            .register(meterRegistry, datasource, HikariDataSource::getHikariPoolMXBean::getActiveConnections);
    }
}
```

### 性能测试

#### JMeter测试配置
```xml
<!-- 性能测试计划 -->
<jmeterTestPlan version="1.2">
  <hashTree>
    <TestPlan>
      <elementProp name="TestPlan.arguments" elementType="Arguments" guiclass="ArgumentsPanel">
        <collectionProp name="Arguments.arguments">
          <elementProp name="host" elementType="Argument">
            <stringProp name="Argument.name">host</stringProp>
            <stringProp name="Argument.value">localhost</stringProp>
          </elementProp>
          <elementProp name="port" elementType="Argument">
            <stringProp name="Argument.name">port</stringProp>
            <stringProp name="Argument.value">8080</stringProp>
          </elementProp>
        </collectionProp>
      </elementProp>
    </TestPlan>
  </hashTree>
</jmeterTestPlan>
```

#### 压力测试脚本
```bash
#!/bin/bash
# 性能测试脚本

echo "开始性能测试..."

# 登录获取Token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}' \
  | jq -r '.token')

# 商品列表API压力测试
ab -n 10000 -c 100 -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/products

# 用户列表API压力测试  
ab -n 5000 -c 50 -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/users

echo "性能测试完成"
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

## 🚀 CI/CD 和开发流程

### 开发工作流

#### 1. Git Flow 工作流程
```
main (生产)
├── develop (开发)
    ├── feature/user-management (功能分支)
    ├── feature/product-catalog (功能分支)
    └── release/v1.1.0 (发布分支)
└── hotfix/critical-bug (热修复分支)
```

#### 2. 分支命名规范
```bash
# 功能分支
feature/user-authentication
feature/product-search
feature/order-management

# 修复分支
fix/login-token-expire
fix/database-connection

# 热修复分支
hotfix/critical-security-patch
hotfix/data-corruption

# 发布分支
release/v1.0.0
release/v1.1.0
```

#### 3. 提交消息规范
```bash
# 提交类型
feat: 新功能
fix: 修复Bug
docs: 文档更新
style: 代码格式调整
refactor: 代码重构
test: 测试相关
chore: 构建过程或辅助工具的变动

# 提交示例
feat: 添加用户注册功能
fix: 修复登录token过期问题
docs: 更新API文档
refactor: 重构商品查询逻辑
test: 添加用户服务单元测试
chore: 更新Maven依赖版本
```

### CI/CD 配置

#### 1. GitHub Actions 工作流
```yaml
# .github/workflows/ci-cd.yml
name: CI/CD Pipeline

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main, develop ]

jobs:
  test:
    runs-on: ubuntu-latest
    
    services:
      mysql:
        image: mysql:8.0
        env:
          MYSQL_ROOT_PASSWORD: testpass
          MYSQL_DATABASE: online_store_test
        ports:
          - 3306:3306
        options: --health-cmd="mysqladmin ping" --health-interval=10s --health-timeout=5s --health-retries=3
      
      redis:
        image: redis:7-alpine
        ports:
          - 6379:6379
        options: --health-cmd="redis-cli ping" --health-interval=10s --health-timeout=5s --health-retries=3

    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
        cache: maven
    
    - name: Run tests
      run: |
        mvn clean test
        mvn jacoco:report
    
    - name: Upload coverage to Codecov
      uses: codecov/codecov-action@v3
      with:
        file: target/site/jacoco/jacoco.xml
    
    - name: SonarQube Scan
      uses: sonarqube-quality-gate-action@master
      env:
        SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}

  build:
    needs: test
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
        cache: maven
    
    - name: Build application
      run: mvn clean package -DskipTests
    
    - name: Build Docker image
      run: |
        docker build -t online-store:${{ github.sha }} .
        docker tag online-store:${{ github.sha }} online-store:latest
    
    - name: Login to DockerHub
      uses: docker/login-action@v2
      with:
        username: ${{ secrets.DOCKER_USERNAME }}
        password: ${{ secrets.DOCKER_TOKEN }}
    
    - name: Push Docker image
      run: |
        docker push online-store:${{ github.sha }}
        docker push online-store:latest

  deploy:
    needs: build
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    
    steps:
    - name: Deploy to staging
      uses: appleboy/ssh-action@v0.1.5
      with:
        host: ${{ secrets.STAGING_HOST }}
        username: ${{ secrets.STAGING_USER }}
        key: ${{ secrets.STAGING_SSH_KEY }}
        script: |
          docker pull online-store:latest
          docker stop online-store || true
          docker rm online-store || true
          docker run -d --name online-store \
            -p 8080:8080 \
            -e SPRING_PROFILES_ACTIVE=staging \
            -e MYSQL_HOST=${{ secrets.MYSQL_HOST }} \
            -e MYSQL_USERNAME=${{ secrets.MYSQL_USERNAME }} \
            -e MYSQL_PASSWORD=${{ secrets.MYSQL_PASSWORD }} \
            -e REDIS_HOST=${{ secrets.REDIS_HOST }} \
            online-store:latest
```

#### 2. Jenkins Pipeline
```groovy
// Jenkinsfile
pipeline {
    agent any
    
    environment {
        DOCKER_IMAGE = 'online-store'
        DOCKER_TAG = "${BUILD_NUMBER}"
        MAVEN_OPTS = '-Xmx1024m'
    }
    
    stages {
        stage('Clean Workspace') {
            steps {
                cleanWs()
            }
        }
        
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/example/online-store.git'
            }
        }
        
        stage('Test') {
            parallel {
                stage('Unit Tests') {
                    steps {
                        sh 'mvn clean test'
                    }
                    post {
                        always {
                            junit 'target/surefire-reports/*.xml'
                            jacoco execPattern: 'target/jacoco.exec'
                        }
                    }
                }
                
                stage('Integration Tests') {
                    steps {
                        sh 'mvn verify -Pfailsafe'
                    }
                }
                
                stage('Code Quality') {
                    steps {
                        withSonarQubeEnv('SonarQube') {
                            sh 'mvn sonar:sonar'
                        }
                    }
                }
            }
        }
        
        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
        
        stage('Docker Build') {
            steps {
                script {
                    dockerImage = docker.build("${DOCKER_IMAGE}:${DOCKER_TAG}")
                    dockerImage.push()
                    dockerImage.push("latest")
                }
            }
        }
        
        stage('Deploy to Staging') {
            when {
                branch 'main'
            }
            steps {
                sh """
                    ssh user@staging-server '
                        docker pull ${DOCKER_IMAGE}:${DOCKER_TAG}
                        docker stop online-store || true
                        docker rm online-store || true
                        docker run -d --name online-store \\
                            -p 8080:8080 \\
                            -e SPRING_PROFILES_ACTIVE=staging \\
                            ${DOCKER_IMAGE}:${DOCKER_TAG}
                    '
                """
            }
        }
        
        stage('Health Check') {
            steps {
                script {
                    sh 'sleep 30' // 等待应用启动
                    sh 'curl -f http://staging-server:8080/actuator/health || exit 1'
                }
            }
        }
        
        stage('Deploy to Production') {
            when {
                branch 'main'
            }
            steps {
                input message: 'Deploy to Production?', ok: 'Deploy'
                sh """
                    ssh user@prod-server '
                        docker pull ${DOCKER_IMAGE}:${DOCKER_TAG}
                        docker stop online-store || true
                        docker rm online-store || true
                        docker run -d --name online-store \\
                            -p 8080:8080 \\
                            -e SPRING_PROFILES_ACTIVE=prod \\
                            ${DOCKER_IMAGE}:${DOCKER_TAG}
                    '
                """
            }
        }
    }
    
    post {
        always {
            cleanWs()
        }
        success {
            mail to: 'team@example.com',
                 subject: "SUCCESS: ${env.JOB_NAME} - ${env.BUILD_NUMBER}",
                 body: "Build succeeded: ${env.BUILD_URL}"
        }
        failure {
            mail to: 'team@example.com',
                 subject: "FAILED: ${env.JOB_NAME} - ${env.BUILD_NUMBER}",
                 body: "Build failed: ${env.BUILD_URL}"
        }
    }
}
```

### 质量保证

#### 1. 代码质量检查
```xml
<!-- pom.xml 质量插件配置 -->
<plugin>
    <groupId>com.github.spotbugs</groupId>
    <artifactId>spotbugs-maven-plugin</artifactId>
    <version>4.7.3</version>
    <configuration>
        <effort>Max</effort>
        <threshold>Medium</threshold>
        <failOnError>true</failOnError>
    </configuration>
</plugin>

<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.8</version>
    <configuration>
        <rules>
            <rule>
                <element>CLASS</element>
                <limits>
                    <limit>
                        <counter>LINE</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.80</minimum>
                    </limit>
                </limits>
            </rule>
        </rules>
    </configuration>
</plugin>
```

#### 2. SonarQube配置
```properties
# sonar-project.properties
sonar.projectKey=online-store
sonar.projectName=Online Store
sonar.projectVersion=1.0
sonar.sources=src/main/java
sonar.tests=src/test/java
sonar.java.binaries=target/classes
sonar.java.test.binaries=target/test-classes
sonar.java.libraries=target/dependency/*.jar
sonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
sonar.exclusions=**/*Application.java,**/*Config.java
```

### 环境管理

#### 1. 多环境配置
```yaml
# application-dev.yml (开发环境)
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/online_store_dev
  data:
    redis:
      host: localhost
logging:
  level:
    com.example.onlinestore: DEBUG

---
# application-test.yml (测试环境)
spring:
  datasource:
    url: jdbc:mysql://test-db:3306/online_store_test
  data:
    redis:
      host: test-redis
logging:
  level:
    com.example.onlinestore: INFO

---
# application-staging.yml (预发布环境)
spring:
  datasource:
    url: jdbc:mysql://staging-db:3306/online_store_staging
  data:
    redis:
      host: staging-redis
logging:
  level:
    com.example.onlinestore: WARN

---
# application-prod.yml (生产环境)
spring:
  datasource:
    url: jdbc:mysql://prod-db:3306/online_store
  data:
    redis:
      host: prod-redis
logging:
  level:
    com.example.onlinestore: ERROR
```

#### 2. Docker Compose多环境
```yaml
# docker-compose.dev.yml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - MYSQL_HOST=mysql
      - REDIS_HOST=redis
    depends_on:
      - mysql
      - redis
    volumes:
      - .:/workspace

  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: devpass
      MYSQL_DATABASE: online_store_dev

  redis:
    image: redis:7-alpine

---
# docker-compose.prod.yml
version: '3.8'
services:
  app:
    image: online-store:latest
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - MYSQL_HOST=mysql
      - REDIS_HOST=redis
    depends_on:
      - mysql
      - redis
    restart: unless-stopped

  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      MYSQL_DATABASE: online_store
    volumes:
      - mysql_data:/var/lib/mysql
    restart: unless-stopped

  redis:
    image: redis:7-alpine
    volumes:
      - redis_data:/data
    restart: unless-stopped

volumes:
  mysql_data:
  redis_data:
```

### 发布策略

#### 1. 蓝绿部署
```bash
#!/bin/bash
# 蓝绿部署脚本

GREEN_VERSION=$(docker ps --filter "name=online-store-green" --format "table {{.Image}}" | tail -n +2)
BLUE_VERSION=$(docker ps --filter "name=online-store-blue" --format "table {{.Image}}" | tail -n +2)

if [ -z "$GREEN_VERSION" ]; then
    # 部署到绿环境
    echo "部署到绿环境..."
    docker run -d --name online-store-green \
        -p 8081:8080 \
        -e SPRING_PROFILES_ACTIVE=prod \
        online-store:$NEW_VERSION
    
    # 健康检查
    sleep 30
    curl -f http://localhost:8081/actuator/health
    
    if [ $? -eq 0 ]; then
        # 切换流量
        nginx -s reload  # 更新Nginx配置
        # 停止蓝环境
        docker stop online-store-blue
        docker rm online-store-blue
    fi
else
    # 部署到蓝环境
    echo "部署到蓝环境..."
    docker run -d --name online-store-blue \
        -p 8082:8080 \
        -e SPRING_PROFILES_ACTIVE=prod \
        online-store:$NEW_VERSION
    
    # 健康检查和切换...
fi
```

#### 2. 滚动更新(Kubernetes)
```yaml
# k8s-deployment.yml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: online-store
spec:
  replicas: 3
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxUnavailable: 1
      maxSurge: 1
  selector:
    matchLabels:
      app: online-store
  template:
    metadata:
      labels:
        app: online-store
    spec:
      containers:
      - name: online-store
        image: online-store:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 10
          periodSeconds: 5
        resources:
          limits:
            memory: "2Gi"
            cpu: "1000m"
          requests:
            memory: "1Gi"
            cpu: "500m"
```

## 🤝 开发指南

### 代码规范
- 遵循阿里巴巴Java开发手册
- 使用统一的代码格式化规则
- 必须编写单元测试，覆盖率不低于80%
- 所有公共方法必须有完整的JavaDoc注释

### 开发环境搭建
```bash
# 1. 安装必要工具
# Java 17
# Maven 3.6+
# Docker & Docker Compose
# Git

# 2. 克隆项目
git clone https://github.com/example/online-store.git
cd online-store

# 3. 启动开发环境
docker-compose -f docker-compose.dev.yml up -d

# 4. 运行应用
mvn spring-boot:run -Dspring.profiles.active=dev

# 5. 运行测试
mvn test
```

### 代码提交流程
```bash
# 1. 创建功能分支
git checkout -b feature/new-feature

# 2. 开发和测试
# ... 编写代码 ...
mvn test

# 3. 提交代码
git add .
git commit -m "feat: 添加新功能"

# 4. 推送分支
git push origin feature/new-feature

# 5. 创建Pull Request
# 在GitHub上创建PR，等待代码审查

# 6. 合并到主分支
git checkout main
git pull origin main
git merge feature/new-feature
git push origin main

# 7. 删除功能分支
git branch -d feature/new-feature
git push origin --delete feature/new-feature
```

### 分支管理策略
- `main`: 主分支，用于生产发布，要求稳定
- `develop`: 开发分支，用于功能集成
- `feature/*`: 功能分支，用于新功能开发
- `release/*`: 发布分支，用于版本发布准备
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

## 🛠 故障排除

### 启动相关问题

#### 1. 数据库连接失败
```bash
# 错误信息
com.mysql.cj.jdbc.exceptions.CommunicationsException: Communications link failure

# 解决方案
# 1. 检查MySQL服务状态
systemctl status mysql
# 或
brew services list | grep mysql

# 2. 检查数据库配置
mysql -u root -p
SHOW DATABASES;
SELECT User, Host FROM mysql.user;

# 3. 检查网络连接
telnet localhost 3306

# 4. 检查防火墙设置
sudo ufw status
sudo firewall-cmd --list-ports

# 5. 验证数据库URL和凭据
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/online_store?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
```

#### 2. Redis连接异常
```bash
# 错误信息
redis.clients.jedis.exceptions.JedisConnectionException: Could not get a resource from the pool

# 解决方案
# 1. 检查Redis服务状态
redis-cli ping
# 或
systemctl status redis

# 2. 检查Redis配置
redis-cli
127.0.0.1:6379> CONFIG GET "*"

# 3. 检查连接池配置
spring:
  data:
    redis:
      host: localhost
      port: 6379
      timeout: 2000ms
      jedis:
        pool:
          max-active: 8
          max-idle: 8
          min-idle: 0
          max-wait: -1ms

# 4. 清理Redis连接
redis-cli
FLUSHDB
```

#### 3. 端口占用问题
```bash
# 错误信息
Web server failed to start. Port 8080 was already in use.

# 解决方案
# 1. 查找占用端口的进程
lsof -i :8080
netstat -tulpn | grep 8080

# 2. 终止占用进程
kill -9 <PID>

# 3. 修改应用端口
server:
  port: 8081
```

#### 4. JVM内存不足
```bash
# 错误信息
java.lang.OutOfMemoryError: Java heap space

# 解决方案
# 1. 增加堆内存大小
java -Xms1g -Xmx2g -jar online-store.jar

# 2. 分析内存使用
jmap -histo <PID>
jstat -gc <PID>

# 3. 生成堆转储分析
java -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp/heapdump.hprof -jar online-store.jar
```

### 运行时问题

#### 1. 接口响应慢
```bash
# 问题诊断
# 1. 检查应用日志
tail -f /var/log/online-store/application.log

# 2. 监控SQL执行时间
SELECT * FROM information_schema.processlist WHERE command != 'Sleep';
SHOW FULL PROCESSLIST;

# 3. 检查Redis性能
redis-cli --latency-history -i 1

# 4. 查看JVM GC情况
jstat -gc <PID> 5s

# 解决方案
# 1. 优化数据库查询
EXPLAIN SELECT * FROM products WHERE name LIKE '%keyword%';

# 2. 添加适当索引
CREATE INDEX idx_product_name ON products(name);

# 3. 启用缓存
@Cacheable(value = "products", key = "#id")
public Product getProduct(Long id) { ... }

# 4. 使用异步处理
@Async
public CompletableFuture<List<Product>> getProductsAsync() { ... }
```

#### 2. 内存泄漏问题
```bash
# 问题诊断
# 1. 监控内存使用趋势
jstat -gc <PID> 10s 100

# 2. 分析内存分布
jmap -dump:format=b,file=heapdump.hprof <PID>

# 3. 使用MAT工具分析堆转储
# Eclipse Memory Analyzer Tool

# 解决方案
# 1. 检查静态集合
private static List<Object> cache = new ArrayList<>(); // 潜在泄漏

# 2. 正确关闭资源
try (Connection conn = dataSource.getConnection()) {
    // 使用连接
} // 自动关闭

# 3. 避免监听器泄漏
@PreDestroy
public void cleanup() {
    eventListener.removeAllListeners();
}
```

#### 3. 死锁问题
```bash
# 问题诊断
# 1. 检查数据库死锁
SELECT * FROM information_schema.innodb_locks;
SHOW ENGINE INNODB STATUS;

# 2. 检查应用线程死锁
jstack <PID>

# 解决方案
# 1. 统一锁顺序
synchronized (lock1) {
    synchronized (lock2) {
        // 业务逻辑
    }
}

# 2. 使用超时锁
if (lock.tryLock(10, TimeUnit.SECONDS)) {
    try {
        // 业务逻辑
    } finally {
        lock.unlock();
    }
}

# 3. 数据库事务超时
spring:
  transaction:
    default-timeout: 30
```

### 配置相关问题

#### 1. Nacos配置不生效
```bash
# 问题诊断
# 1. 检查Nacos连接
curl http://localhost:8848/nacos/v1/ns/instance/list?serviceName=online-store

# 2. 验证配置拉取
curl http://localhost:8848/nacos/v1/cs/configs?dataId=online-store&group=DEFAULT_GROUP

# 解决方案
# 1. 检查bootstrap.yml配置
spring:
  cloud:
    nacos:
      config:
        enabled: true
        server-addr: localhost:8848
        file-extension: yml

# 2. 验证配置刷新
@RefreshScope
@RestController
public class ConfigController {
    @Value("${test.config:default}")
    private String testConfig;
}
```

#### 2. 国际化不生效
```bash
# 问题诊断
# 1. 检查国际化文件路径
src/main/resources/i18n/messages.properties
src/main/resources/i18n/messages_zh_CN.properties

# 2. 验证MessageSource配置
@Configuration
public class MessageConfig {
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("i18n.messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}

# 解决方案
# 1. 检查请求头
Accept-Language: zh-CN,zh;q=0.9,en;q=0.8

# 2. 使用LocaleContextHolder
Locale locale = LocaleContextHolder.getLocale();
String message = messageSource.getMessage("error.invalid.user", null, locale);
```

### 性能问题排查

#### 1. CPU使用率过高
```bash
# 问题诊断
# 1. 查看系统负载
top
htop
sar -u 1 10

# 2. 分析Java进程
jstack <PID> > thread_dump.txt
# 多次执行，对比线程状态

# 3. 查看方法调用热点
java -XX:+FlightRecorder -XX:StartFlightRecording=duration=60s,filename=profile.jfr -jar app.jar

# 解决方案
# 1. 优化算法复杂度
# 2. 使用缓存减少计算
# 3. 异步处理耗时操作
# 4. 数据库查询优化
```

#### 2. 频繁GC问题
```bash
# 问题诊断
# 1. GC日志分析
-XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xloggc:gc.log

# 2. 使用GC分析工具
GCViewer、GCEasy.io

# 解决方案
# 1. 调整堆大小
-Xms2g -Xmx4g

# 2. 选择合适的GC算法
-XX:+UseG1GC
-XX:MaxGCPauseMillis=200

# 3. 减少对象创建
使用对象池、StringBuilder等
```

### 部署问题

#### 1. Docker容器启动失败
```bash
# 问题诊断
# 1. 查看容器日志
docker logs <container_id>

# 2. 检查容器状态
docker ps -a
docker inspect <container_id>

# 3. 进入容器调试
docker exec -it <container_id> /bin/bash

# 解决方案
# 1. 检查Dockerfile
FROM openjdk:17-jdk-slim
COPY target/online-store.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

# 2. 检查端口映射
docker run -p 8080:8080 online-store

# 3. 检查环境变量
docker run -e MYSQL_HOST=localhost online-store
```

#### 2. K8s部署问题
```bash
# 问题诊断
# 1. 查看Pod状态
kubectl get pods
kubectl describe pod <pod-name>

# 2. 查看Pod日志
kubectl logs <pod-name>

# 3. 查看服务状态
kubectl get svc
kubectl describe svc <service-name>

# 解决方案
# 1. 检查资源限制
resources:
  limits:
    memory: "2Gi"
    cpu: "1000m"
  requests:
    memory: "1Gi"
    cpu: "500m"

# 2. 检查健康检查
livenessProbe:
  httpGet:
    path: /actuator/health
    port: 8080
  initialDelaySeconds: 60
  periodSeconds: 10
```

### 常见问题快速解决

#### Q1: 启动时报"找不到主类"错误
```bash
# 问题：java.lang.ClassNotFoundException: com.example.onlinestore.OnlineStoreApplication
# 解决：
mvn clean package
java -jar target/online-store-1.0-SNAPSHOT.jar
```

#### Q2: 接口返回404错误
```bash
# 问题：请求/api/users返回404
# 解决：
# 1. 检查Controller注解
@RestController
@RequestMapping("/api/users")
public class UserController { ... }

# 2. 检查ComponentScan配置
@SpringBootApplication(scanBasePackages = "com.example.onlinestore")
```

#### Q3: 数据库乱码问题
```bash
# 问题：中文数据显示为问号
# 解决：
# 1. 检查数据库字符集
ALTER DATABASE online_store CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 2. 检查连接参数
jdbc:mysql://localhost:3306/online_store?useUnicode=true&characterEncoding=utf-8
```

#### Q4: Token过期问题
```bash
# 问题：接口返回401 Unauthorized
# 解决：
# 1. 重新登录获取token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}'

# 2. 检查token过期时间配置
admin:
  auth:
    token-expire-time: 7200 # 2小时
```

### 日志分析技巧

#### 1. 关键日志位置
```bash
# 应用日志
/var/log/online-store/application.log

# GC日志
/var/log/online-store/gc.log

# 访问日志
/var/log/online-store/access.log

# 错误日志
/var/log/online-store/error.log
```

#### 2. 日志分析命令
```bash
# 查看错误日志
grep "ERROR" application.log | tail -20

# 统计接口访问频率
grep "GET /api" access.log | awk '{print $7}' | sort | uniq -c | sort -nr

# 查看慢查询
grep "slow" application.log | grep -o "cost=[0-9]*ms" | sort -nr

# 监控实时日志
tail -f application.log | grep "ERROR\|WARN"
```

### 监控和告警

#### 1. 关键监控指标
- CPU使用率 > 80%
- 内存使用率 > 85%
- 磁盘使用率 > 90%
- 数据库连接数 > 80%
- Redis连接数 > 80%
- 接口响应时间 > 2秒
- 错误率 > 5%

#### 2. 告警配置示例
```yaml
# Prometheus告警规则
groups:
- name: online-store
  rules:
  - alert: HighCPUUsage
    expr: cpu_usage_percent > 80
    for: 5m
    labels:
      severity: warning
    annotations:
      summary: "CPU使用率过高"
      
  - alert: HighMemoryUsage
    expr: memory_usage_percent > 85
    for: 5m
    labels:
      severity: critical
    annotations:
      summary: "内存使用率过高"
```

### 技术支持
- 📧 邮箱: support@example.com
- 🐛 问题反馈: [GitHub Issues](https://github.com/example/online-store/issues)
- 📖 文档: [项目Wiki](https://github.com/example/online-store/wiki)

---

**Made with ❤️ by Online Store Team** 