package com.nhynhi.dals;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.nhynhi.models.Category;

import java.util.ArrayList;

public class CategoryDAO {

    public static final String DATABASE_NAME = "k23411.sqlite";
    public static final String TABLE_NAME    = "Category";

    public static ArrayList<Category> getCategories(Context context) {
        ArrayList<Category> categories = new ArrayList<>();

        String dbPath = context.getDatabasePath(DATABASE_NAME).getPath();
        SQLiteDatabase database = SQLiteDatabase.openDatabase(
                dbPath, null, SQLiteDatabase.OPEN_READONLY);

        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        while (cursor.moveToNext()) {
            String categoryId   = cursor.getString(0);
            String categoryName = cursor.getString(1);
            String description  = cursor.getString(2);
            categories.add(new Category(categoryId, categoryName, description));
        }
        cursor.close();
        database.close();
        return categories;
    }

    public static long saveNewCategory(Context context, Category category) {
        long result = -1;
        String dbPath = context.getDatabasePath(DATABASE_NAME).getPath();
        SQLiteDatabase database = SQLiteDatabase.openDatabase(
                dbPath, null, SQLiteDatabase.OPEN_READWRITE);

        ContentValues values = new ContentValues();
        values.put("CategoryId", category.getCategoryID());
        values.put("CategoryName", category.getCategoryName());
        values.put("Description", category.getDescription());
        result = database.insert(TABLE_NAME, null, values);
        database.close();
        return result;
    }

    public static boolean updateCategory(Context context, Category category) {
        String dbPath = context.getDatabasePath(DATABASE_NAME).getPath();
        SQLiteDatabase database = SQLiteDatabase.openDatabase(
                dbPath, null, SQLiteDatabase.OPEN_READWRITE);

        ContentValues values = new ContentValues();
        values.put("CategoryName", category.getCategoryName());
        values.put("Description", category.getDescription());
        int rows = database.update(TABLE_NAME, values, "CategoryId = ?",
                new String[]{category.getCategoryID()});
        database.close();
        return rows > 0;
    }

    public static boolean deleteCategory(Context context, Category category) {
        String dbPath = context.getDatabasePath(DATABASE_NAME).getPath();
        SQLiteDatabase database = SQLiteDatabase.openDatabase(
                dbPath, null, SQLiteDatabase.OPEN_READWRITE);

        int rows = database.delete(TABLE_NAME, "CategoryId = ?",
                new String[]{category.getCategoryID()});
        database.close();
        return rows > 0;
    }
}
