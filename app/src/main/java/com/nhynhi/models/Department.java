package com.nhynhi.models;

import androidx.annotation.NonNull;
import java.util.ArrayList;

public class Department {
    private String departmentId;
    private String departmentName;
    private ArrayList<Employee> employees;

    // Các hàm xử lý danh sách
    public void addEmployee(Employee employee){
        employees.add(employee);
    }

    public void removeEmployee(Employee employee){
        employees.remove(employee);
    }

    public void addListEmployee(ArrayList<Employee> employees){
        if (employees != null) {
            this.employees.addAll(employees);
        }
    }

    // Constructor mặc định
    public Department(){
        this.employees = new ArrayList<>();
    }

    // Constructor có tham số
    public Department(String departmentId, String departmentName) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.employees = new ArrayList<>(); // ĐÃ THÊM: Tránh lỗi NullPointerException
    }

    // Getter và Setter
    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public ArrayList<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(ArrayList<Employee> employees) {
        this.employees = employees;
    }

    @NonNull
    @Override
    public String toString() {
        // Trả về tên phòng ban (Rất tiện khi đổ data lên Spinner hoặc ListView)
        if (departmentName != null) {
            return departmentName;
        }
        return "Unknown Department";
    }
}