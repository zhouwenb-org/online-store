package com.example.onlinestore.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ProductPageRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    class ValidationTests {

        @Test
        void testValidRequest() {
            ProductPageRequest request = new ProductPageRequest();
            request.setPageNum(1);
            request.setPageSize(10);
            request.setName("Test Product");

            Set<ConstraintViolation<ProductPageRequest>> violations = validator.validate(request);

            assertTrue(violations.isEmpty());
        }

        @Test
        void testDefaultValues() {
            ProductPageRequest request = new ProductPageRequest();

            Set<ConstraintViolation<ProductPageRequest>> violations = validator.validate(request);

            assertTrue(violations.isEmpty());
            assertEquals(1, request.getPageNum());
            assertEquals(10, request.getPageSize());
        }

        @Test
        void testInvalidPageNum_Zero() {
            ProductPageRequest request = new ProductPageRequest();
            request.setPageNum(0);

            Set<ConstraintViolation<ProductPageRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("error.page.number.min")));
        }

        @Test
        void testInvalidPageNum_Negative() {
            ProductPageRequest request = new ProductPageRequest();
            request.setPageNum(-1);

            Set<ConstraintViolation<ProductPageRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("error.page.number.min")));
        }

        @Test
        void testInvalidPageSize_Zero() {
            ProductPageRequest request = new ProductPageRequest();
            request.setPageSize(0);

            Set<ConstraintViolation<ProductPageRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("error.page.size.min")));
        }

        @Test
        void testInvalidPageSize_Negative() {
            ProductPageRequest request = new ProductPageRequest();
            request.setPageSize(-1);

            Set<ConstraintViolation<ProductPageRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("error.page.size.min")));
        }

        @Test
        void testInvalidPageSize_TooLarge() {
            ProductPageRequest request = new ProductPageRequest();
            request.setPageSize(101);

            Set<ConstraintViolation<ProductPageRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("error.page.size.max")));
        }

        @Test
        void testMaxValidPageSize() {
            ProductPageRequest request = new ProductPageRequest();
            request.setPageSize(100);

            Set<ConstraintViolation<ProductPageRequest>> violations = validator.validate(request);

            assertTrue(violations.isEmpty());
        }

        @Test
        void testMinValidPageSize() {
            ProductPageRequest request = new ProductPageRequest();
            request.setPageSize(1);

            Set<ConstraintViolation<ProductPageRequest>> violations = validator.validate(request);

            assertTrue(violations.isEmpty());
        }

        @Test
        void testNullName() {
            ProductPageRequest request = new ProductPageRequest();
            request.setName(null);

            Set<ConstraintViolation<ProductPageRequest>> violations = validator.validate(request);

            assertTrue(violations.isEmpty());
        }

        @Test
        void testEmptyName() {
            ProductPageRequest request = new ProductPageRequest();
            request.setName("");

            Set<ConstraintViolation<ProductPageRequest>> violations = validator.validate(request);

            assertTrue(violations.isEmpty());
        }
    }

    @Nested
    class GetterSetterTests {

        @Test
        void testPageNumGetterSetter() {
            ProductPageRequest request = new ProductPageRequest();
            int pageNum = 5;

            request.setPageNum(pageNum);

            assertEquals(pageNum, request.getPageNum());
        }

        @Test
        void testPageSizeGetterSetter() {
            ProductPageRequest request = new ProductPageRequest();
            int pageSize = 20;

            request.setPageSize(pageSize);

            assertEquals(pageSize, request.getPageSize());
        }

        @Test
        void testNameGetterSetter() {
            ProductPageRequest request = new ProductPageRequest();
            String name = "Test Product";

            request.setName(name);

            assertEquals(name, request.getName());
        }
    }
}
