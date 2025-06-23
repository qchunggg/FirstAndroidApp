package com.example.firstandroidapp;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private TextView tvRegister, tvForgot;
    private boolean isPasswordVisible = false;
    private android.app.ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);  // hoặc R.layout.activity_login nếu bạn đặt tên khác

        // Khởi tạo Firebase
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        // Ánh xạ view
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvForgot = findViewById(R.id.tvForgot);

        // Đặt mật khẩu mặc định là ẩn và cập nhật biểu tượng
        etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        etPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_password, 0, R.drawable.ic_visibility_off, 0);

        // Set up password visibility toggle
        setupPasswordVisibilityToggle(etPassword, R.drawable.ic_visibility_off, R.drawable.ic_visibility);

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

        progressDialog = new android.app.ProgressDialog(this);
        progressDialog.setMessage("Đang đăng nhập...");
        progressDialog.setCancelable(false);

    }

    private void setupPasswordVisibilityToggle(EditText editText, int visibilityOffIcon, int visibilityOnIcon) {
        editText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                Drawable[] drawables = editText.getCompoundDrawables();
                if (drawables[2] != null && event.getRawX() >= (editText.getRight() - drawables[2].getBounds().width() - editText.getPaddingEnd())) {
                    if (isPasswordVisible) {
                        // Ẩn mật khẩu
                        editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_password, 0, visibilityOffIcon, 0);
                        isPasswordVisible = false;
                    } else {
                        // Hiển thị mật khẩu
                        editText.setTransformationMethod(SingleLineTransformationMethod.getInstance());
                        editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_password, 0, visibilityOnIcon, 0);
                        isPasswordVisible = true;
                    }
                    // Di chuyển con trỏ đến cuối
                    editText.setSelection(editText.getText().length());
                    return true;
                }
            }
            return false;
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString();

        // Validate
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Nhập email");
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            etPassword.setError("Nhập mật khẩu");
            return;
        }

        FirebaseAuth.getInstance().signOut();

        progressDialog.show();

        // Đăng nhập
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid(); // Lấy UID
                    checkIfAdmin(uid); // Gọi kiểm tra từ Realtime Database
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();

                    if (e instanceof FirebaseAuthException) {
                        String errorCode = ((FirebaseAuthException) e).getErrorCode();

                        Log.e("LoginErrorCode", "Firebase Auth Error: " + errorCode);
                        switch (errorCode) {
                            case "ERROR_USER_NOT_FOUND":
                                Toast.makeText(this, "Đăng nhập thất bại: Tài khoản chưa được đăng ký", Toast.LENGTH_LONG).show();
                                break;
                            case "ERROR_WRONG_PASSWORD":
                                Toast.makeText(this, "Đăng nhập thất bại: Email hoặc mật khẩu không chính xác", Toast.LENGTH_LONG).show();
                                break;
                            case "ERROR_INVALID_CREDENTIAL":
                                Toast.makeText(this, "Đăng nhập thất bại: Email hoặc mật khẩu không chính xác", Toast.LENGTH_SHORT).show();
                                break;
                            case "ERROR_INVALID_EMAIL":
                                Toast.makeText(this, "Đăng nhập thất bại: Email không hợp lệ", Toast.LENGTH_LONG).show();
                                break;
                            default:
                                Toast.makeText(this, "Đăng nhập thất bại: Lỗi không xác định", Toast.LENGTH_LONG).show();
                                break;
                        }
                    } else {
                        Toast.makeText(this, "Đăng nhập thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Hàm kiểm tra admin
    private void checkIfAdmin(String uid) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                progressDialog.dismiss(); // ✅ Dismiss khi có dữ liệu

                if (snapshot.exists()) {
                    Boolean isAdmin = snapshot.child("isAdmin").getValue(Boolean.class);

                    if (isAdmin != null && isAdmin) {
                        Log.d("AuthCheck", "Đăng nhập với quyền admin.");
                        startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                    } else {
                        Log.d("AuthCheck", "Đăng nhập với quyền người dùng.");
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }
                    finish();
                } else {
                    Log.w("AuthCheck", "Không tìm thấy user trong Realtime Database.");
                    Toast.makeText(LoginActivity.this, "Tài khoản chưa được đăng ký trong hệ thống!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                progressDialog.dismiss(); // ✅ Dismiss khi có lỗi

                Log.e("FirebaseError", "Lỗi khi kiểm tra quyền admin: " + error.getMessage());
                Toast.makeText(LoginActivity.this, "Lỗi hệ thống: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}
