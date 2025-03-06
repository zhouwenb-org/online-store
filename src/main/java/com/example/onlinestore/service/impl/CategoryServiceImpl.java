package com.example.onlinestore.service.impl;

import com.alibaba.nacos.shaded.com.google.common.collect.Maps;
import com.example.onlinestore.bean.Category;
import com.example.onlinestore.entity.CategoryEntity;
import com.example.onlinestore.mapper.CategoryMapper;
import com.example.onlinestore.service.CategoryService;
import jakarta.annotation.PostConstruct;
import net.sf.cglib.beans.BeanCopier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Service
public class CategoryServiceImpl implements CategoryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryServiceImpl.class);

    private static final Object LOAD_LOCKER = new Object();
    //一级类目列表
    private Set<Long> rootCategories = new HashSet<>();

    private final ScheduledExecutorService scheduleExecutorService = Executors.newScheduledThreadPool(1);

    private final ExecutorService executorService = Executors.newFixedThreadPool(1, Thread::new);

    private final Map<Long, Category> categoryMap = Maps.newConcurrentMap();

    @Autowired
    private CategoryMapper categoryMapper;

    @PostConstruct
    private void init(){
        scheduleExecutorService.scheduleAtFixedRate(this::loadCategory, 3, 1, java.util.concurrent.TimeUnit.MINUTES);

        executorService.submit(() -> {
            try {
                LOGGER.info("Start to load category when server startup");
                loadCategory();
            } catch (Throwable t) {
                LOGGER.error("Load category failed", t);
            }
        });

    }


    @Override
    public boolean isRootCategory(Long categoryId) {
        if (categoryMap.containsKey(categoryId)) {
            return Category.ROOT_CATEGORY_PARENT_ID.equals(categoryMap.get(categoryId).getParentId());
        }
        return false;
    }

    @Override
    public List<Category> getRootCategories() {
        if (!rootCategories.isEmpty()) {
            return rootCategories.stream().map(categoryMap::get).toList();
        }
        return List.of();
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
                        if (Objects.equals(value.getParentId(), Category.ROOT_CATEGORY_PARENT_ID)) {
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
                LOGGER.error("Load category failed", t);
            }
        }

        LOGGER.info("Complete to load category.");
    }

}
