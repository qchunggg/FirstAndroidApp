package com.example.firstandroidapp;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
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

    // 1. Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;

    // 2. Views
    private EditText etEmail, etPassword, etConfirmPassword;
    private MaterialButton btnRegister;
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        // Khởi tạo Firebase
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("users");

        // Ánh xạ View
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        TextView tvLogin = findViewById(R.id.tvLogin);

        // Mặc định ẩn mật khẩu
        etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        etPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_password, 0, R.drawable.ic_visibility_off, 0);
        etConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        etConfirmPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_password, 0, R.drawable.ic_visibility_off, 0);

        // Hiển thị/ẩn mật khẩu khi nhấn icon
        setupPasswordVisibilityToggle(etPassword, R.drawable.ic_visibility_off, R.drawable.ic_visibility);
        setupPasswordVisibilityToggle(etConfirmPassword, R.drawable.ic_visibility_off, R.drawable.ic_visibility);

        // Đăng ký
        btnRegister.setOnClickListener(v -> registerUser());

        // Chuyển sang Đăng nhập
        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void setupPasswordVisibilityToggle(EditText editText, int visibilityOffIcon, int visibilityOnIcon) {
        editText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Drawable[] drawables = editText.getCompoundDrawables();
                if (drawables[2] != null && event.getRawX() >= (editText.getRight() - drawables[2].getBounds().width() - editText.getPaddingEnd())) {
                    boolean isVisible = editText == etPassword ? isPasswordVisible : isConfirmPasswordVisible;
                    if (isVisible) {
                        editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_password, 0, visibilityOffIcon, 0);
                        if (editText == etPassword) isPasswordVisible = false;
                        else isConfirmPasswordVisible = false;
                    } else {
                        editText.setTransformationMethod(SingleLineTransformationMethod.getInstance());
                        editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_password, 0, visibilityOnIcon, 0);
                        if (editText == etPassword) isPasswordVisible = true;
                        else isConfirmPasswordVisible = true;
                    }
                    editText.setSelection(editText.getText().length());
                    return true;
                }
            }
            return false;
        });
    }

    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString();
        String confirm = etConfirmPassword.getText().toString();

        // Kiểm tra nhập
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

        // Tạo tài khoản Firebase
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();

                    // Tạo dữ liệu lưu vào Realtime Database
                    Map<String, Object> userProfile = new HashMap<>();
                    userProfile.put("email", email);
                    userProfile.put("createdAt", System.currentTimeMillis());
                    userProfile.put("isAdmin", false); // ✅ user thường mặc định

                    dbRef.child(uid)
                            .setValue(userProfile)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, LoginActivity.class));
                                finish();
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Lưu hồ sơ thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show());
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Đăng ký thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
