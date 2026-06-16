package com.nhynhi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.nhynhi.k23411tapp.R;
import com.nhynhi.models.Category;
import java.util.ArrayList;

public class CategoryAdapter extends ArrayAdapter<Category> {

    private static class ViewHolder {
        TextView tvBadge, tvTitle, tvSub1;
    }

    public CategoryAdapter(Context context, ArrayList<Category> list) {
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
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Category item = getItem(position);
        if (item != null) {
            holder.tvBadge.setText(item.getCategoryID());
            holder.tvTitle.setText(item.getCategoryName());
            holder.tvSub1.setText(item.getDescription());
            convertView.findViewById(R.id.tvSub2).setVisibility(View.GONE);
        }
        return convertView;
    }
}
