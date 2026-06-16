package com.nhynhi.k23411tapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.nhynhi.adapters.OrderAdapter;
import com.nhynhi.models.DataWareHouse;
import com.nhynhi.models.Order;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;

public class OrderManagmentActivity extends AppCompatActivity {
    ImageView imgFromDate, imgToDate, imgClear, imgFilter;
    EditText etFromDate, etToDate;
    TextView tvCurrentStatus;
    ListView lvOrderList;
    ArrayList<Order> allOrders;
    OrderAdapter adapter;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    private String selectedStatus = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_managment);
        addViews();

        allOrders = DataWareHouse.getOrders();
        adapter = new OrderAdapter(this, allOrders);
        lvOrderList.setAdapter(adapter);

        imgFromDate.setOnClickListener(v -> showDatePicker(etFromDate));
        imgToDate.setOnClickListener(v -> showDatePicker(etToDate));

        imgFilter.setOnClickListener(v -> applyFilters());
        imgClear.setOnClickListener(v -> {
            etFromDate.setText("");
            etToDate.setText("");
            selectedStatus = "All";
            tvCurrentStatus.setText(R.string.str_oder_status_all);
            applyFilters();
        });

        lvOrderList.setOnItemClickListener((parent, view, position, id) -> {
            Order selectedOrder = (Order) parent.getItemAtPosition(position);
            android.content.Intent intent = new android.content.Intent(OrderManagmentActivity.this, OrderDetailActivity.class);
            intent.putExtra("ORDER_DATA", selectedOrder);
            startActivity(intent);
        });
    }

    private void addViews() {
        imgFromDate = findViewById(R.id.imgFromDate);
        etFromDate = findViewById(R.id.etFromDate);
        imgToDate = findViewById(R.id.imgToDate);
        etToDate = findViewById(R.id.etToDate);
        lvOrderList = findViewById(R.id.lvOrderList);
        tvCurrentStatus = findViewById(R.id.tvCurrentStatus);
        imgClear = findViewById(R.id.imgClear);
        imgFilter = findViewById(R.id.imgFilter);
    }

    private void showDatePicker(EditText et) {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, (view, y, m, d) -> {
            cal.set(y, m, d);
            et.setText(sdf.format(cal.getTime()));
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void applyFilters() {
        String fromStr = etFromDate.getText().toString().trim();
        String toStr = etToDate.getText().toString().trim();

        Date fromDate = null;
        Date toDate = null;

        try {
            if (!fromStr.isEmpty()) {
                fromDate = sdf.parse(fromStr);
                Calendar calFrom = Calendar.getInstance();
                calFrom.setTime(fromDate);
                calFrom.set(Calendar.HOUR_OF_DAY, 0);
                calFrom.set(Calendar.MINUTE, 0);
                calFrom.set(Calendar.SECOND, 0);
                calFrom.set(Calendar.MILLISECOND, 0);
                fromDate = calFrom.getTime();
            }

            if (!toStr.isEmpty()) {
                toDate = sdf.parse(toStr);
                Calendar calTo = Calendar.getInstance();
                calTo.setTime(toDate);
                calTo.set(Calendar.HOUR_OF_DAY, 23);
                calTo.set(Calendar.MINUTE, 59);
                calTo.set(Calendar.SECOND, 59);
                calTo.set(Calendar.MILLISECOND, 999);
                toDate = calTo.getTime();
            }

            if (fromDate != null && toDate != null && fromDate.after(toDate)) {
                Toast.makeText(this, "Từ ngày không thể lớn hơn Đến ngày!", Toast.LENGTH_SHORT).show();
                return;
            }

            ArrayList<Order> filtered = new ArrayList<>();
            for (Order o : allOrders) {
                boolean matchesStatus = selectedStatus.equalsIgnoreCase("All") || 
                                       (o.getOrder_status() != null && o.getOrder_status().equalsIgnoreCase(selectedStatus));
                
                boolean matchesDate = true;
                if (fromDate != null && o.getOrderDate().before(fromDate)) matchesDate = false;
                if (toDate != null && o.getOrderDate().after(toDate)) matchesDate = false;

                if (matchesStatus && matchesDate) {
                    filtered.add(o);
                }
            }
            adapter = new OrderAdapter(this, filtered);
            lvOrderList.setAdapter(adapter);
        } catch (Exception e) {
            Toast.makeText(this, "Định dạng ngày không hợp lệ!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.order_menu_status, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.mnu_oder_status_all) {
            selectedStatus = "All";
            tvCurrentStatus.setText(R.string.str_oder_status_all);
        }
        else if (id == R.id.mnu_oder_status_completed) {
            selectedStatus = "Completed";
            tvCurrentStatus.setText(R.string.str_oder_status_completed);
        }
        else if (id == R.id.mnu_oder_status_not_payment) {
            selectedStatus = "Not Payment";
            tvCurrentStatus.setText(R.string.str_oder_status_not_payment);
        }
        else if (id == R.id.mnu_oder_status_on_logistic) {
            selectedStatus = "On Logistic";
            tvCurrentStatus.setText(R.string.str_oder_status_on_logistic);
        }
        else if (id == R.id.mnu_oder_status_complain) {
            selectedStatus = "Complain";
            tvCurrentStatus.setText(R.string.str_oder_status_complain);
        } else {
            return super.onOptionsItemSelected(item);
        }

        applyFilters();
        return true;
    }

    private void filterOrdersByStatus(String status, String statusDisplayName) {
        tvCurrentStatus.setText(statusDisplayName);
        ArrayList<Order> filtered = new ArrayList<>();
        if (status.equalsIgnoreCase("All")) {
            filtered.addAll(allOrders);
        } else {
            for (Order o : allOrders) {
                if (o.getOrder_status() != null && o.getOrder_status().equalsIgnoreCase(status)) {
                    filtered.add(o);
                }
            }
        }
        adapter = new OrderAdapter(this, filtered);
        lvOrderList.setAdapter(adapter);
    }
}