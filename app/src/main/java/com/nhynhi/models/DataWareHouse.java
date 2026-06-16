package com.nhynhi.models;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class DataWareHouse {

    private static final Random rand = new Random(123);

    // Khai báo các danh sách để lưu cache
    private static ArrayList<Category> categories;
    private static ArrayList<Product> products;
    private static ArrayList<Employee> employees;
    private static ArrayList<Customer> customers;
    private static ArrayList<Order> orders;
    private static ArrayList<OrderDetail> orderDetails;

    @NonNull
    public static ArrayList<Category> getCategories() {
        if (categories == null) {
            categories = new ArrayList<>();
            categories.add(new Category("C001", "Mì", "Các loại mì ăn liền"));
            categories.add(new Category("C002", "Rau củ quả", "Rau củ quả tươi sạch"));
            categories.add(new Category("C003", "Nước uống có ga", "Các loại nước ngọt đóng chai"));
            categories.add(new Category("C004", "Trái cây", "Trái cây tươi theo mùa"));
            categories.add(new Category("C005", "Thịt", "Thịt tươi sống các loại"));
        }
        return categories;
    }

    @NonNull
    public static ArrayList<Product> getProducts() {
        if (products == null) {
            products = new ArrayList<>();
            products.add(new Product("P001", "Mì Hảo Hảo", 4000, 100, 0, 0.1, "C001"));
            products.add(new Product("P002", "Mì Omachi", 7000, 50, 0.05, 0.1, "C001"));
            products.add(new Product("P003", "Mì Cung Đình", 6500, 60, 0, 0.1, "C001"));
            products.add(new Product("P004", "Bắp cải", 15000, 50, 0, 0.08, "C002"));
            products.add(new Product("P005", "Cà rốt", 12000, 40, 0.02, 0.08, "C002"));
            products.add(new Product("P006", "Súp lơ", 20000, 30, 0, 0.08, "C002"));
            products.add(new Product("P007", "Dưa leo", 10000, 70, 0.05, 0.08, "C002"));
            products.add(new Product("P008", "Coca Cola", 10000, 200, 0, 0.1, "C003"));
            products.add(new Product("P009", "Pepsi", 9500, 150, 0.02, 0.1, "C003"));
            products.add(new Product("P010", "7Up", 9500, 100, 0, 0.1, "C003"));
            products.add(new Product("P011", "Táo Mỹ", 30000, 80, 0.1, 0.08, "C004"));
            products.add(new Product("P012", "Nho xanh", 80000, 40, 0.05, 0.08, "C004"));
            products.add(new Product("P013", "Cam sành", 25000, 60, 0, 0.08, "C004"));
            products.add(new Product("P014", "Dâu tây", 120000, 20, 0.1, 0.08, "C004"));
            products.add(new Product("P015", "Xoài cát", 45000, 30, 0, 0.08, "C004"));
            products.add(new Product("P016", "Thịt bò", 250000, 30, 0, 0.05, "C005"));
            products.add(new Product("P017", "Thịt heo", 120000, 50, 0, 0.05, "C005"));
            products.add(new Product("P018", "Ức gà", 85000, 40, 0.05, 0.05, "C005"));
            products.add(new Product("P019", "Thịt bê", 300000, 15, 0, 0.05, "C005"));
        }
        return products;
    }
    public static Product downloadProduct(int i){
        ArrayList<Product> products = getProducts();
        if (i<0 || i>=products.size())
            return null;
        return products.get(i);

    }
    @NonNull
    public static ArrayList<Employee> getEmployees() {
        if (employees == null) {
            employees = new ArrayList<>();
            employees.add(new Employee("E001", "Nguyễn Văn An", "0901111111", "Ho Chi Minh City"));
            employees.add(new Employee("E002", "Trần Thị Bình", "0902222222", "Ha Noi City"));
            employees.add(new Employee("E003", "Lê Văn Cường", "0903333333", "Da Nang City"));
            employees.add(new Employee("E004", "Phạm Thị Dung", "0904444444", "Hai Phong City"));
            employees.add(new Employee("E005", "Hoàng Văn Em", "0905555555", "Hue City"));
            employees.add(new Employee("E006", "Đặng Thị Phương", "0906666666", "Can Tho City"));
            employees.add(new Employee("E007", "Ngô Văn Giang", "0907777777", "Khanh Hoa Province"));
            employees.add(new Employee("E008", "Đỗ Thị Hiền", "0908888888", "Dak Lak Province"));
            employees.add(new Employee("E009", "Vũ Văn Khánh", "0909999999", "Quang Ninh Province"));
            employees.add(new Employee("E010", "Bùi Thị Lan", "0900000000", "Lam Dong Province"));
        }
        return employees;
    }

    @NonNull
    public static ArrayList<Customer> getCustomers() {
        if (customers == null) {
            customers = new ArrayList<>();
            Calendar cal = Calendar.getInstance();
            String[] names = {"Nguyễn Văn A", "Trần Thị B", "Lê Văn C", "Phạm Thị D", "Hoàng Văn E"};
            String[] addresses = {"Ho Chi Minh City", "Ha Noi City", "Da Nang City", "Can Tho City", "Hai Phong City"};
            for (int i = 1; i <= 100; i++) {
                cal.set(1980 + (i % 26), (i % 12), (i % 28) + 1);
                customers.add(new Customer(String.format("Cus%03d", i), names[(i - 1) % names.length] + " " + i, addresses[(i - 1) % addresses.length], "090" + String.format("%07d", i), "customer" + i + "@example.com", cal.getTime()));
            }
        }
        return customers;
    }

    @NonNull
    public static ArrayList<Order> getOrders() {
        if (orders == null) {
            orders = new ArrayList<>();
            ArrayList<Customer> custs = getCustomers();
            ArrayList<Employee> emps = getEmployees();
            Calendar cal = Calendar.getInstance();
            String[] statuses = {"On Logistic", "Not Payment", "Completed", "Complain"};
            for (int i = 1; i <= 1000; i++) {
                int year = 2024 + rand.nextInt(3);
                int month = (year < 2026) ? rand.nextInt(12) : rand.nextInt(3);
                cal.set(year, month, 1 + rand.nextInt(28));
                String status = statuses[rand.nextInt(statuses.length)];
                orders.add(new Order(String.format("ORD%04d", i), custs.get(rand.nextInt(custs.size())).getCustomerID(), cal.getTime(), emps.get(rand.nextInt(emps.size())).getId(), status));
            }
        }
        return orders;
    }

    @NonNull
    public static ArrayList<OrderDetail> getOrderDetails() {
        if (orderDetails == null) {
            orderDetails = new ArrayList<>();
            ArrayList<Order> ords = getOrders();
            ArrayList<Product> prods = getProducts();
            int counter = 1;
            for (Order order : ords) {
                int count = 1 + rand.nextInt(10);
                for (int i = 0; i < count; i++) {
                    Product p = prods.get(rand.nextInt(prods.size()));
                    orderDetails.add(new OrderDetail(String.format("OD%06d", counter++), order.getOrderID(), p.getProductID(), 1 + rand.nextInt(5), p.getPrice(), p.getCoupon(), p.getVAT()));
                }
            }
        }
        return orderDetails;
    }
    public static double SumOfMoneyForOrder(Order order) {
        double total = 0;
        ArrayList<OrderDetail> details = getOrderDetails();

        for (OrderDetail od : details) {
            if (od.getOrderID().equals(order.getOrderID())) {
                double priceAfterCoupon = od.getPrice() * (1 - od.getCoupon());
                double priceWithVAT = priceAfterCoupon * (1 + od.getVAT());

                total += priceWithVAT * od.getQuantity();
            }
        }
        return total;
    }
}