# 在线商店 API 快速参考

## 🚀 快速开始

### 基础信息
- **基础URL**: `http://localhost:8080`
- **认证方式**: Token (Header: `X-Token`)
- **数据格式**: JSON

### 获取Token
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

## 📋 API端点总览

### 🔐 认证 `/api/auth`
| 方法 | 端点 | 描述 | 权限 |
|------|------|------|------|
| POST | `/login` | 用户登录 | 无 |
| POST | `/logout` | 用户登出 | Token |

### 🛍️ 商品 `/api/products`
| 方法 | 端点 | 描述 | 权限 |
|------|------|------|------|
| POST | `/` | 创建商品 | 管理员 |
| GET | `/` | 查询商品列表 | 无 |
| GET | `/{id}` | 根据ID查询 | 无 |
| PUT | `/{id}` | 更新商品 | 管理员 |
| DELETE | `/{id}` | 删除商品 | 管理员 |
| GET | `/categories` | 获取分类 | 无 |

### 📦 库存 `/api/inventory`
| 方法 | 端点 | 描述 | 权限 |
|------|------|------|------|
| GET | `/` | 查询库存列表 | 无 |
| GET | `/product/{id}` | 根据商品ID查询 | 无 |
| POST | `/stock-in` | 入库操作 | 管理员 |
| POST | `/stock-out` | 出库操作 | 管理员 |
| PUT | `/adjust` | 库存调整 | 管理员 |
| PUT | `/{id}/settings` | 更新设置 | 管理员 |
| GET | `/records` | 变更记录 | 无 |
| GET | `/stats` | 库存统计 | 无 |
| GET | `/check` | 检查库存 | 无 |
| GET | `/low-stock/count` | 低库存数量 | 无 |

### 👥 用户 `/api/users`
| 方法 | 端点 | 描述 | 权限 |
|------|------|------|------|
| GET | `/` | 查询用户列表 | 管理员 |

## 🔥 常用操作示例

### 商品管理
```bash
# 创建商品
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -H "X-Token: your-token" \
  -d '{"name":"iPhone 15","category":"电子产品","price":6999.00}'

# 查询商品
curl "http://localhost:8080/api/products?pageNum=1&pageSize=10&category=电子产品"
```

### 库存管理
```bash
# 入库
curl -X POST http://localhost:8080/api/inventory/stock-in \
  -H "Content-Type: application/json" \
  -H "X-Token: your-token" \
  -d '{"productId":1,"quantity":100,"reason":"采购入库"}'

# 出库
curl -X POST http://localhost:8080/api/inventory/stock-out \
  -H "Content-Type: application/json" \
  -H "X-Token: your-token" \
  -d '{"productId":1,"quantity":30,"reason":"销售出库"}'

# 查询库存
curl "http://localhost:8080/api/inventory?pageNum=1&pageSize=10"
```

## ⚡ 状态码说明

| 状态码 | 含义 |
|--------|------|
| 200 | 成功 |
| 400 | 请求错误 |
| 401 | 未认证 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器错误 |

## 📎 工具

- **Postman集合**: `OnlineStore_API.postman_collection.json`
- **完整文档**: `API_DOCUMENTATION.md`
- **测试脚本**: `run-tests.sh`