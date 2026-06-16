package com.nhynhi.dals;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.nhynhi.models.Category;
import com.nhynhi.models.MyDatabaseHelper;
import java.util.ArrayList;

public class CategoryDAL {
    private final MyDatabaseHelper dbHelper;

    public CategoryDAL(Context context) {
        dbHelper = new MyDatabaseHelper(context);
    }

    public ArrayList<Category> getAll() {
        ArrayList<Category> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.openDatabase();
        Cursor cursor = db.rawQuery("SELECT CategoryId, CategoryName, Description FROM Category", null);
        while (cursor.moveToNext()) {
            Category c = new Category();
            c.setCategoryID(cursor.getString(0));
            c.setCategoryName(cursor.getString(1));
            c.setDescription(cursor.getString(2));
            list.add(c);
        }
        cursor.close();
        db.close();
        return list;
    }
}
