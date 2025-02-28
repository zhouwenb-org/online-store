package com.example.onlinestore.entity;

import java.io.Serial;
import java.io.Serializable;

// 商品表
public class ItemEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 2418723127713742396L;

    private Long id;
    private Long categoryId;
    private String name;
    private String description;
    private String image;
    private String skuId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }
}
