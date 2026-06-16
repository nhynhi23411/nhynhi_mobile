package com.nhynhi.models;

import java.io.Serializable;
import java.util.Date;

public class Order implements Serializable {
    private String Order_status;
    private String orderID;
    private String customerID;
    private Date orderDate;
    private String employeeID;
    
    // Các trường bổ sung cho chi tiết đầy đủ
    private String requiredDate;
    private String shippedDate;
    private String shipAddress;
    private String shipCity;
    private String shipPhone;
    private String shipVia;
    private String shipName;
    private double freight;
    private String notes;

    public Order() {}

    public Order(String orderID, String customerID, Date orderDate, String employeeID, String Order_status) {
        this.orderID = orderID;
        this.customerID = customerID;
        this.orderDate = orderDate;
        this.employeeID = employeeID;
        this.Order_status = Order_status;
    }

    // Getters and Setters
    public String getOrder_status() { return Order_status; }
    public void setOrder_status(String order_status) { Order_status = order_status; }
    public String getOrderID() { return orderID; }
    public void setOrderID(String orderID) { this.orderID = orderID; }
    public String getCustomerID() { return customerID; }
    public void setCustomerID(String customerID) { this.customerID = customerID; }
    public Date getOrderDate() { return orderDate; }
    public void setOrderDate(Date orderDate) { this.orderDate = orderDate; }
    public String getEmployeeID() { return employeeID; }
    public void setEmployeeID(String employeeID) { this.employeeID = employeeID; }

    public String getRequiredDate() { return requiredDate; }
    public void setRequiredDate(String requiredDate) { this.requiredDate = requiredDate; }
    public String getShippedDate() { return shippedDate; }
    public void setShippedDate(String shippedDate) { this.shippedDate = shippedDate; }
    public String getShipAddress() { return shipAddress; }
    public void setShipAddress(String shipAddress) { this.shipAddress = shipAddress; }
    public String getShipCity() { return shipCity; }
    public void setShipCity(String shipCity) { this.shipCity = shipCity; }
    public String getShipPhone() { return shipPhone; }
    public void setShipPhone(String shipPhone) { this.shipPhone = shipPhone; }
    public String getShipVia() { return shipVia; }
    public void setShipVia(String shipVia) { this.shipVia = shipVia; }
    public String getShipName() { return shipName; }
    public void setShipName(String shipName) { this.shipName = shipName; }
    public double getFreight() { return freight; }
    public void setFreight(double freight) { this.freight = freight; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}