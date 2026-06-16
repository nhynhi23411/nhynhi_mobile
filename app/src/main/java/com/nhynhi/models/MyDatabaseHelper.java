package com.nhynhi.models;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class MyDatabaseHelper {
    private static final String DB_NAME = "k23411.sqlite";
    private Context context;

    /**
     * HƯỚNG DẪN CẬP NHẬT DỮ LIỆU:
     * Sau khi sửa/thêm dữ liệu trong file assets/k23411.sqlite bằng DB Browser,
     * hãy TĂNG số DB_ASSETS_VERSION lên 1 rồi chạy lại app.
     * App sẽ tự động copy bản mới từ assets vào bộ nhớ thiết bị.
     * (Ví dụ: thêm 50 đơn mới → đổi thành 3, rebuild app.)
     */
    private static final int DB_ASSETS_VERSION = 1781054104;
    private static final String PREFS_NAME = "db_prefs";
    private static final String KEY_DB_VERSION = "db_assets_version";

    public MyDatabaseHelper(Context context) {
        this.context = context;
        ensureDatabaseReady();
    }

    private void ensureDatabaseReady() {
        File dbFile = context.getDatabasePath(DB_NAME);
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int storedVersion = prefs.getInt(KEY_DB_VERSION, 0);

        // Copy từ assets nếu: lần đầu cài app (chưa có file) HOẶC assets có version mới hơn.
        if (!dbFile.exists() || storedVersion < DB_ASSETS_VERSION) {
            copyDatabaseFromAssets(dbFile);
            prefs.edit().putInt(KEY_DB_VERSION, DB_ASSETS_VERSION).apply();
            Log.i("DB_HELPER", "Database updated to version " + DB_ASSETS_VERSION);
        }
    }

    private void copyDatabaseFromAssets(File dbFile) {
        try {
            File parentDir = dbFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            InputStream is = context.getAssets().open(DB_NAME);
            OutputStream os = new FileOutputStream(dbFile, false);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            os.flush();
            os.close();
            is.close();
        } catch (Exception e) {
            Log.e("DB_HELPER", "Error copying database", e);
        }
    }

    public SQLiteDatabase openDatabase() {
        File dbFile = context.getDatabasePath(DB_NAME);
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
        createTables(db);
        return db;
    }

    private void createTables(SQLiteDatabase db) {
        // 1. Bảng Employee
        db.execSQL("CREATE TABLE IF NOT EXISTS Employees (" +
                "EmployeeID TEXT PRIMARY KEY, " +
                "EmployeeName TEXT, " +
                "Phone TEXT, " +
                "Address TEXT)");

        // 2. Bảng Product
        db.execSQL("CREATE TABLE IF NOT EXISTS Products (" +
                "ProductID TEXT PRIMARY KEY, " +
                "ProductName TEXT, " +
                "Quantity INTEGER, " +
                "Price REAL, " +
                "Coupon REAL, " +
                "VAT REAL, " +
                "CategoryId TEXT)");

        // 3. Bảng Orders (Khớp hoàn toàn với ảnh của bạn)
        db.execSQL("CREATE TABLE IF NOT EXISTS Orders (" +
                "OrderID TEXT PRIMARY KEY, " +
                "CustomerID TEXT, " +
                "EmployeeID TEXT, " +
                "OrderDate TEXT, " +
                "OrderStatus TEXT, " +
                "ShipAddress TEXT, " +
                "ShipCity TEXT, " +
                "ShipPhone TEXT, " +
                "Freight REAL, " +
                "Notes TEXT)");

        // 4. Bảng OrderDetails
        db.execSQL("CREATE TABLE IF NOT EXISTS OrderDetails (" +
                "OrderDetailID TEXT PRIMARY KEY, " +
                "OrderID TEXT, " +
                "ProductID TEXT, " +
                "Quantity INTEGER, " +
                "Price REAL, " +
                "Coupon REAL, " +
                "VAT REAL)");

        // 5. Bảng Category
        db.execSQL("CREATE TABLE IF NOT EXISTS Category (" +
                "CategoryId TEXT PRIMARY KEY, " +
                "CategoryName TEXT, " +
                "Description TEXT)");

        syncMockData(db);
    }

    private void syncMockData(SQLiteDatabase db) {
        // Đồng bộ Product nếu trống
        android.database.Cursor curProd = db.rawQuery("SELECT COUNT(*) FROM Products", null);
        curProd.moveToFirst();
        if (curProd.getInt(0) == 0) {
            for (Product p : DataWareHouse.getProducts()) {
                ContentValues v = new ContentValues();
                v.put("ProductID", p.getProductID());
                v.put("ProductName", p.getProductName());
                v.put("Quantity", p.getQuantity());
                v.put("Price", p.getPrice());
                v.put("Coupon", p.getCoupon());
                v.put("VAT", p.getVAT());
                v.put("CategoryId", p.getCategoryID());
                db.insert("Products", null, v);
            }
        }
        curProd.close();

        // Đồng bộ Employee nếu trống
        android.database.Cursor curEmp = db.rawQuery("SELECT COUNT(*) FROM Employees", null);
        curEmp.moveToFirst();
        if (curEmp.getInt(0) == 0) {
            for (Employee e : DataWareHouse.getEmployees()) {
                ContentValues v = new ContentValues();
                v.put("EmployeeID", e.getId());
                v.put("EmployeeName", e.getName());
                v.put("Phone", e.getPhone());
                v.put("Address", e.getAddress());
                db.insert("Employees", null, v);
            }
        }
        curEmp.close();

        // Đồng bộ Category — dùng tên khớp với cột Category trong bảng Products
        android.database.Cursor curCat = db.rawQuery("SELECT COUNT(*) FROM Category", null);
        curCat.moveToFirst();
        if (curCat.getInt(0) == 0) {
            String[][] categories = {
                {"CAT001", "Đặc sản miền Tây",    "Các sản phẩm đặc sản truyền thống miền Tây"},
                {"CAT002", "Đồ uống",              "Các loại trà, nước uống đóng gói"},
                {"CAT003", "Thực phẩm khô",        "Bánh, hàng khô tiện lợi"},
                {"CAT004", "Gia vị",               "Nước mắm và các loại gia vị chế biến"},
                {"CAT005", "Lương thực",           "Gạo và các loại lương thực thiết yếu"},
                {"CAT006", "Thực phẩm đông lạnh",  "Sản phẩm cần bảo quản lạnh"},
            };
            for (String[] cat : categories) {
                ContentValues v = new ContentValues();
                v.put("CategoryId",   cat[0]);
                v.put("CategoryName", cat[1]);
                v.put("Description",  cat[2]);
                db.insertWithOnConflict("Category", null, v, SQLiteDatabase.CONFLICT_IGNORE);
            }
        }
        curCat.close();
    }

    public long addOrder(Order order) {
        SQLiteDatabase db = openDatabase();
        ContentValues v = new ContentValues();
        v.put("OrderID", order.getOrderID());
        v.put("CustomerID", order.getCustomerID());
        v.put("EmployeeID", order.getEmployeeID());
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        v.put("OrderDate", order.getOrderDate() != null ? sdf.format(order.getOrderDate()) : null);
        v.put("OrderStatus", order.getOrder_status());
        v.put("ShipAddress", order.getShipAddress());
        v.put("ShipCity", order.getShipCity());
        v.put("ShipPhone", order.getShipPhone());
        v.put("Freight", order.getFreight());
        v.put("Notes", order.getNotes());
        long res = db.insert("Orders", null, v);
        db.close();
        return res;
    }

    /**
     * Lấy tổng tiền của TẤT CẢ đơn hàng trong 1 truy vấn duy nhất (GROUP BY).
     * Dùng để adapter không phải mở DB cho từng dòng -> cuộn list mượt, không lag.
     */
    public java.util.HashMap<String, Double> getAllOrderTotals() {
        java.util.HashMap<String, Double> map = new java.util.HashMap<>();
        SQLiteDatabase db = openDatabase();
        android.database.Cursor cursor = db.rawQuery(
                "SELECT OrderID, SUM(Quantity * Price * (1 - Coupon) * (1 + VAT)) " +
                        "FROM OrderDetails GROUP BY OrderID", null);
        while (cursor.moveToNext()) {
            map.put(cursor.getString(0), cursor.getDouble(1));
        }
        cursor.close();
        db.close();
        return map;
    }

    /**
     * Cập nhật trạng thái 1 đơn hàng và LƯU bền vững vào file SQLite.
     * @return true nếu có dòng được cập nhật.
     */
    public boolean updateOrderStatus(String orderId, String newStatus) {
        SQLiteDatabase db = openDatabase();
        ContentValues v = new ContentValues();
        v.put("OrderStatus", newStatus);
        int rows = db.update("Orders", v, "OrderID = ?", new String[]{orderId});
        db.close();
        return rows > 0;
    }

    public double getOrderTotal(String orderId) {
        double total = 0;
        SQLiteDatabase db = openDatabase();
        android.database.Cursor cursor = db.rawQuery(
                "SELECT SUM(Quantity * Price * (1 - Coupon) * (1 + VAT)) FROM OrderDetails WHERE OrderID = ?",
                new String[]{orderId});
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return total;
    }
}
