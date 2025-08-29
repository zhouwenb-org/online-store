package com.example.onlinestore.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class InventoryTest {

    private Inventory inventory;

    @BeforeEach
    void setUp() {
        inventory = new Inventory();
        inventory.setId(1L);
        inventory.setProductId(100L);
        inventory.setCurrentStock(50);
        inventory.setMinStock(10);
        inventory.setMaxStock(1000);
        inventory.setCreatedAt(LocalDateTime.now());
        inventory.setUpdatedAt(LocalDateTime.now());
    }

    @Nested
    class PropertyTests {

        @Test
        void settersAndGetters_ShouldWorkCorrectly() {
            // Given
            Long expectedId = 2L;
            Long expectedProductId = 200L;
            Integer expectedCurrentStock = 75;
            Integer expectedMinStock = 5;
            Integer expectedMaxStock = 500;
            LocalDateTime expectedCreatedAt = LocalDateTime.now().minusDays(1);
            LocalDateTime expectedUpdatedAt = LocalDateTime.now();

            // When
            inventory.setId(expectedId);
            inventory.setProductId(expectedProductId);
            inventory.setCurrentStock(expectedCurrentStock);
            inventory.setMinStock(expectedMinStock);
            inventory.setMaxStock(expectedMaxStock);
            inventory.setCreatedAt(expectedCreatedAt);
            inventory.setUpdatedAt(expectedUpdatedAt);

            // Then
            assertEquals(expectedId, inventory.getId());
            assertEquals(expectedProductId, inventory.getProductId());
            assertEquals(expectedCurrentStock, inventory.getCurrentStock());
            assertEquals(expectedMinStock, inventory.getMinStock());
            assertEquals(expectedMaxStock, inventory.getMaxStock());
            assertEquals(expectedCreatedAt, inventory.getCreatedAt());
            assertEquals(expectedUpdatedAt, inventory.getUpdatedAt());
        }
    }

    @Nested
    class BusinessLogicTests {

        @Test
        void isLowStock_WhenCurrentStockEqualsMinStock_ShouldReturnTrue() {
            // Given
            inventory.setCurrentStock(10);
            inventory.setMinStock(10);

            // When
            boolean result = inventory.isLowStock();

            // Then
            assertTrue(result);
        }

        @Test
        void isLowStock_WhenCurrentStockBelowMinStock_ShouldReturnTrue() {
            // Given
            inventory.setCurrentStock(5);
            inventory.setMinStock(10);

            // When
            boolean result = inventory.isLowStock();

            // Then
            assertTrue(result);
        }

        @Test
        void isLowStock_WhenCurrentStockAboveMinStock_ShouldReturnFalse() {
            // Given
            inventory.setCurrentStock(50);
            inventory.setMinStock(10);

            // When
            boolean result = inventory.isLowStock();

            // Then
            assertFalse(result);
        }

        @Test
        void isLowStock_WhenCurrentStockIsNull_ShouldReturnFalse() {
            // Given
            inventory.setCurrentStock(null);
            inventory.setMinStock(10);

            // When
            boolean result = inventory.isLowStock();

            // Then
            assertFalse(result);
        }

        @Test
        void isLowStock_WhenMinStockIsNull_ShouldReturnFalse() {
            // Given
            inventory.setCurrentStock(50);
            inventory.setMinStock(null);

            // When
            boolean result = inventory.isLowStock();

            // Then
            assertFalse(result);
        }

        @Test
        void hasEnoughStock_WhenCurrentStockGreaterThanRequired_ShouldReturnTrue() {
            // Given
            inventory.setCurrentStock(50);
            Integer requiredQuantity = 30;

            // When
            boolean result = inventory.hasEnoughStock(requiredQuantity);

            // Then
            assertTrue(result);
        }

        @Test
        void hasEnoughStock_WhenCurrentStockEqualsRequired_ShouldReturnTrue() {
            // Given
            inventory.setCurrentStock(50);
            Integer requiredQuantity = 50;

            // When
            boolean result = inventory.hasEnoughStock(requiredQuantity);

            // Then
            assertTrue(result);
        }

        @Test
        void hasEnoughStock_WhenCurrentStockLessThanRequired_ShouldReturnFalse() {
            // Given
            inventory.setCurrentStock(30);
            Integer requiredQuantity = 50;

            // When
            boolean result = inventory.hasEnoughStock(requiredQuantity);

            // Then
            assertFalse(result);
        }

        @Test
        void hasEnoughStock_WhenCurrentStockIsNull_ShouldReturnFalse() {
            // Given
            inventory.setCurrentStock(null);
            Integer requiredQuantity = 10;

            // When
            boolean result = inventory.hasEnoughStock(requiredQuantity);

            // Then
            assertFalse(result);
        }

        @Test
        void hasEnoughStock_WhenRequiredQuantityIsNull_ShouldReturnFalse() {
            // Given
            inventory.setCurrentStock(50);
            Integer requiredQuantity = null;

            // When
            boolean result = inventory.hasEnoughStock(requiredQuantity);

            // Then
            assertFalse(result);
        }

        @Test
        void hasEnoughStock_WhenBothAreNull_ShouldReturnFalse() {
            // Given
            inventory.setCurrentStock(null);
            Integer requiredQuantity = null;

            // When
            boolean result = inventory.hasEnoughStock(requiredQuantity);

            // Then
            assertFalse(result);
        }
    }

    @Nested
    class EdgeCaseTests {

        @Test
        void hasEnoughStock_WithZeroCurrentStock_ShouldReturnFalseForAnyPositiveQuantity() {
            // Given
            inventory.setCurrentStock(0);
            Integer requiredQuantity = 1;

            // When
            boolean result = inventory.hasEnoughStock(requiredQuantity);

            // Then
            assertFalse(result);
        }

        @Test
        void hasEnoughStock_WithZeroCurrentStock_ShouldReturnTrueForZeroQuantity() {
            // Given
            inventory.setCurrentStock(0);
            Integer requiredQuantity = 0;

            // When
            boolean result = inventory.hasEnoughStock(requiredQuantity);

            // Then
            assertTrue(result);
        }

        @Test
        void isLowStock_WithZeroMinStock_ShouldReturnFalseForPositiveCurrentStock() {
            // Given
            inventory.setCurrentStock(10);
            inventory.setMinStock(0);

            // When
            boolean result = inventory.isLowStock();

            // Then
            assertFalse(result);
        }

        @Test
        void isLowStock_WithZeroMinStock_ShouldReturnTrueForZeroCurrentStock() {
            // Given
            inventory.setCurrentStock(0);
            inventory.setMinStock(0);

            // When
            boolean result = inventory.isLowStock();

            // Then
            assertTrue(result);
        }
    }

    @Nested
    class StringRepresentationTests {

        @Test
        void toString_ShouldContainAllImportantFields() {
            // When
            String result = inventory.toString();

            // Then
            assertNotNull(result);
            assertTrue(result.contains("Inventory"));
            assertTrue(result.contains("id=1"));
            assertTrue(result.contains("productId=100"));
            assertTrue(result.contains("currentStock=50"));
            assertTrue(result.contains("minStock=10"));
            assertTrue(result.contains("maxStock=1000"));
        }
    }
}