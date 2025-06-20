package com.example.firstandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;

    private EditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private TextView tvRegister, tvForgot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);  // hoặc R.layout.activity_login nếu bạn đặt tên khác

        // Khởi tạo Firebase
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("users");

        // Ánh xạ view
        etEmail    = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin   = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvForgot   = findViewById(R.id.tvForgot);

        // Xử lý click Đăng nhập
        btnLogin.setOnClickListener(v -> loginUser());

        // Chuyển sang màn Đăng ký
        tvRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );

        // Chuyển sang màn Quên mật khẩu (nếu có)
        tvForgot.setOnClickListener(v ->
                startActivity(new Intent(this, ForgotPasswordActivity.class))
        );
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String pass  = etPassword.getText().toString();

        // Validate
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Nhập email");
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            etPassword.setError("Nhập mật khẩu");
            return;
        }

        // Đăng nhập
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();

                    // Tùy chọn: lấy thêm profile từ Realtime DB
                    dbRef.child(uid)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        // Ví dụ: lấy fullName để gán vào session/local
                                        String fullName = snapshot.child("fullName")
                                                .getValue(String.class);
                                        // ... bạn có thể lưu Local hoặc truyền Intent
                                    }
                                    // Điều hướng sang MainActivity
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                }

                                @Override
                                public void onCancelled(DatabaseError error) {
                                    // Nếu không đọc được profile, vẫn cho qua
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                }
                            });
                })
                .addOnFailureListener(e ->
                        Toast.makeText(LoginActivity.this,
                                "Đăng nhập thất bại: " + e.getMessage(),
                                Toast.LENGTH_LONG).show()
                );
    }
}
