package com.nhynhi.k23411tapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.nhynhi.adapters.ProductAdapter;
import com.nhynhi.models.DataWareHouse;
import com.nhynhi.models.Product;

import java.util.ArrayList;

public class MultiThreadingObjectActivity extends AppCompatActivity {

    EditText edtNumberOfProduct;
    TextView txtPercent;
    ProgressBar progressBarPercent;
    ListView lvProduct;

    ArrayList<Product> products;
    ProductAdapter adapterProduct;

    Handler mainThread = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            Product p = (Product) msg.obj;
            int percent = msg.arg1;

            if (p != null) {
                products.add(p);
                adapterProduct.notifyDataSetChanged();
            }
            progressBarPercent.setProgress(percent);
            txtPercent.setText(percent + "%");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_multi_threading_object);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addViews();
    }

    private void addViews() {
        edtNumberOfProduct = findViewById(R.id.edtNumberOfProduct);
        txtPercent = findViewById(R.id.textView4);
        progressBarPercent = findViewById(R.id.progressBarPercent);
        lvProduct = findViewById(R.id.lvProduct);

        products = new ArrayList<>();
        adapterProduct = new ProductAdapter(this, products);
        lvProduct.setAdapter(adapterProduct);
    }

    public void processDownloadProduct(View view) {
        String input = edtNumberOfProduct.getText().toString().trim();
        if (input.isEmpty()) {
            edtNumberOfProduct.setError("Nhập số sản phẩm");
            return;
        }
        int n = Integer.parseInt(input);

        products.clear();
        adapterProduct.notifyDataSetChanged();
        progressBarPercent.setMax(100);
        progressBarPercent.setProgress(0);
        txtPercent.setText("0%");

        Thread downloadProductThread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < n; i++) {
                    Product p = DataWareHouse.downloadProduct(i);
                    if (p == null) {
                        break; // giả sử dừng luôn
                    }

                    // lấy message từ MainThread:
                    Message message = mainThread.obtainMessage();
                    message.arg1 = (int) ((i + 1) * 100.0 / n); // tỉ lệ hoàn thành
                    message.obj = p; // product tải thành công ở vị trí i
                    mainThread.sendMessage(message);

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        downloadProductThread.start();
    }
}
