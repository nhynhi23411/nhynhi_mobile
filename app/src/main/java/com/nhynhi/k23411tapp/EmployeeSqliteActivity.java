package com.nhynhi.k23411tapp;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.nhynhi.adapters.EmployeeSqliteAdapter;
import com.nhynhi.dals.EmployeeDAL;
import com.nhynhi.models.Employee;
import java.util.ArrayList;

public class EmployeeSqliteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_sqlite);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Nhân viên");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        ArrayList<Employee> list = new EmployeeDAL(this).getAll();

        ((TextView) findViewById(R.id.tvCount)).setText(list.size() + " nhân viên");

        ListView lv = findViewById(R.id.lvData);
        lv.setAdapter(new EmployeeSqliteAdapter(this, list));
    }
}
