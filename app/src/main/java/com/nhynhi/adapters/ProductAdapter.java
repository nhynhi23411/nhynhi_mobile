package com.nhynhi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.nhynhi.k23411tapp.R;
import com.nhynhi.models.Product;
import java.util.ArrayList;
import java.util.Locale;

public class ProductAdapter extends ArrayAdapter<Product> {

    private static class ViewHolder {
        TextView tvBadge, tvTitle, tvSub1, tvSub2;
    }

    public ProductAdapter(Context context, ArrayList<Product> list) {
        super(context, 0, list);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_list_card, parent, false);
            holder = new ViewHolder();
            holder.tvBadge = convertView.findViewById(R.id.tvBadge);
            holder.tvTitle = convertView.findViewById(R.id.tvTitle);
            holder.tvSub1  = convertView.findViewById(R.id.tvSub1);
            holder.tvSub2  = convertView.findViewById(R.id.tvSub2);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Product item = getItem(position);
        if (item != null) {
            holder.tvBadge.setText(item.getProductID());
            holder.tvTitle.setText(item.getProductName());
            holder.tvSub1.setText(String.format(Locale.getDefault(),
                    "💰 %,.0f đ", item.getPrice()));
            holder.tvSub2.setText("📦 Tồn kho: " + item.getQuantity());
            holder.tvSub2.setVisibility(View.VISIBLE);
        }
        return convertView;
    }
}
