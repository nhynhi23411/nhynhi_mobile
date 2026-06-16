package com.nhynhi.k23411tapp;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.nhynhi.models.DataWareHouse;
import com.nhynhi.models.MyDatabaseHelper;
import com.nhynhi.models.Order;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class OrderDetailActivity extends AppCompatActivity {

    private TextView tvOrderID, tvOrderDate, tvStatus, tvCustomerID, tvEmployeeID, tvTotal;
    private TextView tvShipAddress, tvShipPhone, tvShippedDate, tvFreight, tvNotes;
    private MaterialButton btnUpdateStatus;
    private MaterialToolbar toolbar;
    private Order currentOrder;
    private final String[] statusOptions = {"On Logistic", "Not Payment", "Completed", "Complain"};
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        initViews();
        setupToolbar();
        displayOrderDetails();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvOrderID = findViewById(R.id.tvDetailOrderID);
        tvOrderDate = findViewById(R.id.tvDetailOrderDate);
        tvStatus = findViewById(R.id.tvDetailStatus);
        tvCustomerID = findViewById(R.id.tvDetailCustomerID);
        tvEmployeeID = findViewById(R.id.tvDetailEmployeeID);
        tvTotal = findViewById(R.id.tvDetailTotal);

        tvShipAddress = findViewById(R.id.tvDetailShipAddress);
        tvShipPhone = findViewById(R.id.tvDetailShipPhone);
        tvShippedDate = findViewById(R.id.tvDetailShippedDate);
        tvFreight = findViewById(R.id.tvDetailFreight);
        tvNotes = findViewById(R.id.tvDetailNotes);
        btnUpdateStatus = findViewById(R.id.btnUpdateStatus);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void displayOrderDetails() {
        currentOrder = (Order) getIntent().getSerializableExtra("ORDER_DATA");
        Order order = currentOrder;
        if (order != null) {
            tvOrderID.setText(order.getOrderID());
            tvOrderDate.setText(sdf.format(order.getOrderDate()));
            tvCustomerID.setText(order.getCustomerID());
            tvEmployeeID.setText(order.getEmployeeID());

            applyStatusUi(order.getOrder_status());

            // Populate shipping details
            tvShipAddress.setText(order.getShipAddress() != null ? order.getShipAddress() : "N/A");
            tvShipPhone.setText(order.getShipPhone() != null ? order.getShipPhone() : "N/A");
            tvShippedDate.setText(order.getShippedDate() != null ? order.getShippedDate() : "N/A");
            tvFreight.setText(String.format(Locale.getDefault(), "%,.0f đ", order.getFreight()));
            tvNotes.setText(order.getNotes() != null ? order.getNotes() : "N/A");

            // Calculate Total Amount
            double total;
            boolean isSqliteOrder = order.getOrderID() != null && order.getOrderID().startsWith("ORD-SQL-");
            if (isSqliteOrder) {
                total = new MyDatabaseHelper(this).getOrderTotal(order.getOrderID());
            } else {
                total = DataWareHouse.SumOfMoneyForOrder(order);
            }
            tvTotal.setText(String.format(Locale.getDefault(), "%,.0f đ", total));

            // Chỉ cho phép cập nhật trạng thái với đơn hàng nằm trong SQLite (mới lưu bền vững được).
            if (isSqliteOrder) {
                btnUpdateStatus.setVisibility(android.view.View.VISIBLE);
                btnUpdateStatus.setOnClickListener(v -> showUpdateStatusDialog());
            } else {
                btnUpdateStatus.setVisibility(android.view.View.GONE);
            }
        }
    }

    private void applyStatusUi(String status) {
        tvStatus.setText(status);
        int colorResId = R.color.gray_700;
        if (status != null) {
            switch (status) {
                case "Completed": colorResId = R.color.status_completed; break;
                case "On Logistic": colorResId = R.color.status_on_logistic; break;
                case "Not Payment": colorResId = R.color.status_not_payment; break;
                case "Complain": colorResId = R.color.status_complain; break;
            }
        }
        tvStatus.setTextColor(ContextCompat.getColor(this, colorResId));
    }

    private void showUpdateStatusDialog() {
        int checked = -1;
        String current = currentOrder.getOrder_status();
        for (int i = 0; i < statusOptions.length; i++) {
            if (statusOptions[i].equals(current)) {
                checked = i;
                break;
            }
        }
        final int[] selected = {checked};
        new AlertDialog.Builder(this)
                .setTitle(R.string.order_update_status)
                .setSingleChoiceItems(statusOptions, checked, (d, which) -> selected[0] = which)
                .setPositiveButton("Lưu", (d, w) -> {
                    if (selected[0] < 0) return;
                    String newStatus = statusOptions[selected[0]];
                    boolean ok = new MyDatabaseHelper(this)
                            .updateOrderStatus(currentOrder.getOrderID(), newStatus);
                    if (ok) {
                        currentOrder.setOrder_status(newStatus);
                        applyStatusUi(newStatus);
                        Toast.makeText(this, R.string.order_status_updated, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }
}