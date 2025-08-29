package com.example.onlinestore.dto;

public class InventoryStatsResponse {
    private long totalProducts;
    private long lowStockProducts;
    private long outOfStockProducts;
    private long normalStockProducts;

    public long getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(long totalProducts) {
        this.totalProducts = totalProducts;
    }

    public long getLowStockProducts() {
        return lowStockProducts;
    }

    public void setLowStockProducts(long lowStockProducts) {
        this.lowStockProducts = lowStockProducts;
    }

    public long getOutOfStockProducts() {
        return outOfStockProducts;
    }

    public void setOutOfStockProducts(long outOfStockProducts) {
        this.outOfStockProducts = outOfStockProducts;
    }

    public long getNormalStockProducts() {
        return normalStockProducts;
    }

    public void setNormalStockProducts(long normalStockProducts) {
        this.normalStockProducts = normalStockProducts;
    }

    @Override
    public String toString() {
        return "InventoryStatsResponse{" +
                "totalProducts=" + totalProducts +
                ", lowStockProducts=" + lowStockProducts +
                ", outOfStockProducts=" + outOfStockProducts +
                ", normalStockProducts=" + normalStockProducts +
                '}';
    }
}