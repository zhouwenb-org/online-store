# 数据库设计文档

## 📊 概览

在线商店管理系统数据库设计，包含用户管理、商品管理、库存管理等核心功能模块。

## 🗄️ 数据库表结构

### 核心表统计
- **用户相关**: 1个表
- **商品相关**: 5个表
- **库存相关**: 2个表
- **总计**: 8个核心表

## 📋 表详细说明

### 1. 用户管理模块

#### `users` - 用户表
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

**字段说明**:
- `id`: 用户唯一标识
- `username`: 用户名，唯一约束
- `token`: 登录令牌
- `token_expire_time`: 令牌过期时间
- `created_at/updated_at`: 创建/更新时间

### 2. 商品管理模块

#### `product_categories` - 商品分类表
支持分层分类结构，包含父子关系。

**核心字段**:
- `name`: 分类名称（唯一）
- `parent_id`: 父分类ID，支持多级分类
- `sort_order`: 排序权重
- `is_active`: 启用状态

#### `products` - 商品表（增强版）
```sql
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL COMMENT '商品名称',
    category VARCHAR(100) COMMENT '商品分类（冗余字段）',
    category_id BIGINT COMMENT '分类ID',
    price DECIMAL(10,2) NOT NULL COMMENT '商品价格',
    original_price DECIMAL(10,2) COMMENT '原价',
    sku VARCHAR(100) UNIQUE COMMENT '商品SKU编码',
    barcode VARCHAR(100) COMMENT '条形码',
    description TEXT COMMENT '商品描述',
    specifications JSON COMMENT '规格参数',
    images JSON COMMENT '商品图片',
    brand VARCHAR(100) COMMENT '品牌',
    model VARCHAR(100) COMMENT '型号',
    weight DECIMAL(8,3) COMMENT '重量（kg）',
    dimensions VARCHAR(100) COMMENT '尺寸',
    unit VARCHAR(20) DEFAULT '件' COMMENT '计量单位',
    is_active TINYINT(1) DEFAULT 1 COMMENT '上架状态',
    is_featured TINYINT(1) DEFAULT 0 COMMENT '推荐商品',
    sort_order INT DEFAULT 0 COMMENT '排序权重',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    sale_count INT DEFAULT 0 COMMENT '销售数量',
    created_by BIGINT COMMENT '创建人',
    updated_by BIGINT COMMENT '更新人',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

**设计亮点**:
- **JSON字段**: `specifications`和`images`使用JSON存储灵活数据
- **冗余设计**: `category`字段便于快速查询
- **业务字段**: 支持原价、促销价、浏览统计等
- **完整索引**: 针对常用查询场景优化
- **审计字段**: 记录创建人和更新人

#### `product_tags` - 商品标签表
用于商品标签管理，支持多标签。

#### `product_tag_relations` - 商品标签关联表
多对多关系表，支持一个商品拥有多个标签。

#### `product_attributes` - 商品属性定义表
动态属性系统，支持不同类型的属性定义。

**属性类型**:
- `TEXT`: 文本类型
- `NUMBER`: 数字类型  
- `SELECT`: 单选类型
- `MULTI_SELECT`: 多选类型
- `BOOLEAN`: 布尔类型

#### `product_attribute_values` - 商品属性值表
存储具体商品的属性值。

### 3. 库存管理模块

#### `inventories` - 库存表
```sql
CREATE TABLE inventories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    current_stock INT NOT NULL DEFAULT 0,
    min_stock INT NOT NULL DEFAULT 0,
    max_stock INT NOT NULL DEFAULT 1000,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_product_id (product_id),
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);
```

**业务逻辑**:
- 一个商品对应一条库存记录
- 支持最小库存预警
- 支持最大库存限制

#### `inventory_records` - 库存操作记录表
```sql
CREATE TABLE inventory_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    operation_type ENUM('IN', 'OUT', 'ADJUST') NOT NULL,
    quantity INT NOT NULL,
    before_stock INT NOT NULL,
    after_stock INT NOT NULL,
    reason VARCHAR(500),
    operator_id BIGINT,
    operated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

