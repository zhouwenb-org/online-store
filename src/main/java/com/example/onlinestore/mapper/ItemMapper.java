package com.example.onlinestore.mapper;

import com.example.onlinestore.entity.ItemEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ItemMapper {
    void insertItem(ItemEntity item);
    ItemEntity findById(Long id);
    void updateItem(ItemEntity item);
    void deleteItem(Long id);
    List<ItemEntity> findAllWithPagination(@Param("offset") int offset, @Param("limit") int limit);
}
