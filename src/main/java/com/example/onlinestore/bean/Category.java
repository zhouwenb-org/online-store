package com.example.onlinestore.bean;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class Category implements Serializable {
    @Serial
    private static final long serialVersionUID = -4454913370248394676L;

    // 类目ID
    private Long id;

    // 父类目ID=0时，代表的是一级类目
    private Long parentId;

    // 类目名称
    private String name;

    // 描述
    private String description;

    // 是否可见
    private Boolean visible;

    // 子类目
    private List<Category> children;
}
