package com.nhynhi.store.model;

import java.io.Serializable;

/** Chi tiết đơn hàng — khớp node "orderDetails" trên Firebase. */
public class SOrderDetail implements Serializable {
    private String id;            // = key (OD1001_1...)
    private String orderId;
    private String productId;
    private int quantity;
    private double unitPrice;

    public SOrderDetail() { }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
}
