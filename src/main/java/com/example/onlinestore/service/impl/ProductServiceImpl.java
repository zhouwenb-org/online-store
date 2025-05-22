package com.example.onlinestore.service.impl;

import com.example.onlinestore.dto.CreateProductRequest;
import com.example.onlinestore.dto.PageResponse;
import com.example.onlinestore.dto.ProductPageRequest;
import com.example.onlinestore.mapper.ProductMapper;
import com.example.onlinestore.model.Product;
import com.example.onlinestore.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
    private static final String PRODUCT_CACHE_PREFIX = "product:";
    private static final Duration PRODUCT_CACHE_TTL = Duration.ofHours(1);

    private final ProductMapper productMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    public ProductServiceImpl(ProductMapper productMapper,
                              StringRedisTemplate stringRedisTemplate,
                              ObjectMapper objectMapper) {
        this.productMapper = productMapper;
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
    }

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

        try {
            String productJson = objectMapper.writeValueAsString(product);
            String cacheKey = PRODUCT_CACHE_PREFIX + product.getId();
            stringRedisTemplate.opsForValue().set(cacheKey, productJson, PRODUCT_CACHE_TTL);
            logger.info("Product {} cached in Redis with TTL {} seconds", product.getId(), PRODUCT_CACHE_TTL.getSeconds());
        } catch (Exception e) {
            logger.error("Error caching product {} in Redis: {}", product.getId(), e.getMessage(), e);
            // Non-critical error, so don't re-throw, but log it.
        }
        return product;
    }

    @Override
    public PageResponse<Product> listProducts(ProductPageRequest request) {
        logger.info("Fetching product list from database. PageNum: {}, PageSize: {}, Name: {}",
            request.getPageNum(), request.getPageSize(), request.getName());

        // 计算分页参数
        int offset = (request.getPageNum() - 1) * request.getPageSize();
        int limit = request.getPageSize();
        
        // 查询数据
        List<Product> products = productMapper.findWithPagination(request.getName(), offset, limit);
        long total = productMapper.countTotal(request.getName());

        logger.info("Fetched {} product records from database", products.size());

        // Cache Warming
        if (products != null && !products.isEmpty()) {
            logger.info("Warming Redis cache with {} fetched products.", products.size());
            for (Product product : products) {
                try {
                    String productJson = objectMapper.writeValueAsString(product);
                    String cacheKey = PRODUCT_CACHE_PREFIX + product.getId();
                    stringRedisTemplate.opsForValue().set(cacheKey, productJson, PRODUCT_CACHE_TTL);
                } catch (Exception e) {
                    logger.error("Error caching product {} during listProducts warm-up: {}", product.getId(), e.getMessage(), e);
                }
            }
        }

        // 构建响应
        PageResponse<Product> response = new PageResponse<>();
        response.setRecords(products);
        response.setTotal(total);
        response.setPageNum(request.getPageNum());
        response.setPageSize(request.getPageSize());

        return response;
    }
} 