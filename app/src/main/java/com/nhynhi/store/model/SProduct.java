package com.nhynhi.store.model;

import java.io.Serializable;

/** Sản phẩm — khớp node "products" trên Firebase. */
public class SProduct implements Serializable {
    private String id;            // = key (PROD001...)
    private String productName;
    private String categoryId;
    private String imageUrl;
    private double price;
    private int stock;
    private boolean active = true;

    public SProduct() { }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
