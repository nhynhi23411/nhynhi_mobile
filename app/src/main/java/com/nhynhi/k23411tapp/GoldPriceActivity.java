package com.nhynhi.k23411tapp;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Locale;

public class GoldPriceActivity extends AppCompatActivity {

    private static final String API_URL = "https://gw.vnexpress.net/cr/?name=tygia_vangv202206";

    TextView txtUpdateTime;
    LinearLayout containerGold;
    ProgressBar progressBar;
    TextView txtError;

    RequestQueue requestQueue;
    NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gold_price);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtUpdateTime = findViewById(R.id.txtUpdateTime);
        containerGold = findViewById(R.id.containerGold);
        progressBar = findViewById(R.id.progressBar);
        txtError = findViewById(R.id.txtError);

        requestQueue = Volley.newRequestQueue(this);
        fetchGoldPrice();
    }

    public void onRefreshClick(View view) {
        fetchGoldPrice();
    }

    private void fetchGoldPrice() {
        progressBar.setVisibility(View.VISIBLE);
        containerGold.setVisibility(View.GONE);
        txtError.setVisibility(View.GONE);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, API_URL, null,
            response -> {
                try {
                    JSONObject gold = response
                            .getJSONObject("data")
                            .getJSONObject("data")
                            .getJSONObject("gold")
                            .getJSONObject("new");

                    containerGold.removeAllViews();

                    String[] keys = {
                        "sjc_1l_10l", "sjc_2c_1c_5_phan", "sjc_5c",
                        "ha_noi_sjc", "tphcm_sjc", "ha_noi_pnj", "tphcm_pnj",
                        "nu_trang_99.99percent", "nu_trang_99percent", "thegioi"
                    };

                    String dateLabel = "";
                    for (String key : keys) {
                        if (!gold.has(key)) continue;
                        JSONObject item = gold.getJSONObject(key);
                        String label = item.getString("label");
                        double buy = item.getDouble("buy");
                        double sell = item.getDouble("sell");
                        if (dateLabel.isEmpty()) dateLabel = item.optString("date_label", "");

                        boolean isWorld = key.equals("thegioi");
                        addGoldRow(label, buy, sell, isWorld);
                    }

                    txtUpdateTime.setText("Cập nhật: " + dateLabel);
                    progressBar.setVisibility(View.GONE);
                    containerGold.setVisibility(View.VISIBLE);

                } catch (Exception e) {
                    showError();
                }
            },
            error -> showError()
        );

        requestQueue.add(request);
    }

    private void addGoldRow(String label, double buy, double sell, boolean isWorld) {
        View row = getLayoutInflater().inflate(R.layout.item_gold_price, containerGold, false);

        TextView tvLabel = row.findViewById(R.id.tvGoldLabel);
        TextView tvBuy = row.findViewById(R.id.tvGoldBuy);
        TextView tvSell = row.findViewById(R.id.tvGoldSell);

        tvLabel.setText(label);
        if (isWorld) {
            tvBuy.setText(String.format(Locale.US, "%.1f USD", buy));
            tvSell.setText(String.format(Locale.US, "%.1f USD", sell));
        } else {
            tvBuy.setText(nf.format((long) buy) + " đ");
            tvSell.setText(nf.format((long) sell) + " đ");
        }

        containerGold.addView(row);
    }

    private void showError() {
        progressBar.setVisibility(View.GONE);
        txtError.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Không thể tải dữ liệu. Kiểm tra kết nối mạng.", Toast.LENGTH_SHORT).show();
    }
}
