package com.nhynhi.dals;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.nhynhi.models.Employee;
import com.nhynhi.models.MyDatabaseHelper;
import java.util.ArrayList;

public class EmployeeDAL {
    private final MyDatabaseHelper dbHelper;

    public EmployeeDAL(Context context) {
        dbHelper = new MyDatabaseHelper(context);
    }

    public ArrayList<Employee> getAll() {
        ArrayList<Employee> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.openDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT EmployeeID, EmployeeName, Phone, Position FROM Employees", null);
        while (cursor.moveToNext()) {
            Employee e = new Employee();
            e.setId(cursor.getString(0));
            e.setName(cursor.getString(1));
            e.setPhone(cursor.getString(2));
            e.setPosition(cursor.getString(3));
            list.add(e);
        }
        cursor.close();
        db.close();
        return list;
    }
}
