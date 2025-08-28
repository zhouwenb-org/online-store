# 在线商店管理系统

这是一个基于Spring Boot开发的现代化在线商店管理系统，提供完整的商品管理、库存管理、用户管理和认证功能。

## 🚀 快速开始

### 环境要求
- Java 17+
- MySQL 8.0+
- Redis 6.0+
- Maven 3.6+

### 启动步骤
1. 克隆项目
```bash
git clone <repository-url>
cd online-store
```

2. 配置数据库
```bash
# 创建数据库
mysql -u root -p
CREATE DATABASE online_store DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. 配置应用
```bash
# 编辑配置文件，设置数据库和Redis连接信息
vim src/main/resources/application.yml
```

4. 运行应用
```bash
mvn spring-boot:run
```

5. 访问应用
- API服务: http://localhost:8080
- API文档: [API_DOCUMENTATION.md](API_DOCUMENTATION.md)

## 📚 文档

- **📖 完整API文档**: [API_DOCUMENTATION.md](API_DOCUMENTATION.md)
- **⚡ 快速参考**: [API_QUICK_REFERENCE.md](API_QUICK_REFERENCE.md)
- **📊 测试覆盖率报告**: [TEST_COVERAGE_REPORT.md](TEST_COVERAGE_REPORT.md)
- **🧪 Postman集合**: [OnlineStore_API.postman_collection.json](OnlineStore_API.postman_collection.json)

## 🏗️ 系统架构

### 核心模块
- **用户认证模块**: 登录/登出、Token管理
- **商品管理模块**: 商品CRUD、分类管理、分页查询
- **库存管理模块**: 入库/出库、库存调整、统计预警
- **用户管理模块**: 用户信息管理

### 技术栈
- **后端框架**: Spring Boot 3.1.5
- **数据库**: MySQL 8.0 + MyBatis 3.0.2
- **缓存**: Redis (Jedis 4.3.1)
- **认证**: Token-based认证
- **验证**: Bean Validation
- **测试**: JUnit 5 + Mockito (260+测试用例)
- **日志**: SLF4J + Logback

## 🛠️ 项目结构

```
src/
├── main/java/com/example/onlinestore/
│   ├── annotation/          # 自定义注解 (@RequireAdmin, @ValidateParams)
│   ├── aspect/              # AOP切面 (权限验证, 参数验证)
│   ├── config/              # 配置类 (Redis, MyBatis, Web等)
│   ├── controller/          # 控制器层
│   │   ├── AuthController.java      # 认证接口
│   │   ├── ProductController.java   # 商品管理接口
│   │   ├── InventoryController.java # 库存管理接口
│   │   └── UserController.java     # 用户管理接口
│   ├── dto/                 # 数据传输对象
│   ├── mapper/              # 数据访问层 (MyBatis)
│   ├── model/               # 数据模型
│   │   ├── Product.java             # 商品模型
│   │   ├── User.java                # 用户模型
│   │   ├── Inventory.java           # 库存模型
│   │   └── InventoryRecord.java     # 库存记录模型
│   ├── service/             # 业务逻辑层
│   └── OnlineStoreApplication.java  # 启动类
├── main/resources/
│   ├── mapper/              # MyBatis XML映射文件
│   ├── db/schema.sql        # 数据库表结构
│   ├── i18n/                # 国际化资源文件
│   └── application.yml      # 应用配置
└── test/                    # 测试代码 (15个测试类, 260+测试用例)
    ├── service/             # 服务层单元测试
    ├── controller/          # 控制器集成测试
    ├── mapper/              # 数据访问层测试
    ├── dto/                 # DTO验证测试
    └── model/               # 模型测试
```

## 🧪 测试

项目包含完整的测试套件，**覆盖率超过50%**：

### 测试统计
- **测试文件总数**: 15个
- **测试用例总数**: 260+个
- **代码覆盖率**: 50%+

### 运行测试
```bash
# 运行所有测试
mvn test

# 运行特定测试模块
mvn test -Dtest="*ServiceTest"      # 服务层测试
mvn test -Dtest="*ControllerTest"   # 控制器测试

# 运行特定测试类
mvn test -Dtest="InventoryServiceTest"

# 使用测试脚本 (推荐)
./run-tests.sh

