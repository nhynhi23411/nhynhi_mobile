package com.nhynhi.k23411tapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {
    EditText edtUsername;
    EditText edtPassword;
    TextView txtMessage;

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

    private void addViews(){
        edtPassword = findViewById(R.id.edtPassword);
        edtUsername = findViewById(R.id.edtUsername);
        txtMessage = findViewById(R.id.txtMessage);
    }

    public void loginSystem(View view) {
        // Đã sửa 'edtUserName' thành 'edtUsername' và 'edtPasword' thành 'edtPassword'
        String username = edtUsername.getText().toString();
        String password = edtPassword.getText().toString();

        if(username.equalsIgnoreCase("admin") &&
                password.equals("123"))
        {
            txtMessage.setText(getString(R.string.loginSucess));

            // Đã thêm dấu chấm phẩy ; vào cuối dòng này giúp bạn
            Intent intent=new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
        else
        {
            txtMessage.setText(getString(R.string.loginFail));
        }
    }

    public void exitSystem(View view) {
        finish();
    }
}