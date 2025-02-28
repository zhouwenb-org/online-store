package com.example.onlinestore.enums;

public enum AttributeValueType {
    TEXT(1),
    NUMBER(2),
    BOOLEAN(3),
    DATE(4),
    DATETIME(5),
    TIME(6),
    IMAGE(7),
    FILE(8),
    LINK(9),
    RADIO(10),
    CHECKBOX(11),
    SELECT(12),
    MULTISELECT(13),
    TEXTAREA(14),
    RICHTEXT(15),
    COLOR(16),
    FONT(17),
    SIZE(18),

    private int name;
    AttributeValueType(int name) {
        this.name = name;
    }
    public int getName() {
        return name;
    }
}
