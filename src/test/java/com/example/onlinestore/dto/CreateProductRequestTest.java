package com.example.onlinestore.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class CreateProductRequestTest {

    private Validator validator;
    private CreateProductRequest validRequest;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        validRequest = new CreateProductRequest();
        validRequest.setName("测试商品");
        validRequest.setCategory("电子产品");
        validRequest.setPrice(new BigDecimal("199.99"));
    }

    @Nested
    class ValidationTests {

        @Test
        void validRequest_ShouldPassValidation() {
            // When
            Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(validRequest);

            // Then
            assertTrue(violations.isEmpty());
        }

        @Test
        void nullName_ShouldFailValidation() {
            // Given
            validRequest.setName(null);

            // When
            Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(validRequest);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("error.product.name.empty")));
        }

        @Test
        void emptyName_ShouldFailValidation() {
            // Given
            validRequest.setName("");

            // When
            Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(validRequest);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("error.product.name.empty")));
        }

        @Test
        void blankName_ShouldFailValidation() {
            // Given
            validRequest.setName("   ");

            // When
            Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(validRequest);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("error.product.name.empty")));
        }

        @Test
        void nullCategory_ShouldFailValidation() {
            // Given
            validRequest.setCategory(null);

            // When
            Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(validRequest);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("error.product.category.empty")));
        }

        @Test
        void emptyCategory_ShouldFailValidation() {
            // Given
            validRequest.setCategory("");

            // When
            Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(validRequest);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("error.product.category.empty")));
        }

        @Test
        void nullPrice_ShouldFailValidation() {
            // Given
            validRequest.setPrice(null);

            // When
            Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(validRequest);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("error.product.price.empty")));
        }

        @Test
        void zeroPrice_ShouldFailValidation() {
            // Given
            validRequest.setPrice(BigDecimal.ZERO);

            // When
            Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(validRequest);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("error.product.price.min")));
        }

        @Test
        void negativePrice_ShouldFailValidation() {
            // Given
            validRequest.setPrice(new BigDecimal("-10.00"));

            // When
            Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(validRequest);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("error.product.price.min")));
        }

        @Test
        void minimumValidPrice_ShouldPassValidation() {
            // Given
            validRequest.setPrice(new BigDecimal("0.01"));

            // When
            Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(validRequest);

            // Then
            assertTrue(violations.isEmpty());
        }
    }

    @Nested
    class PropertyTests {

        @Test
        void settersAndGetters_ShouldWorkCorrectly() {
            // Given
            String expectedName = "新商品";
            String expectedCategory = "新分类";
            BigDecimal expectedPrice = new BigDecimal("299.99");

            // When
            validRequest.setName(expectedName);
            validRequest.setCategory(expectedCategory);
            validRequest.setPrice(expectedPrice);

            // Then
            assertEquals(expectedName, validRequest.getName());
            assertEquals(expectedCategory, validRequest.getCategory());
            assertEquals(expectedPrice, validRequest.getPrice());
        }
    }

    @Nested
    class BoundaryValueTests {

        @Test
        void verySmallValidPrice_ShouldPassValidation() {
            // Given
            validRequest.setPrice(new BigDecimal("0.01"));

            // When
            Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(validRequest);

            // Then
            assertTrue(violations.isEmpty());
        }

        @Test
        void veryLargePrice_ShouldPassValidation() {
            // Given
            validRequest.setPrice(new BigDecimal("999999.99"));

            // When
            Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(validRequest);

            // Then
            assertTrue(violations.isEmpty());
        }

        @Test
        void longProductName_ShouldPassValidation() {
            // Given
            String longName = "这是一个非常长的商品名称".repeat(10);
            validRequest.setName(longName);

            // When
            Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(validRequest);

            // Then
            // Should pass as there's no length constraint in the current implementation
            assertTrue(violations.isEmpty());
        }
    }

    @Nested
    class SpecialCharacterTests {

        @Test
        void nameWithSpecialCharacters_ShouldPassValidation() {
            // Given
            validRequest.setName("商品@#$%^&*()_+-=[]{}|;':\",./<>?");

            // When
            Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(validRequest);

            // Then
            assertTrue(violations.isEmpty());
        }

        @Test
        void nameWithUnicodeCharacters_ShouldPassValidation() {
            // Given
            validRequest.setName("商品™®©€£¥");

            // When
            Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(validRequest);

            // Then
            assertTrue(violations.isEmpty());
        }
    }

    @Nested
    class WhitespaceTests {

        @Test
        void nameWithLeadingAndTrailingSpaces_ShouldPassValidation() {
            // Given
            validRequest.setName("  测试商品  ");

            // When
            Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(validRequest);

            // Then
            assertTrue(violations.isEmpty());
            assertEquals("  测试商品  ", validRequest.getName());
        }

        @Test
        void categoryWithSpaces_ShouldPassValidation() {
            // Given
            validRequest.setCategory("电子 产品");

            // When
            Set<ConstraintViolation<CreateProductRequest>> violations = validator.validate(validRequest);

            // Then
            assertTrue(violations.isEmpty());
        }
    }
}