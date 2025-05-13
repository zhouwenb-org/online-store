package com.example.onlinestore.bean;

import org.apache.commons.collections.CollectionUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

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
    private Set<Long> children;

    // 排序权重
    private Integer weight;

    public boolean hasChildren() {
        return CollectionUtils.isNotEmpty(children);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Set<Long> getChildren() {
        return children;
    }

    public void setChildren(Set<Long> children) {
        this.children = children;
    }


    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }
}
