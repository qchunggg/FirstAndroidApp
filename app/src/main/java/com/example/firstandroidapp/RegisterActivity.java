package com.example.firstandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    // 1. Khai báo Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;

    // 2. Khai báo View
    private EditText etEmail, etPassword, etConfirmPassword;
    private MaterialButton btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        // Khởi tạo Firebase
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("users");

        // Ánh xạ View
        etEmail           = findViewById(R.id.etEmail);
        etPassword        = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister       = findViewById(R.id.btnRegister);

        // Xử lý nút Đăng ký
        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String email   = etEmail.getText().toString().trim();
        String pass    = etPassword.getText().toString();
        String confirm = etConfirmPassword.getText().toString();

        // Validate input
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Nhập email");
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            etPassword.setError("Nhập mật khẩu");
            return;
        }
        if (!pass.equals(confirm)) {
            etConfirmPassword.setError("Mật khẩu không khớp");
            return;
        }

        // Tạo user trên FirebaseAuth
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();
                    Map<String,Object> userProfile = new HashMap<>();
                    userProfile.put("email", email);
                    userProfile.put("createdAt", System.currentTimeMillis());

                    // Lưu profile vào Realtime Database
                    dbRef.child(uid)
                            .setValue(userProfile)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, MainActivity.class));
                                finish();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Lưu hồ sơ thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show()
                            );
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Đăng ký thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}
