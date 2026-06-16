package com.nhynhi.k23411tapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class MultiThreadingActivity extends AppCompatActivity {

    // ── Views ──────────────────────────────────────────────────
    TextView tvHandlerStatus, tvPercent, tvDownloadStatus;
    ProgressBar pbHandler, pbPercent, pbDownload;
    MaterialButton btnHandlerStart, btnHandlerStop, btnGenerate, btnDownload;
    TextInputEditText edtNumberOfControls, edtImageUrl;
    LinearLayout layoutControls;
    ImageView imvPhoto;

    // ── Handler demo ───────────────────────────────────────────
    static final int MSG_UPDATE = 1;
    static final int MSG_DONE   = 2;

    volatile boolean isRunning = false;
    Thread workerThread;

    Handler uiHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_UPDATE) {
                int progress = msg.arg1;
                pbHandler.setProgress(progress);
                tvHandlerStatus.setText("Đang xử lý: " + progress + " %");
            } else if (msg.what == MSG_DONE) {
                tvHandlerStatus.setText("Hoàn thành!");
                pbHandler.setProgress(100);
                btnHandlerStart.setEnabled(true);
                btnHandlerStop.setEnabled(false);
            }
        }
    };

    // ── Random ─────────────────────────────────────────────────
    final Random random = new Random();

    // ───────────────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_multi_threading);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addViews();
        addEvents();
    }

    private void addViews() {
        tvHandlerStatus    = findViewById(R.id.tvHandlerStatus);
        tvPercent          = findViewById(R.id.tvPercent);
        tvDownloadStatus   = findViewById(R.id.tvDownloadStatus);
        pbHandler          = findViewById(R.id.pbHandler);
        pbPercent          = findViewById(R.id.pbPercent);
        pbDownload         = findViewById(R.id.pbDownload);
        btnHandlerStart    = findViewById(R.id.btnHandlerStart);
        btnHandlerStop     = findViewById(R.id.btnHandlerStop);
        btnGenerate        = findViewById(R.id.btnGenerate);
        btnDownload        = findViewById(R.id.btnDownload);
        edtNumberOfControls = findViewById(R.id.edtNumberOfControls);
        edtImageUrl        = findViewById(R.id.edtImageUrl);
        layoutControls     = findViewById(R.id.layoutControls);
        imvPhoto           = findViewById(R.id.imvPhoto);
    }

    private void addEvents() {
        btnHandlerStart.setOnClickListener(v -> startHandlerDemo());
        btnHandlerStop.setOnClickListener(v -> stopHandlerDemo());
        btnGenerate.setOnClickListener(v -> {
            String text = edtNumberOfControls.getText() != null
                    ? edtNumberOfControls.getText().toString() : "10";
            int n = text.isEmpty() ? 10 : Integer.parseInt(text);
            new GenerateTask().execute(n);
        });
        btnDownload.setOnClickListener(v -> {
            String url = edtImageUrl.getText() != null
                    ? edtImageUrl.getText().toString().trim() : "";
            if (!url.isEmpty()) new DownloadImageTask().execute(url);
        });
    }

    // ══════════════════════════════════════════════════════════
    //  PHẦN 1 – HANDLER
    // ══════════════════════════════════════════════════════════

    private void startHandlerDemo() {
        isRunning = true;
        pbHandler.setProgress(0);
        btnHandlerStart.setEnabled(false);
        btnHandlerStop.setEnabled(true);

        // Worker Thread gửi message lên UI Thread qua Handler
        workerThread = new Thread(() -> {
            for (int i = 1; i <= 100 && isRunning; i++) {
                Message msg = uiHandler.obtainMessage(MSG_UPDATE);
                msg.arg1 = i;
                uiHandler.sendMessage(msg);
                SystemClock.sleep(50);
            }
            if (isRunning) {
                uiHandler.sendEmptyMessage(MSG_DONE);
            }
            isRunning = false;
        });
        workerThread.start();
    }

    private void stopHandlerDemo() {
        isRunning = false;
        tvHandlerStatus.setText("Đã dừng.");
        btnHandlerStart.setEnabled(true);
        btnHandlerStop.setEnabled(false);
    }

    // ══════════════════════════════════════════════════════════
    //  PHẦN 2 – ASYNCTASK + PROGRESSBAR
    //  Theo slide 22-24: sinh n controls ngẫu nhiên, cập nhật %
    // ══════════════════════════════════════════════════════════

    class GenerateTask extends AsyncTask<Integer, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            tvPercent.setText("0 %");
            pbPercent.setProgress(0);
            layoutControls.removeAllViews();
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            int n = integers[0];
            for (int i = 1; i <= n; i++) {
                int percent = i * 100 / n;
                int value   = random.nextInt(100);
                publishProgress(percent, value);
                SystemClock.sleep(100);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int percent = values[0];
            int value   = values[1];

            tvPercent.setText(percent + " %");
            pbPercent.setProgress(percent);

            // Thêm TextView mới vào layout (cập nhật UI từ UI Thread – đúng chuẩn)
            TextView tv = new TextView(MultiThreadingActivity.this);
            tv.setText("Value #" + percent + " = " + value);
            tv.setTextSize(14);
            tv.setPadding(8, 4, 8, 4);
            tv.setBackgroundColor(Color.rgb(
                    random.nextInt(200) + 55,
                    random.nextInt(200) + 55,
                    random.nextInt(200) + 55));
            tv.setTextColor(Color.WHITE);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 4, 0, 4);
            tv.setLayoutParams(lp);

            layoutControls.addView(tv);
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            tvPercent.setText("DONE");
        }
    }

    // ══════════════════════════════════════════════════════════
    //  PHẦN 3 – ASYNCTASK TẢI ẢNH TỪ INTERNET
    //  Theo slide 26-29: HttpURLConnection + BitmapFactory
    // ══════════════════════════════════════════════════════════

    class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbDownload.setVisibility(View.VISIBLE);
            imvPhoto.setVisibility(View.GONE);
            tvDownloadStatus.setText("Đang tải...");
            btnDownload.setEnabled(false);
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap = null;
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                bitmap = BitmapFactory.decodeStream(connection.getInputStream());
            } catch (Exception e) {
                Log.e("DownloadImage", e.toString());
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            pbDownload.setVisibility(View.GONE);
            btnDownload.setEnabled(true);
            if (bitmap != null) {
                imvPhoto.setVisibility(View.VISIBLE);
                imvPhoto.setImageBitmap(bitmap);
                tvDownloadStatus.setText("Tải thành công!");
            } else {
                tvDownloadStatus.setText("Tải thất bại. Kiểm tra URL hoặc mạng.");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRunning = false;
    }
}
