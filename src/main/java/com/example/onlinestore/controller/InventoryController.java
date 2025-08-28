package com.example.onlinestore.controller;

import com.example.onlinestore.annotation.RequireAdmin;
import com.example.onlinestore.annotation.ValidateParams;
import com.example.onlinestore.dto.*;
import com.example.onlinestore.model.Inventory;
import com.example.onlinestore.service.InventoryService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    
    private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);
    
    @Autowired
    private InventoryService inventoryService;
    
    @Autowired
    private MessageSource messageSource;

    /**
     * 分页查询库存列表
     */
    @GetMapping
    @ValidateParams
    public ResponseEntity<?> listInventories(@Valid InventoryPageRequest request) {
        try {
            logger.debug("开始查询库存列表，请求参数：{}", request);
            return ResponseEntity.ok(inventoryService.listInventories(request));
        } catch (IllegalArgumentException e) {
            logger.warn("查询库存列表失败：{}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("查询库存列表失败：{}", e.getMessage(), e);
            String errorMessage = messageSource.getMessage(
                "error.system.internal", null, LocaleContextHolder.getLocale());
            return ResponseEntity.internalServerError().body(new ErrorResponse(errorMessage));
        }
    }

    /**
     * 根据商品ID查询库存
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getInventoryByProductId(@PathVariable Long productId) {
        try {
            logger.debug("开始查询商品库存，商品ID：{}", productId);
            Inventory inventory = inventoryService.getInventoryByProductId(productId);
            return ResponseEntity.ok(inventory);
        } catch (IllegalArgumentException e) {
            logger.warn("查询商品库存失败：{}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("查询商品库存失败：{}", e.getMessage(), e);
            String errorMessage = messageSource.getMessage(
                "error.system.internal", null, LocaleContextHolder.getLocale());
            return ResponseEntity.internalServerError().body(new ErrorResponse(errorMessage));
        }
    }

    /**
     * 入库操作
     */
    @PostMapping("/stock-in")
    @RequireAdmin
    @ValidateParams
    public ResponseEntity<?> stockIn(@RequestBody @Valid InventoryStockRequest request) {
        try {
            logger.debug("开始入库操作，请求参数：{}", request);
            Inventory inventory = inventoryService.stockIn(request);
            logger.debug("入库操作成功，商品ID：{}", request.getProductId());
            
            String successMessage = messageSource.getMessage(
                "success.inventory.stock.in", null, LocaleContextHolder.getLocale());
            return ResponseEntity.ok(Map.of("message", successMessage, "inventory", inventory));
        } catch (IllegalArgumentException e) {
            logger.warn("入库操作失败：{}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("入库操作失败：{}", e.getMessage(), e);
            String errorMessage = messageSource.getMessage(
                "error.system.internal", null, LocaleContextHolder.getLocale());
            return ResponseEntity.internalServerError().body(new ErrorResponse(errorMessage));
        }
    }

    /**
     * 出库操作
     */
    @PostMapping("/stock-out")
    @RequireAdmin
    @ValidateParams
    public ResponseEntity<?> stockOut(@RequestBody @Valid InventoryStockRequest request) {
        try {
            logger.debug("开始出库操作，请求参数：{}", request);
            Inventory inventory = inventoryService.stockOut(request);
            logger.debug("出库操作成功，商品ID：{}", request.getProductId());
            
            String successMessage = messageSource.getMessage(
                "success.inventory.stock.out", null, LocaleContextHolder.getLocale());
            return ResponseEntity.ok(Map.of("message", successMessage, "inventory", inventory));
        } catch (IllegalArgumentException e) {
            logger.warn("出库操作失败：{}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("出库操作失败：{}", e.getMessage(), e);
            String errorMessage = messageSource.getMessage(
                "error.system.internal", null, LocaleContextHolder.getLocale());
            return ResponseEntity.internalServerError().body(new ErrorResponse(errorMessage));
        }
    }

    /**
     * 库存调整
     */
    @PutMapping("/adjust")
    @RequireAdmin
    @ValidateParams
    public ResponseEntity<?> adjustStock(@RequestBody @Valid InventoryAdjustRequest request) {
        try {
            logger.debug("开始库存调整，请求参数：{}", request);
            Inventory inventory = inventoryService.adjustStock(request);
            logger.debug("库存调整成功，商品ID：{}", request.getProductId());
            
            String successMessage = messageSource.getMessage(
                "success.inventory.adjust", null, LocaleContextHolder.getLocale());
            return ResponseEntity.ok(Map.of("message", successMessage, "inventory", inventory));
        } catch (IllegalArgumentException e) {
            logger.warn("库存调整失败：{}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("库存调整失败：{}", e.getMessage(), e);
            String errorMessage = messageSource.getMessage(
                "error.system.internal", null, LocaleContextHolder.getLocale());
            return ResponseEntity.internalServerError().body(new ErrorResponse(errorMessage));
        }
    }

    /**
     * 更新库存设置
     */
    @PutMapping("/{inventoryId}/settings")
    @RequireAdmin
    public ResponseEntity<?> updateInventorySettings(@PathVariable Long inventoryId,
                                                    @RequestParam(required = false) Integer minStock,
                                                    @RequestParam(required = false) Integer maxStock) {
        try {
            logger.debug("开始更新库存设置，库存ID：{}，最小库存：{}，最大库存：{}", inventoryId, minStock, maxStock);
            Inventory inventory = inventoryService.updateInventorySettings(inventoryId, minStock, maxStock);
            logger.debug("库存设置更新成功，库存ID：{}", inventoryId);
            
            String successMessage = messageSource.getMessage(
                "success.inventory.settings.updated", null, LocaleContextHolder.getLocale());
            return ResponseEntity.ok(Map.of("message", successMessage, "inventory", inventory));
        } catch (IllegalArgumentException e) {
            logger.warn("更新库存设置失败：{}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("更新库存设置失败：{}", e.getMessage(), e);
            String errorMessage = messageSource.getMessage(
                "error.system.internal", null, LocaleContextHolder.getLocale());
            return ResponseEntity.internalServerError().body(new ErrorResponse(errorMessage));
        }
    }

    /**
     * 分页查询库存变更记录
     */
    @GetMapping("/records")
    @ValidateParams
    public ResponseEntity<?> listInventoryRecords(@Valid InventoryRecordPageRequest request) {
        try {
            logger.debug("开始查询库存变更记录，请求参数：{}", request);
            return ResponseEntity.ok(inventoryService.listInventoryRecords(request));
        } catch (IllegalArgumentException e) {
            logger.warn("查询库存变更记录失败：{}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("查询库存变更记录失败：{}", e.getMessage(), e);
            String errorMessage = messageSource.getMessage(
                "error.system.internal", null, LocaleContextHolder.getLocale());
            return ResponseEntity.internalServerError().body(new ErrorResponse(errorMessage));
        }
    }

    /**
     * 获取库存统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getInventoryStats() {
        try {
            logger.debug("开始查询库存统计信息");
            return ResponseEntity.ok(inventoryService.getInventoryStats());
        } catch (Exception e) {
            logger.error("查询库存统计信息失败：{}", e.getMessage(), e);
            String errorMessage = messageSource.getMessage(
                "error.system.internal", null, LocaleContextHolder.getLocale());
            return ResponseEntity.internalServerError().body(new ErrorResponse(errorMessage));
        }
    }

    /**
     * 检查库存是否充足
     */
    @GetMapping("/check")
    public ResponseEntity<?> checkStock(@RequestParam Long productId, @RequestParam Integer quantity) {
        try {
            logger.debug("开始检查库存，商品ID：{}，所需数量：{}", productId, quantity);
            boolean isEnough = inventoryService.checkStock(productId, quantity);
            return ResponseEntity.ok(Map.of("productId", productId, "quantity", quantity, "isEnough", isEnough));
        } catch (Exception e) {
            logger.error("检查库存失败：{}", e.getMessage(), e);
            String errorMessage = messageSource.getMessage(
                "error.system.internal", null, LocaleContextHolder.getLocale());
            return ResponseEntity.internalServerError().body(new ErrorResponse(errorMessage));
        }
    }

    /**
     * 获取低库存商品数量
     */
    @GetMapping("/low-stock/count")
    public ResponseEntity<?> getLowStockCount() {
        try {
            logger.debug("开始查询低库存商品数量");
            long count = inventoryService.getLowStockCount();
            return ResponseEntity.ok(Map.of("lowStockCount", count));
        } catch (Exception e) {
            logger.error("查询低库存商品数量失败：{}", e.getMessage(), e);
            String errorMessage = messageSource.getMessage(
                "error.system.internal", null, LocaleContextHolder.getLocale());
            return ResponseEntity.internalServerError().body(new ErrorResponse(errorMessage));
        }
    }
}