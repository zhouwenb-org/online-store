CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    token VARCHAR(100),
    token_expire_time DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 商品分类表
CREATE TABLE IF NOT EXISTS product_categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE COMMENT '分类名称',
    description VARCHAR(500) COMMENT '分类描述',
    parent_id BIGINT COMMENT '父分类ID，支持分类层级',
    sort_order INT DEFAULT 0 COMMENT '排序权重',
    is_active TINYINT(1) DEFAULT 1 COMMENT '是否启用：1-启用，0-禁用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_parent_id (parent_id),
    INDEX idx_name (name),
    INDEX idx_sort_order (sort_order),
    FOREIGN KEY (parent_id) REFERENCES product_categories(id) ON DELETE SET NULL
) COMMENT='商品分类表';

-- 商品表（优化版）
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL COMMENT '商品名称',
    category VARCHAR(100) COMMENT '商品分类（冗余字段，便于查询）',
    category_id BIGINT COMMENT '分类ID',
    price DECIMAL(10,2) NOT NULL COMMENT '商品价格',
    original_price DECIMAL(10,2) COMMENT '原价（用于促销活动）',
    sku VARCHAR(100) UNIQUE COMMENT '商品SKU编码',
    barcode VARCHAR(100) COMMENT '商品条形码',
    description TEXT COMMENT '商品描述',
    specifications JSON COMMENT '商品规格参数（JSON格式）',
    images JSON COMMENT '商品图片URLs（JSON数组）',
    brand VARCHAR(100) COMMENT '品牌',
    model VARCHAR(100) COMMENT '型号',
    weight DECIMAL(8,3) COMMENT '重量（kg）',
    dimensions VARCHAR(100) COMMENT '尺寸（长x宽x高）',
    unit VARCHAR(20) DEFAULT '件' COMMENT '计量单位',
    is_active TINYINT(1) DEFAULT 1 COMMENT '是否上架：1-上架，0-下架',
    is_featured TINYINT(1) DEFAULT 0 COMMENT '是否推荐：1-推荐，0-普通',
    sort_order INT DEFAULT 0 COMMENT '排序权重',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    sale_count INT DEFAULT 0 COMMENT '销售数量',
    created_by BIGINT COMMENT '创建人ID',
    updated_by BIGINT COMMENT '更新人ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 索引
    INDEX idx_name (name),
    INDEX idx_category (category),
    INDEX idx_category_id (category_id),
    INDEX idx_price (price),
    INDEX idx_sku (sku),
    INDEX idx_barcode (barcode),
    INDEX idx_brand (brand),
    INDEX idx_is_active (is_active),
    INDEX idx_is_featured (is_featured),
    INDEX idx_sort_order (sort_order),
    INDEX idx_created_at (created_at),
    INDEX idx_sale_count (sale_count),
    
    -- 复合索引（优化常用查询）
    INDEX idx_category_price (category, price),
    INDEX idx_active_featured (is_active, is_featured),
    INDEX idx_category_active (category_id, is_active),
    
    -- 外键约束
    FOREIGN KEY (category_id) REFERENCES product_categories(id) ON DELETE SET NULL,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL
) COMMENT='商品表';

-- 商品标签表
CREATE TABLE IF NOT EXISTS product_tags (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE COMMENT '标签名称',
    color VARCHAR(7) DEFAULT '#007bff' COMMENT '标签颜色（十六进制）',
    description VARCHAR(200) COMMENT '标签描述',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_name (name)
) COMMENT='商品标签表';

-- 商品标签关联表
CREATE TABLE IF NOT EXISTS product_tag_relations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL COMMENT '商品ID',
    tag_id BIGINT NOT NULL COMMENT '标签ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_product_tag (product_id, tag_id),
    INDEX idx_product_id (product_id),
    INDEX idx_tag_id (tag_id),
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES product_tags(id) ON DELETE CASCADE
) COMMENT='商品标签关联表';

-- 商品属性表（用于动态属性，如颜色、尺寸等）
CREATE TABLE IF NOT EXISTS product_attributes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '属性名称',
    type ENUM('TEXT', 'NUMBER', 'SELECT', 'MULTI_SELECT', 'BOOLEAN') DEFAULT 'TEXT' COMMENT '属性类型',
    options JSON COMMENT '选项值（对于SELECT类型）',
    is_required TINYINT(1) DEFAULT 0 COMMENT '是否必填',
    sort_order INT DEFAULT 0 COMMENT '排序权重',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_name (name),
    INDEX idx_sort_order (sort_order)
) COMMENT='商品属性定义表';

-- 商品属性值表
CREATE TABLE IF NOT EXISTS product_attribute_values (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL COMMENT '商品ID',
    attribute_id BIGINT NOT NULL COMMENT '属性ID',
    value TEXT COMMENT '属性值',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_product_attribute (product_id, attribute_id),
    INDEX idx_product_id (product_id),
    INDEX idx_attribute_id (attribute_id),
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    FOREIGN KEY (attribute_id) REFERENCES product_attributes(id) ON DELETE CASCADE
) COMMENT='商品属性值表';

CREATE TABLE IF NOT EXISTS inventories (
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

CREATE TABLE IF NOT EXISTS inventory_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    operation_type ENUM('IN', 'OUT', 'ADJUST') NOT NULL,
    quantity INT NOT NULL,
    before_stock INT NOT NULL,
    after_stock INT NOT NULL,
    reason VARCHAR(500),
    operator_id BIGINT,
    operated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_product_id (product_id),
    INDEX idx_operated_at (operated_at),
    INDEX idx_operator_id (operator_id),
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    FOREIGN KEY (operator_id) REFERENCES users(id) ON DELETE SET NULL
); 