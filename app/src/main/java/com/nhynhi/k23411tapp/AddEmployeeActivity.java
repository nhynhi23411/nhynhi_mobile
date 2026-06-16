package com.nhynhi.k23411tapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.nhynhi.models.Employee;

public class AddEmployeeActivity extends AppCompatActivity {
    EditText edtId, edtName, edtPhone;
    AutoCompleteTextView actBirthPlace;
    ImageView btnSave, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);

        edtId = findViewById(R.id.edtId);
        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        actBirthPlace = findViewById(R.id.actvBirthplace);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        String[] places = getResources().getStringArray(R.array.provinces_and_cities_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, places);
        actBirthPlace.setAdapter(adapter);

        btnSave.setOnClickListener(v -> {
            String id = edtId.getText().toString().trim();
            String name = edtName.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();

            if (id.isEmpty() || name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            } else {
                Intent result = new Intent();
                result.putExtra("NEW_EMPLOYEE", new Employee(id, name, phone));
                setResult(RESULT_OK, result);
                finish();
            }
        });

        btnCancel.setOnClickListener(v -> finish());
    }
}