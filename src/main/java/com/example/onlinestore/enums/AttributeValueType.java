package com.example.onlinestore.enums;

public enum AttributeValueType {
    TEXT(0),
    NUMBER(1),
    BOOLEAN(2),
    MULTI_OPTIONS(3),
    SINGLE_OPTION(4),
    ;
    private final int valueType;
    AttributeValueType(int value) {
        this.valueType = value;
    }
    public int getValueType() {
        return valueType;
    }
}
