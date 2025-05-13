package com.example.onlinestore.service;

import com.example.onlinestore.bean.Category;

import java.util.List;

public interface CategoryService {

    // 判断是否为根类目
    boolean isRootCategory(Long categoryId);

    // 获取根类目
    List<Category> getRootCategories();

    // 根据id获取类目
    Category getCategoryById(Long categoryId);
}