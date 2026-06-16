package com.nhynhi.k23411tapp;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.nhynhi.adapters.CustomerAdapter;
import com.nhynhi.dals.CustomerDAL;
import com.nhynhi.models.Customer;
import java.util.ArrayList;

public class CustomerSqliteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_sqlite);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Khách hàng");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        ArrayList<Customer> list = new CustomerDAL(this).getAll();

        ((TextView) findViewById(R.id.tvCount)).setText(list.size() + " khách hàng");

        ListView lv = findViewById(R.id.lvData);
        lv.setAdapter(new CustomerAdapter(this, list));
    }
}
