package com.example.onlinestore.mapper;

import com.example.onlinestore.bean.Category;
import com.example.onlinestore.entity.CategoryEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CategoryMapper {
    // 根据id查询分类
    CategoryEntity findById(Long id);

    // 查询所有分类
    List<CategoryEntity> FindAllCategories(@Param("offset") int offset, @Param("limit") int limit);
}
