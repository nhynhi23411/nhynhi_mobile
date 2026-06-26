package com.nhynhi.k23411tapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.nhynhi.store.StoreFormat;
import com.nhynhi.store.StoreRepository;
import com.nhynhi.store.model.SCategory;
import com.nhynhi.store.model.SCustomer;
import com.nhynhi.store.model.SOrder;
import com.nhynhi.store.model.SOrderDetail;
import com.nhynhi.store.model.SProduct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Dashboard thống kê doanh số cho Admin. */
public class StoreAdminActivity extends AppCompatActivity {

    private ProgressBar progress;
    private View content;

    private TextView tvRevenue, tvRevenueDone, tvOrders, tvProducts, tvCustomers, tvStatusBreak;
    private LinearLayout containerCategory, containerTopProducts, containerTopCustomers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_admin);

        MaterialToolbar toolbar = findViewById(R.id.toolbarAdmin);
        toolbar.setNavigationOnClickListener(v -> finish());

        progress = findViewById(R.id.progressAdmin);
        content = findViewById(R.id.contentAdmin);
        tvRevenue = findViewById(R.id.tvRevenue);
        tvRevenueDone = findViewById(R.id.tvRevenueDone);
        tvOrders = findViewById(R.id.tvOrders);
        tvProducts = findViewById(R.id.tvProducts);
        tvCustomers = findViewById(R.id.tvCustomers);
        tvStatusBreak = findViewById(R.id.tvStatusBreak);
        containerCategory = findViewById(R.id.containerCategory);
        containerTopProducts = findViewById(R.id.containerTopProducts);
        containerTopCustomers = findViewById(R.id.containerTopCustomers);

        loadData();
    }

    private void loadData() {
        progress.setVisibility(View.VISIBLE);
        content.setVisibility(View.GONE);
        new StoreRepository().loadAll(new StoreRepository.DataCallback() {
            @Override public void onData(StoreRepository.StoreData data) {
                progress.setVisibility(View.GONE);
                content.setVisibility(View.VISIBLE);
                bind(data);
            }
            @Override public void onError(String message) {
                progress.setVisibility(View.GONE);
                Toast.makeText(StoreAdminActivity.this, "Lỗi tải dữ liệu: " + message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void bind(StoreRepository.StoreData data) {
        // ── Tổng quan ──────────────────────────────────────────────────────
        double totalRevenue = 0, doneRevenue = 0;
        Map<String, Integer> statusCount = new LinkedHashMap<>();
        for (SOrder o : data.orders) {
            totalRevenue += o.getTotalAmount();
            if ("Completed".equalsIgnoreCase(o.getStatus())) doneRevenue += o.getTotalAmount();
            String st = o.getStatus() == null ? "?" : o.getStatus();
            statusCount.put(st, statusCount.getOrDefault(st, 0) + 1);
        }
        tvRevenue.setText(StoreFormat.money(totalRevenue));
        tvRevenueDone.setText("Đã hoàn tất: " + StoreFormat.money(doneRevenue));
        tvOrders.setText(String.valueOf(data.orders.size()));
        tvProducts.setText(String.valueOf(data.products.size()));
        tvCustomers.setText(String.valueOf(data.customers.size()));

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> e : statusCount.entrySet()) {
            if (sb.length() > 0) sb.append("   ");
            sb.append("• ").append(e.getKey()).append(": ").append(e.getValue());
        }
        tvStatusBreak.setText(sb.toString());

        // ── Doanh thu theo danh mục (dựa trên orderDetails) ─────────────────
        Map<String, Double> revByCat = new HashMap<>();
        for (SOrderDetail d : data.orderDetails) {
            SProduct p = data.productMap.get(d.getProductId());
            if (p == null) continue;
            String catId = p.getCategoryId();
            revByCat.put(catId, revByCat.getOrDefault(catId, 0d) + d.getUnitPrice() * d.getQuantity());
        }
        buildCategoryBars(data, revByCat);

        // ── Top sản phẩm bán chạy (theo số lượng bán) ───────────────────────
        Map<String, Integer> qtyByProduct = new HashMap<>();
        Map<String, Double> revByProduct = new HashMap<>();
        for (SOrderDetail d : data.orderDetails) {
            qtyByProduct.put(d.getProductId(), qtyByProduct.getOrDefault(d.getProductId(), 0) + d.getQuantity());
            revByProduct.put(d.getProductId(), revByProduct.getOrDefault(d.getProductId(), 0d) + d.getUnitPrice() * d.getQuantity());
        }
        buildTopProducts(data, qtyByProduct, revByProduct);

        // ── Top khách hàng (theo tổng chi tiêu) ─────────────────────────────
        Map<String, Double> spentByCustomer = new HashMap<>();
        Map<String, Integer> ordersByCustomer = new HashMap<>();
        for (SOrder o : data.orders) {
            spentByCustomer.put(o.getCustomerId(), spentByCustomer.getOrDefault(o.getCustomerId(), 0d) + o.getTotalAmount());
            ordersByCustomer.put(o.getCustomerId(), ordersByCustomer.getOrDefault(o.getCustomerId(), 0) + 1);
        }
        buildTopCustomers(data, spentByCustomer, ordersByCustomer);
    }

    // ── Biểu đồ cột doanh thu theo danh mục ────────────────────────────────
    private void buildCategoryBars(StoreRepository.StoreData data, Map<String, Double> revByCat) {
        containerCategory.removeAllViews();
        double max = 0;
        for (double v : revByCat.values()) max = Math.max(max, v);
        if (max <= 0) max = 1;

        // Sắp xếp danh mục theo doanh thu giảm dần
        List<SCategory> cats = new ArrayList<>(data.categories);
        Collections.sort(cats, (a, b) -> Double.compare(
                revByCat.getOrDefault(b.getId(), 0d), revByCat.getOrDefault(a.getId(), 0d)));

        for (SCategory cat : cats) {
            double v = revByCat.getOrDefault(cat.getId(), 0d);
            View row = LayoutInflater.from(this).inflate(R.layout.item_bar, containerCategory, false);
            TextView label = row.findViewById(R.id.tvBarLabel);
            TextView value = row.findViewById(R.id.tvBarValue);
            View fill = row.findViewById(R.id.barFill);
            View rest = row.findViewById(R.id.barRest);

            label.setText(cat.getCategoryName());
            value.setText(StoreFormat.money(v));

            float ratio = (float) (v / max);
            if (ratio < 0.02f && v > 0) ratio = 0.02f;
            ((LinearLayout.LayoutParams) fill.getLayoutParams()).weight = ratio;
            ((LinearLayout.LayoutParams) rest.getLayoutParams()).weight = 1f - ratio;
            fill.requestLayout();
            rest.requestLayout();

            containerCategory.addView(row);
        }
    }

    private void buildTopProducts(StoreRepository.StoreData data,
                                  Map<String, Integer> qtyByProduct, Map<String, Double> revByProduct) {
        containerTopProducts.removeAllViews();
        List<String> ids = new ArrayList<>(qtyByProduct.keySet());
        Collections.sort(ids, (a, b) -> qtyByProduct.get(b) - qtyByProduct.get(a));

        int rank = 1;
        for (String pid : ids) {
            SProduct p = data.productMap.get(pid);
            if (p == null) continue;
            String title = p.getProductName();
            String sub = "Đã bán: " + qtyByProduct.get(pid) + " • " + StoreFormat.money(revByProduct.getOrDefault(pid, 0d));
            addStatRow(containerTopProducts, rank++, title, sub, "x" + qtyByProduct.get(pid), p.getImageUrl());
        }
        if (rank == 1) emptyRow(containerTopProducts);
    }

    private void buildTopCustomers(StoreRepository.StoreData data,
                                   Map<String, Double> spentByCustomer, Map<String, Integer> ordersByCustomer) {
        containerTopCustomers.removeAllViews();
        List<String> ids = new ArrayList<>(spentByCustomer.keySet());
        Collections.sort(ids, (a, b) -> Double.compare(spentByCustomer.get(b), spentByCustomer.get(a)));

        int rank = 1;
        for (String cid : ids) {
            SCustomer c = data.customerMap.get(cid);
            String name = c != null ? c.getFullName() : cid;
            String sub = (c != null && c.getEmail() != null ? c.getEmail() + " • " : "")
                    + ordersByCustomer.getOrDefault(cid, 0) + " đơn";
            addStatRow(containerTopCustomers, rank++, name, sub, StoreFormat.money(spentByCustomer.get(cid)), null);
        }
        if (rank == 1) emptyRow(containerTopCustomers);
    }

    private void addStatRow(LinearLayout container, int rank, String title, String sub, String value, String imageUrl) {
        View row = LayoutInflater.from(this).inflate(R.layout.item_stat_row, container, false);
        TextView tvRank = row.findViewById(R.id.tvRank);
        TextView tvTitle = row.findViewById(R.id.tvStatTitle);
        TextView tvSub = row.findViewById(R.id.tvStatSub);
        TextView tvValue = row.findViewById(R.id.tvStatValue);
        android.widget.ImageView img = row.findViewById(R.id.imgStat);

        tvRank.setText(String.valueOf(rank));
        switch (rank) {
            case 1: tvRank.setBackgroundColor(0xFFF9A825); break;
            case 2: tvRank.setBackgroundColor(0xFF90A4AE); break;
            case 3: tvRank.setBackgroundColor(0xFFA1887F); break;
            default: tvRank.setBackgroundColor(0xFF1565C0); break;
        }
        tvTitle.setText(title);
        tvSub.setText(sub);
        tvValue.setText(value);
        if (imageUrl != null && !imageUrl.isEmpty()) {
            img.setVisibility(View.VISIBLE);
            Glide.with(this).load(imageUrl).placeholder(android.R.drawable.ic_menu_gallery).into(img);
        } else {
            img.setVisibility(View.GONE);
        }
        container.addView(row);
    }

    private void emptyRow(ViewGroup container) {
        TextView tv = new TextView(this);
        tv.setText("Chưa có dữ liệu");
        tv.setPadding(24, 24, 24, 24);
        tv.setTextColor(0xFF9E9E9E);
        container.addView(tv);
    }
}
