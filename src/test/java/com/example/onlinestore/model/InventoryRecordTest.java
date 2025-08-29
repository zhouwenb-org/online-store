package com.example.onlinestore.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class InventoryRecordTest {

    private InventoryRecord record;

    @BeforeEach
    void setUp() {
        record = new InventoryRecord();
        record.setId(1L);
        record.setProductId(100L);
        record.setOperationType(InventoryRecord.OperationType.IN);
        record.setQuantity(50);
        record.setBeforeStock(20);
        record.setAfterStock(70);
        record.setReason("采购入库");
        record.setOperatorId(1L);
        record.setOperatedAt(LocalDateTime.now());
    }

    @Nested
    class PropertyTests {

        @Test
        void settersAndGetters_ShouldWorkCorrectly() {
            // Given
            Long expectedId = 2L;
            Long expectedProductId = 200L;
            InventoryRecord.OperationType expectedOperationType = InventoryRecord.OperationType.OUT;
            Integer expectedQuantity = 25;
            Integer expectedBeforeStock = 100;
            Integer expectedAfterStock = 75;
            String expectedReason = "销售出库";
            Long expectedOperatorId = 2L;
            LocalDateTime expectedOperatedAt = LocalDateTime.now().minusHours(1);

            // When
            record.setId(expectedId);
            record.setProductId(expectedProductId);
            record.setOperationType(expectedOperationType);
            record.setQuantity(expectedQuantity);
            record.setBeforeStock(expectedBeforeStock);
            record.setAfterStock(expectedAfterStock);
            record.setReason(expectedReason);
            record.setOperatorId(expectedOperatorId);
            record.setOperatedAt(expectedOperatedAt);

            // Then
            assertEquals(expectedId, record.getId());
            assertEquals(expectedProductId, record.getProductId());
            assertEquals(expectedOperationType, record.getOperationType());
            assertEquals(expectedQuantity, record.getQuantity());
            assertEquals(expectedBeforeStock, record.getBeforeStock());
            assertEquals(expectedAfterStock, record.getAfterStock());
            assertEquals(expectedReason, record.getReason());
            assertEquals(expectedOperatorId, record.getOperatorId());
            assertEquals(expectedOperatedAt, record.getOperatedAt());
        }
    }

    @Nested
    class OperationTypeTests {

        @Test
        void operationType_IN_ShouldHaveCorrectDescription() {
            // When
            InventoryRecord.OperationType operationType = InventoryRecord.OperationType.IN;

            // Then
            assertEquals("入库", operationType.getDescription());
        }

        @Test
        void operationType_OUT_ShouldHaveCorrectDescription() {
            // When
            InventoryRecord.OperationType operationType = InventoryRecord.OperationType.OUT;

            // Then
            assertEquals("出库", operationType.getDescription());
        }

        @Test
        void operationType_ADJUST_ShouldHaveCorrectDescription() {
            // When
            InventoryRecord.OperationType operationType = InventoryRecord.OperationType.ADJUST;

            // Then
            assertEquals("调整", operationType.getDescription());
        }

        @Test
        void operationType_EnumValues_ShouldBeCorrect() {
            // When
            InventoryRecord.OperationType[] values = InventoryRecord.OperationType.values();

            // Then
            assertEquals(3, values.length);
            assertEquals(InventoryRecord.OperationType.IN, values[0]);
            assertEquals(InventoryRecord.OperationType.OUT, values[1]);
            assertEquals(InventoryRecord.OperationType.ADJUST, values[2]);
        }

        @Test
        void operationType_ValueOf_ShouldWorkCorrectly() {
            // When & Then
            assertEquals(InventoryRecord.OperationType.IN, 
                InventoryRecord.OperationType.valueOf("IN"));
            assertEquals(InventoryRecord.OperationType.OUT, 
                InventoryRecord.OperationType.valueOf("OUT"));
            assertEquals(InventoryRecord.OperationType.ADJUST, 
                InventoryRecord.OperationType.valueOf("ADJUST"));
        }
    }

    @Nested
    class BusinessLogicTests {

        @Test
        void stockInRecord_ShouldHaveCorrectValues() {
            // Given
            record.setOperationType(InventoryRecord.OperationType.IN);
            record.setQuantity(30);
            record.setBeforeStock(50);
            record.setAfterStock(80);

            // When & Then
            assertEquals(InventoryRecord.OperationType.IN, record.getOperationType());
            assertEquals(30, record.getQuantity());
            assertEquals(50, record.getBeforeStock());
            assertEquals(80, record.getAfterStock());
            // After stock should equal before stock + quantity for IN operation
            assertEquals(record.getBeforeStock() + record.getQuantity(), record.getAfterStock());
        }

        @Test
        void stockOutRecord_ShouldHaveCorrectValues() {
            // Given
            record.setOperationType(InventoryRecord.OperationType.OUT);
            record.setQuantity(20);
            record.setBeforeStock(80);
            record.setAfterStock(60);

            // When & Then
            assertEquals(InventoryRecord.OperationType.OUT, record.getOperationType());
            assertEquals(20, record.getQuantity());
            assertEquals(80, record.getBeforeStock());
            assertEquals(60, record.getAfterStock());
            // After stock should equal before stock - quantity for OUT operation
            assertEquals(record.getBeforeStock() - record.getQuantity(), record.getAfterStock());
        }

        @Test
        void adjustRecord_ShouldHaveCorrectValues() {
            // Given
            record.setOperationType(InventoryRecord.OperationType.ADJUST);
            record.setQuantity(10); // Difference between old and new stock
            record.setBeforeStock(50);
            record.setAfterStock(60);

            // When & Then
            assertEquals(InventoryRecord.OperationType.ADJUST, record.getOperationType());
            assertEquals(10, record.getQuantity());
            assertEquals(50, record.getBeforeStock());
            assertEquals(60, record.getAfterStock());
        }
    }

    @Nested
    class ValidationTests {

        @Test
        void record_WithNullValues_ShouldBeAllowed() {
            // Given
            InventoryRecord nullRecord = new InventoryRecord();

            // When & Then
            assertNull(nullRecord.getId());
            assertNull(nullRecord.getProductId());
            assertNull(nullRecord.getOperationType());
            assertNull(nullRecord.getQuantity());
            assertNull(nullRecord.getBeforeStock());
            assertNull(nullRecord.getAfterStock());
            assertNull(nullRecord.getReason());
            assertNull(nullRecord.getOperatorId());
            assertNull(nullRecord.getOperatedAt());
        }

        @Test
        void record_WithZeroValues_ShouldBeAllowed() {
            // Given
            record.setQuantity(0);
            record.setBeforeStock(0);
            record.setAfterStock(0);

            // When & Then
            assertEquals(0, record.getQuantity());
            assertEquals(0, record.getBeforeStock());
            assertEquals(0, record.getAfterStock());
        }

        @Test
        void record_WithEmptyReason_ShouldBeAllowed() {
            // Given
            record.setReason("");

            // When & Then
            assertEquals("", record.getReason());
        }

        @Test
        void record_WithLongReason_ShouldBeAllowed() {
            // Given
            String longReason = "这是一个很长的原因描述".repeat(50);
            record.setReason(longReason);

            // When & Then
            assertEquals(longReason, record.getReason());
        }
    }

    @Nested
    class EdgeCaseTests {

        @Test
        void record_WithNegativeQuantity_ShouldBeAllowed() {
            // Given - This might represent a correction or special case
            record.setQuantity(-10);

            // When & Then
            assertEquals(-10, record.getQuantity());
        }

        @Test
        void record_WithNegativeStock_ShouldBeAllowed() {
            // Given - This might represent special inventory scenarios
            record.setBeforeStock(-5);
            record.setAfterStock(-3);

            // When & Then
            assertEquals(-5, record.getBeforeStock());
            assertEquals(-3, record.getAfterStock());
        }

        @Test
        void record_WithSpecialCharactersInReason_ShouldBeAllowed() {
            // Given
            String specialReason = "原因包含特殊字符: @#$%^&*()_+-=[]{}|;':\",./<>?";
            record.setReason(specialReason);

            // When & Then
            assertEquals(specialReason, record.getReason());
        }

        @Test
        void record_WithUnicodeCharactersInReason_ShouldBeAllowed() {
            // Given
            String unicodeReason = "Unicode字符: ™®©€£¥中文한국어日本語";
            record.setReason(unicodeReason);

            // When & Then
            assertEquals(unicodeReason, record.getReason());
        }
    }

    @Nested
    class StringRepresentationTests {

        @Test
        void toString_ShouldContainAllImportantFields() {
            // When
            String result = record.toString();

            // Then
            assertNotNull(result);
            assertTrue(result.contains("InventoryRecord"));
            assertTrue(result.contains("id=1"));
            assertTrue(result.contains("productId=100"));
            assertTrue(result.contains("operationType=IN"));
            assertTrue(result.contains("quantity=50"));
            assertTrue(result.contains("beforeStock=20"));
            assertTrue(result.contains("afterStock=70"));
            assertTrue(result.contains("reason='采购入库'"));
            assertTrue(result.contains("operatorId=1"));
        }

        @Test
        void toString_WithNullValues_ShouldNotThrowException() {
            // Given
            InventoryRecord nullRecord = new InventoryRecord();

            // When & Then
            assertDoesNotThrow(() -> {
                String result = nullRecord.toString();
                assertNotNull(result);
                assertTrue(result.contains("InventoryRecord"));
            });
        }
    }
}