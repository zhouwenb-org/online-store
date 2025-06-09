package com.example.onlinestore.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CreateProductRequestTest {

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
            CreateProductRequest request = new CreateProductRequest();
            request.setName("Test Product");
            request.setCategory("Electronics");
            request.setPrice(new BigDecimal("99.99"));

            Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(request);

            assertTrue(violations.isEmpty());
        }

        @Test
        void testBlankName() {
            CreateProductRequest request = new CreateProductRequest();
            request.setName("");
            request.setCategory("Electronics");
            request.setPrice(new BigDecimal("99.99"));

            Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("error.product.name.empty")));
        }

        @Test
        void testNullName() {
            CreateProductRequest request = new CreateProductRequest();
            request.setName(null);
            request.setCategory("Electronics");
            request.setPrice(new BigDecimal("99.99"));

            Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("error.product.name.empty")));
        }

        @Test
        void testBlankCategory() {
            CreateProductRequest request = new CreateProductRequest();
            request.setName("Test Product");
            request.setCategory("");
            request.setPrice(new BigDecimal("99.99"));

            Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("error.product.category.empty")));
        }

        @Test
        void testNullCategory() {
            CreateProductRequest request = new CreateProductRequest();
            request.setName("Test Product");
            request.setCategory(null);
            request.setPrice(new BigDecimal("99.99"));

            Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("error.product.category.empty")));
        }

        @Test
        void testNullPrice() {
            CreateProductRequest request = new CreateProductRequest();
            request.setName("Test Product");
            request.setCategory("Electronics");
            request.setPrice(null);

            Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("error.product.price.empty")));
        }

        @Test
        void testZeroPrice() {
            CreateProductRequest request = new CreateProductRequest();
            request.setName("Test Product");
            request.setCategory("Electronics");
            request.setPrice(BigDecimal.ZERO);

            Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("error.product.price.min")));
        }

        @Test
        void testNegativePrice() {
            CreateProductRequest request = new CreateProductRequest();
            request.setName("Test Product");
            request.setCategory("Electronics");
            request.setPrice(new BigDecimal("-10.00"));

            Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(request);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("error.product.price.min")));
        }

        @Test
        void testMinimumValidPrice() {
            CreateProductRequest request = new CreateProductRequest();
            request.setName("Test Product");
            request.setCategory("Electronics");
            request.setPrice(new BigDecimal("0.01"));

            Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(request);

            assertTrue(violations.isEmpty());
        }
    }

    @Nested
    class GetterSetterTests {

        @Test
        void testNameGetterSetter() {
            CreateProductRequest request = new CreateProductRequest();
            String name = "Test Product";

            request.setName(name);

            assertEquals(name, request.getName());
        }

        @Test
        void testCategoryGetterSetter() {
            CreateProductRequest request = new CreateProductRequest();
            String category = "Electronics";

            request.setCategory(category);

            assertEquals(category, request.getCategory());
        }

        @Test
        void testPriceGetterSetter() {
            CreateProductRequest request = new CreateProductRequest();
            BigDecimal price = new BigDecimal("99.99");

            request.setPrice(price);

            assertEquals(price, request.getPrice());
        }
    }
}
