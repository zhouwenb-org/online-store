package com.example.onlinestore.bean;

import java.io.Serial;
import java.io.Serializable;

public class Item implements Serializable {
    @Serial
    private static final long serialVersionUID = 8328093958488219105L;
    private Long id;
    private Long categoryId;
    private String name;
    private String description;
    private String image;
    private Long skuId;
    private String secondareyName;
    private String pingJia;

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

    public String getSecondareyName() {
        return secondareyName;
    }

    public void setSecondareyName(String secondareyName) {
        this.secondareyName = secondareyName;
    }

    public String getPingJia() {
        return pingJia;
    }

    public void setPingJia(String pingJia) {
        this.pingJia = pingJia;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }
}
