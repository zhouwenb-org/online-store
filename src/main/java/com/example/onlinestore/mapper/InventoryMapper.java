package com.example.onlinestore.mapper;

import com.example.onlinestore.dto.InventoryResponse;
import com.example.onlinestore.model.Inventory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface InventoryMapper {
    
    /**
     * 插入库存记录
     */
    void insertInventory(Inventory inventory);
    
    /**
     * 根据商品ID查找库存
     */
    Inventory findByProductId(@Param("productId") Long productId);
    
    /**
     * 根据ID查找库存
     */
    Inventory findById(@Param("id") Long id);
    
    /**
     * 更新库存数量
     */
    int updateStock(@Param("productId") Long productId, 
                   @Param("newStock") Integer newStock,
                   @Param("updatedAt") java.time.LocalDateTime updatedAt);
    
    /**
     * 批量更新库存设置
     */
    int updateInventorySettings(@Param("id") Long id,
                               @Param("minStock") Integer minStock,
                               @Param("maxStock") Integer maxStock,
                               @Param("updatedAt") java.time.LocalDateTime updatedAt);
    
    /**
     * 分页查询库存（带商品信息）
     */
    List<InventoryResponse> findInventoryWithProducts(@Param("productName") String productName,
                                                     @Param("category") String category,
                                                     @Param("lowStockOnly") Boolean lowStockOnly,
                                                     @Param("offset") int offset,
                                                     @Param("limit") int limit);
    
    /**
     * 统计库存总数
     */
    long countInventory(@Param("productName") String productName,
                       @Param("category") String category,
                       @Param("lowStockOnly") Boolean lowStockOnly);
    
    /**
     * 查询低库存商品数量
     */
    long countLowStockProducts();
    
    /**
     * 查询零库存商品数量
     */
    long countOutOfStockProducts();
    
    /**
     * 查询所有库存
     */
    List<Inventory> findAll();
    
    /**
     * 删除库存记录
     */
    int deleteByProductId(@Param("productId") Long productId);
}