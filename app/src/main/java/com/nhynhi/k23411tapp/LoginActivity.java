package com.nhynhi.k23411tapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.View;
import android.widget.Button; // Thêm import Button
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.nhynhi.models.ListUserAccount;
import com.nhynhi.models.UserAccount;

public class LoginActivity extends AppCompatActivity {
    EditText edtUsername, edtPassword;
    TextView txtMessage;
    CheckBox chkSaveInfor;
    String name_share_ref = "LoginInfor";
    RadioButton radAdmin, radEmployee;
    Button btnLogin; // 1. Khai báo biến đại diện cho nút Login

    // Bộ lắng nghe sự kiện thay đổi mạng (Wifi/Mobile data)
    BroadcastReceiver Internetreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isWifiConnected(context)) {
                txtMessage.setText("Đã kết nối Wi-Fi thành công!");
                txtMessage.setTextColor(Color.GREEN);

                // 2. Có Wi-Fi -> Bật nút Đăng nhập
                btnLogin.setEnabled(true);
            } else {
                txtMessage.setText("Không có Wi-Fi! Nút đăng nhập đã bị khóa.");
                txtMessage.setTextColor(Color.RED);

                // 2. Mất Wi-Fi -> Vô hiệu hóa nút Đăng nhập
                btnLogin.setEnabled(false);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addViews();
    }

    private void addViews() {
        edtPassword = findViewById(R.id.edtPassword);
        edtUsername = findViewById(R.id.edtUsername);
        txtMessage = findViewById(R.id.txtMessage);
        chkSaveInfor = findViewById(R.id.chkSaveInfor);
        radAdmin = findViewById(R.id.radAdmin);
        radEmployee = findViewById(R.id.radEmployee);

        // 3. Ánh xạ nút đăng nhập từ XML (Bạn nhớ kiểm tra xem id bên file XML có trùng tên không nhé)
        btnLogin = findViewById(R.id.btn_Login);
    }

    // Hàm kiểm tra xem thiết bị có đang kết nối bằng Wifi hay không
    private boolean isWifiConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            Network activeNetwork = cm.getActiveNetwork();
            if (activeNetwork != null) {
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(activeNetwork);
                return capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
            }
        }
        return false;
    }

    public void loginSystem(View view) {
        String username = edtUsername.getText().toString();
        String password = edtPassword.getText().toString();
        UserAccount user = ListUserAccount.login(username, password);

        if (user != null) {
            SharedPreferences preferences = getSharedPreferences(name_share_ref, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            if (chkSaveInfor.isChecked()) {
                editor.putString("username", username);
                editor.putString("password", password);
                editor.putBoolean("saved", true);
            } else {
                editor.clear();
            }
            editor.apply();

            if (radAdmin.isChecked()) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                finish();
            } else if (radEmployee.isChecked()) {
                Intent intent = new Intent(LoginActivity.this, EmployeeAdvancedMainActivity.class);
                startActivity(intent);
                finish();
            } else {
                txtMessage.setText("Vui lòng chọn vai trò!");
                txtMessage.setTextColor(Color.RED);
            }
        } else {
            txtMessage.setText("Sai tài khoản hoặc mật khẩu!");
            txtMessage.setTextColor(Color.RED);
        }
    }

    public void exitSystem(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Thông báo");
        builder.setMessage("Bạn có muốn thoát?");
        builder.setPositiveButton("Có", (dialogInterface, i) -> finish());
        builder.setNegativeButton("Không", (dialogInterface, i) -> dialogInterface.dismiss());
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Load thông tin tài khoản đã lưu
        SharedPreferences preferences = getSharedPreferences(name_share_ref, MODE_PRIVATE);
        String username = preferences.getString("username", "");
        String password = preferences.getString("password", "");
        boolean saved = preferences.getBoolean("saved", false);
        if (saved) {
            edtUsername.setText(username);
            edtPassword.setText(password);
        }
        chkSaveInfor.setChecked(saved);

        // Đăng ký bộ lắng nghe mạng khi màn hình hoạt động
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(Internetreceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Hủy đăng ký khi thoát màn hình để tránh leak memory hoặc crash
        unregisterReceiver(Internetreceiver);
    }
}