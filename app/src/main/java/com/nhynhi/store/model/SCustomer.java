package com.nhynhi.store.model;

import java.io.Serializable;

/** Khách hàng — khớp node "customers" trên Firebase. */
public class SCustomer implements Serializable {
    private String id;            // = key (CUST001...)
    private String fullName;
    private String email;
    private String phone;
    private String address;

    public SCustomer() { }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
