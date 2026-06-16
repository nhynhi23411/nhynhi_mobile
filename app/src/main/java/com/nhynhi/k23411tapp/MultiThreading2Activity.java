package com.nhynhi.k23411tapp;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Random;

public class MultiThreading2Activity extends AppCompatActivity {

    TextInputEditText edtNumberOfButtons;
    MaterialButton btnCreate;
    ProgressBar pbCreate;
    TextView tvPercent;
    LinearLayout layoutButtons;

    final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_multi_threading2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addViews();
        addEvents();
    }

    private void addViews() {
        edtNumberOfButtons = findViewById(R.id.edtNumberOfButtons);
        btnCreate          = findViewById(R.id.btnCreate);
        pbCreate           = findViewById(R.id.pbCreate);
        tvPercent          = findViewById(R.id.tvPercent);
        layoutButtons      = findViewById(R.id.layoutButtons);
    }

    private void addEvents() {
        btnCreate.setOnClickListener(v -> {
            String input = edtNumberOfButtons.getText() != null
                    ? edtNumberOfButtons.getText().toString().trim() : "";
            if (input.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập số lượng button", Toast.LENGTH_SHORT).show();
                return;
            }
            int n = Integer.parseInt(input);
            if (n <= 0 || n > 200) {
                Toast.makeText(this, "Nhập số từ 1 đến 200", Toast.LENGTH_SHORT).show();
                return;
            }
            new CreateButtonsTask().execute(n);
        });
    }

    // ── AsyncTask ─────────────────────────────────────────────
    class CreateButtonsTask extends AsyncTask<Integer, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            layoutButtons.removeAllViews();
            pbCreate.setProgress(0);
            tvPercent.setText("0 %");
            btnCreate.setEnabled(false);
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            int n = integers[0];
            for (int i = 1; i <= n; i++) {
                int percent = i * 100 / n;
                publishProgress(i, percent);
                SystemClock.sleep(150);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int index   = values[0];
            int percent = values[1];

            // Cập nhật progress
            pbCreate.setProgress(percent);
            tvPercent.setText(percent + " %");

            // Tạo Button động
            Button btn = new Button(MultiThreading2Activity.this);
            btn.setText("Button " + index);
            btn.setAllCaps(false);
            btn.setTextColor(Color.WHITE);
            btn.setTextSize(14f);

            int bg = Color.rgb(
                    random.nextInt(156) + 50,
                    random.nextInt(156) + 50,
                    random.nextInt(156) + 50);
            btn.setBackgroundColor(bg);

            int btnIndex = index;
            btn.setOnClickListener(v ->
                    Toast.makeText(MultiThreading2Activity.this,
                            "Bạn nhấn Button " + btnIndex, Toast.LENGTH_SHORT).show());

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 0, 8);
            btn.setLayoutParams(lp);

            layoutButtons.addView(btn);
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            tvPercent.setText("DONE");
            btnCreate.setEnabled(true);
        }
    }
}
