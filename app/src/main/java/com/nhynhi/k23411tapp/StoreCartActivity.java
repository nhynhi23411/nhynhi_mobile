package com.nhynhi.k23411tapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nhynhi.store.Cart;
import com.nhynhi.store.StoreFormat;
import com.nhynhi.store.StoreRepository;
import com.nhynhi.store.adapter.CartAdapter;
import com.nhynhi.store.model.CartItem;
import com.nhynhi.store.model.SCustomer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/** Giỏ hàng + thanh toán (ghi đơn hàng mới lên Firebase). */
public class StoreCartActivity extends AppCompatActivity {

    private ListView lvCart;
    private TextView tvEmpty, tvTotal;
    private MaterialButton btnCheckout;
    private CartAdapter adapter;

    private final List<SCustomer> customers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_cart);

        MaterialToolbar toolbar = findViewById(R.id.toolbarCart);
        toolbar.setNavigationOnClickListener(v -> finish());

        lvCart = findViewById(R.id.lvCart);
        tvEmpty = findViewById(R.id.tvCartEmpty);
        tvTotal = findViewById(R.id.tvCartTotal);
        btnCheckout = findViewById(R.id.btnCheckout);

        adapter = new CartAdapter(this, Cart.get().getItems(), new CartAdapter.OnCartChange() {
            @Override public void onIncrease(CartItem item) { Cart.get().increase(item); refresh(); }
            @Override public void onDecrease(CartItem item) { Cart.get().decrease(item); refresh(); }
            @Override public void onRemove(CartItem item) { Cart.get().remove(item); refresh(); }
        });
        lvCart.setAdapter(adapter);

        btnCheckout.setOnClickListener(v -> startCheckout());

        // Nạp danh sách khách hàng để chọn khi thanh toán
        new StoreRepository().loadAll(new StoreRepository.DataCallback() {
            @Override public void onData(StoreRepository.StoreData d) { customers.addAll(d.customers); }
            @Override public void onError(String message) { }
        });

        refresh();
    }

    private void refresh() {
        adapter.notifyDataSetChanged();
        boolean empty = Cart.get().isEmpty();
        tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        lvCart.setVisibility(empty ? View.GONE : View.VISIBLE);
        btnCheckout.setEnabled(!empty);
        tvTotal.setText(StoreFormat.money(Cart.get().totalAmount()));
    }

    private void startCheckout() {
        if (Cart.get().isEmpty()) return;
        if (customers.isEmpty()) {
            Toast.makeText(this, "Đang tải danh sách khách hàng, thử lại sau giây lát...", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] names = new String[customers.size()];
        for (int i = 0; i < customers.size(); i++) names[i] = customers.get(i).getFullName();

        Spinner spinner = new Spinner(this);
        spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, names));
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        spinner.setPadding(pad, pad, pad, pad);

        new AlertDialog.Builder(this)
                .setTitle("Đặt hàng cho khách")
                .setMessage("Tổng tiền: " + StoreFormat.money(Cart.get().totalAmount()))
                .setView(spinner)
                .setPositiveButton("Xác nhận đặt", (d, w) ->
                        placeOrder(customers.get(spinner.getSelectedItemPosition())))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void placeOrder(SCustomer customer) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        String orderId = rootRef.child("orders").push().getKey();
        if (orderId == null) { Toast.makeText(this, "Không tạo được mã đơn", Toast.LENGTH_SHORT).show(); return; }

        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        fmt.setTimeZone(TimeZone.getTimeZone("UTC"));

        Map<String, Object> order = new HashMap<>();
        order.put("customerId", customer.getId());
        order.put("employeeId", "EMP001");
        order.put("orderDate", fmt.format(new Date()));
        order.put("status", "Pending");
        order.put("totalAmount", Cart.get().totalAmount());

        Map<String, Object> updates = new HashMap<>();
        updates.put("/orders/" + orderId, order);

        int i = 1;
        for (CartItem item : Cart.get().getItems()) {
            Map<String, Object> det = new HashMap<>();
            det.put("orderId", orderId);
            det.put("productId", item.getProduct().getId());
            det.put("quantity", item.getQuantity());
            det.put("unitPrice", item.getProduct().getPrice());
            updates.put("/orderDetails/" + orderId + "_" + i, det);
            i++;
        }

        rootRef.updateChildren(updates)
                .addOnSuccessListener(unused -> {
                    Cart.get().clear();
                    refresh();
                    Toast.makeText(this, "Đặt hàng thành công! Mã đơn: " + orderId, Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi đặt hàng: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
