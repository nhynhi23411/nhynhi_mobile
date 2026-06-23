package com.nhynhi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.nhynhi.k23411tapp.R;
import com.nhynhi.models.FirebaseContact;

import java.util.List;

public class FirebaseContactAdapter extends ArrayAdapter<FirebaseContact> {

    private static class ViewHolder {
        TextView tvAvatar, tvName, tvEmail, tvPhone;
    }

    public FirebaseContactAdapter(Context context, List<FirebaseContact> list) {
        super(context, 0, list);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_firebase_contact, parent, false);
            holder           = new ViewHolder();
            holder.tvAvatar  = convertView.findViewById(R.id.tvFbAvatar);
            holder.tvName    = convertView.findViewById(R.id.tvFbName);
            holder.tvEmail   = convertView.findViewById(R.id.tvFbEmail);
            holder.tvPhone   = convertView.findViewById(R.id.tvFbPhone);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        FirebaseContact c = getItem(position);
        if (c != null) {
            String name = c.getName() != null ? c.getName() : "";
            holder.tvAvatar.setText(name.isEmpty() ? "#" :
                    String.valueOf(name.charAt(0)).toUpperCase());
            holder.tvName.setText(name);
            holder.tvEmail.setText(c.getEmail() != null ? c.getEmail() : "");
            holder.tvPhone.setText(c.getPhone() != null ? c.getPhone() : "");
        }
        return convertView;
    }
}
