package com.nhynhi.dals;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.nhynhi.models.Customer;
import com.nhynhi.models.MyDatabaseHelper;
import java.util.ArrayList;

public class CustomerDAL {
    private final MyDatabaseHelper dbHelper;

    public CustomerDAL(Context context) {
        dbHelper = new MyDatabaseHelper(context);
    }

    public ArrayList<Customer> getAll() {
        ArrayList<Customer> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.openDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT CustomerID, CustomerName, Phone, Email, Address, City FROM Customers", null);
        while (cursor.moveToNext()) {
            Customer c = new Customer();
            c.setCustomerID(cursor.getString(0));
            c.setCustomerName(cursor.getString(1));
            c.setPhone(cursor.getString(2));
            c.setEmail(cursor.getString(3));
            c.setAddress(cursor.getString(4));
            c.setCity(cursor.getString(5));
            list.add(c);
        }
        cursor.close();
        db.close();
        return list;
    }
}
