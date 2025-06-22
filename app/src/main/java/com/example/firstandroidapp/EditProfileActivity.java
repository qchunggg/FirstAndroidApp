package com.example.firstandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfileActivity extends AppCompatActivity {

    public static final int EDIT_PROFILE_REQUEST_CODE = 1;
    private EditText etUserName, etStudentId, etClass, etDepartment, etPhone;
    private Button btnSave;
    private String userId; // UID lấy từ Firebase
    private DatabaseReference profileRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.design_infor);

        // Lấy userId của người dùng hiện tại từ Firebase Authentication
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Tham chiếu đến "profile/userId" trong Firebase
        profileRef = FirebaseDatabase.getInstance().getReference("profile").child(userId);

        // Ánh xạ các EditText và Button
        etUserName = findViewById(R.id.etUserName);
        etStudentId = findViewById(R.id.etStudentId);
        etClass = findViewById(R.id.etClass);
        etDepartment = findViewById(R.id.etDepartment);
        etPhone = findViewById(R.id.etPhoneNumber);
        btnSave = findViewById(R.id.btnSave);

        // Lấy dữ liệu từ Firebase (Nếu cần)
        loadUserData();

        // Lưu dữ liệu khi nhấn "Xác nhận"
        btnSave.setOnClickListener(v -> saveUserData());
    }

    private void loadUserData() {
        // Tải dữ liệu người dùng từ Firebase (nếu có)
        profileRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                User user = task.getResult().getValue(User.class);
                if (user != null) {
                    etUserName.setText(user.getUserName());
                    etStudentId.setText(user.getStudentId());
                    etClass.setText(user.getClassName());
                    etDepartment.setText(user.getDepartment());
                    etPhone.setText(user.getPhone());
                } else {
                    Toast.makeText(this, "Không có dữ liệu người dùng", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Lỗi tải dữ liệu: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveUserData() {
        String userName = etUserName.getText().toString().trim();
        String studentId = etStudentId.getText().toString().trim();
        String className = etClass.getText().toString().trim();
        String department = etDepartment.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        // Kiểm tra dữ liệu đầu vào
        if (userName.isEmpty() || studentId.isEmpty() || className.isEmpty() || department.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cập nhật đối tượng User
        User updatedUser = new User(userName, studentId, className, department, phone);

        // Lưu dữ liệu vào Firebase
        profileRef.setValue(updatedUser).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();

                // Trả kết quả về Activity trước đó (AccountFragment)
                Intent resultIntent = new Intent();
                resultIntent.putExtra("updatedUserName", userName);
                resultIntent.putExtra("updatedStudentId", studentId);
                resultIntent.putExtra("updatedClass", className);
                resultIntent.putExtra("updatedDepartment", department);
                resultIntent.putExtra("updatedPhoneNumber", phone);
                setResult(RESULT_OK, resultIntent);
                finish(); // Đóng activity sau khi trả kết quả
            } else {
                Toast.makeText(this, "Cập nhật thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
