package com.nhynhi.k23411tapp;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.nhynhi.adapters.ProductAdapter;
import com.nhynhi.dals.ProductDAL;
import com.nhynhi.models.Product;
import java.util.ArrayList;

public class ProductSqliteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_sqlite);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Sản phẩm");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Nhận category filter từ CategorySqliteActivity (null = hiển thị tất cả)
        String categoryFilter = getIntent().getStringExtra("CATEGORY_NAME");
        String categoryId     = getIntent().getStringExtra("CATEGORY_ID");

        if (categoryFilter != null) {
            toolbar.setTitle("Sản phẩm — " + categoryFilter);
        }

        ArrayList<Product> list = new ProductDAL(this).getByCategory(categoryFilter);

        String countLabel = categoryFilter != null
                ? list.size() + " sản phẩm trong \"" + categoryFilter + "\""
                : list.size() + " sản phẩm";
        ((TextView) findViewById(R.id.tvCount)).setText(countLabel);

        ListView lv = findViewById(R.id.lvData);
        lv.setAdapter(new ProductAdapter(this, list));
    }
}
