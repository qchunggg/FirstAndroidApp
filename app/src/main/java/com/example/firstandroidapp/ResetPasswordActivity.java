package com.example.firstandroidapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

public class ResetPasswordActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String oobCode;

    private EditText etNewPassword, etConfirmPassword;
    private MaterialButton btnResetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password);

        // 1. Khởi tạo Firebase Auth
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        // 2. Ánh xạ view
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        ImageButton btnBack = findViewById(R.id.btnBack);

        // 3. Bắt Dynamic Link và lấy oobCode
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(pendingResult -> {
                    Uri deepLink = (pendingResult != null ? pendingResult.getLink() : null);
                    if (deepLink != null) {
                        oobCode = deepLink.getQueryParameter("oobCode");
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this,
                            "Không nhận được link khôi phục.",
                            Toast.LENGTH_LONG).show();
                    finish();
                });

        // 4. Xử lý click nút Back để quay về ForgotPasswordActivity
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ResetPasswordActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
            finish();
        });

        // 5. Xử lý click Đặt lại mật khẩu
        btnResetPassword.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword() {
        String newPass = etNewPassword.getText().toString().trim();
        String confirm = etConfirmPassword.getText().toString().trim();

        // 6. Validate
        if (TextUtils.isEmpty(newPass)) {
            etNewPassword.setError("Nhập mật khẩu mới");
            return;
        }
        if (newPass.length() < 6) {
            etNewPassword.setError("Mật khẩu tối thiểu 6 ký tự");
            return;
        }
        if (!newPass.equals(confirm)) {
            etConfirmPassword.setError("Mật khẩu không khớp");
            return;
        }
        if (oobCode == null) {
            Toast.makeText(this,
                    "Link khôi phục không hợp lệ.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // 7. Gọi API xác nhận thay đổi mật khẩu
        mAuth.confirmPasswordReset(oobCode, newPass)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this,
                            "Đặt lại mật khẩu thành công!",
                            Toast.LENGTH_SHORT).show();
                    finish(); // Quay về màn đăng nhập
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this,
                            "Thất bại: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }
}