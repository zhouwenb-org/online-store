package com.example.onlinestore.dto;

import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

public class UpdateProductRequest {
    private String name;
    
    private String category;
    
    @DecimalMin(value = "0.01", message = "error.product.price.min")
    private BigDecimal price;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * 检查是否有任何字段需要更新
     * @return true如果至少有一个字段不为null
     */
    public boolean hasUpdates() {
        return name != null || category != null || price != null;
    }

    @Override
    public String toString() {
        return "UpdateProductRequest{" +
                "name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", price=" + price +
                '}';
    }
}