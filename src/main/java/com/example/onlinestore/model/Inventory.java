package com.example.onlinestore.model;

import java.time.LocalDateTime;

public class Inventory {
    private Long id;
    private Long productId;
    private Integer currentStock;
    private Integer minStock;
    private Integer maxStock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }

    public Integer getMinStock() {
        return minStock;
    }

    public void setMinStock(Integer minStock) {
        this.minStock = minStock;
    }

    public Integer getMaxStock() {
        return maxStock;
    }

    public void setMaxStock(Integer maxStock) {
        this.maxStock = maxStock;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * 检查是否为低库存
     */
    public boolean isLowStock() {
        return currentStock != null && minStock != null && currentStock <= minStock;
    }

    /**
     * 检查库存是否充足
     */
    public boolean hasEnoughStock(Integer requiredQuantity) {
        return currentStock != null && requiredQuantity != null && currentStock >= requiredQuantity;
    }

    @Override
    public String toString() {
        return "Inventory{" +
                "id=" + id +
                ", productId=" + productId +
                ", currentStock=" + currentStock +
                ", minStock=" + minStock +
                ", maxStock=" + maxStock +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}