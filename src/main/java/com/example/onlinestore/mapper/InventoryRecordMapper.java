package com.example.onlinestore.mapper;

import com.example.onlinestore.model.InventoryRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface InventoryRecordMapper {
    
    /**
     * 插入库存变更记录
     */
    void insertInventoryRecord(InventoryRecord record);
    
    /**
     * 根据商品ID查询库存变更记录
     */
    List<InventoryRecord> findByProductId(@Param("productId") Long productId,
                                         @Param("offset") int offset,
                                         @Param("limit") int limit);
    
    /**
     * 分页查询库存变更记录
     */
    List<InventoryRecord> findWithPagination(@Param("productId") Long productId,
                                            @Param("productName") String productName,
                                            @Param("operationType") String operationType,
                                            @Param("operatorId") Long operatorId,
                                            @Param("offset") int offset,
                                            @Param("limit") int limit);
    
    /**
     * 统计库存变更记录总数
     */
    long countTotal(@Param("productId") Long productId,
                   @Param("productName") String productName,
                   @Param("operationType") String operationType,
                   @Param("operatorId") Long operatorId);
    
    /**
     * 查询商品的最近库存变更记录
     */
    List<InventoryRecord> findRecentByProductId(@Param("productId") Long productId,
                                               @Param("limit") int limit);
    
    /**
     * 根据操作人查询记录
     */
    List<InventoryRecord> findByOperatorId(@Param("operatorId") Long operatorId,
                                          @Param("offset") int offset,
                                          @Param("limit") int limit);
    
    /**
     * 查询所有记录
     */
    List<InventoryRecord> findAll();
}