-- ================================================
-- 在线商店初始化数据
-- ================================================

-- 清空数据（用于重新初始化）
SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM product_attribute_values;
DELETE FROM product_tag_relations;
DELETE FROM product_attributes;
DELETE FROM product_tags;
DELETE FROM inventory_records;
DELETE FROM inventories;
DELETE FROM products;
DELETE FROM product_categories;
DELETE FROM users;
SET FOREIGN_KEY_CHECKS = 1;

-- ================================================
-- 用户数据
-- ================================================
INSERT INTO users (id, username, created_at, updated_at) VALUES
(1, 'admin', NOW(), NOW()),
(2, 'manager', NOW(), NOW()),
(3, 'staff', NOW(), NOW());

-- ================================================
-- 商品分类数据
-- ================================================
INSERT INTO product_categories (id, name, description, parent_id, sort_order, is_active, created_at, updated_at) VALUES
-- 一级分类
(1, '电子产品', '各类电子设备和数码产品', NULL, 1, 1, NOW(), NOW()),
(2, '家居用品', '家庭生活必需品', NULL, 2, 1, NOW(), NOW()),
(3, '服装鞋帽', '时尚服饰和配饰', NULL, 3, 1, NOW(), NOW()),
(4, '图书文具', '图书、办公文具用品', NULL, 4, 1, NOW(), NOW()),
(5, '运动户外', '运动健身和户外用品', NULL, 5, 1, NOW(), NOW()),

-- 二级分类 - 电子产品
(11, '手机通讯', '智能手机、对讲机等通讯设备', 1, 11, 1, NOW(), NOW()),
(12, '电脑办公', '笔记本电脑、台式机、办公设备', 1, 12, 1, NOW(), NOW()),
(13, '家用电器', '冰箱、洗衣机、空调等家电', 1, 13, 1, NOW(), NOW()),
(14, '数码配件', '充电器、数据线、保护套等', 1, 14, 1, NOW(), NOW()),

-- 二级分类 - 家居用品
(21, '厨房用品', '锅具、餐具、厨房小电器', 2, 21, 1, NOW(), NOW()),
(22, '家纺布艺', '床上用品、窗帘、地毯', 2, 22, 1, NOW(), NOW()),
(23, '家具装饰', '沙发、桌椅、装饰品', 2, 23, 1, NOW(), NOW()),

-- 二级分类 - 服装鞋帽
(31, '男装', '男士服装', 3, 31, 1, NOW(), NOW()),
(32, '女装', '女士服装', 3, 32, 1, NOW(), NOW()),
(33, '鞋靴', '各类鞋子', 3, 33, 1, NOW(), NOW()),
(34, '箱包配饰', '手提包、钱包、饰品', 3, 34, 1, NOW(), NOW());

-- ================================================
-- 商品标签数据
-- ================================================
INSERT INTO product_tags (id, name, color, description, created_at) VALUES
(1, '热销', '#ff4757', '销量排行前列的商品', NOW()),
(2, '新品', '#2ed573', '新上架的商品', NOW()),
(3, '特价', '#ffa502', '促销特价商品', NOW()),
(4, '推荐', '#3742fa', '编辑推荐商品', NOW()),
(5, '限量', '#ff6348', '限量发售商品', NOW()),
(6, '包邮', '#1e90ff', '免费配送商品', NOW()),
(7, '进口', '#9c88ff', '进口商品', NOW()),
(8, '环保', '#2f3542', '环保材质商品', NOW());

-- ================================================
-- 商品属性定义数据
-- ================================================
INSERT INTO product_attributes (id, name, type, options, is_required, sort_order, created_at) VALUES
(1, '颜色', 'SELECT', '["黑色", "白色", "红色", "蓝色", "灰色", "金色", "银色"]', 0, 1, NOW()),
(2, '尺寸', 'SELECT', '["XS", "S", "M", "L", "XL", "XXL"]', 0, 2, NOW()),
(3, '容量', 'SELECT', '["64GB", "128GB", "256GB", "512GB", "1TB"]', 0, 3, NOW()),
(4, '材质', 'TEXT', NULL, 0, 4, NOW()),
(5, '产地', 'TEXT', NULL, 0, 5, NOW()),
(6, '保修期', 'SELECT', '["1年", "2年", "3年", "终身"]', 0, 6, NOW()),
(7, '品牌', 'TEXT', NULL, 0, 7, NOW()),
(8, '重量', 'NUMBER', NULL, 0, 8, NOW());

