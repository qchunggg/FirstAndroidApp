package com.example.firstandroidapp;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DetailEventActivity extends AppCompatActivity {

    private TextView tvTitle, tvDescription, tvTime, tvLocation, tvQuantity, tvEventOrganizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_event);  // Layout cho chi tiết sự kiện

        // Khởi tạo các TextView
        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        tvTime = findViewById(R.id.text_date_event);
        tvQuantity = findViewById(R.id.text_capacity_event);
        tvEventOrganizer = findViewById(R.id.tvCategory2);  // TextView hiển thị tổ chức sự kiện
        tvLocation = findViewById(R.id.text_location_event);  // TextView hiển thị địa điểm sự kiện

        // Nhận dữ liệu từ Intent
        String activityName = getIntent().getStringExtra("name");
        String activityDescription = getIntent().getStringExtra("description");
        String activityTime = getIntent().getStringExtra("time");
        String activityQuantity = getIntent().getStringExtra("quantity");
        String eventOrganizer = getIntent().getStringExtra("eventOrganizer");  // Nhận thông tin tổ chức sự kiện
        String location = getIntent().getStringExtra("location");  // Nhận thông tin địa điểm

        String quantityWithText = activityQuantity + " sinh viên";

        // Cập nhật giao diện với dữ liệu từ Intent
        tvTitle.setText(activityName);
        tvDescription.setText(activityDescription);
        tvTime.setText(activityTime);
        tvQuantity.setText(quantityWithText);
        tvEventOrganizer.setText(eventOrganizer);  // Hiển thị thông tin tổ chức sự kiện
        tvLocation.setText(location);  // Hiển thị thông tin địa điểm sự kiện

        // Xử lý sự kiện nhấn nút back (ivBack)
        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> onBackPressed());
    }
}
