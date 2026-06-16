package com.nhynhi.adapters;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nhynhi.k23411tapp.R;
import com.nhynhi.models.Employee;

import java.util.List;

public class EmployeeAdapter extends ArrayAdapter<Employee> {
    private Activity context;
    private int resource;

    public EmployeeAdapter(Activity context, int resource, List<Employee> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Sử dụng LayoutInflater từ Activity
        LayoutInflater inflater = context.getLayoutInflater();
        View customView = inflater.inflate(resource, parent, false);

        // Lấy đối tượng dữ liệu tại vị trí position
        Employee employee = getItem(position);

        // Ánh xạ các View
        TextView txtID = customView.findViewById(R.id.txtID);
        TextView txtName = customView.findViewById(R.id.txtName);
        TextView txtPhone = customView.findViewById(R.id.txtPhone);
        ImageView imgCall = customView.findViewById(R.id.imgCall);
        ImageView imgSms = customView.findViewById(R.id.imgSms);

        if (employee != null) {
            // Gán dữ liệu văn bản
            txtID.setText(employee.getId());
            txtName.setText(employee.getName());
            txtPhone.setText(employee.getPhone());

            // Xử lý sự kiện Gọi điện (ACTION_DIAL mở bàn phím số, không cần xin quyền CALL_PHONE)
            imgCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intentCall = new Intent(Intent.ACTION_DIAL);
                    intentCall.setData(Uri.parse("tel:" + employee.getPhone()));
                    context.startActivity(intentCall);
                }
            });

            // Xử lý sự kiện Nhắn tin (ACTION_SENDTO mở ứng dụng tin nhắn mặc định)
            imgSms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intentSms = new Intent(Intent.ACTION_SENDTO);
                    intentSms.setData(Uri.parse("smsto:" + employee.getPhone()));
                    context.startActivity(intentSms);
                }
            });
        }

        return customView;
    }
}