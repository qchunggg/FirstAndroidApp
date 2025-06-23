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
    private String userId;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.design_infor);

        // Lấy UID hiện tại từ Firebase Auth
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Tham chiếu đến node "users/{uid}"
        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        // Ánh xạ view
        etUserName = findViewById(R.id.etUserName);
        etStudentId = findViewById(R.id.etStudentId);
        etClass = findViewById(R.id.etClass);
        etDepartment = findViewById(R.id.etDepartment);
        etPhone = findViewById(R.id.etPhoneNumber);
        btnSave = findViewById(R.id.btnSave);

        loadUserData();

        btnSave.setOnClickListener(v -> saveUserData());
    }

    private void loadUserData() {
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                UserModel user = task.getResult().getValue(UserModel.class);
                if (user != null) {
                    etUserName.setText(user.getFullName());
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
        String fullName = etUserName.getText().toString().trim();
        String studentId = etStudentId.getText().toString().trim();
        String className = etClass.getText().toString().trim();
        String department = etDepartment.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (fullName.isEmpty() || studentId.isEmpty() || className.isEmpty() || department.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        long currentTime = System.currentTimeMillis();

        // Tạo model mới (với thông tin hiện tại)
        UserModel updatedUser = new UserModel();
        updatedUser.setFullName(fullName);
        updatedUser.setStudentId(studentId);
        updatedUser.setClassName(className);
        updatedUser.setDepartment(department);
        updatedUser.setPhone(phone);
        updatedUser.setCreatedAt(currentTime);  // hoặc giữ nguyên nếu đã tồn tại
        updatedUser.setEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        updatedUser.setAdmin(false); // Hoặc lấy giá trị cũ nếu cần

        userRef.setValue(updatedUser).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();

                Intent resultIntent = new Intent();
                resultIntent.putExtra("updatedUserName", fullName);
                resultIntent.putExtra("updatedStudentId", studentId);
                resultIntent.putExtra("updatedClass", className);
                resultIntent.putExtra("updatedDepartment", department);
                resultIntent.putExtra("updatedPhoneNumber", phone);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "Cập nhật thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
