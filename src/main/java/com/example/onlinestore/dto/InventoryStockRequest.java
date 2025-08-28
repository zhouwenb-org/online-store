package com.example.onlinestore.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class InventoryStockRequest {
    @NotNull(message = "error.inventory.product.id.empty")
    private Long productId;

    @NotNull(message = "error.inventory.quantity.empty")
    @Min(value = 1, message = "error.inventory.quantity.min")
    private Integer quantity;

    private String reason;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "InventoryStockRequest{" +
                "productId=" + productId +
                ", quantity=" + quantity +
                ", reason='" + reason + '\'' +
                '}';
    }
}