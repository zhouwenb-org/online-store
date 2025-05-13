package com.example.onlinestore.enums;

public enum CategoryStatusEnum {
    ENABLE(0),
    DISABLE(1),
    ;

    private final int status;

    CategoryStatusEnum(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
