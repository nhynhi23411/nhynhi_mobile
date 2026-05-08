package com.nhynhi.k23411tapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import net.objecthunter.exp4j.ExpressionBuilder;

public class CalculatorActivity extends AppCompatActivity {
    EditText edtFormular;
    Button btnDel, btnEqual;
    TextView btnMC, btnMR, btnMPlus, btnMMinus, btnMS, btnM;
    View.OnClickListener m_click_listener;

    String sharedPrefName = "CalculatorData";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calculator);

        addViews();
        addEvents();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void addViews() {
        edtFormular = findViewById(R.id.edtFormular);
        btnDel = findViewById(R.id.btnDel);
        btnEqual = findViewById(R.id.btnEqual);
        btnMC = findViewById(R.id.btnMC);
        btnMR = findViewById(R.id.btnMR);
        btnMPlus = findViewById(R.id.btnMPlus);
        btnMMinus = findViewById(R.id.btnMMinus);
        btnMS = findViewById(R.id.btnMS);
        btnM = findViewById(R.id.btnM);
    }

    private void addEvents() {
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String formular = edtFormular.getText().toString();
                if (formular.equals("Error") || formular.isEmpty()) {
                    edtFormular.setText("");
                } else {
                    String new_formular = formular.substring(0, formular.length() - 1);
                    edtFormular.setText(new_formular);
                }
                edtFormular.setSelection(edtFormular.getText().length()); // Đưa con trỏ về cuối
            }
        });

        btnEqual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String formular = edtFormular.getText().toString();
                if (formular.isEmpty() || formular.equals("Error")) return;

                try {
                    String processed = formular.replace("÷", "/")
                            .replace("×", "*")
                            .replace("−", "-")
                            .replace("%", "/100");

                    double result = new ExpressionBuilder(processed).build().evaluate();
                    if (result == (long) result) {
                        edtFormular.setText(String.valueOf((long) result));
                    } else {
                        edtFormular.setText(String.valueOf(result));
                    }
                    edtFormular.setSelection(edtFormular.getText().length()); // Đưa con trỏ về cuối
                } catch (Exception e) {
                    edtFormular.setText("Error");
                }
            }
        });

        m_click_listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tag = "";
                if (view.getId() == R.id.btnMC) tag = "MC";
                else if (view.getId() == R.id.btnMR) tag = "MR";
                else if (view.getId() == R.id.btnMPlus) tag = "M+";
                else if (view.getId() == R.id.btnMMinus) tag = "M-";
                else if (view.getId() == R.id.btnMS) tag = "MS";
                else if (view.getId() == R.id.btnM) tag = "M";

                if (!tag.isEmpty()) {
                    Toast.makeText(CalculatorActivity.this, tag, Toast.LENGTH_SHORT).show();
                }
            }
        };

        btnMC.setOnClickListener(m_click_listener);
        btnMR.setOnClickListener(m_click_listener);
        btnMPlus.setOnClickListener(m_click_listener);
        btnMMinus.setOnClickListener(m_click_listener);
        btnMS.setOnClickListener(m_click_listener);
        btnM.setOnClickListener(m_click_listener);
    }


    public void processInputData(View view) {
        Button btn = (Button) view;
        String btnValue = btn.getText().toString();
        String current = edtFormular.getText().toString();

        if (current.equals("0") || current.equals("Error")) {
            current = "";
        }

        switch (btnValue) {
            case "C":
            case "CE":
                edtFormular.setText("");
                break;
            case "X²":
                if (!current.isEmpty()) edtFormular.setText(current + "^2");
                break;
            case "√X":
                if (!current.isEmpty()) edtFormular.setText("sqrt(" + current + ")");
                else edtFormular.setText("sqrt(");
                break;
            case "1/X":
                if (!current.isEmpty()) edtFormular.setText("1/(" + current + ")");
                break;
            default:
                edtFormular.setText(current + btnValue);
                break;
        }
        edtFormular.setSelection(edtFormular.getText().length()); // Đưa con trỏ nháy văn bản về cuối dòng
    }


    @Override
    protected void onPause() {
        super.onPause();
        String currentFormular = edtFormular.getText().toString();

        SharedPreferences preferences = getSharedPreferences(sharedPrefName, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("last_formular", currentFormular);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences(sharedPrefName, MODE_PRIVATE);
        String savedFormular = preferences.getString("last_formular", "");

        edtFormular.setText(savedFormular);

        if (!savedFormular.isEmpty()) {
            edtFormular.setSelection(savedFormular.length());
        }
    }
}