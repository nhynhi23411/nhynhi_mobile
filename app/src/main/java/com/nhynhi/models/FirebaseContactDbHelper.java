package com.nhynhi.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class FirebaseContactDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME    = "contact.sqlite";
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
        seedDefaultContacts(db);
    }

    private void seedDefaultContacts(SQLiteDatabase db) {
        String[][] defaults = {
            {"contact1", "Trần Duy Thanh",       "thanhtd@uel.edu.vn",         "987773061"},
            {"contact2", "Phạm Thị Xuân Diệu",   "dieuptx@uel.edu.vn",         "9111111111"},
            {"contact3", "Trần Phạm Thanh Trà",   "thanhtra@gmail.com",          "1112223334"},
            {"contact4", "Trần Phạm Mẫn Nhi",     "tranphammannhi@gmail.com",    "3334446667"},
            {"contact5", "Ho Trung Thanh",         "thanhht@uel.edu.vn",          "1131141159"},
            {"contact6", "Nguyễn Văn An",          "nguyenvanan@gmail.com",       "0901234567"},
            {"contact7", "Lê Thị Bảo Ngọc",       "lethibn@uel.edu.vn",          "0987654321"},
            {"contact8", "Võ Thành Đạt",           "vothanhdat@yahoo.com",        "0933112233"},
        };
        for (String[] r : defaults) {
            ContentValues cv = new ContentValues();
            cv.put(COL_ID,    r[0]);
            cv.put(COL_NAME,  r[1]);
            cv.put(COL_EMAIL, r[2]);
            cv.put(COL_PHONE, r[3]);
            db.insertWithOnConflict(TABLE, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
        }
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
