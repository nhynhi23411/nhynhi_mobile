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
import com.nhynhi.store.model.StatRow;

import java.util.List;

/** Adapter cho danh sách Top (Top sản phẩm bán chạy / Top khách hàng). */
public class StatRowAdapter extends BaseAdapter {

    private final Context context;
    private final List<StatRow> rows;

    public StatRowAdapter(Context context, List<StatRow> rows) {
        this.context = context;
        this.rows = rows;
    }

    @Override public int getCount() { return rows.size(); }
    @Override public Object getItem(int position) { return rows.get(position); }
    @Override public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_stat_row, parent, false);
        }
        StatRow r = rows.get(position);

        TextView tvRank = convertView.findViewById(R.id.tvRank);
        TextView tvTitle = convertView.findViewById(R.id.tvStatTitle);
        TextView tvSub = convertView.findViewById(R.id.tvStatSub);
        TextView tvValue = convertView.findViewById(R.id.tvStatValue);
        ImageView img = convertView.findViewById(R.id.imgStat);

        tvRank.setText(String.valueOf(r.rank));
        // Top 3 nổi bật bằng màu vàng/cam
        switch (r.rank) {
            case 1: tvRank.setBackgroundColor(0xFFF9A825); break;
            case 2: tvRank.setBackgroundColor(0xFF90A4AE); break;
            case 3: tvRank.setBackgroundColor(0xFFA1887F); break;
            default: tvRank.setBackgroundColor(0xFF1565C0); break;
        }

        tvTitle.setText(r.title);
        tvSub.setText(r.subtitle);
        tvValue.setText(r.value);

        if (r.imageUrl != null && !r.imageUrl.isEmpty()) {
            img.setVisibility(View.VISIBLE);
            Glide.with(context).load(r.imageUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_report_image)
                    .into(img);
        } else {
            img.setVisibility(View.GONE);
        }
        return convertView;
    }
}
