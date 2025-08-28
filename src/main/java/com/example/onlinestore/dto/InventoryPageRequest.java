package com.example.onlinestore.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

public class InventoryPageRequest {
    @Min(value = 1, message = "error.page.number.min")
    private int pageNum = 1;

    @Min(value = 1, message = "error.page.size.min")
    @Max(value = 100, message = "error.page.size.max")
    private int pageSize = 10;

    private String productName;
    private String category;
    private Boolean lowStockOnly = false;

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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Boolean getLowStockOnly() {
        return lowStockOnly;
    }

    public void setLowStockOnly(Boolean lowStockOnly) {
        this.lowStockOnly = lowStockOnly;
    }

    @Override
    public String toString() {
        return "InventoryPageRequest{" +
                "pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                ", productName='" + productName + '\'' +
                ", category='" + category + '\'' +
                ", lowStockOnly=" + lowStockOnly +
                '}';
    }
}