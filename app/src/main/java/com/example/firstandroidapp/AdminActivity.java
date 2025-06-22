package com.example.firstandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class AdminActivity extends AppCompatActivity {

    private static final long INACTIVITY_TIMEOUT = 30000; // 30 giây
    private FirebaseAuth mAuth;
    private Handler handler;
    private Runnable logoutRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_main);  // Đây là layout dành cho admin

        LinearLayout itemManageCategory = findViewById(R.id.item_manage_category);
        itemManageCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, ActivityManageCategory.class);
                startActivity(intent);
            }
        });
        LinearLayout itemManageActivity = findViewById(R.id.item_manage_activity);
        itemManageActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, ActivitiesManageActivity.class);
                startActivity(intent);
            }
        });


        // Khởi tạo Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Kiểm tra đăng nhập
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return; // Dừng việc thực thi tiếp
        }

        // Khởi tạo Handler và Runnable để thực hiện logout sau 30 giây
        handler = new Handler();
        logoutRunnable = new Runnable() {
            @Override
            public void run() {
                // Đăng xuất người dùng và chuyển về màn hình đăng nhập
                mAuth.signOut();
                startActivity(new Intent(AdminActivity.this, LoginActivity.class));
                finish();
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Reset và bắt đầu lại timer khi activity trở lại foreground
        resetLogoutTimer();
    }

    private void resetLogoutTimer() {
        // Hủy bỏ timer cũ nếu có
        handler.removeCallbacks(logoutRunnable);

        // Đặt lại timer cho việc đăng xuất sau 30 giây
        handler.postDelayed(logoutRunnable, INACTIVITY_TIMEOUT); // 30 giây
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Hủy bỏ timer khi app chuyển sang background
        handler.removeCallbacks(logoutRunnable);
    }
}
