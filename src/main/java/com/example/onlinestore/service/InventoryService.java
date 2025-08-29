package com.example.onlinestore.service;

import com.example.onlinestore.dto.*;
import com.example.onlinestore.model.Inventory;
import com.example.onlinestore.model.InventoryRecord;

public interface InventoryService {
    
    /**
     * 创建商品库存
     */
    Inventory createInventory(Long productId, Integer initialStock, Integer minStock, Integer maxStock);
    
    /**
     * 根据商品ID获取库存信息
     */
    Inventory getInventoryByProductId(Long productId);
    
    /**
     * 分页查询库存列表
     */
    PageResponse<InventoryResponse> listInventories(InventoryPageRequest request);
    
    /**
     * 入库操作
     */
    Inventory stockIn(InventoryStockRequest request);
    
    /**
     * 出库操作
     */
    Inventory stockOut(InventoryStockRequest request);
    
    /**
     * 库存调整
     */
    Inventory adjustStock(InventoryAdjustRequest request);
    
    /**
     * 更新库存设置（最小/最大库存）
     */
    Inventory updateInventorySettings(Long inventoryId, Integer minStock, Integer maxStock);
    
    /**
     * 分页查询库存变更记录
     */
    PageResponse<InventoryRecord> listInventoryRecords(InventoryRecordPageRequest request);
    
    /**
     * 获取库存统计信息
     */
    InventoryStatsResponse getInventoryStats();
    
    /**
     * 检查库存是否充足
     */
    boolean checkStock(Long productId, Integer requiredQuantity);
    
    /**
     * 查询低库存商品数量
     */
    long getLowStockCount();
}