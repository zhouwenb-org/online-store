package com.example.onlinestore.service.impl;

import com.example.onlinestore.bean.Item;
import com.example.onlinestore.entity.ItemEntity;
import com.example.onlinestore.mapper.ItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ItemServiceImpl extends CommonItemService{

    @Autowired
    private ItemMapper itemMapper;

    @Override
    public void addItem(String userId, Item item) {

    }

    @Override
    public Item getItemById(long itemId) {
        ItemEntity itemEntity = itemMapper.findById(itemId);
        return convertToItem(itemEntity);
    }


    private ItemEntity buildItemEntity(String userId, String name, String description, String image, String secondaryName, String pingJia, Long skuId, Map<String, Map<String, String>> itemAttributes, Map<String, Map<String, String>> itemExtensions) {
        ItemEntity entity = new ItemEntity();
        entity.setName(name);
        entity.setDescription(description);
        entity.setImage(image);
        entity.setSecondaryName(secondaryName);
        entity.setSkuId(skuId);
        return entity;
    }

    protected ItemEntity convertToItemEntity(Item item) {
        ItemEntity entity = new ItemEntity();
        entity.setName(item.getName());
        entity.setDescription(item.getDescription());
        entity.setImage(item.getImage());
        entity.setSkuId(item.getSkuId());
        return entity;
    }
}
