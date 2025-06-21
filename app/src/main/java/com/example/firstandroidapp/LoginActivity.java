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

        // Đăng nhập
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener(authResult -> {
                    // Đăng nhập thành công, chuyển sang MainActivity hoặc AdminActivity
                    if (isAdmin(authResult.getUser().getEmail())) {
                        startActivity(new Intent(LoginActivity.this, AdminActivity.class)); // Chuyển tới AdminActivity nếu là admin
                    } else {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class)); // Hoặc MainActivity nếu là người dùng bình thường
                    }
                    finish(); // Đóng LoginActivity
                })
                .addOnFailureListener(e -> {
                    // Ghi log chi tiết lỗi để dễ debug
                    Log.e("LoginError", "Error: " + e.getMessage());
                    Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    // Hàm kiểm tra admin
    private void checkIfAdmin(String email) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        boolean isAdmin = snapshot.child("isAdmin").getValue(Boolean.class);
                        if (isAdmin) {
                            // Nếu là admin, chuyển đến AdminActivity
                            startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                        } else {
                            // Nếu là user, chuyển đến MainActivity
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        }
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseError", "Error checking admin: " + databaseError.getMessage());
            }
        });
    }

    private boolean isAdmin(String email) {
        // Kiểm tra email admin (hoặc bạn có thể kiểm tra từ Firestore/Realtime Database)
        return email.equals("admin@tlu.edu.vn");
    }
}
