package com.example.onlinestore.service.impl;

import com.alibaba.nacos.shaded.com.google.common.collect.Maps;
import com.example.onlinestore.bean.Category;
import com.example.onlinestore.entity.CategoryEntity;
import com.example.onlinestore.mapper.CategoryMapper;
import com.example.onlinestore.service.CategoryService;
import net.sf.cglib.beans.BeanCopier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CategoryServiceImpl implements CategoryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryServiceImpl.class);

    private static final Object LOAD_LOCKER = new Object();
    //一级技术栈列表
    private Set<Long> rootCategories = new HashSet<>();

    private Map<Long, Category> categoryMap = Maps.newConcurrentMap();

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public Category isRouteCategory(Long categoryId) {
        return null;
    }

    private void loadCategory() {
        LOGGER.info("Start to load category.");
        synchronized (LOAD_LOCKER) {
            int offset  = 1;
            int limit = 1000;
            try {
                List<CategoryEntity> allCategories = categoryMapper.FindAllCategories(offset, limit);
                BeanCopier beanCopier = BeanCopier.create(CategoryEntity.class, Category.class, false);
                Map<Long, Set<Long>> parentCategoryMap = new HashMap<>();
                Set<Long> newCategoryIds = new HashSet<>();
                for (CategoryEntity categoryEntity : allCategories) {
                    newCategoryIds.add(categoryEntity.getId());

                    Category category = new Category();
                    beanCopier.copy(categoryEntity, category, null);
                    categoryMap.put(categoryEntity.getId(), category);

                    long parentId = categoryEntity.getParentId();
                    if (parentId > category.getParentId()) {
                        Set<Long> childCategories = parentCategoryMap.computeIfAbsent(parentId, k -> new HashSet<>());
                        childCategories.add(categoryEntity.getId());
                    }
                }
                //
                Set<Long> newRoots = new HashSet<>();
                Iterator<Map.Entry<Long, Category>> it = categoryMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<Long, Category> entry = it.next();
                    long key = entry.getKey();
                    if (newCategoryIds.contains(key)) {
                        Category value = entry.getValue();
                        if (value.getParentId() == value.ROOT_CATEGORY_PARENT_ID) {
                            newRoots.add(key);
                        }
                        if (!value.getLeaf()) {
                            value.setChildren(parentCategoryMap.get(key));
                        }

                    } else {
                        it.remove();
                    }
                }

                rootCategories = newRoots;
            } catch (Throwable t) {
                LOGGER.error("Load stack failed", t);
            }
        }

        LOGGER.info("Complete to load stack.");
    }

}
