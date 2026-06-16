package com.nhynhi.k23411tapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.nhynhi.adapters.EmployeeAdapter;
import com.nhynhi.models.Department;
import com.nhynhi.models.Employee;
import java.util.ArrayList;

public class EmployeeAdvancedMainActivity extends AppCompatActivity {
    ListView lvEmployee;
    ArrayList<Employee> ListofEmployee;
    EmployeeAdapter adapterEmployee;
    Spinner spDepartment;
    ArrayList<Department> listOffDepartment;
    ArrayAdapter<Department> adapterDepartment;
    ImageView imgAddEmployee;

    final ActivityResultLauncher<Intent> addEmployeeLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Employee newEmp = (Employee) result.getData().getSerializableExtra("NEW_EMPLOYEE");
                    if (newEmp == null) return;

                    int selectedPosition = spDepartment.getSelectedItemPosition();
                    String targetDeptId;

                    // LOGIC: Nếu đang ở "ALL" (vị trí 0), ép về "D001" (HR)
                    if (selectedPosition == 0) {
                        targetDeptId = "D001";
                    } else {
                        targetDeptId = listOffDepartment.get(selectedPosition).getDepartmentId();
                    }

                    for (Department dept : listOffDepartment) {
                        if (dept.getDepartmentId().equals(targetDeptId)) {
                            dept.addEmployee(newEmp);
                            break;
                        }
                    }
                    updateListView(selectedPosition);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_advanced_main);
        addViews();
        sampleData();
        addEvents();
    }

    private void addViews() {
        lvEmployee = findViewById(R.id.lvEmployee);
        spDepartment = findViewById(R.id.spDepartment);
        imgAddEmployee = findViewById(R.id.imgAddEmployee);

        ListofEmployee = new ArrayList<>();
        adapterEmployee = new EmployeeAdapter(this, R.layout.item_custom_employee, ListofEmployee);
        lvEmployee.setAdapter(adapterEmployee);

        listOffDepartment = new ArrayList<>();
        adapterDepartment = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listOffDepartment);
        spDepartment.setAdapter(adapterDepartment);
    }

    private void addEvents() {
        spDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                updateListView(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        imgAddEmployee.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEmployeeActivity.class);
            addEmployeeLauncher.launch(intent);
        });
    }

    private void updateListView(int position) {
        if (listOffDepartment.isEmpty()) return;
        ListofEmployee.clear();
        Department selected = listOffDepartment.get(position);

        if (selected.getDepartmentId().equals("ALL")) {
            for (int i = 1; i < listOffDepartment.size(); i++) {
                ListofEmployee.addAll(listOffDepartment.get(i).getEmployees());
            }
        } else {
            ListofEmployee.addAll(selected.getEmployees());
        }
        adapterEmployee.notifyDataSetChanged();
    }

    private void sampleData() {
        listOffDepartment.add(new Department("ALL", "ALL"));
        listOffDepartment.add(new Department("D001", "HR"));
        listOffDepartment.add(new Department("D002", "IT"));
        adapterDepartment.notifyDataSetChanged();
    }
}