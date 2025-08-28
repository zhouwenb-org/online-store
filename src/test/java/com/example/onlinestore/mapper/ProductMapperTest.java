package com.example.onlinestore.mapper;

import com.example.onlinestore.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringJUnitConfig
public class ProductMapperTest {

    @Autowired
    private ProductMapper productMapper;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setName("测试商品");
        testProduct.setCategory("电子产品");
        testProduct.setPrice(new BigDecimal("299.99"));
        testProduct.setCreatedAt(LocalDateTime.now());
        testProduct.setUpdatedAt(LocalDateTime.now());
    }

    @Nested
    class BasicCrudTests {

        @Test
        void insertProduct_ShouldWorkCorrectly() {
            // When
            productMapper.insertProduct(testProduct);

            // Then
            assertNotNull(testProduct.getId()); // Assuming auto-generated ID
        }

        @Test
        void findById_ExistingProduct_ShouldReturnProduct() {
            // Given
            productMapper.insertProduct(testProduct);
            Long productId = testProduct.getId();

            // When
            Product foundProduct = productMapper.findById(productId);

            // Then
            assertNotNull(foundProduct);
            assertEquals(testProduct.getName(), foundProduct.getName());
            assertEquals(testProduct.getCategory(), foundProduct.getCategory());
            assertEquals(testProduct.getPrice(), foundProduct.getPrice());
        }

        @Test
        void findById_NonExistingProduct_ShouldReturnNull() {
            // When
            Product foundProduct = productMapper.findById(999L);

            // Then
            assertNull(foundProduct);
        }

        @Test
        void updateById_ShouldModifyProduct() {
            // Given
            productMapper.insertProduct(testProduct);
            Long productId = testProduct.getId();
            
            String newName = "更新后的商品";
            String newCategory = "家居用品";
            BigDecimal newPrice = new BigDecimal("399.99");
            LocalDateTime updateTime = LocalDateTime.now();

            // When
            int affectedRows = productMapper.updateById(
                productId, newName, newCategory, newPrice, updateTime);

            // Then
            assertEquals(1, affectedRows);
            
            Product updatedProduct = productMapper.findById(productId);
            assertEquals(newName, updatedProduct.getName());
            assertEquals(newCategory, updatedProduct.getCategory());
            assertEquals(newPrice, updatedProduct.getPrice());
        }

        @Test
        void deleteById_ShouldRemoveProduct() {
            // Given
            productMapper.insertProduct(testProduct);
            Long productId = testProduct.getId();

            // When
            int affectedRows = productMapper.deleteById(productId);

            // Then
            assertEquals(1, affectedRows);
            
            Product deletedProduct = productMapper.findById(productId);
            assertNull(deletedProduct);
        }

        @Test
        void deleteById_NonExistingProduct_ShouldReturnZero() {
            // When
            int affectedRows = productMapper.deleteById(999L);

            // Then
            assertEquals(0, affectedRows);
        }
    }

    @Nested
    class QueryTests {

        @Test
        void findAll_ShouldReturnAllProducts() {
            // Given
            productMapper.insertProduct(testProduct);
            
            Product anotherProduct = new Product();
            anotherProduct.setName("另一个商品");
            anotherProduct.setCategory("服装");
            anotherProduct.setPrice(new BigDecimal("99.99"));
            anotherProduct.setCreatedAt(LocalDateTime.now());
            anotherProduct.setUpdatedAt(LocalDateTime.now());
            productMapper.insertProduct(anotherProduct);

            // When
            List<Product> allProducts = productMapper.findAll();

            // Then
            assertNotNull(allProducts);
            assertTrue(allProducts.size() >= 2);
        }

        @Test
        void findAllCategories_ShouldReturnDistinctCategories() {
            // Given
            productMapper.insertProduct(testProduct);
            
            Product anotherProduct = new Product();
            anotherProduct.setName("另一个商品");
            anotherProduct.setCategory("服装");
            anotherProduct.setPrice(new BigDecimal("99.99"));
            anotherProduct.setCreatedAt(LocalDateTime.now());
            anotherProduct.setUpdatedAt(LocalDateTime.now());
            productMapper.insertProduct(anotherProduct);

            // When
            List<String> categories = productMapper.findAllCategories();

            // Then
            assertNotNull(categories);
            assertTrue(categories.contains("电子产品"));
            assertTrue(categories.contains("服装"));
        }

        @Test
        void findWithPagination_ShouldReturnPagedResults() {
            // Given
            insertMultipleTestProducts();

            // When
            List<Product> pagedProducts = productMapper.findWithPagination(
                null, null, null, null, 0, 2);

            // Then
            assertNotNull(pagedProducts);
            assertTrue(pagedProducts.size() <= 2);
        }

        @Test
        void findWithPagination_WithNameFilter_ShouldReturnFilteredResults() {
            // Given
            insertMultipleTestProducts();

            // When
            List<Product> filteredProducts = productMapper.findWithPagination(
                "测试", null, null, null, 0, 10);

            // Then
            assertNotNull(filteredProducts);
            filteredProducts.forEach(product -> 
                assertTrue(product.getName().contains("测试")));
        }

        @Test
        void findWithPagination_WithCategoryFilter_ShouldReturnFilteredResults() {
            // Given
            insertMultipleTestProducts();

            // When
            List<Product> filteredProducts = productMapper.findWithPagination(
                null, "电子产品", null, null, 0, 10);

            // Then
            assertNotNull(filteredProducts);
            filteredProducts.forEach(product -> 
                assertEquals("电子产品", product.getCategory()));
        }

        @Test
        void findWithPagination_WithPriceRange_ShouldReturnFilteredResults() {
            // Given
            insertMultipleTestProducts();

            // When
            List<Product> filteredProducts = productMapper.findWithPagination(
                null, null, new BigDecimal("100"), new BigDecimal("500"), 0, 10);

            // Then
            assertNotNull(filteredProducts);
            filteredProducts.forEach(product -> {
                assertTrue(product.getPrice().compareTo(new BigDecimal("100")) >= 0);
                assertTrue(product.getPrice().compareTo(new BigDecimal("500")) <= 0);
            });
        }

        @Test
        void countTotal_ShouldReturnCorrectCount() {
            // Given
            insertMultipleTestProducts();

            // When
            long totalCount = productMapper.countTotal(null, null, null, null);

            // Then
            assertTrue(totalCount >= 3); // At least the 3 test products
        }

        @Test
        void countTotal_WithFilters_ShouldReturnFilteredCount() {
            // Given
            insertMultipleTestProducts();

            // When
            long filteredCount = productMapper.countTotal("测试", null, null, null);

            // Then
            assertTrue(filteredCount >= 1);
        }
    }

    @Nested
    class EdgeCaseTests {

        @Test
        void updateById_PartialUpdate_ShouldWorkCorrectly() {
            // Given
            productMapper.insertProduct(testProduct);
            Long productId = testProduct.getId();

            // When - Update only name
            int affectedRows = productMapper.updateById(
                productId, "新名称", null, null, LocalDateTime.now());

            // Then
            assertEquals(1, affectedRows);
            
            Product updatedProduct = productMapper.findById(productId);
            assertEquals("新名称", updatedProduct.getName());
            assertEquals(testProduct.getCategory(), updatedProduct.getCategory());
            assertEquals(testProduct.getPrice(), updatedProduct.getPrice());
        }

        @Test
        void findWithPagination_EmptyResult_ShouldReturnEmptyList() {
            // When
            List<Product> products = productMapper.findWithPagination(
                "不存在的商品", null, null, null, 0, 10);

            // Then
            assertNotNull(products);
            assertTrue(products.isEmpty());
        }

        @Test
        void findWithPagination_InvalidPriceRange_ShouldReturnEmptyList() {
            // Given
            insertMultipleTestProducts();

            // When - Min price greater than max price
            List<Product> products = productMapper.findWithPagination(
                null, null, new BigDecimal("1000"), new BigDecimal("500"), 0, 10);

            // Then
            assertNotNull(products);
            assertTrue(products.isEmpty());
        }
    }

    private void insertMultipleTestProducts() {
        // Insert multiple test products for pagination and filtering tests
        Product product1 = new Product();
        product1.setName("测试商品1");
        product1.setCategory("电子产品");
        product1.setPrice(new BigDecimal("199.99"));
        product1.setCreatedAt(LocalDateTime.now());
        product1.setUpdatedAt(LocalDateTime.now());
        productMapper.insertProduct(product1);

        Product product2 = new Product();
        product2.setName("测试商品2");
        product2.setCategory("家居用品");
        product2.setPrice(new BigDecimal("299.99"));
        product2.setCreatedAt(LocalDateTime.now());
        product2.setUpdatedAt(LocalDateTime.now());
        productMapper.insertProduct(product2);

        Product product3 = new Product();
        product3.setName("普通商品");
        product3.setCategory("服装");
        product3.setPrice(new BigDecimal("89.99"));
        product3.setCreatedAt(LocalDateTime.now());
        product3.setUpdatedAt(LocalDateTime.now());
        productMapper.insertProduct(product3);
    }
}