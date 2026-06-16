package com.nhynhi.models;

import java.util.ArrayList;

public class ListUserAccount {

    public static ArrayList<UserAccount> getUserAccounts() {
        // 1. Khởi tạo danh sách mới
        ArrayList<UserAccount> database = new ArrayList<>();

        // 2. Thêm các đối tượng UserAccount vào danh sách
        // Giả sử constructor của UserAccount theo thứ tự: username, password, roleCode, fullName, isActive
        database.add(new UserAccount("admin", "123", "admin", "Admin System", true));
        database.add(new UserAccount("employee", "123", "employee", "Staff User", true));
        database.add(new UserAccount("user1", "123", "employee", "User 1", false));
        database.add(new UserAccount("user2", "123", "employee", "User 2", true));

        // 3. Trả về danh sách đã chứa dữ liệu
        return database;
    }
    public static UserAccount login(String username, String password){
        //step1: query database
        ArrayList<UserAccount> database = getUserAccounts();
        //step : check ussername and password
        for (UserAccount user : database){
            if (user.getUsername().equals(username) && user.getPassword().equals(password)){
                return user;
            }
        }
        return null;
    }
}