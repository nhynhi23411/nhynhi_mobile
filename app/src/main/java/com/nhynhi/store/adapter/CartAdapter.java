package com.nhynhi.store.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nhynhi.k23411tapp.R;
import com.nhynhi.store.StoreFormat;
import com.nhynhi.store.model.CartItem;

import java.util.List;

/** Danh sách dòng giỏ hàng với nút tăng/giảm/xóa. */
public class CartAdapter extends BaseAdapter {

    public interface OnCartChange {
        void onIncrease(CartItem item);
        void onDecrease(CartItem item);
        void onRemove(CartItem item);
    }

    private final Context context;
    private final List<CartItem> items;
    private final OnCartChange listener;

    public CartAdapter(Context context, List<CartItem> items, OnCartChange listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @Override public int getCount() { return items.size(); }
    @Override public Object getItem(int position) { return items.get(position); }
    @Override public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        }
        CartItem item = items.get(position);

        ImageView img = convertView.findViewById(R.id.imgCart);
        TextView tvName = convertView.findViewById(R.id.tvCartName);
        TextView tvPrice = convertView.findViewById(R.id.tvCartPrice);
        TextView tvQty = convertView.findViewById(R.id.tvCartQty);
        TextView tvLine = convertView.findViewById(R.id.tvCartLineTotal);
        View btnPlus = convertView.findViewById(R.id.btnPlus);
        View btnMinus = convertView.findViewById(R.id.btnMinus);
        View btnRemove = convertView.findViewById(R.id.btnRemoveCart);

        tvName.setText(item.getProduct().getProductName());
        tvPrice.setText(StoreFormat.money(item.getProduct().getPrice()));
        tvQty.setText(String.valueOf(item.getQuantity()));
        tvLine.setText(StoreFormat.money(item.getLineTotal()));

        Glide.with(context).load(item.getProduct().getImageUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(img);

        btnPlus.setOnClickListener(v -> { if (listener != null) listener.onIncrease(item); });
        btnMinus.setOnClickListener(v -> { if (listener != null) listener.onDecrease(item); });
        btnRemove.setOnClickListener(v -> { if (listener != null) listener.onRemove(item); });

        return convertView;
    }
}
