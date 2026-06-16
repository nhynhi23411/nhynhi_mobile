package com.nhynhi.k23411tapp;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Random;

public class EmployeeManagementActivity extends AppCompatActivity {
    EditText edtID, edtName, edtPhone;
    Button btnSave, btnDelete, btnExit;
    ListView listView;

    ArrayList<String> ListofEmployee;
    ArrayAdapter<String> adapterEmployee;

    int selectedPosition = -1;

    private static final String PREF_NAME = "EmployeePrefs";
    private static final String KEY_SELECTED_POSITION = "selected_position";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employee_management);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.lvEmployee), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        addViews();
        sampleData(); // Đã cập nhật lại để tạo nhiều nhân viên
        addEvents();
        restoreSelectedPosition();
    }

    private void addViews() {
        edtID = findViewById(R.id.edtID);
        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDel);
        btnExit = findViewById(R.id.btnExit);
        listView = findViewById(R.id.listView);

        ListofEmployee = new ArrayList<>();
        adapterEmployee = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ListofEmployee) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                // Highlight dòng được chọn
                view.setBackgroundColor(position == selectedPosition ? Color.parseColor("#FFF59D") : Color.TRANSPARENT);
                return view;
            }
        };
        listView.setAdapter(adapterEmployee);
    }

    private void addEvents() {
        listView.setOnItemClickListener((parent, view, position, id) -> {
            selectedPosition = position;
            saveSelectedPosition(position);
            adapterEmployee.notifyDataSetChanged();
            parseToEdit(ListofEmployee.get(position));
        });

        btnSave.setOnClickListener(v -> {
            String id = edtID.getText().toString().trim();
            String name = edtName.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();

            if (id.isEmpty() || name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, R.string.msg_empty_info, Toast.LENGTH_SHORT).show();
                return;
            }

            String info = getString(R.string.id) + ": " + id + " | " +
                    getString(R.string.name) + ": " + name + " | " +
                    getString(R.string.phone) + ": " + phone;

            int pos = findPos(id);
            if (pos != -1) {
                ListofEmployee.set(pos, info);
                Toast.makeText(this, R.string.msg_update_success, Toast.LENGTH_SHORT).show();
            } else {
                ListofEmployee.add(info);
                Toast.makeText(this, R.string.msg_save_success, Toast.LENGTH_SHORT).show();
            }
            adapterEmployee.notifyDataSetChanged();
            clearInputs();
        });

        btnDelete.setOnClickListener(v -> {
            if (selectedPosition != -1) {
                ListofEmployee.remove(selectedPosition);
                clearInputs();
                Toast.makeText(this, R.string.msg_del_success, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.msg_select_to_del, Toast.LENGTH_SHORT).show();
            }
        });

        btnExit.setOnClickListener(this::exitSystem);
    }

    // Tìm vị trí dựa trên ID (Dùng vòng lặp linh hoạt theo size danh sách)
    private int findPos(String id) {
        for (int i = 0; i < ListofEmployee.size(); i++) {
            if (ListofEmployee.get(i).contains(": " + id + " |")) return i;
        }
        return -1;
    }

    private void parseToEdit(String data) {
        try {
            String[] parts = data.split(" \\| ");
            edtID.setText(parts[0].split(": ")[1]);
            edtName.setText(parts[1].split(": ")[1]);
            edtPhone.setText(parts[2].split(": ")[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearInputs() {
        edtID.setText(""); edtName.setText(""); edtPhone.setText("");
        edtID.requestFocus();
        selectedPosition = -1;
        saveSelectedPosition(-1);
        adapterEmployee.notifyDataSetChanged();
    }

    private void saveSelectedPosition(int p) {
        sharedPreferences.edit().putInt(KEY_SELECTED_POSITION, p).apply();
    }

    private void restoreSelectedPosition() {
        int p = sharedPreferences.getInt(KEY_SELECTED_POSITION, -1);
        if (p != -1 && p < ListofEmployee.size()) {
            selectedPosition = p;
            parseToEdit(ListofEmployee.get(p));
            adapterEmployee.notifyDataSetChanged();
        }
    }

    private void sampleData() {
        String lblID = getString(R.string.id);
        String lblNm = getString(R.string.name);
        String lblPh = getString(R.string.phone);

        // Thêm Admin mặc định
        ListofEmployee.add(lblID + ": NV001 | " + lblNm + ": Admin | " + lblPh + ": 0900000000");

        // Tạo thêm 20 nhân viên ngẫu nhiên
        Random random = new Random();
        String[] firstNames = {"John", "Emma", "Michael", "Sophia", "William", "Olivia", "James", "Ava"};
        String[] lastNames = {"Smith", "Johnson", "Brown", "Taylor", "Miller", "Wilson", "Moore"};

        for (int i = 2; i <= 21; i++) {
            String id = "NV" + String.format("%03d", i);
            String name = firstNames[random.nextInt(firstNames.length)] + " " +
                    lastNames[random.nextInt(lastNames.length)];
            String phone = "09" + String.format("%08d", random.nextInt(100000000));

            ListofEmployee.add(lblID + ": " + id + " | " + lblNm + ": " + name + " | " + lblPh + ": " + phone);
        }

        adapterEmployee.notifyDataSetChanged();
    }

    public void exitSystem(View view) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        ImageView btnYes = dialog.findViewById(R.id.imgBtnYes);
        ImageView btnNo = dialog.findViewById(R.id.imgBtnNo);

        btnYes.setOnClickListener(v -> finish());
        btnNo.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}