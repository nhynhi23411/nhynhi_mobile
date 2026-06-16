package com.nhynhi.k23411tapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.nhynhi.adapters.CategoryAdapter;
import com.nhynhi.dals.CategoryDAO;
import com.nhynhi.dals.CategoryDAL;
import com.nhynhi.models.Category;

import java.util.ArrayList;

public class CategorySqliteActivity extends AppCompatActivity {

    private ArrayList<Category> list;
    private CategoryAdapter adapter;
    private TextView tvCount;

    private final ActivityResultLauncher<Intent> addLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    reloadData();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_sqlite);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Danh mục sản phẩm");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        tvCount = findViewById(R.id.tvCount);
        ListView lv = findViewById(R.id.lvData);

        list = new CategoryDAL(this).getAll();
        adapter = new CategoryAdapter(this, list);
        lv.setAdapter(adapter);
        tvCount.setText(list.size() + " danh mục");

        lv.setOnItemClickListener((parent, view, position, id) -> {
            Category selected = list.get(position);
            Intent intent = new Intent(this, ProductSqliteActivity.class);
            intent.putExtra("CATEGORY_ID", selected.getCategoryID());
            intent.putExtra("CATEGORY_NAME", selected.getCategoryName());
            startActivity(intent);
        });

        lv.setOnItemLongClickListener((parent, view, position, id) -> {
            Category selected = list.get(position);
            new AlertDialog.Builder(this)
                    .setTitle("Xóa danh mục")
                    .setMessage("Bạn có muốn xóa danh mục \"" + selected.getCategoryName() + "\" không?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        boolean success = CategoryDAO.deleteCategory(this, selected);
                        if (success) {
                            Toast.makeText(this, "Đã xóa danh mục!", Toast.LENGTH_SHORT).show();
                            reloadData();
                        } else {
                            Toast.makeText(this, "Xóa thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
            return true;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.category_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_add_category) {
            addLauncher.launch(new Intent(this, CategoryNewActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void reloadData() {
        list.clear();
        list.addAll(new CategoryDAL(this).getAll());
        adapter.notifyDataSetChanged();
        tvCount.setText(list.size() + " danh mục");
    }
}