-- ================================================
-- 商品数据
-- ================================================
INSERT INTO products (
    id, name, category, category_id, price, original_price, sku, barcode, 
    description, specifications, images, brand, model, weight, dimensions, 
    unit, is_active, is_featured, sort_order, view_count, sale_count, 
    created_by, updated_by, created_at, updated_at
) VALUES
-- 电子产品
(1, 'iPhone 15 Pro', '手机通讯', 11, 7999.00, 8999.00, 'IP15P-001', '1234567890123',
 'Apple iPhone 15 Pro，搭载A17 Pro芯片，支持5G网络，拍照性能卓越', 
 '{"screen": "6.1英寸", "chip": "A17 Pro", "camera": "4800万像素", "battery": "3274mAh"}',
 '["https://example.com/images/iphone15pro_1.jpg", "https://example.com/images/iphone15pro_2.jpg"]',
 'Apple', 'iPhone 15 Pro', 0.187, '146.6x70.6x8.25mm', '台', 1, 1, 10, 1250, 89, 1, 1, NOW(), NOW()),

(2, 'MacBook Pro 14英寸', '电脑办公', 12, 15999.00, 17999.00, 'MBP14-001', '1234567890124',
 'Apple MacBook Pro 14英寸，M3 Pro芯片，专业级性能',
 '{"screen": "14.2英寸", "chip": "M3 Pro", "memory": "18GB", "storage": "512GB SSD"}',
 '["https://example.com/images/macbookpro14_1.jpg"]',
 'Apple', 'MacBook Pro 14', 1.55, '312.6x221.2x15.5mm', '台', 1, 1, 9, 567, 23, 1, 1, NOW(), NOW()),

(3, '小米13 Ultra', '手机通讯', 11, 5999.00, 6499.00, 'MI13U-001', '1234567890125',
 '小米13 Ultra，徕卡影像，骁龙8 Gen2处理器',
 '{"screen": "6.73英寸", "chip": "骁龙8 Gen2", "camera": "5000万像素徕卡", "battery": "5000mAh"}',
 '["https://example.com/images/mi13ultra_1.jpg"]',
 '小米', 'Mi 13 Ultra', 0.227, '163.18x74.64x9.06mm', '台', 1, 0, 8, 892, 156, 1, 1, NOW(), NOW()),

(4, '戴尔XPS 13', '电脑办公', 12, 8999.00, 9999.00, 'DELL-XPS13-001', '1234567890126',
 '戴尔XPS 13笔记本电脑，Intel Core i7处理器，轻薄便携',
 '{"screen": "13.4英寸", "cpu": "Intel Core i7-1365U", "memory": "16GB", "storage": "512GB SSD"}',
 '["https://example.com/images/dellxps13_1.jpg"]',
 'Dell', 'XPS 13', 1.27, '295.7x199.04x14.8mm', '台', 1, 0, 7, 234, 45, 1, 1, NOW(), NOW()),

-- 家居用品
(5, '九阳豆浆机', '厨房用品', 21, 299.00, 399.00, 'JY-DJ-001', '1234567890127',
 '九阳免滤豆浆机，一键制作，营养豆浆',
 '{"capacity": "1.3L", "power": "1100W", "material": "不锈钢"}',
 '["https://example.com/images/joyoung_soymilk_1.jpg"]',
 '九阳', 'DJ13B-D08D', 3.2, '200x200x350mm', '台', 1, 0, 6, 678, 234, 1, 1, NOW(), NOW()),

(6, '宜家沙发', '家具装饰', 23, 1999.00, 2499.00, 'IKEA-SOFA-001', '1234567890128',
 '宜家三人座沙发，舒适布艺，简约设计',
 '{"material": "布艺", "seats": "3人座", "color": "灰色"}',
 '["https://example.com/images/ikea_sofa_1.jpg"]',
 'IKEA', 'KIVIK', 45.0, '2280x950x830mm', '张', 1, 1, 5, 345, 67, 1, 1, NOW(), NOW()),

-- 服装鞋帽
(7, 'Uniqlo优衣库 男士T恤', '男装', 31, 99.00, 149.00, 'UNI-TEE-M-001', '1234567890129',
 '优衣库男士纯棉T恤，舒适透气，多色可选',
 '{"material": "100%棉", "fit": "常规版型", "care": "机洗"}',
 '["https://example.com/images/uniqlo_tee_1.jpg"]',
 'Uniqlo', 'UT', 0.2, '胸围108cm', '件', 1, 0, 4, 1567, 456, 1, 1, NOW(), NOW()),

