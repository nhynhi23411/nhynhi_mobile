package com.nhynhi.k23411tapp;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.nhynhi.adapters.OrderAdapter;
import com.nhynhi.models.MyDatabaseHelper;
import com.nhynhi.models.Order;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class OrderSqliteActivity extends AppCompatActivity {
    private ListView lvOrderSqlite;
    private ArrayList<Order> orderList;
    private OrderAdapter adapter;
    private MyDatabaseHelper dbHelper;
    private SimpleDateFormat sqliteSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat displaySdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    private TextView tvFromDate, tvToDate, tvResultCount;
    private Spinner spnStatus;
    private View btnFilter;
    private View btnClearFilter;
    private View emptyView;

    private final String[] statusArray = {"All", "On Logistic", "Not Payment", "Completed", "Complain"};

    private Calendar calendarFrom = Calendar.getInstance();
    private Calendar calendarTo = Calendar.getInstance();
    private boolean isFromDateSelected = false;
    private boolean isToDateSelected = false;
    private boolean isSpinnerReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_sqlite);

        MaterialToolbar toolbar = findViewById(R.id.toolbarSqlite);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        lvOrderSqlite = findViewById(R.id.lvOrderSqlite);
        orderList = new ArrayList<>();
        
        // Tái sử dụng OrderAdapter đã có của bạn
        adapter = new OrderAdapter(this, orderList);
        lvOrderSqlite.setAdapter(adapter);

        dbHelper = new MyDatabaseHelper(this);
        initViews();

        // Nạp sẵn tổng tiền của tất cả đơn trong 1 truy vấn -> list cuộn mượt, không lag.
        adapter.setTotals(dbHelper.getAllOrderTotals());
        loadOrdersFromSqlite(null, null, "All");

        lvOrderSqlite.setOnItemClickListener((parent, view, position, id) -> {
            Order selectedOrder = orderList.get(position);
            android.content.Intent intent = new android.content.Intent(OrderSqliteActivity.this, OrderDetailActivity.class);
            intent.putExtra("ORDER_DATA", selectedOrder);
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.order_menu_status, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull android.view.MenuItem item) {
        int id = item.getItemId();
        String status;

        if (id == R.id.mnu_oder_status_all) status = "All";
        else if (id == R.id.mnu_oder_status_completed) status = "Completed";
        else if (id == R.id.mnu_oder_status_not_payment) status = "Not Payment";
        else if (id == R.id.mnu_oder_status_on_logistic) status = "On Logistic";
        else if (id == R.id.mnu_oder_status_complain) status = "Complain";
        else return super.onOptionsItemSelected(item);

        // Đồng bộ lựa chọn từ menu vào Spinner để tránh lệch trạng thái giữa 2 nơi.
        setSpinnerStatus(status);
        applyCurrentFilter();
        return true;
    }

    private void setSpinnerStatus(String status) {
        for (int i = 0; i < statusArray.length; i++) {
            if (statusArray[i].equals(status)) {
                spnStatus.setSelection(i);
                return;
            }
        }
    }

    /** Đọc trạng thái bộ lọc hiện tại trên giao diện rồi tải lại danh sách. */
    private void applyCurrentFilter() {
        if (isFromDateSelected && isToDateSelected && calendarFrom.after(calendarTo)) {
            Toast.makeText(this, "Từ ngày không thể lớn hơn Đến ngày!", Toast.LENGTH_SHORT).show();
            return;
        }
        String from = isFromDateSelected ? sqliteSdf.format(calendarFrom.getTime()) : null;
        String to = isToDateSelected ? sqliteSdf.format(calendarTo.getTime()) : null;
        String status = spnStatus.getSelectedItem().toString();
        loadOrdersFromSqlite(from, to, status);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Phản ánh ngay các thay đổi đã lưu (vd: đổi trạng thái ở màn hình chi tiết).
        if (spnStatus != null) {
            adapter.setTotals(dbHelper.getAllOrderTotals());
            applyCurrentFilter();
        }
    }

    private void initViews() {
        tvFromDate = findViewById(R.id.tvFromDate);
        tvToDate = findViewById(R.id.tvToDate);
        tvResultCount = findViewById(R.id.tvResultCount);
        emptyView = findViewById(R.id.emptyView);
        spnStatus = findViewById(R.id.spnStatus);
        btnFilter = findViewById(R.id.btnFilter);
        btnClearFilter = findViewById(R.id.btnClearFilter);

        // Setup Spinner
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statusArray);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnStatus.setAdapter(statusAdapter);

        // Tự động lọc ngay khi đổi trạng thái trên Spinner (không cần bấm FILTER).
        spnStatus.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (isSpinnerReady) {
                    applyCurrentFilter();
                }
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) { }
        });

        // Date Pickers
        tvFromDate.setOnClickListener(v -> showDatePicker(true));
        tvToDate.setOnClickListener(v -> showDatePicker(false));

        btnFilter.setOnClickListener(v -> applyCurrentFilter());

        btnClearFilter.setOnClickListener(v -> {
            isFromDateSelected = false;
            isToDateSelected = false;
            tvFromDate.setText("");
            tvToDate.setText("");
            spnStatus.setSelection(0);
            loadOrdersFromSqlite(null, null, "All");
        });

        // Cho phép Spinner tự lọc cho các lần chọn sau (bỏ qua lần callback khởi tạo).
        isSpinnerReady = true;
    }

    private void showDatePicker(boolean isFrom) {
        Calendar cal = isFrom ? calendarFrom : calendarTo;
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            
            if (isFrom) {
                isFromDateSelected = true;
                tvFromDate.setText(displaySdf.format(cal.getTime()));
            } else {
                isToDateSelected = true;
                tvToDate.setText(displaySdf.format(cal.getTime()));
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void loadOrdersFromSqlite(String fromDate, String toDate, String status) {
        try {
            SQLiteDatabase db = dbHelper.openDatabase();
            
            StringBuilder query = new StringBuilder("SELECT OrderID, CustomerID, EmployeeID, OrderDate, OrderStatus, " +
                    "ShipAddress, ShipCity, ShipPhone, Freight, Notes FROM Orders WHERE 1=1");
            ArrayList<String> args = new ArrayList<>();

            if (fromDate != null) {
                query.append(" AND OrderDate >= ?");
                args.add(fromDate);
            }
            if (toDate != null) {
                query.append(" AND OrderDate <= ?");
                args.add(toDate);
            }
            if (status != null && !status.equals("All")) {
                query.append(" AND OrderStatus = ?");
                args.add(status);
            }

            Cursor cursor = db.rawQuery(query.toString(), args.toArray(new String[0]));
            
            orderList.clear();
            if (cursor.moveToFirst()) {
                do {
                    Order order = new Order();
                    order.setOrderID(cursor.getString(0));
                    order.setCustomerID(cursor.getString(1));
                    order.setEmployeeID(cursor.getString(2));
                    
                    // Chuyển đổi Ngày
                    try {
                        order.setOrderDate(sqliteSdf.parse(cursor.getString(3)));
                    } catch (Exception e) {
                        order.setOrderDate(new java.util.Date());
                    }
                    
                    order.setOrder_status(cursor.getString(4));
                    order.setShipAddress(cursor.getString(5));
                    order.setShipCity(cursor.getString(6));
                    order.setShipPhone(cursor.getString(7));
                    order.setFreight(cursor.getDouble(8));
                    order.setNotes(cursor.getString(9));
                    
                    orderList.add(order);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
            adapter.notifyDataSetChanged();

            // Cập nhật số lượng kết quả và trạng thái rỗng
            tvResultCount.setText(orderList.size() + " đơn hàng");
            emptyView.setVisibility(orderList.isEmpty() ? View.VISIBLE : View.GONE);

        } catch (Exception e) {
            Log.e("SQLITE_ERR", "Error: ", e);
            Toast.makeText(this, "Lỗi kết nối: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}