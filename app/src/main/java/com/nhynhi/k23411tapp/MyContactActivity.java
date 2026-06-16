package com.nhynhi.k23411tapp;

import android.Manifest;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nhynhi.adapters.ContactAdapter;
import com.nhynhi.models.MyContact;

import java.util.ArrayList;

public class MyContactActivity extends AppCompatActivity {

    private static final String TAG = "MyContactActivity";
    private static final int REQUEST_CONTACTS_PERMISSIONS = 1;
    private static final int REQUEST_CALL_PERMISSION      = 2;

    private ListView lvContact;
    private TextView tvCount;
    private ArrayList<MyContact> contactList;
    private ContactAdapter adapter;
    private String pendingCallNumber; // số đang chờ quyền CALL_PHONE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_contact);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Quản lý danh bạ");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        lvContact   = findViewById(R.id.lvContact);
        tvCount     = findViewById(R.id.tvCount);
        contactList = new ArrayList<>();
        adapter     = new ContactAdapter(this, contactList);
        lvContact.setAdapter(adapter);

        // 1. Click thường → Gọi điện
        lvContact.setOnItemClickListener((parent, view, position, id) -> {
            MyContact contact = contactList.get(position);
            makeCall(contact.getPhone());
        });

        // 2. Long Click (Nhấn giữ lâu) → Mở Menu chọn Cập nhật hoặc Xóa
        lvContact.setOnItemLongClickListener((parent, view, position, id) -> {
            MyContact contact = contactList.get(position);
            showActionMenuDialog(contact);
            return true; // Trả về true để không kích hoạt sự kiện Click thường
        });

        FloatingActionButton fab = findViewById(R.id.fabAddContact);
        fab.setOnClickListener(v -> showAddContactDialog());

        checkContactsPermissions();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PERMISSIONS
    // ─────────────────────────────────────────────────────────────────────────

    private void checkContactsPermissions() {
        String[] needed = {
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS
        };
        if (hasPermissions(needed)) {
            loadContacts();
        } else {
            Log.d(TAG, "Xin quyền READ/WRITE CONTACTS");
            ActivityCompat.requestPermissions(this, needed, REQUEST_CONTACTS_PERMISSIONS);
        }
    }