(8, 'Nike Air Max 270', '鞋靴', 33, 899.00, 1199.00, 'NIKE-AM270-001', '1234567890130',
 'Nike Air Max 270运动鞋，最大气垫，舒适缓震',
 '{"type": "运动鞋", "technology": "Air Max", "upper": "网布+人造革"}',
 '["https://example.com/images/nike_airmax270_1.jpg"]',
 'Nike', 'Air Max 270', 0.8, '尺码42', '双', 1, 1, 3, 987, 189, 1, 1, NOW(), NOW()),

-- 图书文具
(9, '《深入理解Java虚拟机》', '图书文具', 4, 89.00, 109.00, 'BOOK-JVM-001', '9787111421900',
 'Java程序员必读经典，深入解析JVM原理',
 '{"author": "周志明", "publisher": "机械工业出版社", "pages": "689页"}',
 '["https://example.com/images/jvm_book_1.jpg"]',
 '机械工业出版社', '第三版', 0.8, '260x185x35mm', '本', 1, 1, 2, 456, 123, 1, 1, NOW(), NOW()),

(10, '得力文具套装', '图书文具', 4, 45.00, 65.00, 'DELI-SET-001', '1234567890131',
 '得力学生文具套装，包含笔、尺子、橡皮等',
 '{"contents": "圆珠笔x5、铅笔x10、橡皮x3、尺子x2", "suitable": "学生使用"}',
 '["https://example.com/images/deli_set_1.jpg"]',
 '得力', 'DL-SET-01', 0.3, '200x150x50mm', '套', 1, 0, 1, 234, 78, 1, 1, NOW(), NOW());

-- ================================================
-- 商品标签关联数据
-- ================================================
INSERT INTO product_tag_relations (product_id, tag_id, created_at) VALUES
-- iPhone 15 Pro: 热销、新品、推荐
(1, 1, NOW()), (1, 2, NOW()), (1, 4, NOW()),
-- MacBook Pro: 新品、推荐、包邮
(2, 2, NOW()), (2, 4, NOW()), (2, 6, NOW()),
-- 小米13 Ultra: 热销、特价
(3, 1, NOW()), (3, 3, NOW()),
-- 戴尔XPS 13: 包邮
(4, 6, NOW()),
-- 九阳豆浆机: 热销、特价、包邮
(5, 1, NOW()), (5, 3, NOW()), (5, 6, NOW()),
-- 宜家沙发: 推荐、包邮
(6, 4, NOW()), (6, 6, NOW()),
-- 优衣库T恤: 热销
(7, 1, NOW()),
-- Nike鞋: 新品、推荐
(8, 2, NOW()), (8, 4, NOW()),
-- Java书: 推荐
(9, 4, NOW()),
-- 得力文具: 特价、包邮
(10, 3, NOW()), (10, 6, NOW());

-- ================================================
-- 商品属性值数据
-- ================================================
INSERT INTO product_attribute_values (product_id, attribute_id, value, created_at) VALUES
-- iPhone 15 Pro 属性
(1, 1, '深空黑色', NOW()),  -- 颜色
(1, 3, '128GB', NOW()),    -- 容量
(1, 5, '中国', NOW()),     -- 产地
(1, 6, '1年', NOW()),      -- 保修期
(1, 7, 'Apple', NOW()),    -- 品牌

-- MacBook Pro 属性
(2, 1, '深空灰色', NOW()),  -- 颜色
(2, 3, '512GB', NOW()),    -- 容量
(2, 5, '中国', NOW()),     -- 产地
(2, 6, '1年', NOW()),      -- 保修期
(2, 7, 'Apple', NOW()),    -- 品牌

-- 小米13 Ultra 属性
(3, 1, '黑色', NOW()),      -- 颜色
(3, 3, '256GB', NOW()),    -- 容量
(3, 5, '中国', NOW()),     -- 产地
(3, 6, '2年', NOW()),      -- 保修期
(3, 7, '小米', NOW()),     -- 品牌

-- 优衣库T恤属性
(7, 1, '白色', NOW()),      -- 颜色
(7, 2, 'L', NOW()),        -- 尺寸
(7, 4, '100%棉', NOW()),   -- 材质
(7, 5, '中国', NOW()),     -- 产地

