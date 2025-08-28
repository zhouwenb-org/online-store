# 在线商店 API 文档

## 📋 概述

这是一个基于Spring Boot开发的在线商店管理系统API文档，提供商品管理、库存管理、用户管理和认证功能。

### 版本信息
- **版本**: v1.0.0
- **基础URL**: `http://localhost:8080`
- **文档更新时间**: 2024-12-28

### 技术栈
- **框架**: Spring Boot 3.x
- **数据库**: MySQL/H2
- **缓存**: Redis
- **验证**: Bean Validation
- **日志**: SLF4J + Logback

## 🔐 认证机制

### Token认证
系统使用基于Token的认证机制：
- 登录成功后获得Token
- 后续请求需在Header中携带Token
- Token格式：`X-Token: your-token-here`

### 权限级别
- **普通用户**: 可查询商品、库存信息
- **管理员**: 可执行所有操作（创建、更新、删除）

## 📡 通用响应格式

### 成功响应
```json
{
  "data": {}, // 响应数据
  "timestamp": "2024-12-28T10:00:00"
}
```

### 错误响应
```json
{
  "message": "错误信息",
  "timestamp": "2024-12-28T10:00:00"
}
```

### 分页响应
```json
{
  "records": [],      // 数据列表
  "total": 100,       // 总记录数
  "pageNum": 1,       // 当前页码
  "pageSize": 10      // 每页大小
}
```

## 🔑 认证管理 API

### 1. 用户登录

**接口**: `POST /api/auth/login`

**描述**: 用户登录获取访问Token

**请求参数**:
```json
{
  "username": "string",  // 用户名
  "password": "string"   // 密码
}
```

**响应示例**:
```json
{
  "token": "550e8400-e29b-41d4-a716-446655440000",
  "expireTime": "2024-12-29T10:00:00"
}
```

**错误代码**:
- `400`: 用户名或密码错误
- `500`: 系统内部错误

---

### 2. 用户登出

**接口**: `POST /api/auth/logout`

**描述**: 用户登出，清除Token

**请求头**:
```
X-Token: your-token-here
```

**响应示例**:
```json
{
  "message": "登出成功"
}
```

**错误代码**:
- `400`: Token无效或已过期
- `500`: 系统内部错误

## 🛍️ 商品管理 API

### 1. 创建商品

**接口**: `POST /api/products`

**权限**: 需要管理员权限

**请求参数**:
```json
{
  "name": "string",        // 商品名称 (必填)
  "category": "string",    // 商品分类 (必填)
  "price": 299.99          // 商品价格 (必填, ≥0.01)
}
```

**响应示例**:
```json
{
  "id": 1,
  "name": "iPhone 15",
  "category": "电子产品",
  "price": 6999.00,
  "createdAt": "2024-12-28T10:00:00",
  "updatedAt": "2024-12-28T10:00:00"
}
```

---

### 2. 查询商品列表

**接口**: `GET /api/products`

**查询参数**:
- `pageNum` (int): 页码，默认1
- `pageSize` (int): 每页大小，默认10，最大100
- `name` (string): 商品名称模糊查询
- `category` (string): 分类精确查询
- `minPrice` (decimal): 最低价格
- `maxPrice` (decimal): 最高价格

**请求示例**:
```
GET /api/products?pageNum=1&pageSize=10&category=电子产品&minPrice=1000&maxPrice=10000
```

**响应示例**:
```json
{
  "records": [
    {
      "id": 1,
      "name": "iPhone 15",
      "category": "电子产品",
      "price": 6999.00,
      "createdAt": "2024-12-28T10:00:00",
      "updatedAt": "2024-12-28T10:00:00"
    }
  ],
  "total": 1,
  "pageNum": 1,
  "pageSize": 10
}
```

---

### 3. 根据ID查询商品

**接口**: `GET /api/products/{id}`

**路径参数**:
- `id` (long): 商品ID

**响应示例**:
```json
{
  "id": 1,
  "name": "iPhone 15",
  "category": "电子产品",
  "price": 6999.00,
  "createdAt": "2024-12-28T10:00:00",
  "updatedAt": "2024-12-28T10:00:00"
}
```

**错误代码**:
- `400`: 商品不存在

---

### 4. 更新商品

**接口**: `PUT /api/products/{id}`

**权限**: 需要管理员权限

**路径参数**:
- `id` (long): 商品ID

**请求参数** (部分更新，所有字段可选):
```json
{
  "name": "string",        // 商品名称
  "category": "string",    // 商品分类  
  "price": 299.99          // 商品价格 (≥0.01)
}
```

**响应示例**:
```json
{
  "id": 1,
  "name": "iPhone 15 Pro",
  "category": "电子产品",
  "price": 7999.00,
  "createdAt": "2024-12-28T10:00:00",
  "updatedAt": "2024-12-28T15:30:00"
}
```

---

### 5. 删除商品