    private boolean hasPermissions(String[] permissions) {
        for (String p : permissions) {
            if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CONTACTS_PERMISSIONS) {
            boolean allGranted = true;
            for (int r : grantResults)
                if (r != PackageManager.PERMISSION_GRANTED) { allGranted = false; break; }

            if (allGranted) {
                Log.d(TAG, "Quyền danh bạ được cấp");
                loadContacts();
            } else {
                Log.w(TAG, "Quyền danh bạ bị từ chối");
                Toast.makeText(this, "Cần quyền truy cập danh bạ!", Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == REQUEST_CALL_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Quyền CALL_PHONE được cấp, gọi: " + pendingCallNumber);
                dialNumber(pendingCallNumber);
            } else {
                Log.w(TAG, "Quyền CALL_PHONE bị từ chối");
                Toast.makeText(this, "Cần quyền gọi điện!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // READ CONTACTS
    // ─────────────────────────────────────────────────────────────────────────

    private void loadContacts() {
        contactList.clear();

        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int idIndex    = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
                int nameIndex  = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                String id    = cursor.getString(idIndex);
                String name  = cursor.getString(nameIndex);
                String phone = cursor.getString(phoneIndex);

                contactList.add(new MyContact(
                        id    != null ? id    : "",
                        name  != null ? name  : "",
                        phone != null ? phone : ""
                ));
            }
            cursor.close();
        }

        Log.d(TAG, "Loaded " + contactList.size() + " contacts");
        tvCount.setText(contactList.size() + " liên hệ");
        adapter.notifyDataSetChanged();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CALL PHONE
    // ─────────────────────────────────────────────────────────────────────────

    private void makeCall(String phone) {
        if (phone == null || phone.isEmpty()) {
            Toast.makeText(this, "Không có số điện thoại!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {
            dialNumber(phone);
        } else {
            pendingCallNumber = phone;
            Log.d(TAG, "Xin quyền CALL_PHONE để gọi: " + phone);
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
        }
    }

    private void dialNumber(String phone) {
        Log.d(TAG, "Gọi điện tới: " + phone);
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phone));
        startActivity(intent);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DIALOG MENU (Cập nhật / Xóa)
    // ─────────────────────────────────────────────────────────────────────────

    private void showActionMenuDialog(MyContact contact) {
        String[] options = {"Cập nhật liên hệ", "Xóa liên hệ"};
        new AlertDialog.Builder(this)
                .setTitle("Lựa chọn thao tác với: " + contact.getName())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        // Chọn cập nhật
                        showEditContactDialog(contact);
                    } else if (which == 1) {
                        // Chọn xóa -> Hỏi xác nhận lại trước khi xóa hẳn
                        new AlertDialog.Builder(this)
                                .setTitle("Xác nhận xóa")
                                .setMessage("Bạn có chắc muốn xóa " + contact.getName() + " khỏi danh bạ?")
                                .setPositiveButton("Xóa", (d, w) -> deleteContact(contact.getId()))
                                .setNegativeButton("Hủy", null)
                                .show();
                    }
                })
                .show();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // WRITE CONTACTS (CREATE)
    // ─────────────────────────────────────────────────────────────────────────

    private void showAddContactDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_contact, null);
        EditText edtName  = dialogView.findViewById(R.id.edtContactName);
        EditText edtPhone = dialogView.findViewById(R.id.edtContactPhone);

        new AlertDialog.Builder(this)
                .setTitle("Thêm liên hệ mới")
                .setView(dialogView)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String name  = edtName.getText().toString().trim();
                    String phone = edtPhone.getText().toString().trim();
                    if (name.isEmpty() || phone.isEmpty()) {
                        Toast.makeText(this, "Vui lòng nhập đầy đủ!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    saveContact(name, phone);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void saveContact(String name, String phone) {
        Uri addContactsUri = ContactsContract.Data.CONTENT_URI;
        long rawContactId  = getRawContactId();
        insertContactDisplayName(addContactsUri, rawContactId, name);
        insertContactPhoneNumber(addContactsUri, rawContactId, phone);
        Log.d(TAG, "Đã thêm contact: " + name + " - " + phone);
        Toast.makeText(this, "Đã thêm: " + name, Toast.LENGTH_SHORT).show();
        loadContacts();
    }

    private long getRawContactId() {
        ContentValues values = new ContentValues();
        Uri rawContactUri = getContentResolver().insert(
                ContactsContract.RawContacts.CONTENT_URI, values);
        return ContentUris.parseId(rawContactUri);
    }

    private void insertContactDisplayName(Uri uri, long rawId, String displayName) {
        ContentValues values = new ContentValues();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawId);
        values.put(ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, displayName);
        getContentResolver().insert(uri, values);
    }

    private void insertContactPhoneNumber(Uri uri, long rawId, String phoneNumber) {
        ContentValues values = new ContentValues();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawId);
        values.put(ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber);
        getContentResolver().insert(uri, values);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UPDATE CONTACT (BỔ SUNG)
    // ─────────────────────────────────────────────────────────────────────────

    private void showEditContactDialog(MyContact contact) {
        // Tái sử dụng layout dialog_add_contact để nhập liệu
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_contact, null);
        EditText edtName  = dialogView.findViewById(R.id.edtContactName);
        EditText edtPhone = dialogView.findViewById(R.id.edtContactPhone);

        // Hiển thị sẵn dữ liệu cũ lên ô nhập liệu
        edtName.setText(contact.getName());
        edtPhone.setText(contact.getPhone());

        new AlertDialog.Builder(this)
                .setTitle("Cập nhật thông tin")
                .setView(dialogView)
                .setPositiveButton("Cập nhật", (dialog, which) -> {
                    String newName  = edtName.getText().toString().trim();
                    String newPhone = edtPhone.getText().toString().trim();
                    if (newName.isEmpty() || newPhone.isEmpty()) {
                        Toast.makeText(this, "Vui lòng điền đủ thông tin!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    updateContact(contact.getId(), newName, newPhone);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void updateContact(String contactId, String newName, String newPhone) {
        try {
            // 1. Cập nhật tên hiển thị dựa vào ID danh bạ
            ContentValues nameValues = new ContentValues();
            nameValues.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, newName);

            String nameWhere = ContactsContract.Data.CONTACT_ID + " = ? AND "
                    + ContactsContract.Data.MIMETYPE + " = ?";
            String[] nameArgs = new String[]{contactId, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE};

            getContentResolver().update(ContactsContract.Data.CONTENT_URI, nameValues, nameWhere, nameArgs);

            // 2. Cập nhật số điện thoại dựa vào ID danh bạ
            ContentValues phoneValues = new ContentValues();
            phoneValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, newPhone);

            String phoneWhere = ContactsContract.Data.CONTACT_ID + " = ? AND "
                    + ContactsContract.Data.MIMETYPE + " = ?";
            String[] phoneArgs = new String[]{contactId, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE};

            getContentResolver().update(ContactsContract.Data.CONTENT_URI, phoneValues, phoneWhere, phoneArgs);

            Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
            loadContacts(); // Tải lại danh sách danh bạ thời gian thực
        } catch (Exception e) {
            Log.e(TAG, "Lỗi cập nhật danh bạ: " + e.getMessage());
            Toast.makeText(this, "Không thể cập nhật!", Toast.LENGTH_SHORT).show();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DELETE CONTACT (BỔ SUNG)
    // ─────────────────────────────────────────────────────────────────────────

    private void deleteContact(String contactId) {
        try {
            // Định vị Uri của liên hệ cụ thể qua ID của nó
            Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, contactId);
            int deletedRows = getContentResolver().delete(uri, null, null);

            if (deletedRows > 0) {
                Toast.makeText(this, "Đã xóa liên hệ thành công!", Toast.LENGTH_SHORT).show();
                loadContacts(); // Cập nhật lại ListView
            } else {
                // Thử cách dự phòng qua ID gốc nếu cách Lookup Uri không chạy được ở một số máy ảo
                String where = ContactsContract.RawContacts.CONTACT_ID + " = ?";
                String[] args = new String[]{contactId};
                getContentResolver().delete(ContactsContract.RawContacts.CONTENT_URI, where, args);
                Toast.makeText(this, "Đã xóa liên hệ!", Toast.LENGTH_SHORT).show();
                loadContacts();
            }
        } catch (Exception e) {
            Log.e(TAG, "Lỗi xóa danh bạ: " + e.getMessage());
            Toast.makeText(this, "Không thể xóa liên hệ!", Toast.LENGTH_SHORT).show();
        }
    }
}