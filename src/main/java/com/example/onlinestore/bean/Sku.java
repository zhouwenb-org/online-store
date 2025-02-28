package com.example.onlinestore.bean;

import java.io.Serial;
import java.io.Serializable;

public class Sku implements Serializable {
    @Serial
    private static final long serialVersionUID = -3847863969574874034L;

    private Long id;
    private Long itemId;
    private String name;
    private String description;
}
