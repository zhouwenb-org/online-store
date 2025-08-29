package com.example.onlinestore.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class InventoryAdjustRequest {
    @NotNull(message = "error.inventory.product.id.empty")
    private Long productId;

    @NotNull(message = "error.inventory.new.stock.empty")
    @Min(value = 0, message = "error.inventory.new.stock.min")
    private Integer newStock;

    private String reason;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getNewStock() {
        return newStock;
    }

    public void setNewStock(Integer newStock) {
        this.newStock = newStock;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "InventoryAdjustRequest{" +
                "productId=" + productId +
                ", newStock=" + newStock +
                ", reason='" + reason + '\'' +
                '}';
    }
}