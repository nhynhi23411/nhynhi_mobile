package com.nhynhi.k23411tapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

/** Màn hình trung tâm của Database Store: chọn vai trò Admin hoặc Client. */
public class StoreHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_home);

        MaterialToolbar toolbar = findViewById(R.id.toolbarStore);
        toolbar.setNavigationOnClickListener(v -> finish());

        findViewById(R.id.cardAdmin).setOnClickListener(v ->
                startActivity(new Intent(this, StoreAdminActivity.class)));
        findViewById(R.id.cardClient).setOnClickListener(v ->
                startActivity(new Intent(this, StoreClientActivity.class)));
    }
}
