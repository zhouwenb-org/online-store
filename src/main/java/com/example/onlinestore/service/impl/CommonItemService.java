package com.example.onlinestore.service.impl;

import com.example.onlinestore.bean.Item;
import com.example.onlinestore.entity.ItemEntity;
import com.example.onlinestore.service.ItemService;
import org.springframework.cglib.beans.BeanCopier;

public abstract class CommonItemService implements ItemService {
        protected Item convertToItem(ItemEntity itemEntity) {
            Item item = new Item();
            BeanCopier copier = BeanCopier.create(ItemEntity.class, Item.class, false);
            copier.copy(itemEntity, item, null);
            return item;
        }

        protected ItemEntity convertToItemEntity(Item item) {
            ItemEntity itemEntity = new ItemEntity();
            BeanCopier copier = BeanCopier.create(Item.class, ItemEntity.class, false);
            copier.copy(item, itemEntity, null);
            return itemEntity;
        }
}
