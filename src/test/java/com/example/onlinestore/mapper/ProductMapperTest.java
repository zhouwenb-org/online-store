package com.example.onlinestore.mapper;

import com.example.onlinestore.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ProductMapperTest {

    @Autowired
    private ProductMapper productMapper;

    @Test
    @Sql(scripts = "/sql/clean-products.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void testInsertProduct() {
        Product product = new Product();
        product.setName("Test Product");
        product.setCategory("Electronics");
        product.setPrice(new BigDecimal("99.99"));
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        productMapper.insertProduct(product);

        assertNotNull(product.getId());
        assertTrue(product.getId() > 0);
    }

    @Test
    @Sql(scripts = {"/sql/clean-products.sql", "/sql/insert-test-products.sql"}, 
         executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void testFindAll() {
        List<Product> products = productMapper.findAll();

        assertNotNull(products);
        assertTrue(products.size() > 0);
    }

    @Test
    @Sql(scripts = {"/sql/clean-products.sql", "/sql/insert-test-products.sql"}, 
         executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void testFindWithPagination() {
        List<Product> products = productMapper.findWithPagination(null, 0, 2);

        assertNotNull(products);
        assertTrue(products.size() <= 2);
    }

    @Test
    @Sql(scripts = {"/sql/clean-products.sql", "/sql/insert-test-products.sql"}, 
         executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void testFindWithPaginationAndName() {
        List<Product> products = productMapper.findWithPagination("Test Product 1", 0, 10);

        assertNotNull(products);
        for (Product product : products) {
            assertTrue(product.getName().contains("Test Product 1"));
        }
    }

    @Test
    @Sql(scripts = {"/sql/clean-products.sql", "/sql/insert-test-products.sql"}, 
         executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void testCountTotal() {
        long count = productMapper.countTotal(null);

        assertTrue(count > 0);
    }

    @Test
    @Sql(scripts = {"/sql/clean-products.sql", "/sql/insert-test-products.sql"}, 
         executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void testCountTotalWithName() {
        long count = productMapper.countTotal("Test Product 1");

        assertTrue(count >= 0);
    }
}
