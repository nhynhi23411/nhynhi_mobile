package com.nhynhi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.nhynhi.k23411tapp.R;
import com.nhynhi.models.Employee;
import java.util.ArrayList;

public class EmployeeSqliteAdapter extends ArrayAdapter<Employee> {

    private static class ViewHolder {
        TextView tvBadge, tvTitle, tvSub1, tvSub2;
    }

    public EmployeeSqliteAdapter(Context context, ArrayList<Employee> list) {
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

        Employee item = getItem(position);
        if (item != null) {
            holder.tvBadge.setText(item.getId());
            holder.tvTitle.setText(item.getName());
            holder.tvSub1.setText("💼 " + (item.getPosition() != null ? item.getPosition() : "—"));
            holder.tvSub2.setText("📞 " + item.getPhone());
            holder.tvSub2.setVisibility(View.VISIBLE);
        }
        return convertView;
    }
}