**操作类型**:
- `IN`: 入库操作
- `OUT`: 出库操作
- `ADJUST`: 库存调整

## 🔗 表关系图

```
users (1) ←→ (n) products [created_by/updated_by]
users (1) ←→ (n) inventory_records [operator_id]

product_categories (1) ←→ (n) product_categories [parent_id] (自关联)
product_categories (1) ←→ (n) products [category_id]

products (1) ←→ (1) inventories [product_id]
products (1) ←→ (n) inventory_records [product_id]
products (1) ←→ (n) product_tag_relations [product_id]
products (1) ←→ (n) product_attribute_values [product_id]

product_tags (1) ←→ (n) product_tag_relations [tag_id]
product_attributes (1) ←→ (n) product_attribute_values [attribute_id]
```

## 📊 索引策略

### 单列索引
- **主键索引**: 所有表的`id`字段
- **唯一索引**: `username`, `sku`, `product_id`等
- **常用查询索引**: `name`, `category`, `price`, `brand`等

### 复合索引
- `idx_category_price`: 分类+价格组合查询
- `idx_active_featured`: 上架状态+推荐状态
- `idx_category_active`: 分类+状态组合查询

### 外键索引
- 所有外键字段自动创建索引
- 优化关联查询性能

## 🎯 设计特点

### 1. **可扩展性**
- JSON字段支持灵活的商品规格
- 动态属性系统适应不同商品类型
- 分层分类支持无限级分类

### 2. **性能优化**
- 合理的索引设计
- 冗余字段减少关联查询
- 分页查询友好

### 3. **数据完整性**
- 外键约束保证数据一致性
- 唯一约束防止重复数据
- NOT NULL约束保证必要数据

### 4. **业务友好**
- 软删除vs硬删除策略
- 状态字段便于业务控制
- 审计字段追踪数据变更

### 5. **标准化**
- 统一的命名规范
- 标准的时间戳字段
- 规范的注释说明

## 📁 文件说明

### `schema.sql`
数据库表结构定义文件，包含：
- 所有表的CREATE语句
- 索引和约束定义
- 外键关系定义
- 详细的字段注释

### `data.sql`
初始化数据文件，包含：
- 基础用户数据
- 完整的商品分类体系
- 示例商品数据（10个商品）
- 商品标签和属性数据
- 库存和操作记录数据

## 🚀 使用方法

### 1. 创建数据库表结构
```sql
source /path/to/schema.sql;
```

### 2. 导入初始化数据
```sql
source /path/to/data.sql;
```

### 3. 验证数据
```sql
-- 检查表数量
SHOW TABLES;

-- 检查数据量
SELECT 'Products' as Table_Name, COUNT(*) as Record_Count FROM products
UNION ALL
SELECT 'Categories', COUNT(*) FROM product_categories
UNION ALL
SELECT 'Inventories', COUNT(*) FROM inventories;
```

## 📈 数据统计

### 初始化数据量
- **商品分类**: 23个（包含一级和二级分类）
- **商品标签**: 8个
- **商品属性**: 8种属性类型
- **示例商品**: 10个
- **库存记录**: 10条
- **操作记录**: 12条历史记录

### 数据特点
- 涵盖电子产品、家居用品、服装、图书等多个品类
- 包含热销、新品、特价等多种标签
- 演示完整的商品生命周期
- 提供真实的业务场景数据

## 🔧 维护建议

### 1. **定期优化**
- 监控慢查询日志
- 分析索引使用情况
- 优化热点数据查询

### 2. **数据清理**
- 定期归档历史操作记录
- 清理无效的商品数据
- 维护合理的数据量

### 3. **备份策略**
- 定期全量备份
- 增量备份重要变更
- 测试恢复流程

---

**最后更新**: 2024-12-28  
**设计版本**: v1.0.0  
**兼容性**: MySQL 8.0+