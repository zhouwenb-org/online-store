package com.example.onlinestore.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

public class UserPageRequest {
    @Min(value = 1, message = "error.page.number.min")
    private int pageNum = 1;

    @Min(value = 1, message = "error.page.size.min")
    @Max(value = 100, message = "error.page.size.max")
    private int pageSize = 10;

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
} 