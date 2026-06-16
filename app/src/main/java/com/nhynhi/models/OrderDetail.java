package com.nhynhi.models;

import java.io.Serializable;

public class OrderDetail implements Serializable {
    private String orderDetailID;
    private String orderID;
    private String productID;
    private int quantity;
    private double price;
    private double coupon;
    private double VAT;

    public OrderDetail() {
    }

    public OrderDetail(String orderDetailID, String orderID, String productID, int quantity, double price, double coupon, double VAT) {
        this.orderDetailID = orderDetailID;
        this.orderID = orderID;
        this.productID = productID;
        this.quantity = quantity;
        this.price = price;
        this.coupon = coupon;
        this.VAT = VAT;
    }

    public String getOrderDetailID() {
        return orderDetailID;
    }

    public void setOrderDetailID(String orderDetailID) {
        this.orderDetailID = orderDetailID;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
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

    @Override
    public String toString() {
        return "OrderDetail{" +
                "orderDetailID='" + orderDetailID + '\'' +
                ", orderID='" + orderID + '\'' +
                ", productID='" + productID + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", coupon=" + coupon +
                ", VAT=" + VAT +
                '}';
    }
}
