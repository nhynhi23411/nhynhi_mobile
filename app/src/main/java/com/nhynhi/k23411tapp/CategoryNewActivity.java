package com.nhynhi.k23411tapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.nhynhi.dals.CategoryDAO;
import com.nhynhi.models.Category;

public class CategoryNewActivity extends AppCompatActivity {

    private EditText edtCategoryId, edtCategoryName, edtCategoryDescription;
    private Button btnSave, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category_new);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtCategoryId = findViewById(R.id.edtCategoryId);
        edtCategoryName = findViewById(R.id.edtCategoryName);
        edtCategoryDescription = findViewById(R.id.edtCategoryDescription);
        btnSave = findViewById(R.id.button2);
        btnCancel = findViewById(R.id.button3);

        btnSave.setOnClickListener(v -> saveCategory());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void saveCategory() {
        String id = edtCategoryId.getText().toString().trim();
        String name = edtCategoryName.getText().toString().trim();
        String desc = edtCategoryDescription.getText().toString().trim();

        if (id.isEmpty()) {
            edtCategoryId.setError("Vui lòng nhập mã danh mục");
            edtCategoryId.requestFocus();
            return;
        }
        if (name.isEmpty()) {
            edtCategoryName.setError("Vui lòng nhập tên danh mục");
            edtCategoryName.requestFocus();
            return;
        }

        Category category = new Category(id, name, desc);
        long result = CategoryDAO.saveNewCategory(this, category);

        if (result != -1) {
            Toast.makeText(this, "Thêm danh mục thành công!", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Thất bại! Mã danh mục đã tồn tại.", Toast.LENGTH_SHORT).show();
        }
    }
}
