package com.example.onlinestore.service;

import com.example.onlinestore.bean.Category;
import com.example.onlinestore.constants.Constants;
import com.example.onlinestore.entity.CategoryEntity;
import com.example.onlinestore.mapper.CategoryMapper;
import com.example.onlinestore.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private List<CategoryEntity> mockCategoryEntities;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        mockCategoryEntities = new ArrayList<>();
        
        // 创建根类目
        CategoryEntity rootCategory1 = createCategoryEntity(1L, "Electronics", 0L);
        CategoryEntity rootCategory2 = createCategoryEntity(2L, "Books", 0L);
        
        // 创建子类目
        CategoryEntity subCategory1 = createCategoryEntity(3L, "Phones", 1L);
        CategoryEntity subCategory2 = createCategoryEntity(4L, "Laptops", 1L);
        CategoryEntity subCategory3 = createCategoryEntity(5L, "Fiction", 2L);

        mockCategoryEntities.add(rootCategory1);
        mockCategoryEntities.add(rootCategory2);
        mockCategoryEntities.add(subCategory1);
        mockCategoryEntities.add(subCategory2);
        mockCategoryEntities.add(subCategory3);
    }

    @Test
    void whenIsRootCategory_thenReturnTrue() {
        // 准备测试数据
        setupCategoryCache();

        // 执行测试
        boolean result = categoryService.isRootCategory(1L);

        // 验证结果
        assertTrue(result);
    }

    @Test
    void whenIsRootCategoryForSubCategory_thenReturnFalse() {
        // 准备测试数据
        setupCategoryCache();

        // 执行测试
        boolean result = categoryService.isRootCategory(3L);

        // 验证结果
        assertFalse(result);
    }

    @Test
    void whenIsRootCategoryForNonExistentCategory_thenReturnFalse() {
        // 准备测试数据
        setupCategoryCache();

        // 执行测试
        boolean result = categoryService.isRootCategory(999L);

        // 验证结果
        assertFalse(result);
    }

    @Test
    void whenGetRootCategories_thenReturnRootCategoriesList() {
        // 准备测试数据
        setupCategoryCache();

        // 执行测试
        List<Category> result = categoryService.getRootCategories();

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(cat -> cat.getName().equals("Electronics")));
        assertTrue(result.stream().anyMatch(cat -> cat.getName().equals("Books")));
    }

    @Test
    void whenGetRootCategoriesWithEmptyCache_thenReturnEmptyList() {
        // 清空缓存
        ReflectionTestUtils.setField(categoryService, "rootCategories", new HashSet<>());

        // 执行测试
        List<Category> result = categoryService.getRootCategories();

        // 验证结果
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void whenGetCategoryById_thenReturnCategory() {
        // 准备测试数据
        setupCategoryCache();

        // 执行测试
        Category result = categoryService.getCategoryById(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals("Electronics", result.getName());
        assertEquals(0L, result.getParentId());
    }

    @Test
    void whenGetCategoryByIdForNonExistentCategory_thenReturnNull() {
        // 准备测试数据
        setupCategoryCache();

        // 执行测试
        Category result = categoryService.getCategoryById(999L);

        // 验证结果
        assertNull(result);
    }

    @Test
    void whenLoadCategoryFromMapper_thenCacheIsUpdated() {
        // 准备测试数据
        when(categoryMapper.FindAllCategories(anyInt(), anyInt())).thenReturn(mockCategoryEntities);

        // 通过反射调用私有方法 loadCategory
        try {
            var method = CategoryServiceImpl.class.getDeclaredMethod("loadCategory");
            method.setAccessible(true);
            method.invoke(categoryService);
        } catch (Exception e) {
            fail("Failed to invoke loadCategory method: " + e.getMessage());
        }

        // 验证结果
        assertFalse(categoryService.getRootCategories().isEmpty());
        assertNotNull(categoryService.getCategoryById(1L));
        assertNotNull(categoryService.getCategoryById(2L));
        
        // 验证mapper被调用
        verify(categoryMapper).FindAllCategories(1, 1000);
    }

    @Test
    void whenLoadCategoryWithException_thenHandleGracefully() {
        // 准备测试数据 - 模拟异常
        when(categoryMapper.FindAllCategories(anyInt(), anyInt()))
            .thenThrow(new RuntimeException("Database error"));

        // 通过反射调用私有方法 loadCategory
        try {
            var method = CategoryServiceImpl.class.getDeclaredMethod("loadCategory");
            method.setAccessible(true);
            method.invoke(categoryService);
        } catch (Exception e) {
            fail("Failed to invoke loadCategory method: " + e.getMessage());
        }

        // 验证异常被处理，不会影响应用运行
        assertTrue(categoryService.getRootCategories().isEmpty());
    }

    @Test
    void whenLoadCategoryWithHierarchy_thenChildrenAreSetCorrectly() {
        // 准备测试数据
        when(categoryMapper.FindAllCategories(anyInt(), anyInt())).thenReturn(mockCategoryEntities);

        // 通过反射调用私有方法 loadCategory
        try {
            var method = CategoryServiceImpl.class.getDeclaredMethod("loadCategory");
            method.setAccessible(true);
            method.invoke(categoryService);
        } catch (Exception e) {
            fail("Failed to invoke loadCategory method: " + e.getMessage());
        }

        // 验证父子关系
        Category electronics = categoryService.getCategoryById(1L);
        assertNotNull(electronics);
        assertTrue(electronics.hasChildren());
        assertTrue(electronics.getChildren().contains(3L)); // Phones
        assertTrue(electronics.getChildren().contains(4L)); // Laptops
        
        Category books = categoryService.getCategoryById(2L);
        assertNotNull(books);
        assertTrue(books.hasChildren());
        assertTrue(books.getChildren().contains(5L)); // Fiction
    }

    @Test
    void whenCategoryHasParentIdGreaterThanOwnParentId_thenHandleCorrectly() {
        // 这个测试用于覆盖特殊的父类目逻辑
        // 创建特殊的测试数据来覆盖第78行的条件: if (parentId > category.getParentId())
        List<CategoryEntity> specialEntities = new ArrayList<>();
        CategoryEntity entity1 = createCategoryEntity(1L, "Category1", 2L);
        CategoryEntity entity2 = createCategoryEntity(2L, "Category2", 1L);
        specialEntities.add(entity1);
        specialEntities.add(entity2);

        when(categoryMapper.FindAllCategories(anyInt(), anyInt())).thenReturn(specialEntities);

        // 通过反射调用私有方法 loadCategory
        try {
            var method = CategoryServiceImpl.class.getDeclaredMethod("loadCategory");
            method.setAccessible(true);
            method.invoke(categoryService);
        } catch (Exception e) {
            fail("Failed to invoke loadCategory method: " + e.getMessage());
        }

        // 验证特殊情况被处理
        assertNotNull(categoryService.getCategoryById(1L));
        assertNotNull(categoryService.getCategoryById(2L));
    }

    private void setupCategoryCache() {
        // 手动设置缓存数据
        Map<Long, Category> categoryMap = new HashMap<>();
        Set<Long> rootCategories = new HashSet<>();

        // 创建根类目
        Category rootCat1 = createCategory(1L, "Electronics", 0L);
        rootCat1.setChildren(Set.of(3L, 4L));
        categoryMap.put(1L, rootCat1);
        rootCategories.add(1L);

        Category rootCat2 = createCategory(2L, "Books", 0L);
        rootCat2.setChildren(Set.of(5L));
        categoryMap.put(2L, rootCat2);
        rootCategories.add(2L);

        // 创建子类目
        categoryMap.put(3L, createCategory(3L, "Phones", 1L));
        categoryMap.put(4L, createCategory(4L, "Laptops", 1L));
        categoryMap.put(5L, createCategory(5L, "Fiction", 2L));

        // 通过反射设置私有字段
        ReflectionTestUtils.setField(categoryService, "categoryMap", categoryMap);
        ReflectionTestUtils.setField(categoryService, "rootCategories", rootCategories);
    }

    private CategoryEntity createCategoryEntity(Long id, String name, Long parentId) {
        CategoryEntity entity = new CategoryEntity();
        entity.setId(id);
        entity.setName(name);
        entity.setParentId(parentId);
        entity.setVisible(true);
        entity.setStatus(1);
        entity.setWeight(1);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return entity;
    }

    private Category createCategory(Long id, String name, Long parentId) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        category.setParentId(parentId);
        category.setVisible(true);
        category.setWeight(1);
        return category;
    }
}