# 生成覆盖率报告
mvn test jacoco:report
```

### 测试覆盖范围
- **单元测试**: Service层、Model层、DTO层
- **集成测试**: Controller层、Mapper层
- **组件测试**: Aspect层、Configuration层

## 🔧 API使用示例

### 认证
```bash
# 登录获取Token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 响应
{
  "token": "550e8400-e29b-41d4-a716-446655440000",
  "expireTime": "2024-12-29T10:00:00"
}
```

### 商品管理
```bash
# 创建商品 (需要管理员权限)
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -H "X-Token: your-token-here" \
  -d '{"name":"iPhone 15","category":"电子产品","price":6999.00}'

# 查询商品列表 (支持分页和筛选)
curl "http://localhost:8080/api/products?pageNum=1&pageSize=10&category=电子产品"

# 根据ID查询商品
curl "http://localhost:8080/api/products/1"
```

### 库存管理
```bash
# 入库操作 (需要管理员权限)
curl -X POST http://localhost:8080/api/inventory/stock-in \
  -H "Content-Type: application/json" \
  -H "X-Token: your-token-here" \
  -d '{"productId":1,"quantity":100,"reason":"采购入库"}'

# 出库操作 (需要管理员权限)
curl -X POST http://localhost:8080/api/inventory/stock-out \
  -H "Content-Type: application/json" \
  -H "X-Token: your-token-here" \
  -d '{"productId":1,"quantity":30,"reason":"销售出库"}'

# 查询库存统计
curl "http://localhost:8080/api/inventory/stats"

# 检查库存充足性
curl "http://localhost:8080/api/inventory/check?productId=1&quantity=50"
```

## 📋 主要功能

### ✅ 用户认证
- 用户登录/登出
- Token-based认证
- 管理员权限验证
- 自动Token过期管理

### ✅ 商品管理
- 商品CRUD操作
- 分页查询和条件筛选
- 分类管理
- 价格范围查询
- 缓存优化

### ✅ 库存管理
- 库存CRUD操作
- 入库/出库操作
- 库存调整功能
- 库存预警 (低库存/零库存)
- 操作记录追踪
- 库存统计报表

### ✅ 系统功能
- 参数验证 (@Valid, 自定义验证)
- 异常处理 (统一错误响应)
- 国际化支持 (中英文)
- 操作日志记录
- Redis缓存优化

## 🔒 安全特性

- **Token认证**: 基于UUID的安全Token机制
- **权限控制**: `@RequireAdmin` 注解控制管理员权限
- **参数验证**: `@ValidateParams` 注解 + Bean Validation
- **操作审计**: 完整的库存操作日志记录
- **错误处理**: 统一的异常处理，不暴露敏感信息
- **输入验证**: 防止SQL注入和XSS攻击

## 🌐 国际化

支持中英文双语：
- 错误消息国际化 (`messages.properties`, `messages_zh_CN.properties`)
- 成功消息国际化
- 自动语言检测 (`LocaleContextHolder`)

## 📊 监控和日志

- **日志级别**: DEBUG/INFO/WARN/ERROR
- **操作审计**: 完整的用户操作记录
- **性能监控**: 关键操作性能追踪
- **错误追踪**: 详细的异常堆栈信息
- **缓存监控**: Redis操作日志

## 🚀 生产部署

### 环境配置
```yaml
# application-prod.yml
spring:
  datasource:
    url: jdbc:mysql://prod-db:3306/online_store
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  
  redis:
    host: ${REDIS_HOST}
    port: ${REDIS_PORT}
    password: ${REDIS_PASSWORD}

logging:
  level:
    com.example.onlinestore: INFO
  file:
    name: /var/log/online-store/application.log
```

### Docker部署
```bash
# 构建镜像
docker build -t online-store:latest .

# 运行容器
docker run -d \
  --name online-store \
  -p 8080:8080 \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=password \
  -e REDIS_HOST=redis \
  online-store:latest
```

## 📈 性能优化

- **缓存策略**: 商品信息缓存、Token缓存
- **分页查询**: 大数据量分页处理
- **连接池**: 数据库连接池优化
- **异步处理**: 库存变更记录异步写入
- **索引优化**: 数据库索引优化

## 🤝 贡献指南

1. Fork项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开Pull Request

### 开发规范
- 遵循阿里巴巴Java开发手册
- 单元测试覆盖率不低于70%
- 所有API需要添加相应的集成测试
- 提交前运行完整测试套件

## 📞 联系我们

- 项目维护者: 开发团队
- 技术支持: dev-team@example.com
- 项目地址: [GitHub Repository](https://github.com/example/online-store)

## 📄 许可证

该项目基于MIT许可证开源 - 查看 [LICENSE](LICENSE) 文件了解详情。

---

**最后更新**: 2024-12-28  
**当前版本**: v1.0.0  
**测试覆盖率**: 50%+  
**API端点数**: 20+