package com.example.firstandroidapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetailEventActivity extends AppCompatActivity {

    private TextView tvTitle, tvDescription, tvTime, tvLocation, tvQuantity, tvEventOrganizer, tvCategory1;
    private ActivityModel activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_event);

        // 1. Nhận activity từ Intent
        activity = (ActivityModel) getIntent().getSerializableExtra("activity");

        // 2. Ánh xạ View
        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        tvTime = findViewById(R.id.text_date_event);
        tvQuantity = findViewById(R.id.text_capacity_event);
        tvEventOrganizer = findViewById(R.id.tvCategory2);
        tvLocation = findViewById(R.id.text_location_event);
        tvCategory1 = findViewById(R.id.tvCategory1);
        ImageView ivBack = findViewById(R.id.ivBack);
        MaterialButton btnRegister = findViewById(R.id.btnRegister);

        tvQuantity.setText(activity.getQuantity() + " sinh viên");

        // 3. Gán dữ liệu ban đầu
        tvTitle.setText(activity.getName());
        tvDescription.setText(activity.getDescription());
        tvTime.setText(activity.getStartTime() + " - " + activity.getEndTime());
        tvEventOrganizer.setText(activity.getEventOrganizer());
        tvLocation.setText(activity.getLocation());
        tvCategory1.setText(activity.getType());

        // 4. Đọc lại quantity từ Firebase để cập nhật mới nhất
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("activities")
                .child(activity.getKey());

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String quantityStr = snapshot.child("quantity").getValue(String.class);
                activity.setQuantity(quantityStr); // ép lại từ chuỗi "33" nếu dùng admin

                Long current = snapshot.child("currentQuantity").getValue(Long.class);
                if (current != null) {
                    activity.setCurrentQuantity(current.intValue());
                }

                tvQuantity.setText(activity.getQuantity() + " sinh viên"); // ✅ luôn đè lại từ Firebase

                // Sau khi có dữ liệu mới, xử lý nút đăng ký đúng logic
                if (activity.getCurrentQuantity() >= activity.getTotalQuantity()) {
                    btnRegister.setText("Đã đăng ký");
                    btnRegister.setEnabled(false);
                    btnRegister.setBackgroundTintList(null);
                } else {
                    btnRegister.setText("Đăng ký");
                    btnRegister.setEnabled(true);
                    btnRegister.setOnClickListener(v -> showRegisterDialog());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) { }
        });

        // 5. Xử lý nút back
        ivBack.setOnClickListener(v -> onBackPressed());

        Log.d("DetailEventActivity", "Activity: " + activity.getName() +
                ", Quantity: " + activity.getCurrentQuantity() + "/" + activity.getTotalQuantity());
    }


    private void showRegisterDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_register_activity, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnConfirm = dialogView.findViewById(R.id.btnConfirm);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            dialog.dismiss();

            String activityKey = activity.getKey();
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            int total = activity.getTotalQuantity();

            // Kiểm tra trong Firebase xem user đã đăng ký chưa
            DatabaseReference userRef = FirebaseDatabase.getInstance()
                    .getReference("activities")
                    .child(activityKey)
                    .child("registeredUsers")
                    .child(userId);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Log.d("DetailEventActivity", "➡ onDataChange được gọi từ Firebase");
                    Log.d("DetailEventActivity", "Raw snapshot: " + snapshot.toString());
                    if (snapshot.exists()) {
                        // Người dùng đã đăng ký
                        Toast.makeText(DetailEventActivity.this, "Bạn đã đăng ký hoạt động này rồi!", Toast.LENGTH_SHORT).show();
                        btnConfirm.setEnabled(false);
                        btnConfirm.setText("Đã đăng ký");
                    } else {
                        int current = activity.getCurrentQuantity();

                        if (current >= total) {
                            Toast.makeText(DetailEventActivity.this, "Hoạt động đã hết chỗ!", Toast.LENGTH_SHORT).show();
                            btnConfirm.setEnabled(false);
                            btnConfirm.setText("Hết chỗ");
                            return;
                        }

                        // Cho phép đăng ký
                        int newCurrent = current + 1;
                        activity.setCurrentQuantity(newCurrent);
                        activity.setQuantity(newCurrent + "/" + total);

                        // ✅ Ghi lại currentQuantity vào Firebase
                        DatabaseReference ref = FirebaseDatabase.getInstance()
                                .getReference("activities")
                                .child(activityKey);
                        ref.child("currentQuantity").setValue(newCurrent);

                        Log.d("DetailEventActivity", "Firebase-set currentQuantity = " + activity.getCurrentQuantity());
                        Log.d("DetailEventActivity", "TextView cập nhật sẽ là: " + activity.getQuantity());
                        Log.d("DetailEventActivity", "Cập nhật lại hiển thị TV sau khi load Firebase");
                        tvQuantity.setText(activity.getQuantity() + " sinh viên");

                        // Thêm userId vào danh sách đã đăng ký
                        DatabaseReference regRef = FirebaseDatabase.getInstance()
                                .getReference("activities")
                                .child(activityKey)
                                .child("registeredUsers")
                                .child(userId);
                        regRef.setValue(true);

                        // ✅ Thêm vào bảng history
                        DatabaseReference historyRef = FirebaseDatabase.getInstance()
                                .getReference("history")
                                .child(userId)
                                .child(activityKey);

                        HistoryModel history = new HistoryModel(
                                activity.getName(),
                                "Đã đăng ký",
                                activity.getType(),
                                activity.getDescription(),
                                activity.getStartTime(),
                                activity.getPoints(),
                                "Chưa nộp minh chứng"
                        );

                        historyRef.setValue(history);

                        Toast.makeText(DetailEventActivity.this, "Đăng ký hoạt động thành công", Toast.LENGTH_SHORT).show();

                        Log.d("DetailEventActivity", "Sending updatedActivity: " + activity.getQuantity());

                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("updatedActivity", activity);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(DetailEventActivity.this, "Lỗi khi kiểm tra trạng thái đăng ký", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}