**接口**: `DELETE /api/products/{id}`

**权限**: 需要管理员权限

**路径参数**:
- `id` (long): 商品ID

**响应示例**:
```json
{
  "message": "商品删除成功"
}
```

---

### 6. 获取所有商品分类

**接口**: `GET /api/products/categories`

**响应示例**:
```json
[
  "电子产品",
  "家居用品",
  "服装",
  "图书"
]
```

## 📦 库存管理 API

### 1. 查询库存列表

**接口**: `GET /api/inventory`

**查询参数**:
- `pageNum` (int): 页码，默认1
- `pageSize` (int): 每页大小，默认10
- `productName` (string): 商品名称模糊查询
- `category` (string): 分类查询
- `lowStockOnly` (boolean): 仅显示低库存商品，默认false

**响应示例**:
```json
{
  "records": [
    {
      "id": 1,
      "productId": 1,
      "productName": "iPhone 15",
      "category": "电子产品",
      "price": 6999.00,
      "currentStock": 50,
      "minStock": 10,
      "maxStock": 1000,
      "isLowStock": false,
      "createdAt": "2024-12-28T10:00:00",
      "updatedAt": "2024-12-28T10:00:00"
    }
  ],
  "total": 1,
  "pageNum": 1,
  "pageSize": 10
}
```

---

### 2. 根据商品ID查询库存

**接口**: `GET /api/inventory/product/{productId}`

**路径参数**:
- `productId` (long): 商品ID

**响应示例**:
```json
{
  "id": 1,
  "productId": 1,
  "currentStock": 50,
  "minStock": 10,
  "maxStock": 1000,
  "createdAt": "2024-12-28T10:00:00",
  "updatedAt": "2024-12-28T10:00:00"
}
```

---

### 3. 入库操作

**接口**: `POST /api/inventory/stock-in`

**权限**: 需要管理员权限

**请求参数**:
```json
{
  "productId": 1,          // 商品ID (必填)
  "quantity": 100,         // 入库数量 (必填, ≥1)
  "reason": "采购入库"      // 入库原因 (可选)
}
```

**响应示例**:
```json
{
  "message": "入库操作成功",
  "inventory": {
    "id": 1,
    "productId": 1,
    "currentStock": 150,   // 更新后的库存
    "minStock": 10,
    "maxStock": 1000,
    "updatedAt": "2024-12-28T15:30:00"
  }
}
```

---

### 4. 出库操作

**接口**: `POST /api/inventory/stock-out`

**权限**: 需要管理员权限

**请求参数**:
```json
{
  "productId": 1,          // 商品ID (必填)
  "quantity": 30,          // 出库数量 (必填, ≥1)
  "reason": "销售出库"      // 出库原因 (可选)
}
```

**响应示例**:
```json
{
  "message": "出库操作成功",
  "inventory": {
    "id": 1,
    "productId": 1,
    "currentStock": 120,   // 更新后的库存
    "minStock": 10,
    "maxStock": 1000,
    "updatedAt": "2024-12-28T16:00:00"
  }
}
```

**错误代码**:
- `400`: 库存不足

---

### 5. 库存调整

**接口**: `PUT /api/inventory/adjust`

**权限**: 需要管理员权限

**请求参数**:
```json
{
  "productId": 1,          // 商品ID (必填)
  "newStock": 200,         // 新库存数量 (必填, ≥0)
  "reason": "盘点调整"      // 调整原因 (可选)
}
```

**响应示例**:
```json
{
  "message": "库存调整成功",
  "inventory": {
    "id": 1,
    "productId": 1,
    "currentStock": 200,
    "minStock": 10,
    "maxStock": 1000,
    "updatedAt": "2024-12-28T16:30:00"
  }
}
```

---

### 6. 更新库存设置

**接口**: `PUT /api/inventory/{inventoryId}/settings`

**权限**: 需要管理员权限

**路径参数**:
- `inventoryId` (long): 库存记录ID

**查询参数**:
- `minStock` (int): 最小库存 (可选)
- `maxStock` (int): 最大库存 (可选)

**请求示例**:
```
PUT /api/inventory/1/settings?minStock=20&maxStock=2000
```

**响应示例**:
```json
{
  "message": "库存设置更新成功",
  "inventory": {
    "id": 1,
    "productId": 1,
    "currentStock": 200,
    "minStock": 20,        // 更新的最小库存
    "maxStock": 2000,      // 更新的最大库存
    "updatedAt": "2024-12-28T17:00:00"
  }
}
```

---

### 7. 查询库存变更记录

**接口**: `GET /api/inventory/records`

**查询参数**:
- `pageNum` (int): 页码，默认1
- `pageSize` (int): 每页大小，默认10
- `productId` (long): 商品ID
- `productName` (string): 商品名称
- `operationType` (string): 操作类型 (IN/OUT/ADJUST)
- `operatorId` (long): 操作人ID

