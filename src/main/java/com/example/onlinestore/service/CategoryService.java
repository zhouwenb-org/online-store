package com.example.onlinestore.service;

import com.example.onlinestore.bean.Category;

import java.util.List;

public interface CategoryService {
    boolean isRootCategory(Long categoryId);

    List<Category> getRootCategories();
}