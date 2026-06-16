package com.nhynhi.dals;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.nhynhi.models.MyDatabaseHelper;
import com.nhynhi.models.Product;
import java.util.ArrayList;

public class ProductDAL {
    private final MyDatabaseHelper dbHelper;

    public ProductDAL(Context context) {
        dbHelper = new MyDatabaseHelper(context);
    }

    public ArrayList<Product> getAll() {
        return getByCategory(null);
    }

    /** Lấy sản phẩm theo danh mục. Truyền null để lấy tất cả. */
    public ArrayList<Product> getByCategory(String categoryName) {
        ArrayList<Product> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.openDatabase();
        Cursor cursor;
        if (categoryName == null || categoryName.isEmpty()) {
            cursor = db.rawQuery(
                    "SELECT ProductID, ProductName, Price, Stock, Category FROM Products", null);
        } else {
            cursor = db.rawQuery(
                    "SELECT ProductID, ProductName, Price, Stock, Category FROM Products WHERE Category = ?",
                    new String[]{categoryName});
        }
        while (cursor.moveToNext()) {
            Product p = new Product();
            p.setProductID(cursor.getString(0));
            p.setProductName(cursor.getString(1));
            p.setPrice(cursor.getDouble(2));
            p.setQuantity(cursor.getInt(3));
            p.setCategoryID(cursor.getString(4));
            list.add(p);
        }
        cursor.close();
        db.close();
        return list;
    }
}
