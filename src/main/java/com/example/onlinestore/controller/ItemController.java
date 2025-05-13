package com.example.onlinestore.controller;

import com.example.onlinestore.bean.Item;
import com.example.onlinestore.service.ItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/items")
public class ItemController {

    private final static Logger LOGGER = LoggerFactory.getLogger(ItemController.class);
    private long itemAccessCount = 0L;

    @Autowired
    private ItemService itemService;


    @GetMapping("/{itemId}")
    public Item getItem(@PathVariable long itemId) {
        itemAccessCount++;
        LOGGER.info("item access count: {}", itemAccessCount);
        return itemService.getItemById(itemId);
    }
}
