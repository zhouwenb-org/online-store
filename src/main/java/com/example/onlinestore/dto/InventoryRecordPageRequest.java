package com.example.onlinestore.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import com.example.onlinestore.model.InventoryRecord;

public class InventoryRecordPageRequest {
    @Min(value = 1, message = "error.page.number.min")
    private int pageNum = 1;

    @Min(value = 1, message = "error.page.size.min")
    @Max(value = 100, message = "error.page.size.max")
    private int pageSize = 10;

    private Long productId;
    private String productName;
    private InventoryRecord.OperationType operationType;
    private Long operatorId;

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public InventoryRecord.OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(InventoryRecord.OperationType operationType) {
        this.operationType = operationType;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    @Override
    public String toString() {
        return "InventoryRecordPageRequest{" +
                "pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", operationType=" + operationType +
                ", operatorId=" + operatorId +
                '}';
    }
}