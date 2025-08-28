package com.example.onlinestore.model;

import java.time.LocalDateTime;

public class InventoryRecord {
    private Long id;
    private Long productId;
    private OperationType operationType;
    private Integer quantity;
    private Integer beforeStock;
    private Integer afterStock;
    private String reason;
    private Long operatorId;
    private LocalDateTime operatedAt;

    public enum OperationType {
        IN("入库"),
        OUT("出库"), 
        ADJUST("调整");

        private final String description;

        OperationType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

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

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getBeforeStock() {
        return beforeStock;
    }

    public void setBeforeStock(Integer beforeStock) {
        this.beforeStock = beforeStock;
    }

    public Integer getAfterStock() {
        return afterStock;
    }

    public void setAfterStock(Integer afterStock) {
        this.afterStock = afterStock;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public LocalDateTime getOperatedAt() {
        return operatedAt;
    }

    public void setOperatedAt(LocalDateTime operatedAt) {
        this.operatedAt = operatedAt;
    }

    @Override
    public String toString() {
        return "InventoryRecord{" +
                "id=" + id +
                ", productId=" + productId +
                ", operationType=" + operationType +
                ", quantity=" + quantity +
                ", beforeStock=" + beforeStock +
                ", afterStock=" + afterStock +
                ", reason='" + reason + '\'' +
                ", operatorId=" + operatorId +
                ", operatedAt=" + operatedAt +
                '}';
    }
}