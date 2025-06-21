package com.example.firstandroidapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfileActivity extends AppCompatActivity {

    private TextView tvUserName, tvStudentId;
    private EditText etClass, etDepartment, etPhoneNumber;
    private Button btnSave;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.design_infor);  // Layout bạn đã cung cấp

        // Ánh xạ các view
        tvUserName = findViewById(R.id.tvUserName);
        tvStudentId = findViewById(R.id.tvStudentId);
        etClass = findViewById(R.id.etClass);  // Sử dụng EditText cho việc cập nhật lớp học
        etDepartment = findViewById(R.id.etDepartment);  // Sử dụng EditText cho việc cập nhật khoa
        etPhoneNumber = findViewById(R.id.etPhoneNumber);  // Sử dụng EditText cho việc cập nhật số điện thoại
        btnSave = findViewById(R.id.btnSave);

        // Khởi tạo Firebase Auth và Database Reference
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();



        // Giả sử bạn đã có thông tin người dùng từ Firebase
        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.child("users").child(userId).get().addOnSuccessListener(dataSnapshot -> {
            // Đặt các giá trị vào các trường TextView và EditText
            User user = dataSnapshot.getValue(User.class);
            if (user != null) {
                tvUserName.setText(user.getName());
                tvStudentId.setText("Mã sinh viên: " + user.getStudentId());
                etClass.setText(user.getClassName());  // Hiển thị lớp học
                etDepartment.setText(user.getDepartment());  // Hiển thị khoa
                etPhoneNumber.setText(user.getPhone());  // Hiển thị số điện thoại
            }
        });

        // Cài đặt sự kiện cho nút Lưu
        btnSave.setOnClickListener(v -> {
            String className = etClass.getText().toString().trim();  // Lấy thông tin lớp học
            String department = etDepartment.getText().toString().trim();  // Lấy thông tin khoa
            String phone = etPhoneNumber.getText().toString().trim();  // Lấy thông tin số điện thoại

            if (className.isEmpty() || department.isEmpty() || phone.isEmpty()) {
                Toast.makeText(EditProfileActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            } else {
                // Cập nhật thông tin người dùng trong Firebase
                updateProfile(className, department, phone);
            }
        });
    }

    private void updateProfile(String className, String department, String phone) {
        String userId = mAuth.getCurrentUser().getUid();

        // Lấy các thông tin khác (Ví dụ như tên và mã sinh viên có thể đã có trong Firebase)
        String name = tvUserName.getText().toString();  // Bạn có thể lấy từ TextView nếu cần
        String studentId = tvStudentId.getText().toString().replace("Mã sinh viên: ", "");  // Loại bỏ "Mã sinh viên:"

        // Tạo đối tượng User với thông tin mới
        User updatedUser = new User(className, department, phone); // Sử dụng constructor mới

        // Cập nhật thông tin người dùng trong Firebase
        mDatabase.child("users").child(userId).setValue(updatedUser).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(EditProfileActivity.this, "Thông tin đã được cập nhật", Toast.LENGTH_SHORT).show();
                finish();  // Quay lại màn hình trước đó sau khi lưu
            } else {
                Toast.makeText(EditProfileActivity.this, "Lỗi khi cập nhật thông tin", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
