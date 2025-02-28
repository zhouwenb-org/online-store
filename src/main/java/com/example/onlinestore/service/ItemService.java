package com.example.onlinestore.service;

import com.example.onlinestore.bean.Item;
import com.example.onlinestore.entity.ItemEntity;

public interface ItemService {
    void addItem(String userId, Item item);
}
