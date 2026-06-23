package com.nhynhi.models;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class FirebaseContactDbHelper {

    private static final String TAG      = "ContactDbHelper";
    private static final String DB_NAME  = "contact.sqlite";
    // Tăng số này lên mỗi khi thay đổi file contact.sqlite trong assets
    private static final int    DB_ASSETS_VERSION = 1;
    private static final String PREFS_NAME        = "contact_db_prefs";
    private static final String KEY_VERSION       = "contact_assets_version";

    public static final String TABLE     = "contacts";
    public static final String COL_ID    = "firebase_id";
    public static final String COL_NAME  = "name";
    public static final String COL_EMAIL = "email";
    public static final String COL_PHONE = "phone";

    private final Context context;

    public FirebaseContactDbHelper(Context context) {
        this.context = context;
        ensureDbReady();
    }

    // ── Copy từ assets nếu lần đầu hoặc có version mới ─────────────────────
    private void ensureDbReady() {
        File dbFile = context.getDatabasePath(DB_NAME);
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int stored = prefs.getInt(KEY_VERSION, 0);

        if (!dbFile.exists() || stored < DB_ASSETS_VERSION) {
            copyFromAssets(dbFile);
            ensureTableExists();
            prefs.edit().putInt(KEY_VERSION, DB_ASSETS_VERSION).apply();
            Log.i(TAG, "contact.sqlite copied from assets, version=" + DB_ASSETS_VERSION);
        }
    }

    private void copyFromAssets(File dbFile) {
        try {
            File dir = dbFile.getParentFile();
            if (dir != null && !dir.exists()) dir.mkdirs();

            InputStream  is = context.getAssets().open(DB_NAME);
            OutputStream os = new FileOutputStream(dbFile, false);
            byte[] buf = new byte[4096];
            int len;
            while ((len = is.read(buf)) > 0) os.write(buf, 0, len);
            os.flush();
            os.close();
            is.close();
        } catch (Exception e) {
            Log.e(TAG, "copyFromAssets failed", e);
        }
    }

    // Đảm bảo bảng contacts tồn tại (phòng khi file assets chưa có table)
    private void ensureTableExists() {
        SQLiteDatabase db = openDb();
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
            COL_ID    + " TEXT PRIMARY KEY, " +
            COL_NAME  + " TEXT, " +
            COL_EMAIL + " TEXT, " +
            COL_PHONE + " TEXT)"
        );
        db.close();
    }

    private SQLiteDatabase openDb() {
        File dbFile = context.getDatabasePath(DB_NAME);
        return SQLiteDatabase.openDatabase(
                dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
    }

    // ── CRUD ────────────────────────────────────────────────────────────────

    // Sync toàn bộ từ Firebase về (xóa cũ, insert mới)
    public void replaceAll(List<FirebaseContact> contacts) {
        SQLiteDatabase db = openDb();
        db.beginTransaction();
        try {
            db.delete(TABLE, null, null);
            for (FirebaseContact c : contacts) db.insert(TABLE, null, toValues(c));
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    // Thêm hoặc cập nhật 1 contact
    public void upsert(FirebaseContact contact) {
        SQLiteDatabase db = openDb();
        db.insertWithOnConflict(TABLE, null, toValues(contact), SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    // Xóa theo firebase_id
    public void delete(String firebaseId) {
        SQLiteDatabase db = openDb();
        db.delete(TABLE, COL_ID + "=?", new String[]{firebaseId});
        db.close();
    }

    // Lấy toàn bộ (dùng khi offline)
    public List<FirebaseContact> getAll() {
        List<FirebaseContact> list = new ArrayList<>();
        SQLiteDatabase db = openDb();
        Cursor cursor = db.query(TABLE, null, null, null, null, null, COL_NAME + " ASC");
        if (cursor != null) {
            while (cursor.moveToNext()) list.add(fromCursor(cursor));
            cursor.close();
        }
        db.close();
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

    private FirebaseContact fromCursor(Cursor c) {
        return new FirebaseContact(
                c.getString(c.getColumnIndexOrThrow(COL_ID)),
                c.getString(c.getColumnIndexOrThrow(COL_NAME)),
                c.getString(c.getColumnIndexOrThrow(COL_EMAIL)),
                c.getString(c.getColumnIndexOrThrow(COL_PHONE))
        );
    }
}