**响应示例**:
```json
{
  "records": [
    {
      "id": 1,
      "productId": 1,
      "operationType": "IN",
      "quantity": 100,
      "beforeStock": 50,
      "afterStock": 150,
      "reason": "采购入库",
      "operatorId": 1,
      "operatedAt": "2024-12-28T15:30:00"
    }
  ],
  "total": 1,
  "pageNum": 1,
  "pageSize": 10
}
```

---

### 8. 获取库存统计

**接口**: `GET /api/inventory/stats`

**响应示例**:
```json
{
  "totalProducts": 100,      // 总商品数
  "lowStockProducts": 15,    // 低库存商品数
  "outOfStockProducts": 5,   // 零库存商品数
  "normalStockProducts": 80  // 正常库存商品数
}
```

---

### 9. 检查库存充足性

**接口**: `GET /api/inventory/check`

**查询参数**:
- `productId` (long): 商品ID (必填)
- `quantity` (int): 所需数量 (必填)

**请求示例**:
```
GET /api/inventory/check?productId=1&quantity=50
```

**响应示例**:
```json
{
  "productId": 1,
  "quantity": 50,
  "isEnough": true         // 库存是否充足
}
```

---

### 10. 获取低库存商品数量

**接口**: `GET /api/inventory/low-stock/count`

**响应示例**:
```json
{
  "lowStockCount": 15
}
```

## 👥 用户管理 API

### 1. 查询用户列表

**接口**: `GET /api/users`

**权限**: 需要管理员权限

**查询参数**:
- `pageNum` (int): 页码，默认1
- `pageSize` (int): 每页大小，默认10

**响应示例**:
```json
{
  "records": [
    {
      "id": 1,
      "username": "admin",
      "createdAt": "2024-12-28T10:00:00",
      "updatedAt": "2024-12-28T10:00:00"
    }
  ],
  "total": 1,
  "pageNum": 1,
  "pageSize": 10
}
```

## ❌ 错误代码说明

### HTTP状态码
- `200`: 请求成功
- `400`: 请求参数错误或业务逻辑错误
- `401`: 未认证或Token无效
- `403`: 权限不足
- `404`: 资源不存在
- `500`: 服务器内部错误

### 常见错误消息
- `"用户名或密码错误"`: 登录凭据无效
- `"无效或已过期的token"`: Token验证失败
- `"商品不存在，ID：{id}"`: 指定商品不存在
- `"库存不足，当前库存：{current}，需要：{required}"`: 出库数量超过库存
- `"商品ID不能为空"`: 必填参数缺失
- `"系统内部错误"`: 服务器异常

## 🔧 使用示例

### 完整的操作流程示例

#### 1. 管理员登录
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

#### 2. 创建商品
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -H "X-Token: your-token-here" \
  -d '{
    "name": "iPhone 15",
    "category": "电子产品",
    "price": 6999.00
  }'
```

#### 3. 创建库存
```bash
curl -X POST http://localhost:8080/api/inventory/stock-in \
  -H "Content-Type: application/json" \
  -H "X-Token: your-token-here" \
  -d '{
    "productId": 1,
    "quantity": 100,
    "reason": "初始库存"
  }'
```

#### 4. 查询库存
```bash
curl -X GET "http://localhost:8080/api/inventory?pageNum=1&pageSize=10"
```

#### 5. 出库操作
```bash
curl -X POST http://localhost:8080/api/inventory/stock-out \
  -H "Content-Type: application/json" \
  -H "X-Token: your-token-here" \
  -d '{
    "productId": 1,
    "quantity": 10,
    "reason": "销售出库"
  }'
```

#### 6. 登出
```bash
curl -X POST http://localhost:8080/api/auth/logout \
  -H "X-Token: your-token-here"
```

## 📚 SDK和工具

### Postman集合
项目包含完整的Postman测试集合，包含所有API的测试用例。

### 测试数据
```sql
-- 示例商品数据
INSERT INTO products (name, category, price, created_at, updated_at) 
VALUES ('iPhone 15', '电子产品', 6999.00, NOW(), NOW());

-- 示例库存数据
INSERT INTO inventories (product_id, current_stock, min_stock, max_stock, created_at, updated_at)
VALUES (1, 100, 10, 1000, NOW(), NOW());
```

## 🔄 版本更新记录

### v1.0.0 (2024-12-28)
- ✅ 实现用户认证功能 (登录/登出)
- ✅ 实现商品管理功能 (CRUD操作)
- ✅ 实现库存管理功能 (入库/出库/调整/查询)
- ✅ 实现用户管理功能
- ✅ 支持分页查询和条件筛选
- ✅ 支持权限控制和参数验证
- ✅ 支持国际化错误消息
- ✅ 完整的操作审计日志

## 🤝 技术支持

如有API使用问题，请联系开发团队或查阅项目文档。

---

**文档维护**: 开发团队  
**最后更新**: 2024-12-28  
**API版本**: v1.0.0