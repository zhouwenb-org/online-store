package com.example.onlinestore.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class InventoryStockRequestTest {

    private Validator validator;
    private InventoryStockRequest validRequest;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        validRequest = new InventoryStockRequest();
        validRequest.setProductId(1L);
        validRequest.setQuantity(50);
        validRequest.setReason("测试入库");
    }

    @Nested
    class ValidationTests {

        @Test
        void validRequest_ShouldPassValidation() {
            // When
            Set<ConstraintViolation<InventoryStockRequest>> violations = validator.validate(validRequest);

            // Then
            assertTrue(violations.isEmpty());
        }

        @Test
        void nullProductId_ShouldFailValidation() {
            // Given
            validRequest.setProductId(null);

            // When
            Set<ConstraintViolation<InventoryStockRequest>> violations = validator.validate(validRequest);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("error.inventory.product.id.empty")));
        }

        @Test
        void nullQuantity_ShouldFailValidation() {
            // Given
            validRequest.setQuantity(null);

            // When
            Set<ConstraintViolation<InventoryStockRequest>> violations = validator.validate(validRequest);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("error.inventory.quantity.empty")));
        }

        @Test
        void zeroQuantity_ShouldFailValidation() {
            // Given
            validRequest.setQuantity(0);

            // When
            Set<ConstraintViolation<InventoryStockRequest>> violations = validator.validate(validRequest);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("error.inventory.quantity.min")));
        }

        @Test
        void negativeQuantity_ShouldFailValidation() {
            // Given
            validRequest.setQuantity(-1);

            // When
            Set<ConstraintViolation<InventoryStockRequest>> violations = validator.validate(validRequest);

            // Then
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().contains("error.inventory.quantity.min")));
        }

        @Test
        void minimumValidQuantity_ShouldPassValidation() {
            // Given
            validRequest.setQuantity(1);

            // When
            Set<ConstraintViolation<InventoryStockRequest>> violations = validator.validate(validRequest);

            // Then
            assertTrue(violations.isEmpty());
        }

        @Test
        void nullReason_ShouldPassValidation() {
            // Given
            validRequest.setReason(null);

            // When
            Set<ConstraintViolation<InventoryStockRequest>> violations = validator.validate(validRequest);

            // Then
            assertTrue(violations.isEmpty()); // Reason is optional
        }

        @Test
        void emptyReason_ShouldPassValidation() {
            // Given
            validRequest.setReason("");

            // When
            Set<ConstraintViolation<InventoryStockRequest>> violations = validator.validate(validRequest);

            // Then
            assertTrue(violations.isEmpty()); // Reason is optional
        }
    }

    @Nested
    class PropertyTests {

        @Test
        void settersAndGetters_ShouldWorkCorrectly() {
            // Given
            Long expectedProductId = 123L;
            Integer expectedQuantity = 75;
            String expectedReason = "库存补充";

            // When
            validRequest.setProductId(expectedProductId);
            validRequest.setQuantity(expectedQuantity);
            validRequest.setReason(expectedReason);

            // Then
            assertEquals(expectedProductId, validRequest.getProductId());
            assertEquals(expectedQuantity, validRequest.getQuantity());
            assertEquals(expectedReason, validRequest.getReason());
        }

        @Test
        void toString_ShouldReturnCorrectFormat() {
            // When
            String result = validRequest.toString();

            // Then
            assertNotNull(result);
            assertTrue(result.contains("InventoryStockRequest"));
            assertTrue(result.contains("productId=1"));
            assertTrue(result.contains("quantity=50"));
            assertTrue(result.contains("reason='测试入库'"));
        }
    }

    @Nested
    class BoundaryValueTests {

        @Test
        void largeProductId_ShouldPassValidation() {
            // Given
            validRequest.setProductId(Long.MAX_VALUE);

            // When
            Set<ConstraintViolation<InventoryStockRequest>> violations = validator.validate(validRequest);

            // Then
            assertTrue(violations.isEmpty());
        }

        @Test
        void largeQuantity_ShouldPassValidation() {
            // Given
            validRequest.setQuantity(Integer.MAX_VALUE);

            // When
            Set<ConstraintViolation<InventoryStockRequest>> violations = validator.validate(validRequest);

            // Then
            assertTrue(violations.isEmpty());
        }

        @Test
        void longReason_ShouldPassValidation() {
            // Given
            String longReason = "这是一个很长的原因描述".repeat(100);
            validRequest.setReason(longReason);

            // When
            Set<ConstraintViolation<InventoryStockRequest>> violations = validator.validate(validRequest);

            // Then
            assertTrue(violations.isEmpty());
        }
    }

    @Nested
    class SpecialValueTests {

        @Test
        void reasonWithSpecialCharacters_ShouldPassValidation() {
            // Given
            validRequest.setReason("特殊字符: @#$%^&*()_+-=[]{}|;':\",./<>?");

            // When
            Set<ConstraintViolation<InventoryStockRequest>> violations = validator.validate(validRequest);

            // Then
            assertTrue(violations.isEmpty());
        }

        @Test
        void reasonWithUnicodeCharacters_ShouldPassValidation() {
            // Given
            validRequest.setReason("Unicode字符: ™®©€£¥中文한국어日本語");

            // When
            Set<ConstraintViolation<InventoryStockRequest>> violations = validator.validate(validRequest);

            // Then
            assertTrue(violations.isEmpty());
        }

        @Test
        void reasonWithWhitespace_ShouldPassValidation() {
            // Given
            validRequest.setReason("  包含空格的原因  ");

            // When
            Set<ConstraintViolation<InventoryStockRequest>> violations = validator.validate(validRequest);

            // Then
            assertTrue(violations.isEmpty());
            assertEquals("  包含空格的原因  ", validRequest.getReason());
        }
    }
}