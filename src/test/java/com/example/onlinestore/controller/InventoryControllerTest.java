package com.example.onlinestore.controller;

import com.example.onlinestore.dto.*;
import com.example.onlinestore.model.Inventory;
import com.example.onlinestore.model.InventoryRecord;
import com.example.onlinestore.service.InventoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventoryController.class)
public class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InventoryService inventoryService;

    @MockBean
    private MessageSource messageSource;

    @Autowired
    private ObjectMapper objectMapper;

    private Inventory testInventory;
    private InventoryStockRequest stockRequest;
    private InventoryAdjustRequest adjustRequest;
    private InventoryPageRequest pageRequest;

    @BeforeEach
    void setUp() {
        testInventory = new Inventory();
        testInventory.setId(1L);
        testInventory.setProductId(1L);
        testInventory.setCurrentStock(100);
        testInventory.setMinStock(10);
        testInventory.setMaxStock(1000);
        testInventory.setCreatedAt(LocalDateTime.now());
        testInventory.setUpdatedAt(LocalDateTime.now());

        stockRequest = new InventoryStockRequest();
        stockRequest.setProductId(1L);
        stockRequest.setQuantity(50);
        stockRequest.setReason("测试入库");

        adjustRequest = new InventoryAdjustRequest();
        adjustRequest.setProductId(1L);
        adjustRequest.setNewStock(80);
        adjustRequest.setReason("测试调整");

        pageRequest = new InventoryPageRequest();
        pageRequest.setPageNum(1);
        pageRequest.setPageSize(10);
    }

    @Nested
    class ListInventoriesTests {

        @Test
        void listInventories_Success() throws Exception {
            // Given
            List<InventoryResponse> mockInventories = Arrays.asList(
                createInventoryResponse(1L, "商品1", 100),
                createInventoryResponse(2L, "商品2", 50)
            );
            PageResponse<InventoryResponse> pageResponse = new PageResponse<>();
            pageResponse.setRecords(mockInventories);
            pageResponse.setTotal(2L);
            pageResponse.setPageNum(1);
            pageResponse.setPageSize(10);

            when(inventoryService.listInventories(any(InventoryPageRequest.class)))
                .thenReturn(pageResponse);

            // When & Then
            mockMvc.perform(get("/api/inventory")
                    .param("pageNum", "1")
                    .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.records").isArray())
                .andExpect(jsonPath("$.records.length()").value(2))
                .andExpect(jsonPath("$.total").value(2))
                .andExpect(jsonPath("$.pageNum").value(1))
                .andExpect(jsonPath("$.pageSize").value(10));
        }

        @Test
        void listInventories_WithFilters() throws Exception {
            // Given
            PageResponse<InventoryResponse> pageResponse = new PageResponse<>();
            pageResponse.setRecords(Arrays.asList());
            pageResponse.setTotal(0L);
            pageResponse.setPageNum(1);
            pageResponse.setPageSize(10);

            when(inventoryService.listInventories(any(InventoryPageRequest.class)))
                .thenReturn(pageResponse);

            // When & Then
            mockMvc.perform(get("/api/inventory")
                    .param("pageNum", "1")
                    .param("pageSize", "10")
                    .param("productName", "测试商品")
                    .param("category", "电子产品")
                    .param("lowStockOnly", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.records").isArray())
                .andExpect(jsonPath("$.total").value(0));
        }

        @Test
        void listInventories_ValidationError() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/inventory")
                    .param("pageNum", "0") // Invalid page number
                    .param("pageSize", "10"))
                .andExpect(status().isBadRequest());
        }

        @Test
        void listInventories_ServiceException() throws Exception {
            // Given
            when(inventoryService.listInventories(any(InventoryPageRequest.class)))
                .thenThrow(new RuntimeException("Database error"));
            when(messageSource.getMessage(eq("error.system.internal"), isNull(), any(Locale.class)))
                .thenReturn("系统内部错误");

            // When & Then
            mockMvc.perform(get("/api/inventory")
                    .param("pageNum", "1")
                    .param("pageSize", "10"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("系统内部错误"));
        }
    }

    @Nested
    class GetInventoryByProductIdTests {

        @Test
        void getInventoryByProductId_Success() throws Exception {
            // Given
            when(inventoryService.getInventoryByProductId(1L))
                .thenReturn(testInventory);

            // When & Then
            mockMvc.perform(get("/api/inventory/product/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.currentStock").value(100))
                .andExpect(jsonPath("$.minStock").value(10))
                .andExpect(jsonPath("$.maxStock").value(1000));
        }

        @Test
        void getInventoryByProductId_NotFound() throws Exception {
            // Given
            when(inventoryService.getInventoryByProductId(1L))
                .thenThrow(new IllegalArgumentException("库存不存在，商品ID：1"));

            // When & Then
            mockMvc.perform(get("/api/inventory/product/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("库存不存在，商品ID：1"));
        }
    }

    @Nested
    class StockInTests {

        @Test
        void stockIn_Success() throws Exception {
            // Given
            when(inventoryService.stockIn(any(InventoryStockRequest.class)))
                .thenReturn(testInventory);
            when(messageSource.getMessage(eq("success.inventory.stock.in"), isNull(), any(Locale.class)))
                .thenReturn("入库操作成功");

            // When & Then
            mockMvc.perform(post("/api/inventory/stock-in")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(stockRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("入库操作成功"))
                .andExpect(jsonPath("$.inventory").exists())
                .andExpect(jsonPath("$.inventory.currentStock").value(100));
        }

        @Test
        void stockIn_ValidationError() throws Exception {
            // Given
            InventoryStockRequest invalidRequest = new InventoryStockRequest();
            invalidRequest.setQuantity(-1); // Invalid quantity

            // When & Then
            mockMvc.perform(post("/api/inventory/stock-in")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
        }

        @Test
        void stockIn_ServiceError() throws Exception {
            // Given
            when(inventoryService.stockIn(any(InventoryStockRequest.class)))
                .thenThrow(new IllegalArgumentException("商品ID不能为空"));

            // When & Then
            mockMvc.perform(post("/api/inventory/stock-in")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(stockRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("商品ID不能为空"));
        }
    }

    @Nested
    class StockOutTests {

        @Test
        void stockOut_Success() throws Exception {
            // Given
            Inventory updatedInventory = new Inventory();
            updatedInventory.setCurrentStock(50); // After stock out
            when(inventoryService.stockOut(any(InventoryStockRequest.class)))
                .thenReturn(updatedInventory);
            when(messageSource.getMessage(eq("success.inventory.stock.out"), isNull(), any(Locale.class)))
                .thenReturn("出库操作成功");

            // When & Then
            mockMvc.perform(post("/api/inventory/stock-out")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(stockRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("出库操作成功"))
                .andExpect(jsonPath("$.inventory.currentStock").value(50));
        }

        @Test
        void stockOut_InsufficientStock() throws Exception {
            // Given
            when(inventoryService.stockOut(any(InventoryStockRequest.class)))
                .thenThrow(new IllegalArgumentException("库存不足，当前库存：10，需要：50"));

            // When & Then
            mockMvc.perform(post("/api/inventory/stock-out")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(stockRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("库存不足，当前库存：10，需要：50"));
        }
    }

    @Nested
    class AdjustStockTests {

        @Test
        void adjustStock_Success() throws Exception {
            // Given
            Inventory adjustedInventory = new Inventory();
            adjustedInventory.setCurrentStock(80);
            when(inventoryService.adjustStock(any(InventoryAdjustRequest.class)))
                .thenReturn(adjustedInventory);
            when(messageSource.getMessage(eq("success.inventory.adjust"), isNull(), any(Locale.class)))
                .thenReturn("库存调整成功");

            // When & Then
            mockMvc.perform(put("/api/inventory/adjust")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(adjustRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("库存调整成功"))
                .andExpect(jsonPath("$.inventory.currentStock").value(80));
        }

        @Test
        void adjustStock_InvalidNewStock() throws Exception {
            // Given
            InventoryAdjustRequest invalidRequest = new InventoryAdjustRequest();
            invalidRequest.setProductId(1L);
            invalidRequest.setNewStock(-1); // Invalid stock

            // When & Then
            mockMvc.perform(put("/api/inventory/adjust")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class UpdateSettingsTests {

        @Test
        void updateInventorySettings_Success() throws Exception {
            // Given
            when(inventoryService.updateInventorySettings(eq(1L), eq(20), eq(2000)))
                .thenReturn(testInventory);
            when(messageSource.getMessage(eq("success.inventory.settings.updated"), isNull(), any(Locale.class)))
                .thenReturn("库存设置更新成功");

            // When & Then
            mockMvc.perform(put("/api/inventory/1/settings")
                    .param("minStock", "20")
                    .param("maxStock", "2000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("库存设置更新成功"))
                .andExpect(jsonPath("$.inventory").exists());
        }

        @Test
        void updateInventorySettings_InventoryNotFound() throws Exception {
            // Given
            when(inventoryService.updateInventorySettings(eq(999L), any(), any()))
                .thenThrow(new IllegalArgumentException("库存不存在，ID：999"));

            // When & Then
            mockMvc.perform(put("/api/inventory/999/settings")
                    .param("minStock", "20"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("库存不存在，ID：999"));
        }
    }

    @Nested
    class InventoryRecordsTests {

        @Test
        void listInventoryRecords_Success() throws Exception {
            // Given
            List<InventoryRecord> mockRecords = Arrays.asList(
                createInventoryRecord(1L, InventoryRecord.OperationType.IN, 50),
                createInventoryRecord(1L, InventoryRecord.OperationType.OUT, 20)
            );
            PageResponse<InventoryRecord> pageResponse = new PageResponse<>();
            pageResponse.setRecords(mockRecords);
            pageResponse.setTotal(2L);
            pageResponse.setPageNum(1);
            pageResponse.setPageSize(10);

            when(inventoryService.listInventoryRecords(any(InventoryRecordPageRequest.class)))
                .thenReturn(pageResponse);

            // When & Then
            mockMvc.perform(get("/api/inventory/records")
                    .param("pageNum", "1")
                    .param("pageSize", "10")
                    .param("productId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.records").isArray())
                .andExpect(jsonPath("$.records.length()").value(2))
                .andExpect(jsonPath("$.total").value(2));
        }
    }

    @Nested
    class StatisticsTests {

        @Test
        void getInventoryStats_Success() throws Exception {
            // Given
            InventoryStatsResponse stats = new InventoryStatsResponse();
            stats.setTotalProducts(100L);
            stats.setLowStockProducts(15L);
            stats.setOutOfStockProducts(5L);
            stats.setNormalStockProducts(80L);

            when(inventoryService.getInventoryStats()).thenReturn(stats);

            // When & Then
            mockMvc.perform(get("/api/inventory/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProducts").value(100))
                .andExpect(jsonPath("$.lowStockProducts").value(15))
                .andExpect(jsonPath("$.outOfStockProducts").value(5))
                .andExpect(jsonPath("$.normalStockProducts").value(80));
        }

        @Test
        void checkStock_Success() throws Exception {
            // Given
            when(inventoryService.checkStock(1L, 50)).thenReturn(true);

            // When & Then
            mockMvc.perform(get("/api/inventory/check")
                    .param("productId", "1")
                    .param("quantity", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.quantity").value(50))
                .andExpect(jsonPath("$.isEnough").value(true));
        }

        @Test
        void getLowStockCount_Success() throws Exception {
            // Given
            when(inventoryService.getLowStockCount()).thenReturn(10L);

            // When & Then
            mockMvc.perform(get("/api/inventory/low-stock/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lowStockCount").value(10));
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

    private InventoryRecord createInventoryRecord(Long productId, InventoryRecord.OperationType operationType, Integer quantity) {
        InventoryRecord record = new InventoryRecord();
        record.setProductId(productId);
        record.setOperationType(operationType);
        record.setQuantity(quantity);
        record.setBeforeStock(100);
        record.setAfterStock(operationType == InventoryRecord.OperationType.IN ? 100 + quantity : 100 - quantity);
        record.setOperatedAt(LocalDateTime.now());
        return record;
    }
}