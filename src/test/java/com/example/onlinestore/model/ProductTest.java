package com.example.onlinestore.model;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Nested
    class GetterSetterTests {

        @Test
        void testIdGetterSetter() {
            Product product = new Product();
            Long id = 1L;

            product.setId(id);

            assertEquals(id, product.getId());
        }

        @Test
        void testNameGetterSetter() {
            Product product = new Product();
            String name = "Test Product";

            product.setName(name);

            assertEquals(name, product.getName());
        }

        @Test
        void testCategoryGetterSetter() {
            Product product = new Product();
            String category = "Electronics";

            product.setCategory(category);

            assertEquals(category, product.getCategory());
        }

        @Test
        void testPriceGetterSetter() {
            Product product = new Product();
            BigDecimal price = new BigDecimal("99.99");

            product.setPrice(price);

            assertEquals(price, product.getPrice());
        }

        @Test
        void testCreatedAtGetterSetter() {
            Product product = new Product();
            LocalDateTime createdAt = LocalDateTime.now();

            product.setCreatedAt(createdAt);

            assertEquals(createdAt, product.getCreatedAt());
        }

        @Test
        void testUpdatedAtGetterSetter() {
            Product product = new Product();
            LocalDateTime updatedAt = LocalDateTime.now();

            product.setUpdatedAt(updatedAt);

            assertEquals(updatedAt, product.getUpdatedAt());
        }
    }

    @Nested
    class ObjectTests {

        @Test
        void testProductCreation() {
            Product product = new Product();

            assertNotNull(product);
            assertNull(product.getId());
            assertNull(product.getName());
            assertNull(product.getCategory());
            assertNull(product.getPrice());
            assertNull(product.getCreatedAt());
            assertNull(product.getUpdatedAt());
        }

        @Test
        void testProductWithAllFields() {
            Product product = new Product();
            Long id = 1L;
            String name = "Test Product";
            String category = "Electronics";
            BigDecimal price = new BigDecimal("99.99");
            LocalDateTime now = LocalDateTime.now();

            product.setId(id);
            product.setName(name);
            product.setCategory(category);
            product.setPrice(price);
            product.setCreatedAt(now);
            product.setUpdatedAt(now);

            assertEquals(id, product.getId());
            assertEquals(name, product.getName());
            assertEquals(category, product.getCategory());
            assertEquals(price, product.getPrice());
            assertEquals(now, product.getCreatedAt());
            assertEquals(now, product.getUpdatedAt());
        }
    }
}
