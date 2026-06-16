package com.nhynhi.models;

import java.io.Serializable;

public class Product implements Serializable {
    private String productID;
    private String productName;
    private double price;
    private int quantity;
    private double coupon;

    private double VAT;

    private String cateID;

    //constuctor
    public Product() {
    }

    public Product(String productID, String productName, double price, int quantity, double coupon, double VAT, String cateID) {
        this.productID = productID;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.coupon = coupon;
        this.VAT = VAT;
        this.cateID = cateID;
    }
    //getter setter
    public String getProductID() {
        return productID;
    }
    public void setProductID(String productID) {
        this.productID = productID;
    }
    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public double getCoupon() {
        return coupon;
    }
    public void setCoupon(double coupon) {
        this.coupon = coupon;
    }
    public double getVAT() {
        return VAT;
    }
    public void setVAT(double VAT) {
        this.VAT = VAT;
    }

    public String getCategoryID() {
        return cateID;
    }

    public void setCategoryID(String cateID) {
        this.cateID = cateID;
    }


    @Override
    public String toString() {
        return "Product{" +
                "productID='" + productID + '\'' +
                ", productName='" + productName + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", coupon=" + coupon +
                ", VAT=" + VAT +
                ", cateID='" + cateID + '\'' +
                '}';
    }
}
