package com.example.onlinestore.enums;

public enum AttributeValueType {
    TEXT(0),
    NUMBER(1),
    BOOLEAN(2),
    ;

    private final int name;
    AttributeValueType(int name) {
        this.name = name;
    }
    public int getName() {
        return name;
    }
}