-- Nike鞋属性
(8, 1, '黑白配色', NOW()),  -- 颜色
(8, 2, '42', NOW()),       -- 尺寸
(8, 4, '网布+人造革', NOW()), -- 材质
(8, 5, '越南', NOW()),     -- 产地
(8, 6, '1年', NOW());      -- 保修期

-- ================================================
-- 库存数据
-- ================================================
INSERT INTO inventories (product_id, current_stock, min_stock, max_stock, created_at, updated_at) VALUES
(1, 50, 10, 200, NOW(), NOW()),   -- iPhone 15 Pro
(2, 15, 5, 50, NOW(), NOW()),     -- MacBook Pro 14英寸
(3, 80, 20, 300, NOW(), NOW()),   -- 小米13 Ultra
(4, 25, 5, 100, NOW(), NOW()),    -- 戴尔XPS 13
(5, 120, 30, 500, NOW(), NOW()),  -- 九阳豆浆机
(6, 8, 2, 30, NOW(), NOW()),      -- 宜家沙发
(7, 200, 50, 1000, NOW(), NOW()), -- 优衣库T恤
(8, 60, 15, 200, NOW(), NOW()),   -- Nike Air Max 270
(9, 150, 20, 500, NOW(), NOW()),  -- Java书籍
(10, 300, 50, 1000, NOW(), NOW()); -- 得力文具套装

-- ================================================
-- 库存操作记录数据（示例）
-- ================================================
INSERT INTO inventory_records (
    product_id, operation_type, quantity, before_stock, after_stock, 
    reason, operator_id, operated_at
) VALUES
-- 初始入库记录
(1, 'IN', 100, 0, 100, '初始库存入库', 1, DATE_SUB(NOW(), INTERVAL 30 DAY)),
(1, 'OUT', 50, 100, 50, '销售出库', 1, DATE_SUB(NOW(), INTERVAL 15 DAY)),

(2, 'IN', 30, 0, 30, '初始库存入库', 1, DATE_SUB(NOW(), INTERVAL 25 DAY)),
(2, 'OUT', 15, 30, 15, '销售出库', 1, DATE_SUB(NOW(), INTERVAL 10 DAY)),

(3, 'IN', 150, 0, 150, '初始库存入库', 1, DATE_SUB(NOW(), INTERVAL 20 DAY)),
(3, 'OUT', 70, 150, 80, '促销活动出库', 1, DATE_SUB(NOW(), INTERVAL 5 DAY)),

(5, 'IN', 200, 0, 200, '初始库存入库', 1, DATE_SUB(NOW(), INTERVAL 40 DAY)),
(5, 'OUT', 80, 200, 120, '销售出库', 2, DATE_SUB(NOW(), INTERVAL 20 DAY)),

(7, 'IN', 500, 0, 500, '初始库存入库', 1, DATE_SUB(NOW(), INTERVAL 35 DAY)),
(7, 'OUT', 300, 500, 200, '大批量销售', 2, DATE_SUB(NOW(), INTERVAL 12 DAY)),

(9, 'IN', 200, 0, 200, '初始库存入库', 1, DATE_SUB(NOW(), INTERVAL 28 DAY)),
(9, 'OUT', 50, 200, 150, '图书促销', 3, DATE_SUB(NOW(), INTERVAL 8 DAY));

-- ================================================
-- 重置自增ID
-- ================================================
ALTER TABLE users AUTO_INCREMENT = 4;
ALTER TABLE product_categories AUTO_INCREMENT = 35;
ALTER TABLE product_tags AUTO_INCREMENT = 9;
ALTER TABLE product_attributes AUTO_INCREMENT = 9;
ALTER TABLE products AUTO_INCREMENT = 11;
ALTER TABLE inventories AUTO_INCREMENT = 11;
ALTER TABLE inventory_records AUTO_INCREMENT = 13;
ALTER TABLE product_tag_relations AUTO_INCREMENT = 16;
ALTER TABLE product_attribute_values AUTO_INCREMENT = 16;

-- ================================================
-- 数据统计查询（验证数据）
-- ================================================
-- SELECT 'Products Count' as Type, COUNT(*) as Count FROM products
-- UNION ALL
-- SELECT 'Categories Count', COUNT(*) FROM product_categories
-- UNION ALL  
-- SELECT 'Tags Count', COUNT(*) FROM product_tags
-- UNION ALL
-- SELECT 'Inventories Count', COUNT(*) FROM inventories
-- UNION ALL
-- SELECT 'Inventory Records Count', COUNT(*) FROM inventory_records;