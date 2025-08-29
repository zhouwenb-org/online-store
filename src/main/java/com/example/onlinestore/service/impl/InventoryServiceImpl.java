package com.example.onlinestore.service.impl;

import com.example.onlinestore.context.UserContext;
import com.example.onlinestore.dto.*;
import com.example.onlinestore.mapper.InventoryMapper;
import com.example.onlinestore.mapper.InventoryRecordMapper;
import com.example.onlinestore.mapper.ProductMapper;
import com.example.onlinestore.model.Inventory;
import com.example.onlinestore.model.InventoryRecord;
import com.example.onlinestore.model.Product;
import com.example.onlinestore.model.User;
import com.example.onlinestore.service.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InventoryServiceImpl implements InventoryService {
    
    private static final Logger logger = LoggerFactory.getLogger(InventoryServiceImpl.class);
    
    @Autowired
    private InventoryMapper inventoryMapper;
    
    @Autowired
    private InventoryRecordMapper inventoryRecordMapper;
    
    @Autowired
    private ProductMapper productMapper;

    @Override
    @Transactional
    public Inventory createInventory(Long productId, Integer initialStock, Integer minStock, Integer maxStock) {
        logger.info("开始创建库存，商品ID：{}，初始库存：{}", productId, initialStock);
        
        if (productId == null) {
            throw new IllegalArgumentException("商品ID不能为空");
        }
        
        // 检查商品是否存在
        Product product = productMapper.findById(productId);
        if (product == null) {
            throw new IllegalArgumentException("商品不存在，ID：" + productId);
        }
        
        // 检查库存是否已存在
        Inventory existingInventory = inventoryMapper.findByProductId(productId);
        if (existingInventory != null) {
            throw new IllegalArgumentException("商品库存已存在，ID：" + productId);
        }
        
        // 创建库存记录
        Inventory inventory = new Inventory();
        inventory.setProductId(productId);
        inventory.setCurrentStock(initialStock != null ? initialStock : 0);
        inventory.setMinStock(minStock != null ? minStock : 0);
        inventory.setMaxStock(maxStock != null ? maxStock : 1000);
        inventory.setCreatedAt(LocalDateTime.now());
        inventory.setUpdatedAt(LocalDateTime.now());
        
        inventoryMapper.insertInventory(inventory);
        
        // 记录库存变更（如果有初始库存）
        if (initialStock != null && initialStock > 0) {
            recordInventoryChange(productId, InventoryRecord.OperationType.IN, 
                                initialStock, 0, initialStock, "初始库存");
        }
        
        logger.info("库存创建成功，商品：{}，初始库存：{}", product.getName(), initialStock);
        return inventory;
    }

    @Override
    public Inventory getInventoryByProductId(Long productId) {
        logger.debug("查询商品库存，商品ID：{}", productId);
        
        if (productId == null) {
            throw new IllegalArgumentException("商品ID不能为空");
        }
        
        Inventory inventory = inventoryMapper.findByProductId(productId);
        if (inventory == null) {
            throw new IllegalArgumentException("库存不存在，商品ID：" + productId);
        }
        
        return inventory;
    }

    @Override
    public PageResponse<InventoryResponse> listInventories(InventoryPageRequest request) {
        logger.info("开始查询库存列表，请求参数：{}", request);
        
        // 计算分页参数
        int offset = (request.getPageNum() - 1) * request.getPageSize();
        int limit = request.getPageSize();
        
        // 查询库存列表
        List<InventoryResponse> inventories = inventoryMapper.findInventoryWithProducts(
            request.getProductName(),
            request.getCategory(),
            request.getLowStockOnly(),
            offset,
            limit
        );
        
        // 统计总数
        long total = inventoryMapper.countInventory(
            request.getProductName(),
            request.getCategory(),
            request.getLowStockOnly()
        );
        
        logger.info("查询到 {} 条库存记录，总数：{}", inventories.size(), total);
        
        // 构建响应
        PageResponse<InventoryResponse> response = new PageResponse<>();
        response.setRecords(inventories);
        response.setTotal(total);
        response.setPageNum(request.getPageNum());
        response.setPageSize(request.getPageSize());
        
        return response;
    }

    @Override
    @Transactional
    public Inventory stockIn(InventoryStockRequest request) {
        logger.info("开始入库操作，请求参数：{}", request);
        
        validateStockRequest(request);
        
        Inventory inventory = getInventoryByProductId(request.getProductId());
        int beforeStock = inventory.getCurrentStock();
        int afterStock = beforeStock + request.getQuantity();
        
        // 更新库存
        inventoryMapper.updateStock(request.getProductId(), afterStock, LocalDateTime.now());
        
        // 记录库存变更
        recordInventoryChange(request.getProductId(), InventoryRecord.OperationType.IN,
                            request.getQuantity(), beforeStock, afterStock, request.getReason());
        
        inventory.setCurrentStock(afterStock);
        inventory.setUpdatedAt(LocalDateTime.now());
        
        logger.info("入库操作完成，商品ID：{}，入库数量：{}，库存变化：{} -> {}", 
                   request.getProductId(), request.getQuantity(), beforeStock, afterStock);
        
        return inventory;
    }

    @Override
    @Transactional
    public Inventory stockOut(InventoryStockRequest request) {
        logger.info("开始出库操作，请求参数：{}", request);
        
        validateStockRequest(request);
        
        Inventory inventory = getInventoryByProductId(request.getProductId());
        int beforeStock = inventory.getCurrentStock();
        
        // 检查库存是否充足
        if (beforeStock < request.getQuantity()) {
            throw new IllegalArgumentException("库存不足，当前库存：" + beforeStock + "，需要：" + request.getQuantity());
        }
        
        int afterStock = beforeStock - request.getQuantity();
        
        // 更新库存
        inventoryMapper.updateStock(request.getProductId(), afterStock, LocalDateTime.now());
        
        // 记录库存变更
        recordInventoryChange(request.getProductId(), InventoryRecord.OperationType.OUT,
                            request.getQuantity(), beforeStock, afterStock, request.getReason());
        
        inventory.setCurrentStock(afterStock);
        inventory.setUpdatedAt(LocalDateTime.now());
        
        logger.info("出库操作完成，商品ID：{}，出库数量：{}，库存变化：{} -> {}", 
                   request.getProductId(), request.getQuantity(), beforeStock, afterStock);
        
        return inventory;
    }

    @Override
    @Transactional
    public Inventory adjustStock(InventoryAdjustRequest request) {
        logger.info("开始库存调整，请求参数：{}", request);
        
        if (request == null || request.getProductId() == null || request.getNewStock() == null) {
            throw new IllegalArgumentException("调整请求参数不能为空");
        }
        
        Inventory inventory = getInventoryByProductId(request.getProductId());
        int beforeStock = inventory.getCurrentStock();
        int afterStock = request.getNewStock();
        int quantity = Math.abs(afterStock - beforeStock);
        
        // 更新库存
        inventoryMapper.updateStock(request.getProductId(), afterStock, LocalDateTime.now());
        
        // 记录库存变更
        recordInventoryChange(request.getProductId(), InventoryRecord.OperationType.ADJUST,
                            quantity, beforeStock, afterStock, request.getReason());
        
        inventory.setCurrentStock(afterStock);
        inventory.setUpdatedAt(LocalDateTime.now());
        
        logger.info("库存调整完成，商品ID：{}，库存变化：{} -> {}", 
                   request.getProductId(), beforeStock, afterStock);
        
        return inventory;
    }

    @Override
    @Transactional
    public Inventory updateInventorySettings(Long inventoryId, Integer minStock, Integer maxStock) {
        logger.info("开始更新库存设置，库存ID：{}，最小库存：{}，最大库存：{}", inventoryId, minStock, maxStock);
        
        if (inventoryId == null) {
            throw new IllegalArgumentException("库存ID不能为空");
        }
        
        Inventory inventory = inventoryMapper.findById(inventoryId);
        if (inventory == null) {
            throw new IllegalArgumentException("库存不存在，ID：" + inventoryId);
        }
        
        // 更新库存设置
        inventoryMapper.updateInventorySettings(inventoryId, minStock, maxStock, LocalDateTime.now());
        
        // 更新对象属性
        if (minStock != null) {
            inventory.setMinStock(minStock);
        }
        if (maxStock != null) {
            inventory.setMaxStock(maxStock);
        }
        inventory.setUpdatedAt(LocalDateTime.now());
        
        logger.info("库存设置更新完成，库存ID：{}", inventoryId);
        
        return inventory;
    }

    @Override
    public PageResponse<InventoryRecord> listInventoryRecords(InventoryRecordPageRequest request) {
        logger.info("开始查询库存变更记录，请求参数：{}", request);
        
        // 计算分页参数
        int offset = (request.getPageNum() - 1) * request.getPageSize();
        int limit = request.getPageSize();
        
        // 查询记录列表
        String operationType = request.getOperationType() != null ? request.getOperationType().name() : null;
        List<InventoryRecord> records = inventoryRecordMapper.findWithPagination(
            request.getProductId(),
            request.getProductName(),
            operationType,
            request.getOperatorId(),
            offset,
            limit
        );
        
        // 统计总数
        long total = inventoryRecordMapper.countTotal(
            request.getProductId(),
            request.getProductName(),
            operationType,
            request.getOperatorId()
        );
        
        logger.info("查询到 {} 条库存变更记录，总数：{}", records.size(), total);
        
        // 构建响应
        PageResponse<InventoryRecord> response = new PageResponse<>();
        response.setRecords(records);
        response.setTotal(total);
        response.setPageNum(request.getPageNum());
        response.setPageSize(request.getPageSize());
        
        return response;
    }

    @Override
    public InventoryStatsResponse getInventoryStats() {
        logger.info("开始查询库存统计信息");
        
        InventoryStatsResponse stats = new InventoryStatsResponse();
        
        // 查询总商品数（有库存记录的）
        long totalProducts = inventoryMapper.countInventory(null, null, null);
        stats.setTotalProducts(totalProducts);
        
        // 查询低库存商品数（库存不足但非零）
        long lowStockProducts = inventoryMapper.countLowStockProducts();
        stats.setLowStockProducts(lowStockProducts);
        
        // 查询零库存商品数
        long outOfStockProducts = inventoryMapper.countOutOfStockProducts();
        stats.setOutOfStockProducts(outOfStockProducts);
        
        // 计算正常库存商品数
        long normalStockProducts = totalProducts - lowStockProducts - outOfStockProducts;
        stats.setNormalStockProducts(Math.max(0, normalStockProducts));
        
        logger.info("库存统计信息：{}", stats);
        
        return stats;
    }

    @Override
    public boolean checkStock(Long productId, Integer requiredQuantity) {
        if (productId == null || requiredQuantity == null) {
            return false;
        }
        
        try {
            Inventory inventory = inventoryMapper.findByProductId(productId);
            return inventory != null && inventory.hasEnoughStock(requiredQuantity);
        } catch (Exception e) {
            logger.warn("检查库存时发生异常，商品ID：{}，所需数量：{}", productId, requiredQuantity, e);
            return false;
        }
    }

    @Override
    public long getLowStockCount() {
        return inventoryMapper.countLowStockProducts();
    }

    /**
     * 验证库存操作请求
     */
    private void validateStockRequest(InventoryStockRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("库存操作请求不能为空");
        }
        if (request.getProductId() == null) {
            throw new IllegalArgumentException("商品ID不能为空");
        }
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new IllegalArgumentException("操作数量必须大于0");
        }
    }

    /**
     * 记录库存变更
     */
    private void recordInventoryChange(Long productId, InventoryRecord.OperationType operationType,
                                     Integer quantity, Integer beforeStock, Integer afterStock, String reason) {
        try {
            InventoryRecord record = new InventoryRecord();
            record.setProductId(productId);
            record.setOperationType(operationType);
            record.setQuantity(quantity);
            record.setBeforeStock(beforeStock);
            record.setAfterStock(afterStock);
            record.setReason(reason != null ? reason : operationType.getDescription());
            
            // 获取当前操作用户
            User currentUser = UserContext.getCurrentUser();
            if (currentUser != null) {
                record.setOperatorId(currentUser.getId());
            }
            
            record.setOperatedAt(LocalDateTime.now());
            
            inventoryRecordMapper.insertInventoryRecord(record);
            
            logger.debug("库存变更记录已保存，商品ID：{}，操作类型：{}", productId, operationType);
        } catch (Exception e) {
            logger.error("保存库存变更记录失败", e);
            // 不抛异常，避免影响主业务流程
        }
    }
}