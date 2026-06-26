package com.nhynhi.store.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.nhynhi.k23411tapp.R;
import com.nhynhi.store.StoreFormat;
import com.nhynhi.store.model.SProduct;

import java.util.List;

/** Lưới sản phẩm cho màn hình Client (ảnh + tên + giá + nút thêm giỏ). */
public class StoreProductAdapter extends BaseAdapter {

    public interface OnAddToCart { void onAdd(SProduct product); }
    public interface OnOpenDetail { void onOpen(SProduct product); }

    private final Context context;
    private final List<SProduct> products;
    private final OnAddToCart addListener;
    private final OnOpenDetail detailListener;

    public StoreProductAdapter(Context context, List<SProduct> products,
                               OnAddToCart addListener, OnOpenDetail detailListener) {
        this.context = context;
        this.products = products;
        this.addListener = addListener;
        this.detailListener = detailListener;
    }

    @Override public int getCount() { return products.size(); }
    @Override public Object getItem(int position) { return products.get(position); }
    @Override public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_store_product, parent, false);
        }
        SProduct p = products.get(position);

        ImageView img = convertView.findViewById(R.id.imgProduct);
        TextView tvName = convertView.findViewById(R.id.tvProductName);
        TextView tvPrice = convertView.findViewById(R.id.tvProductPrice);
        TextView tvStock = convertView.findViewById(R.id.tvProductStock);
        MaterialButton btnAdd = convertView.findViewById(R.id.btnAddCart);

        tvName.setText(p.getProductName());
        tvPrice.setText(StoreFormat.money(p.getPrice()));

        boolean inStock = p.getStock() > 0;
        tvStock.setText(inStock ? "Còn " + p.getStock() : "Hết hàng");
        tvStock.setTextColor(inStock ? 0xFF2E7D32 : 0xFFD32F2F);

        Glide.with(context).load(p.getImageUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .into(img);

        btnAdd.setEnabled(inStock);
        btnAdd.setText(inStock ? "Thêm vào giỏ" : "Hết hàng");
        btnAdd.setOnClickListener(v -> { if (addListener != null) addListener.onAdd(p); });

        img.setOnClickListener(v -> { if (detailListener != null) detailListener.onOpen(p); });
        tvName.setOnClickListener(v -> { if (detailListener != null) detailListener.onOpen(p); });

        return convertView;
    }
}
