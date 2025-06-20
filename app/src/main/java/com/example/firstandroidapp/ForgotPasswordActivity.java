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

public class ForgotPasswordActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText etRecoveryEmail;
    private MaterialButton btnSendRecovery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);

        // Khởi tạo Firebase Auth
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        // Ánh xạ view
        etRecoveryEmail = findViewById(R.id.etRecoveryEmail);
        btnSendRecovery = findViewById(R.id.btnSendRecovery);

        // Xử lý click gửi email khôi phục
        btnSendRecovery.setOnClickListener(v -> sendRecoveryEmail());
    }

    private void sendRecoveryEmail() {
        String email = etRecoveryEmail.getText().toString().trim();

        // Validate email
        if (TextUtils.isEmpty(email)) {
            etRecoveryEmail.setError("Nhập email");
            return;
        }

        // Gửi email khôi phục mật khẩu
        mAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this,
                            "Vui lòng kiểm tra hộp thư để nhận email khôi phục mật khẩu.",
                            Toast.LENGTH_LONG).show();
                    // Sau khi gửi có thể đóng activity hoặc chuyển sang màn login
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this,
                            "Gửi email thất bại: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }
}
