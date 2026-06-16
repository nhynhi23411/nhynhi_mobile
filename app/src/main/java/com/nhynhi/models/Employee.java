package com.nhynhi.models;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Employee implements Serializable {
    private String id;
    private String name;
    private String phone;
    private String address;
    private String position;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Employee(String id, String name, String phone, String address) {
        this(id, name, phone);
        this.address = address;
    }

    // Default Constructor
    public Employee() {
    }

    // Full Constructor
    public Employee(String id, String name, String phone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
    }

    // Getter & Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    @NonNull
    @Override
    public String toString() {
        return "ID: " + this.id + " | Name: " + this.name + " | Phone: " + this.phone;
    }
}