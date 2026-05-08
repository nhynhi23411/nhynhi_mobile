package com.nhynhi.k23411tapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
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

public class LoginActivity extends AppCompatActivity {
    EditText edtUsername;
    EditText edtPassword;
    TextView txtMessage;
    CheckBox chkSaveInfor;
    String name_share_ref="LoginInfor";
    RadioButton radAdmin, radEmployee;
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
        chkSaveInfor = findViewById(R.id.chkSaveInfor);
        radAdmin = findViewById(R.id.radAdmin);
        radEmployee = findViewById(R.id.radEmployee);
    }

    public void loginSystem(View view) {
        String username = edtUsername.getText().toString();
        String password = edtPassword.getText().toString();

        if(username.equalsIgnoreCase("admin") &&
                password.equals("123"))
        {
            SharedPreferences preferences=getSharedPreferences(name_share_ref, MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences.edit();
            editor.putString("username", username);
            editor.putString("password", password);
            boolean saved=chkSaveInfor.isChecked();
            editor.putBoolean("saved", saved);
            editor.commit();
            if (radAdmin.isChecked()){
                Intent inten=new Intent(LoginActivity.this, AdminManagementActivity.class);
                startActivity(inten);
            }
            else if (radEmployee.isChecked()){
                Intent inten=new Intent(LoginActivity.this, EmployeeManagementActivity.class);
                startActivity(inten);
            }
            txtMessage.setText(getString(R.string.loginSucess));

            Intent intent=new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
        else
        {
            txtMessage.setText(getString(R.string.loginFail));
        }
    }

    public void exitSystem(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

        builder.setTitle(R.string.dialog_exit_title);
        builder.setMessage(R.string.dialog_exit_message);
        builder.setIcon(android.R.drawable.ic_dialog_alert);

        builder.setPositiveButton(R.string.dialog_exit_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        builder.setNegativeButton(R.string.dialog_exit_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences=getSharedPreferences(name_share_ref, MODE_PRIVATE);
        String username=preferences.getString("username", "");
        String password=preferences.getString("password", "");
        boolean saved=preferences.getBoolean("saved", false);

        if(saved){
            edtUsername.setText(username);
            edtPassword.setText(password);
        }
        chkSaveInfor.setChecked(saved);

    }
}