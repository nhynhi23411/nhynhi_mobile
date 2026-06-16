package com.nhynhi.k23411tapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.nhynhi.models.UserAccount;

public class MainActivity extends AppCompatActivity {
    TextView txtWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        addViews();

        Intent intent = getIntent();
        UserAccount user = (UserAccount) intent.getSerializableExtra("user");
        if (user != null) {
            txtWelcome.setText("Welcome, " + user.getDisplayName());
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void addViews() {
        txtWelcome = findViewById(R.id.txtWelcome);
    }

    public void click_me(View view){
        Toast.makeText(this, "Hello!", Toast.LENGTH_SHORT).show();
    }

    public void openOrderManagement(View view) {
        Intent intent = new Intent(this, OrderManagmentActivity.class);
        startActivity(intent);
    }

    public void openOrderSqlite(View view) {
        Intent intent = new Intent(this, OrderSqliteActivity.class);
        startActivity(intent);
    }

    public void openCalculatorApp(View view) {
        Intent intent = new Intent(this, CalculatorActivity.class);
        startActivity(intent);
    }

    public void openCategorySqlite(View view) {
        startActivity(new Intent(this, CategorySqliteActivity.class));
    }

    public void openCustomerSqlite(View view) {
        startActivity(new Intent(this, CustomerSqliteActivity.class));
    }

    public void openEmployeeSqlite(View view) {
        startActivity(new Intent(this, EmployeeSqliteActivity.class));
    }

    public void openProductSqlite(View view) {
        startActivity(new Intent(this, ProductSqliteActivity.class));
    }

    public void openAddCategory(View view) {
        startActivity(new Intent(this, CategoryNewActivity.class));
    }

    public void openMyContact(View view) {
        startActivity(new Intent(this, MyContactActivity.class));
    }

    public void openSmsActivity(View view) {
        startActivity(new Intent(this, SMSActivity.class));
    }

    public void openMultiThreading(View view) {
        startActivity(new Intent(this, MultiThreadingActivity.class));
    }

    public void openMultiThreading2(View view) {
        startActivity(new Intent(this, MultiThreadingObjectActivity.class));
    }

    public void openGoldPrice(View view) {
        startActivity(new Intent(this, GoldPriceActivity.class));
    }

    public void exitSystem(View view) {
        finishAffinity();
    }
}