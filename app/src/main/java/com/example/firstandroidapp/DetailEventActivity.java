package com.example.firstandroidapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

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

        MaterialButton btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(v -> showRegisterDialog());
    }

    private void showRegisterDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_register_activity, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        // Ánh xạ nút trong dialog
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnConfirm = dialogView.findViewById(R.id.btnConfirm);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            dialog.dismiss();
            // Hiện thông báo
            Toast.makeText(this, "Đăng ký hoạt động thành công", Toast.LENGTH_SHORT).show();

            // Chuyển về ActivitiesActivity
            Intent intent = new Intent(DetailEventActivity.this, ActivitiesActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  // Để clear Activity stack, tránh quay lại DetailEventActivity
            startActivity(intent);

            // Nếu bạn muốn, có thể gọi finish() để đóng DetailEventActivity:
            finish();
        });
    }

}
