package com.nhynhi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.nhynhi.k23411tapp.R;
import com.nhynhi.models.MyContact;

import java.util.ArrayList;

public class ContactAdapter extends ArrayAdapter<MyContact> {

    private static class ViewHolder {
        TextView tvAvatar, tvName, tvPhone;
    }

    public ContactAdapter(Context context, ArrayList<MyContact> list) {
        super(context, 0, list);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_contact, parent, false);
            holder = new ViewHolder();
            holder.tvAvatar = convertView.findViewById(R.id.tvAvatar);
            holder.tvName   = convertView.findViewById(R.id.tvName);
            holder.tvPhone  = convertView.findViewById(R.id.tvPhone);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MyContact contact = getItem(position);
        if (contact != null) {
            String name = contact.getName();
            holder.tvAvatar.setText(name.isEmpty() ? "#" : String.valueOf(name.charAt(0)).toUpperCase());
            holder.tvName.setText(name);
            holder.tvPhone.setText(contact.getPhone().isEmpty() ? "Không có số" : contact.getPhone());
        }
        return convertView;
    }
}
