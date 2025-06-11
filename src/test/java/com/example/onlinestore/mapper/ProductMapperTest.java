package com.example.onlinestore.mapper;

import com.example.onlinestore.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "/test-data.sql")
@DisplayName("商品映射器测试")
public class ProductMapperTest {

    @Autowired
    private ProductMapper productMapper;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setName("新测试商品");
        testProduct.setCategory("测试分类");
        testProduct.setPrice(new BigDecimal("299.99"));
        testProduct.setCreatedAt(LocalDateTime.now());
        testProduct.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("插入商品测试")
    void whenInsertProduct_thenProductSaved() {
        // 执行插入
        productMapper.insertProduct(testProduct);
        
        // 验证ID已生成
        assertNotNull(testProduct.getId());
        assertTrue(testProduct.getId() > 0);
    }

    @Test
    @DisplayName("查询所有商品")
    void whenFindAll_thenReturnAllProducts() {
        // 执行查询
        List<Product> products = productMapper.findAll();
        
        // 验证结果
        assertNotNull(products);
        assertEquals(3, products.size()); // 测试数据中有3个商品
        assertEquals("测试商品1", products.get(0).getName());
    }

    @Test
    @DisplayName("分页查询商品")
    void whenFindWithPagination_thenReturnPagedResults() {
        // 执行分页查询
        List<Product> products = productMapper.findWithPagination(null, 0, 2);
        
        // 验证结果
        assertNotNull(products);
        assertEquals(2, products.size());
        assertEquals("测试商品1", products.get(0).getName());
        assertEquals("测试商品2", products.get(1).getName());
    }

    @Test
    @DisplayName("根据名称分页查询商品")
    void whenFindWithPaginationByName_thenReturnFilteredResults() {
        // 执行按名称查询
        List<Product> products = productMapper.findWithPagination("测试商品1", 0, 10);
        
        // 验证结果
        assertNotNull(products);
        assertEquals(1, products.size());
        assertEquals("测试商品1", products.get(0).getName());
    }

    @Test
    @DisplayName("统计商品总数")
    void whenCountTotal_thenReturnTotalCount() {
        // 执行统计
        Long total = productMapper.countTotal(null);
        
        // 验证结果
        assertNotNull(total);
        assertEquals(3L, total);
    }

    @Test
    @DisplayName("根据名称统计商品数量")
    void whenCountTotalByName_thenReturnFilteredCount() {
        // 执行按名称统计
        Long total = productMapper.countTotal("测试商品1");
        
        // 验证结果
        assertNotNull(total);
        assertEquals(1L, total);
    }
}