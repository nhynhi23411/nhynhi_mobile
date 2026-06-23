package com.nhynhi.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class FirebaseContactDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME    = "firebase_contacts.db";
    private static final int    DB_VERSION = 1;

    public static final String TABLE   = "contacts";
    public static final String COL_ID    = "firebase_id";
    public static final String COL_NAME  = "name";
    public static final String COL_EMAIL = "email";
    public static final String COL_PHONE = "phone";

    private static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
            COL_ID    + " TEXT PRIMARY KEY, " +
            COL_NAME  + " TEXT, " +
            COL_EMAIL + " TEXT, " +
            COL_PHONE + " TEXT)";

    public FirebaseContactDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    // Xóa toàn bộ rồi insert lại — dùng khi sync từ Firebase về
    public void replaceAll(List<FirebaseContact> contacts) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE, null, null);
            for (FirebaseContact c : contacts) {
                db.insert(TABLE, null, toValues(c));
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    // Thêm hoặc cập nhật 1 contact
    public void upsert(FirebaseContact contact) {
        SQLiteDatabase db = getWritableDatabase();
        db.insertWithOnConflict(TABLE, null, toValues(contact), SQLiteDatabase.CONFLICT_REPLACE);
    }

    // Xóa theo firebase_id
    public void delete(String firebaseId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE, COL_ID + "=?", new String[]{firebaseId});
    }

    // Lấy toàn bộ
    public List<FirebaseContact> getAll() {
        List<FirebaseContact> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE, null, null, null, null, null, COL_NAME + " ASC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(fromCursor(cursor));
            }
            cursor.close();
        }
        return list;
    }

    private ContentValues toValues(FirebaseContact c) {
        ContentValues cv = new ContentValues();
        cv.put(COL_ID,    c.getId());
        cv.put(COL_NAME,  c.getName());
        cv.put(COL_EMAIL, c.getEmail());
        cv.put(COL_PHONE, c.getPhone());
        return cv;
    }

    private FirebaseContact fromCursor(Cursor cursor) {
        return new FirebaseContact(
                cursor.getString(cursor.getColumnIndexOrThrow(COL_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_PHONE))
        );
    }
}
