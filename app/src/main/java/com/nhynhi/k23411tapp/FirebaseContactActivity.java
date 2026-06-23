package com.nhynhi.k23411tapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nhynhi.adapters.FirebaseContactAdapter;
import com.nhynhi.models.FirebaseContact;
import com.nhynhi.models.FirebaseContactDbHelper;

import java.util.ArrayList;
import java.util.List;

public class FirebaseContactActivity extends AppCompatActivity {

    private static final String TAG      = "FirebaseContactAct";
    private static final String FB_NODE  = "Contacts";

    private ListView                lvContacts;
    private TextView                tvCount;
    private TextView                tvNetworkStatus;
    private List<FirebaseContact>   contactList;
    private FirebaseContactAdapter  adapter;
    private FirebaseContactDbHelper dbHelper;
    private DatabaseReference       dbRef;
    private ValueEventListener      firebaseListener;

    // ─────────────────────────────────────────────────────────────────────────
    // LIFECYCLE
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_contact);

        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbarFb);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Views
        lvContacts      = findViewById(R.id.lvFirebaseContacts);
        tvCount         = findViewById(R.id.tvFbCount);
        tvNetworkStatus = findViewById(R.id.tvNetworkStatus);

        // SQLite helper
        dbHelper    = new FirebaseContactDbHelper(this);
        contactList = new ArrayList<>();
        adapter     = new FirebaseContactAdapter(this, contactList);
        lvContacts.setAdapter(adapter);

        // Firebase reference
        dbRef = FirebaseDatabase.getInstance().getReference(FB_NODE);

        // Long press → menu Edit / Delete
        lvContacts.setOnItemLongClickListener((parent, view, position, id) -> {
            showActionMenu(contactList.get(position));
            return true;
        });

        // FAB → Thêm mới
        FloatingActionButton fab = findViewById(R.id.fabAddFbContact);
        fab.setOnClickListener(v -> showAddDialog());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isOnline()) {
            attachFirebaseListener();
            showOnlineBadge();
        } else {
            loadFromSQLite();
            showOfflineBadge();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        detachFirebaseListener();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // KIỂM TRA MẠNG
    // ─────────────────────────────────────────────────────────────────────────

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        NetworkCapabilities cap = cm.getNetworkCapabilities(cm.getActiveNetwork());
        return cap != null && (cap.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                || cap.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                || cap.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
    }

    private void showOnlineBadge() {
        tvNetworkStatus.setText(getString(R.string.fb_status_online));
        tvNetworkStatus.setBackgroundColor(0xFF43A047);
        tvNetworkStatus.setVisibility(View.VISIBLE);
    }

    private void showOfflineBadge() {
        tvNetworkStatus.setText(getString(R.string.fb_status_offline));
        tvNetworkStatus.setBackgroundColor(0xFFE53935);
        tvNetworkStatus.setVisibility(View.VISIBLE);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // FIREBASE REALTIME LISTENER
    // ─────────────────────────────────────────────────────────────────────────

    private void attachFirebaseListener() {
        firebaseListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<FirebaseContact> fetched = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    FirebaseContact c = child.getValue(FirebaseContact.class);
                    if (c != null) {
                        c.setId(child.getKey());
                        fetched.add(c);
                    }
                }
                // Sync toàn bộ vào SQLite local
                dbHelper.replaceAll(fetched);
                // Cập nhật UI
                contactList.clear();
                contactList.addAll(fetched);
                adapter.notifyDataSetChanged();
                updateCount();
                Log.d(TAG, "Firebase sync: " + fetched.size() + " contacts");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Firebase error: " + error.getMessage());
                Toast.makeText(FirebaseContactActivity.this,
                        getString(R.string.fb_error_load) + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
                loadFromSQLite();
            }
        };
        dbRef.addValueEventListener(firebaseListener);
    }

    private void detachFirebaseListener() {
        if (firebaseListener != null) {
            dbRef.removeEventListener(firebaseListener);
            firebaseListener = null;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // LOAD TỪ SQLITE (OFFLINE)
    // ─────────────────────────────────────────────────────────────────────────

    private void loadFromSQLite() {
        List<FirebaseContact> local = dbHelper.getAll();
        contactList.clear();
        contactList.addAll(local);
        adapter.notifyDataSetChanged();
        updateCount();
        Log.d(TAG, "SQLite offline load: " + local.size() + " contacts");
    }

    private void updateCount() {
        int n = contactList.size();
        tvCount.setText(getResources().getQuantityString(R.plurals.fb_contact_count, n, n));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DIALOG: THÊM MỚI
    // ─────────────────────────────────────────────────────────────────────────

    private void showAddDialog() {
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_firebase_contact, null);
        EditText edtName  = dialogView.findViewById(R.id.edtFbName);
        EditText edtEmail = dialogView.findViewById(R.id.edtFbEmail);
        EditText edtPhone = dialogView.findViewById(R.id.edtFbPhone);

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.fb_dialog_add_title))
                .setView(dialogView)
                .setPositiveButton(getString(R.string.str_save), (dialog, which) -> {
                    String name  = edtName.getText().toString().trim();
                    String email = edtEmail.getText().toString().trim();
                    String phone = edtPhone.getText().toString().trim();

                    if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                        Toast.makeText(this, getString(R.string.msg_empty_info),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    createContact(name, email, phone);
                })
                .setNegativeButton(getString(R.string.str_cancel), null)
                .show();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DIALOG: MENU HÀNH ĐỘNG (khi long press)
    // ─────────────────────────────────────────────────────────────────────────

    private void showActionMenu(FirebaseContact contact) {
        String[] options = {
                getString(R.string.fb_action_edit),
                getString(R.string.fb_action_delete)
        };
        new AlertDialog.Builder(this)
                .setTitle(contact.getName())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) showEditDialog(contact);
                    else            confirmDelete(contact);
                })
                .show();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DIALOG: CHỈNH SỬA
    // ─────────────────────────────────────────────────────────────────────────

    private void showEditDialog(FirebaseContact contact) {
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_firebase_contact, null);
        EditText edtName  = dialogView.findViewById(R.id.edtFbName);
        EditText edtEmail = dialogView.findViewById(R.id.edtFbEmail);
        EditText edtPhone = dialogView.findViewById(R.id.edtFbPhone);

        edtName.setText(contact.getName());
        edtEmail.setText(contact.getEmail());
        edtPhone.setText(contact.getPhone());

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.fb_dialog_edit_title))
                .setView(dialogView)
                .setPositiveButton(getString(R.string.fb_action_update), (dialog, which) -> {
                    String name  = edtName.getText().toString().trim();
                    String email = edtEmail.getText().toString().trim();
                    String phone = edtPhone.getText().toString().trim();

                    if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                        Toast.makeText(this, getString(R.string.msg_empty_info),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    updateContact(contact.getId(), name, email, phone);
                })
                .setNegativeButton(getString(R.string.str_cancel), null)
                .show();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DIALOG: XÁC NHẬN XÓA
    // ─────────────────────────────────────────────────────────────────────────

    private void confirmDelete(FirebaseContact contact) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.fb_dialog_delete_title))
                .setMessage(getString(R.string.fb_dialog_delete_msg, contact.getName()))
                .setPositiveButton(getString(R.string.str_del), (d, w) -> deleteContact(contact))
                .setNegativeButton(getString(R.string.str_cancel), null)
                .show();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CRUD — FIREBASE + SQLITE FALLBACK
    // ─────────────────────────────────────────────────────────────────────────

    // CREATE
    private void createContact(String name, String email, String phone) {
        FirebaseContact newContact = new FirebaseContact(null, name, email, phone);

        if (isOnline()) {
            // Tạo key tự động từ Firebase push()
            DatabaseReference newRef = dbRef.push();
            newContact.setId(newRef.getKey());
            newRef.setValue(newContact)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, getString(R.string.fb_msg_added), Toast.LENGTH_SHORT).show();
                        // Listener sẽ tự cập nhật UI
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, getString(R.string.fb_error_write) + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Offline: sinh ID tạm, lưu SQLite
            newContact.setId("local_" + System.currentTimeMillis());
            dbHelper.upsert(newContact);
            contactList.add(newContact);
            adapter.notifyDataSetChanged();
            updateCount();
            Toast.makeText(this, getString(R.string.fb_msg_saved_local), Toast.LENGTH_SHORT).show();
        }
    }

    // UPDATE
    private void updateContact(String id, String name, String email, String phone) {
        FirebaseContact updated = new FirebaseContact(id, name, email, phone);

        if (isOnline()) {
            dbRef.child(id).setValue(updated)
                    .addOnSuccessListener(unused ->
                            Toast.makeText(this, getString(R.string.msg_update_success),
                                    Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(this, getString(R.string.fb_error_write) + e.getMessage(),
                                    Toast.LENGTH_SHORT).show());
        } else {
            dbHelper.upsert(updated);
            loadFromSQLite();
            Toast.makeText(this, getString(R.string.msg_update_success), Toast.LENGTH_SHORT).show();
        }
    }

    // DELETE
    private void deleteContact(FirebaseContact contact) {
        if (isOnline()) {
            dbRef.child(contact.getId()).removeValue()
                    .addOnSuccessListener(unused ->
                            Toast.makeText(this, getString(R.string.msg_del_success),
                                    Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(this, getString(R.string.fb_error_write) + e.getMessage(),
                                    Toast.LENGTH_SHORT).show());
        } else {
            dbHelper.delete(contact.getId());
            contactList.remove(contact);
            adapter.notifyDataSetChanged();
            updateCount();
            Toast.makeText(this, getString(R.string.msg_del_success), Toast.LENGTH_SHORT).show();
        }
    }
}
