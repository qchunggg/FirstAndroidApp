package com.example.firstandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class AdminActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_main);  // Đây là layout dành cho admin

        // Quản lý danh mục
        LinearLayout itemManageCategory = findViewById(R.id.item_manage_category);
        itemManageCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, ActivityManageCategory.class);
                startActivity(intent);
            }
        });

        // Quản lý hoạt động
        LinearLayout itemManageActivity = findViewById(R.id.item_manage_activity);
        itemManageActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, ActivitiesManageActivity.class);
                startActivity(intent);
            }
        });

        // Thống kê
        LinearLayout itemStatistic = findViewById(R.id.item_stats);
        itemStatistic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, StatisticActivity.class);
                startActivity(intent);
            }
        });


        // Duyệt & cấp điểm
        LinearLayout itemApprove = findViewById(R.id.item_approve);
        itemApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Thực hiện hành động khi nhấn vào mục 'Duyệt & cấp điểm'

                // Chuyển hướng sang màn hình duyệt và cấp điểm
                Intent intent = new Intent(AdminActivity.this, PointActivity.class);  // Thay đổi thành PointActivity
                startActivity(intent);
            }
        });

        // Cài đặt
        LinearLayout itemSettings = findViewById(R.id.item_settings);
        itemSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, SettingManageActivity.class);
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
    }
    
}
