package com.nhynhi.k23411tapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.nhynhi.store.Cart;
import com.nhynhi.store.StoreFormat;
import com.nhynhi.store.StoreRepository;
import com.nhynhi.store.adapter.StoreProductAdapter;
import com.nhynhi.store.model.SCategory;
import com.nhynhi.store.model.SProduct;

import java.util.ArrayList;
import java.util.List;

/** Màn hình mua sắm cho Client: tìm kiếm, lọc danh mục, xem & thêm vào giỏ. */
public class StoreClientActivity extends AppCompatActivity {

    private ProgressBar progress;
    private GridView grid;
    private TextInputEditText edtSearch;
    private android.widget.LinearLayout chipContainer;
    private TextView tvResultCount;
    private MaterialButton btnCart;

    private final List<SProduct> allProducts = new ArrayList<>();
    private final List<SProduct> shown = new ArrayList<>();
    private StoreProductAdapter adapter;
    private StoreRepository.StoreData data;

    private String selectedCategoryId = null; // null = tất cả
    private String query = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_client);

        MaterialToolbar toolbar = findViewById(R.id.toolbarClient);
        toolbar.setNavigationOnClickListener(v -> finish());

        progress = findViewById(R.id.progressClient);
        grid = findViewById(R.id.gridProducts);
        edtSearch = findViewById(R.id.edtSearch);
        chipContainer = findViewById(R.id.chipContainer);
        tvResultCount = findViewById(R.id.tvResultCount);
        btnCart = findViewById(R.id.btnCart);

        adapter = new StoreProductAdapter(this, shown, this::addToCart, this::showDetail);
        grid.setAdapter(adapter);

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) { }
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {
                query = s.toString().trim().toLowerCase();
                applyFilter();
            }
            @Override public void afterTextChanged(Editable s) { }
        });

        btnCart.setOnClickListener(v -> startActivity(new Intent(this, StoreCartActivity.class)));

        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartBadge();
    }

    private void loadData() {
        progress.setVisibility(View.VISIBLE);
        new StoreRepository().loadAll(new StoreRepository.DataCallback() {
            @Override public void onData(StoreRepository.StoreData d) {
                progress.setVisibility(View.GONE);
                data = d;
                allProducts.clear();
                for (SProduct p : d.products) if (p.isActive()) allProducts.add(p);
                buildChips();
                applyFilter();
            }
            @Override public void onError(String message) {
                progress.setVisibility(View.GONE);
                Toast.makeText(StoreClientActivity.this, "Lỗi tải dữ liệu: " + message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void buildChips() {
        chipContainer.removeAllViews();
        addChip("Tất cả", null);
        for (SCategory c : data.categories) addChip(c.getCategoryName(), c.getId());
    }

    private void addChip(String label, String categoryId) {
        MaterialButton chip = (MaterialButton) LayoutInflater.from(this)
                .inflate(R.layout.item_category_chip, chipContainer, false);
        chip.setText(label);
        boolean selected = (categoryId == null && selectedCategoryId == null)
                || (categoryId != null && categoryId.equals(selectedCategoryId));
        styleChip(chip, selected);
        chip.setOnClickListener(v -> {
            selectedCategoryId = categoryId;
            buildChips();      // vẽ lại để cập nhật trạng thái chọn
            applyFilter();
        });
        chipContainer.addView(chip);
    }

    private void styleChip(MaterialButton chip, boolean selected) {
        if (selected) {
            chip.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF1565C0));
            chip.setTextColor(0xFFFFFFFF);
        } else {
            chip.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFFFFFFF));
            chip.setTextColor(0xFF1565C0);
        }
    }

    /** Lọc theo danh mục + tìm kiếm (khớp tên sản phẩm HOẶC tên danh mục). */
    private void applyFilter() {
        shown.clear();
        for (SProduct p : allProducts) {
            if (selectedCategoryId != null && !selectedCategoryId.equals(p.getCategoryId())) continue;
            if (!query.isEmpty()) {
                String name = p.getProductName() == null ? "" : p.getProductName().toLowerCase();
                SCategory cat = data.categoryMap.get(p.getCategoryId());
                String catName = cat != null && cat.getCategoryName() != null ? cat.getCategoryName().toLowerCase() : "";
                if (!name.contains(query) && !catName.contains(query)) continue;
            }
            shown.add(p);
        }
        adapter.notifyDataSetChanged();
        tvResultCount.setText(shown.size() + " sản phẩm");
    }

    private void addToCart(SProduct p) {
        Cart.get().add(p, 1);
        updateCartBadge();
        Toast.makeText(this, "Đã thêm: " + p.getProductName(), Toast.LENGTH_SHORT).show();
    }

    private void updateCartBadge() {
        int n = Cart.get().totalQuantity();
        btnCart.setText(n > 0 ? "🛒 " + n : "🛒");
    }

    /** Dialog chi tiết sản phẩm với bộ chọn số lượng. */
    private void showDetail(SProduct p) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_product_detail, null);
        ImageView img = view.findViewById(R.id.imgDetail);
        TextView tvName = view.findViewById(R.id.tvDetailName);
        TextView tvPrice = view.findViewById(R.id.tvDetailPrice);
        TextView tvStock = view.findViewById(R.id.tvDetailStock);
        TextView tvCat = view.findViewById(R.id.tvDetailCat);
        TextView tvQty = view.findViewById(R.id.tvDetailQty);
        View btnPlus = view.findViewById(R.id.btnDetailPlus);
        View btnMinus = view.findViewById(R.id.btnDetailMinus);

        Glide.with(this).load(p.getImageUrl()).placeholder(android.R.drawable.ic_menu_gallery).into(img);
        tvName.setText(p.getProductName());
        tvPrice.setText(StoreFormat.money(p.getPrice()));
        tvStock.setText("Tồn kho: " + p.getStock());
        SCategory cat = data.categoryMap.get(p.getCategoryId());
        tvCat.setText(cat != null ? cat.getCategoryName() : p.getCategoryId());

        final int[] qty = {1};
        tvQty.setText("1");
        btnPlus.setOnClickListener(v -> { if (qty[0] < Math.max(1, p.getStock())) { qty[0]++; tvQty.setText(String.valueOf(qty[0])); } });
        btnMinus.setOnClickListener(v -> { if (qty[0] > 1) { qty[0]--; tvQty.setText(String.valueOf(qty[0])); } });

        new AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton("Thêm vào giỏ", (d, w) -> {
                    Cart.get().add(p, qty[0]);
                    updateCartBadge();
                    Toast.makeText(this, "Đã thêm " + qty[0] + " x " + p.getProductName(), Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Đóng", null)
                .show();
    }
}
