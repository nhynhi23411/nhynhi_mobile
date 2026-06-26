package com.nhynhi.store.model;

import java.io.Serializable;

/** Danh mục sản phẩm — khớp node "categories" trên Firebase. */
public class SCategory implements Serializable {
    private String id;            // = key (CAT001...)
    private String categoryName;
    private String description;

    public SCategory() { }

    public SCategory(String id, String categoryName, String description) {
        this.id = id;
        this.categoryName = categoryName;
        this.description = description;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
