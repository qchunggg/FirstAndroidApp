package com.example.firstandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etClass, etDepartment, etPhoneNumber;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.design_infor);

        // Ánh xạ các thành phần
        etClass = findViewById(R.id.etClass);
        etDepartment = findViewById(R.id.etDepartment);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        btnSave = findViewById(R.id.btnSave);

        // Sự kiện nhấn nút "Xác nhận"
        btnSave.setOnClickListener(v -> {
            // Lấy dữ liệu đã sửa
            String updatedClass = etClass.getText().toString();
            String updatedDepartment = etDepartment.getText().toString();
            String updatedPhoneNumber = etPhoneNumber.getText().toString();


            // Tạo Intent để gửi dữ liệu về MainActivity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("updatedClass", updatedClass);
            resultIntent.putExtra("updatedDepartment", updatedDepartment);
            resultIntent.putExtra("updatedPhoneNumber", updatedPhoneNumber);


            // Trả kết quả về MainActivity
            setResult(RESULT_OK, resultIntent);
            finish(); // Đóng EditProfileActivity và quay lại MainActivity
        });
    }
}
