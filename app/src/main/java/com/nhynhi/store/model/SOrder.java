package com.nhynhi.store.model;

import java.io.Serializable;

/** Đơn hàng — khớp node "orders" trên Firebase. */
public class SOrder implements Serializable {
    private String id;            // = key (ORD1001...)
    private String customerId;
    private String employeeId;
    private String orderDate;
    private String status;
    private double totalAmount;

    public SOrder() { }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
}
