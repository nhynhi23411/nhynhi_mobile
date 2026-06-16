package com.nhynhi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.core.content.ContextCompat;

import com.nhynhi.k23411tapp.R;
import com.nhynhi.models.DataWareHouse;
import com.nhynhi.models.Order;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class OrderAdapter extends ArrayAdapter<Order> {
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    // Cache tổng tiền theo OrderID để KHÔNG phải truy vấn DB lại cho mỗi dòng khi cuộn.
    private final HashMap<String, Double> totalCache = new HashMap<>();

    private static class ViewHolder {
        TextView tvId, tvDate, tvStatus, tvTotal;
        View viewStatusBar;
    }

    public OrderAdapter(Context context, ArrayList<Order> orders) {
        super(context, 0, orders);
    }

    /**
     * Nạp sẵn tổng tiền của tất cả đơn (đã tính từ DB trong 1 truy vấn).
     * Gọi 1 lần từ Activity sau khi mở màn hình -> list hiển thị mượt, không lag.
     */
    public void setTotals(HashMap<String, Double> totals) {
        totalCache.clear();
        if (totals != null) {
            totalCache.putAll(totals);
        }
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.oder_custom_item, parent, false);
            holder = new ViewHolder();
            holder.tvId        = convertView.findViewById(R.id.tvOrderID);
            holder.tvDate      = convertView.findViewById(R.id.tvOrderDate);
            holder.tvStatus    = convertView.findViewById(R.id.tvOrderStatus);
            holder.tvTotal     = convertView.findViewById(R.id.tvOrderTotal);
            holder.viewStatusBar = convertView.findViewById(R.id.viewStatusBar);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Order order = getItem(position);
        if (order != null) {
            holder.tvId.setText(order.getOrderID());
            holder.tvDate.setText(sdf.format(order.getOrderDate()));

            String status = order.getOrder_status();
            holder.tvStatus.setText(status);

            // Màu text + nền pill + thanh bên trái theo trạng thái
            int textColor, bgColor, barColor;
            switch (status != null ? status : "") {
                case "Completed":
                    textColor = R.color.status_completed;
                    bgColor   = R.color.status_completed_bg;
                    barColor  = R.color.status_completed;
                    break;
                case "On Logistic":
                    textColor = R.color.status_on_logistic;
                    bgColor   = R.color.status_on_logistic_bg;
                    barColor  = R.color.status_on_logistic;
                    break;
                case "Not Payment":
                    textColor = R.color.status_not_payment;
                    bgColor   = R.color.status_not_payment_bg;
                    barColor  = R.color.status_not_payment;
                    break;
                case "Complain":
                    textColor = R.color.status_complain;
                    bgColor   = R.color.status_complain_bg;
                    barColor  = R.color.status_complain;
                    break;
                default:
                    textColor = R.color.gray_700;
                    bgColor   = R.color.gray_100;
                    barColor  = R.color.gray_500;
            }
            holder.tvStatus.setTextColor(ContextCompat.getColor(getContext(), textColor));
            // mutate() để mỗi view có bản drawable riêng — tránh tint lan sang các item khác trong list
            android.graphics.drawable.Drawable bg = holder.tvStatus.getBackground();
            if (bg != null) {
                bg.mutate().setTint(ContextCompat.getColor(getContext(), bgColor));
            }
            holder.viewStatusBar.setBackgroundColor(ContextCompat.getColor(getContext(), barColor));

            // Tổng tiền từ cache
            Double cached = totalCache.get(order.getOrderID());
            double total;
            if (cached != null) {
                total = cached;
            } else {
                total = DataWareHouse.SumOfMoneyForOrder(order);
                totalCache.put(order.getOrderID(), total);
            }
            holder.tvTotal.setText(String.format(Locale.getDefault(), "%,.0f đ", total));
        }

        return convertView;
    }
}