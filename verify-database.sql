-- ================================================
-- 数据库设计验证脚本
-- ================================================
-- 用途：验证数据库表结构和数据完整性
-- 使用：mysql -u root -p online_store < verify-database.sql
-- ================================================

USE online_store;

-- 显示验证开始信息
SELECT '=======================================' as Info;
SELECT '开始验证数据库设计' as Info;
SELECT '=======================================' as Info;

-- 1. 检查所有表是否存在
SELECT '1. 检查表结构' as Step;
SELECT 
    TABLE_NAME as '表名',
    TABLE_ROWS as '预估行数',
    ROUND(DATA_LENGTH/1024, 2) as '数据大小(KB)',
    TABLE_COMMENT as '表注释'
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = 'online_store' 
ORDER BY TABLE_NAME;

-- 2. 检查外键约束
SELECT '2. 检查外键约束' as Step;
SELECT 
    CONSTRAINT_NAME as '约束名',
    TABLE_NAME as '表名',
    COLUMN_NAME as '字段名',
    REFERENCED_TABLE_NAME as '引用表',
    REFERENCED_COLUMN_NAME as '引用字段'
FROM information_schema.KEY_COLUMN_USAGE 
WHERE TABLE_SCHEMA = 'online_store' 
  AND REFERENCED_TABLE_NAME IS NOT NULL;

-- 3. 检查索引
SELECT '3. 检查索引信息' as Step;
SELECT 
    TABLE_NAME as '表名',
    INDEX_NAME as '索引名',
    COLUMN_NAME as '字段名',
    INDEX_TYPE as '索引类型',
    NON_UNIQUE as '是否唯一'
FROM information_schema.STATISTICS 
WHERE TABLE_SCHEMA = 'online_store' 
  AND INDEX_NAME != 'PRIMARY'
ORDER BY TABLE_NAME, INDEX_NAME;

-- 4. 数据完整性检查
SELECT '4. 数据完整性检查' as Step;

-- 检查各表数据量
SELECT 'users' as 表名, COUNT(*) as 记录数 FROM users
UNION ALL SELECT 'product_categories', COUNT(*) FROM product_categories
UNION ALL SELECT 'products', COUNT(*) FROM products
UNION ALL SELECT 'product_tags', COUNT(*) FROM product_tags
UNION ALL SELECT 'product_tag_relations', COUNT(*) FROM product_tag_relations
UNION ALL SELECT 'product_attributes', COUNT(*) FROM product_attributes
UNION ALL SELECT 'product_attribute_values', COUNT(*) FROM product_attribute_values
UNION ALL SELECT 'inventories', COUNT(*) FROM inventories
UNION ALL SELECT 'inventory_records', COUNT(*) FROM inventory_records;

-- 5. 业务数据检查
SELECT '5. 业务数据验证' as Step;

-- 检查商品和库存一致性
SELECT '商品库存一致性检查' as 检查项;
SELECT 
    COUNT(p.id) as 总商品数,
    COUNT(i.product_id) as 有库存商品数,
    COUNT(p.id) - COUNT(i.product_id) as 无库存商品数
FROM products p 
LEFT JOIN inventories i ON p.id = i.product_id;

-- 检查分类层级
SELECT '分类层级检查' as 检查项;
SELECT 
    '一级分类' as 分类级别,
    COUNT(*) as 数量
FROM product_categories 
WHERE parent_id IS NULL
UNION ALL
SELECT 
    '二级分类',
    COUNT(*)
FROM product_categories 
WHERE parent_id IS NOT NULL;

-- 检查商品标签关联
SELECT '商品标签统计' as 检查项;
SELECT 
    t.name as 标签名,
    COUNT(ptr.product_id) as 关联商品数
FROM product_tags t
LEFT JOIN product_tag_relations ptr ON t.id = ptr.tag_id
GROUP BY t.id, t.name
ORDER BY COUNT(ptr.product_id) DESC;

-- 6. 库存统计
SELECT '6. 库存统计信息' as Step;

SELECT 
    '总库存数量' as 统计项,
    SUM(current_stock) as 数值
FROM inventories
UNION ALL
SELECT 
    '低库存商品数',
    COUNT(*)
FROM inventories 
WHERE current_stock <= min_stock
UNION ALL
SELECT 
    '零库存商品数',
    COUNT(*)
FROM inventories 
WHERE current_stock = 0
UNION ALL
SELECT 
    '正常库存商品数',
    COUNT(*)
FROM inventories 
WHERE current_stock > min_stock;

-- 7. 示例查询验证
SELECT '7. 示例查询验证' as Step;

-- 商品详情查询（包含分类、标签等）
SELECT '热销商品详情' as 查询类型;
SELECT 
    p.name as 商品名,
    c.name as 分类,
    p.price as 价格,
    i.current_stock as 库存,
    GROUP_CONCAT(t.name) as 标签
FROM products p
LEFT JOIN product_categories c ON p.category_id = c.id
LEFT JOIN inventories i ON p.id = i.product_id
LEFT JOIN product_tag_relations ptr ON p.id = ptr.product_id
LEFT JOIN product_tags t ON ptr.tag_id = t.id
WHERE p.is_active = 1
GROUP BY p.id, p.name, c.name, p.price, i.current_stock
ORDER BY p.sale_count DESC
LIMIT 5;

-- 库存预警查询
SELECT '库存预警商品' as 查询类型;
SELECT 
    p.name as 商品名,
    i.current_stock as 当前库存,
    i.min_stock as 最小库存,
    CASE 
        WHEN i.current_stock = 0 THEN '零库存'
        WHEN i.current_stock <= i.min_stock THEN '低库存'
        ELSE '正常'
    END as 库存状态
FROM inventories i
JOIN products p ON i.product_id = p.id
WHERE i.current_stock <= i.min_stock
ORDER BY i.current_stock ASC;

-- 8. JSON字段验证
SELECT '8. JSON字段验证' as Step;

-- 检查商品规格JSON字段
SELECT '商品规格JSON示例' as 检查项;
SELECT 
    name as 商品名,
    JSON_EXTRACT(specifications, '$.screen') as 屏幕规格,
    JSON_EXTRACT(specifications, '$.chip') as 处理器,
    JSON_EXTRACT(specifications, '$.camera') as 摄像头
FROM products 
WHERE specifications IS NOT NULL 
  AND JSON_VALID(specifications) = 1
LIMIT 3;

-- 显示验证完成信息
SELECT '=======================================' as Info;
SELECT '数据库设计验证完成！' as Info;
SELECT '如果没有错误信息，说明设计正常' as Info;
SELECT '=======================================' as Info;