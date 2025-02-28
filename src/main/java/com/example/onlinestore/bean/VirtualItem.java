package com.example.onlinestore.bean;

import java.io.Serial;

public class VirtualItem extends Item {
    @Serial
    private static final long serialVersionUID = -5025900218621448685L;

    private String name;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
