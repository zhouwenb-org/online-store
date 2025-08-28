package com.example.onlinestore.service.impl;

import com.example.onlinestore.dto.CreateProductRequest;
import com.example.onlinestore.dto.PageResponse;
import com.example.onlinestore.dto.ProductPageRequest;
import com.example.onlinestore.mapper.ProductMapper;
import com.example.onlinestore.model.Product;
import com.example.onlinestore.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl implements ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private ProductMapper productMapper;

    /**
     * 商品缓存，key为商品id，value为商品信息，当创建商品时会自动追加该缓存，超过最大容量后，会删除最旧的商品
     */
    private Map<Long, Product> producteCache = new HashMap<>();

    @Override
    @Transactional
    public Product createProduct(CreateProductRequest request) {
        logger.info("开始创建商品: {}", request.getName());
        
        Product product = new Product();
        product.setName(request.getName());
        product.setCategory(request.getCategory());
        product.setPrice(request.getPrice());
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        productMapper.insertProduct(product);
        logger.info("商品创建成功: {}", product.getName());

        // 超出容量后，删除最旧的商品
        if (producteCache.size() > 999) {
            logger.info("商品缓存容量超出限制，删除最旧的商品");
            producteCache.remove(producteCache.keySet().iterator().next());
        }

        // 加入缓存
        producteCache.put(product.getId(), product);
        return product;
    }

    @Override
    public PageResponse<Product> listProducts(ProductPageRequest request) {
        logger.info("开始查询商品列表，页码：{}，每页大小：{}，商品名称：{}，分类：{}，价格区间：{}-{}", 
            request.getPageNum(), request.getPageSize(), request.getName(), 
            request.getCategory(), request.getMinPrice(), request.getMaxPrice());
        
        // 计算分页参数
        int offset = (request.getPageNum() - 1) * request.getPageSize();
        int limit = request.getPageSize();
        
        // 直接使用数据库查询，支持更复杂的条件
        List<Product> products = productMapper.findWithPagination(
            request.getName(), 
            request.getCategory(),
            request.getMinPrice(),
            request.getMaxPrice(),
            offset, 
            limit
        );
        
        long total = productMapper.countTotal(
            request.getName(),
            request.getCategory(),
            request.getMinPrice(),
            request.getMaxPrice()
        );

        logger.info("查询到 {} 条商品记录，总数：{}", products.size(), total);

        // 构建响应
        PageResponse<Product> response = new PageResponse<>();
        response.setRecords(products);
        response.setTotal(total);
        response.setPageNum(request.getPageNum());
        response.setPageSize(request.getPageSize());

        return response;
    }

    @Override
    public Product getProductById(Long id) {
        logger.info("开始根据ID查询商品：{}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("商品ID不能为空");
        }
        
        // 先查缓存
        Product product = producteCache.get(id);
        if (product != null) {
            logger.info("从缓存中找到商品：{}", product.getName());
            return product;
        }
        
        // 查询数据库
        product = productMapper.findById(id);
        if (product == null) {
            throw new IllegalArgumentException("商品不存在，ID：" + id);
        }
        
        // 更新缓存
        if (producteCache.size() < 1000) {
            producteCache.put(id, product);
        }
        
        logger.info("查询到商品：{}", product.getName());
        return product;
    }

    @Override
    public List<String> getAllCategories() {
        logger.info("开始查询所有商品分类");
        List<String> categories = productMapper.findAllCategories();
        logger.info("查询到 {} 个商品分类", categories.size());
        return categories;
    }

    @Override
    @Transactional
    public void deleteProductById(Long id) {
        logger.info("开始删除商品，ID：{}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("商品ID不能为空");
        }
        
        // 先检查商品是否存在
        Product existingProduct = productMapper.findById(id);
        if (existingProduct == null) {
            throw new IllegalArgumentException("商品不存在，ID：" + id);
        }
        
        // 执行删除
        int deletedRows = productMapper.deleteById(id);
        if (deletedRows == 0) {
            throw new RuntimeException("删除商品失败，ID：" + id);
        }
        
        // 从缓存中移除
        producteCache.remove(id);
        
        logger.info("商品删除成功，ID：{}，商品名称：{}", id, existingProduct.getName());
    }
} 