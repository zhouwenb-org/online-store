package com.example.onlinestore.service;

import com.example.onlinestore.context.UserContext;
import com.example.onlinestore.dto.*;
import com.example.onlinestore.mapper.InventoryMapper;
import com.example.onlinestore.mapper.InventoryRecordMapper;
import com.example.onlinestore.mapper.ProductMapper;
import com.example.onlinestore.model.Inventory;
import com.example.onlinestore.model.InventoryRecord;
import com.example.onlinestore.model.Product;
import com.example.onlinestore.model.User;
import com.example.onlinestore.service.impl.InventoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Nested;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceTest {

    @Mock
    private InventoryMapper inventoryMapper;

    @Mock
    private InventoryRecordMapper inventoryRecordMapper;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private Product testProduct;
    private Inventory testInventory;
    private User testUser;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("测试商品");
        testProduct.setCategory("电子产品");
        testProduct.setPrice(new BigDecimal("299.99"));

        testInventory = new Inventory();
        testInventory.setId(1L);
        testInventory.setProductId(1L);
        testInventory.setCurrentStock(100);
        testInventory.setMinStock(10);
        testInventory.setMaxStock(1000);
        testInventory.setCreatedAt(LocalDateTime.now());
        testInventory.setUpdatedAt(LocalDateTime.now());

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("admin");
    }

    @Nested
    class CreateInventoryTests {

        @Test
        void createInventory_Success() {
            // Given
            when(productMapper.findById(1L)).thenReturn(testProduct);
            when(inventoryMapper.findByProductId(1L)).thenReturn(null);

            // When
            Inventory result = inventoryService.createInventory(1L, 50, 5, 500);

            // Then
            assertNotNull(result);
            assertEquals(1L, result.getProductId());
            assertEquals(50, result.getCurrentStock());
            assertEquals(5, result.getMinStock());
            assertEquals(500, result.getMaxStock());
            
            verify(inventoryMapper).insertInventory(any(Inventory.class));
            verify(inventoryRecordMapper).insertInventoryRecord(any(InventoryRecord.class));
        }

        @Test
        void createInventory_ProductNotFound() {
            // Given
            when(productMapper.findById(1L)).thenReturn(null);

            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> inventoryService.createInventory(1L, 50, 5, 500)
            );
            assertEquals("商品不存在，ID：1", exception.getMessage());
        }

        @Test
        void createInventory_InventoryAlreadyExists() {
            // Given
            when(productMapper.findById(1L)).thenReturn(testProduct);
            when(inventoryMapper.findByProductId(1L)).thenReturn(testInventory);

            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> inventoryService.createInventory(1L, 50, 5, 500)
            );
            assertEquals("商品库存已存在，ID：1", exception.getMessage());
        }

        @Test
        void createInventory_NullProductId() {
            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> inventoryService.createInventory(null, 50, 5, 500)
            );
            assertEquals("商品ID不能为空", exception.getMessage());
        }
    }

    @Nested
    class StockInTests {

        @Test
        void stockIn_Success() {
            // Given
            InventoryStockRequest request = new InventoryStockRequest();
            request.setProductId(1L);
            request.setQuantity(50);
            request.setReason("采购入库");

            when(inventoryMapper.findByProductId(1L)).thenReturn(testInventory);

            try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
                mockedUserContext.when(UserContext::getCurrentUser).thenReturn(testUser);

                // When
                Inventory result = inventoryService.stockIn(request);

                // Then
                assertNotNull(result);
                assertEquals(150, result.getCurrentStock()); // 100 + 50
                
                verify(inventoryMapper).updateStock(eq(1L), eq(150), any(LocalDateTime.class));
                verify(inventoryRecordMapper).insertInventoryRecord(any(InventoryRecord.class));

                // Verify inventory record
                ArgumentCaptor<InventoryRecord> recordCaptor = ArgumentCaptor.forClass(InventoryRecord.class);
                verify(inventoryRecordMapper).insertInventoryRecord(recordCaptor.capture());
                InventoryRecord record = recordCaptor.getValue();
                assertEquals(InventoryRecord.OperationType.IN, record.getOperationType());
                assertEquals(50, record.getQuantity());
                assertEquals(100, record.getBeforeStock());
                assertEquals(150, record.getAfterStock());
                assertEquals("采购入库", record.getReason());
                assertEquals(1L, record.getOperatorId());
            }
        }

        @Test
        void stockIn_InvalidRequest() {
            // Given
            InventoryStockRequest request = new InventoryStockRequest();
            request.setProductId(null);
            request.setQuantity(50);

            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> inventoryService.stockIn(request)
            );
            assertEquals("商品ID不能为空", exception.getMessage());
        }

        @Test
        void stockIn_ZeroQuantity() {
            // Given
            InventoryStockRequest request = new InventoryStockRequest();
            request.setProductId(1L);
            request.setQuantity(0);

            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> inventoryService.stockIn(request)
            );
            assertEquals("操作数量必须大于0", exception.getMessage());
        }
    }

    @Nested
    class StockOutTests {

        @Test
        void stockOut_Success() {
            // Given
            InventoryStockRequest request = new InventoryStockRequest();
            request.setProductId(1L);
            request.setQuantity(30);
            request.setReason("销售出库");

            when(inventoryMapper.findByProductId(1L)).thenReturn(testInventory);

            try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
                mockedUserContext.when(UserContext::getCurrentUser).thenReturn(testUser);

                // When
                Inventory result = inventoryService.stockOut(request);

                // Then
                assertNotNull(result);
                assertEquals(70, result.getCurrentStock()); // 100 - 30
                
                verify(inventoryMapper).updateStock(eq(1L), eq(70), any(LocalDateTime.class));
                verify(inventoryRecordMapper).insertInventoryRecord(any(InventoryRecord.class));
            }
        }

        @Test
        void stockOut_InsufficientStock() {
            // Given
            InventoryStockRequest request = new InventoryStockRequest();
            request.setProductId(1L);
            request.setQuantity(150); // More than available stock (100)

            when(inventoryMapper.findByProductId(1L)).thenReturn(testInventory);

            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> inventoryService.stockOut(request)
            );
            assertEquals("库存不足，当前库存：100，需要：150", exception.getMessage());
        }
    }

    @Nested
    class AdjustStockTests {

        @Test
        void adjustStock_Success() {
            // Given
            InventoryAdjustRequest request = new InventoryAdjustRequest();
            request.setProductId(1L);
            request.setNewStock(80);
            request.setReason("盘点调整");

            when(inventoryMapper.findByProductId(1L)).thenReturn(testInventory);

            try (MockedStatic<UserContext> mockedUserContext = mockStatic(UserContext.class)) {
                mockedUserContext.when(UserContext::getCurrentUser).thenReturn(testUser);

                // When
                Inventory result = inventoryService.adjustStock(request);

                // Then
                assertNotNull(result);
                assertEquals(80, result.getCurrentStock());
                
                verify(inventoryMapper).updateStock(eq(1L), eq(80), any(LocalDateTime.class));
                verify(inventoryRecordMapper).insertInventoryRecord(any(InventoryRecord.class));
            }
        }

        @Test
        void adjustStock_NullRequest() {
            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> inventoryService.adjustStock(null)
            );
            assertEquals("调整请求参数不能为空", exception.getMessage());
        }
    }

    @Nested
    class QueryTests {

        @Test
        void getInventoryByProductId_Success() {
            // Given
            when(inventoryMapper.findByProductId(1L)).thenReturn(testInventory);

            // When
            Inventory result = inventoryService.getInventoryByProductId(1L);

            // Then
            assertNotNull(result);
            assertEquals(testInventory.getId(), result.getId());
            assertEquals(testInventory.getProductId(), result.getProductId());
            assertEquals(testInventory.getCurrentStock(), result.getCurrentStock());
        }

        @Test
        void getInventoryByProductId_NotFound() {
            // Given
            when(inventoryMapper.findByProductId(1L)).thenReturn(null);

            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> inventoryService.getInventoryByProductId(1L)
            );
            assertEquals("库存不存在，商品ID：1", exception.getMessage());
        }

        @Test
        void listInventories_Success() {
            // Given
            InventoryPageRequest request = new InventoryPageRequest();
            request.setPageNum(1);
            request.setPageSize(10);
            request.setLowStockOnly(false);

            List<InventoryResponse> mockInventories = Arrays.asList(
                createInventoryResponse(1L, "商品1", 50),
                createInventoryResponse(2L, "商品2", 20)
            );

            when(inventoryMapper.findInventoryWithProducts(null, null, false, 0, 10))
                .thenReturn(mockInventories);
            when(inventoryMapper.countInventory(null, null, false))
                .thenReturn(2L);

            // When
            PageResponse<InventoryResponse> result = inventoryService.listInventories(request);

            // Then
            assertNotNull(result);
            assertEquals(2, result.getRecords().size());
            assertEquals(2L, result.getTotal());
            assertEquals(1, result.getPageNum());
            assertEquals(10, result.getPageSize());
        }

        @Test
        void checkStock_Success() {
            // Given
            when(inventoryMapper.findByProductId(1L)).thenReturn(testInventory);

            // When
            boolean result = inventoryService.checkStock(1L, 50);

            // Then
            assertTrue(result); // Current stock is 100, required is 50
        }

        @Test
        void checkStock_Insufficient() {
            // Given
            when(inventoryMapper.findByProductId(1L)).thenReturn(testInventory);

            // When
            boolean result = inventoryService.checkStock(1L, 150);

            // Then
            assertFalse(result); // Current stock is 100, required is 150
        }

        @Test
        void getInventoryStats_Success() {
            // Given
            when(inventoryMapper.countInventory(null, null, null)).thenReturn(100L);
            when(inventoryMapper.countLowStockProducts()).thenReturn(15L);
            when(inventoryMapper.countOutOfStockProducts()).thenReturn(5L);

            // When
            InventoryStatsResponse result = inventoryService.getInventoryStats();

            // Then
            assertNotNull(result);
            assertEquals(100L, result.getTotalProducts());
            assertEquals(15L, result.getLowStockProducts());
            assertEquals(5L, result.getOutOfStockProducts());
            assertEquals(80L, result.getNormalStockProducts()); // 100 - 15 - 5
        }
    }

    @Nested
    class ValidationTests {

        @Test
        void updateInventorySettings_Success() {
            // Given
            when(inventoryMapper.findById(1L)).thenReturn(testInventory);

            // When
            Inventory result = inventoryService.updateInventorySettings(1L, 20, 2000);

            // Then
            assertNotNull(result);
            assertEquals(20, result.getMinStock());
            assertEquals(2000, result.getMaxStock());
            
            verify(inventoryMapper).updateInventorySettings(eq(1L), eq(20), eq(2000), any(LocalDateTime.class));
        }

        @Test
        void updateInventorySettings_InventoryNotFound() {
            // Given
            when(inventoryMapper.findById(1L)).thenReturn(null);

            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> inventoryService.updateInventorySettings(1L, 20, 2000)
            );
            assertEquals("库存不存在，ID：1", exception.getMessage());
        }

        @Test
        void getLowStockCount_Success() {
            // Given
            when(inventoryMapper.countLowStockProducts()).thenReturn(10L);

            // When
            long result = inventoryService.getLowStockCount();

            // Then
            assertEquals(10L, result);
        }
    }

    private InventoryResponse createInventoryResponse(Long productId, String productName, Integer currentStock) {
        InventoryResponse response = new InventoryResponse();
        response.setProductId(productId);
        response.setProductName(productName);
        response.setCurrentStock(currentStock);
        response.setMinStock(10);
        response.setMaxStock(1000);
        response.setIsLowStock(currentStock <= 10);
        return response;
    }
}