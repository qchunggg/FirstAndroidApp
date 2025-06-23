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

    private TextView tvTitle, tvDescription, tvTime, tvLocation, tvQuantity, tvEventOrganizer;
    private ActivityModel activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_event);

        // Nhận activity từ Intent
        activity = (ActivityModel) getIntent().getSerializableExtra("activity");

        // Ánh xạ View
        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        tvTime = findViewById(R.id.text_date_event);
        tvQuantity = findViewById(R.id.text_capacity_event);
        tvEventOrganizer = findViewById(R.id.tvCategory2);
        tvLocation = findViewById(R.id.text_location_event);
        ImageView ivBack = findViewById(R.id.ivBack);
        MaterialButton btnRegister = findViewById(R.id.btnRegister);

        // Gán dữ liệu từ activity
        tvTitle.setText(activity.getName());
        tvDescription.setText(activity.getDescription());
        tvTime.setText(activity.getStartTime() + " - " + activity.getEndTime());
        tvQuantity.setText(activity.getCurrentQuantity() + "/" + activity.getTotalQuantity() + " sinh viên");
        tvEventOrganizer.setText(activity.getEventOrganizer());
        tvLocation.setText(activity.getLocation());

        // Xử lý nút back
        ivBack.setOnClickListener(v -> onBackPressed());

        // Xử lý nút đăng ký
        if (activity.getCurrentQuantity() >= activity.getTotalQuantity()) {
            btnRegister.setText("Đã đăng ký");
            btnRegister.setEnabled(false);
            btnRegister.setBackgroundTintList(null);
        } else {
            btnRegister.setText("Đăng ký");
            btnRegister.setEnabled(true);
            btnRegister.setOnClickListener(v -> showRegisterDialog());
        }

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
                        updateQuantityInFirebase(activity.getKey(), newCurrent, total);

                        // Thêm userId vào danh sách đã đăng ký
                        DatabaseReference regRef = FirebaseDatabase.getInstance()
                                .getReference("activities")
                                .child(activityKey)
                                .child("registeredUsers")
                                .child(userId);
                        regRef.setValue(true);

                        Toast.makeText(DetailEventActivity.this, "Đăng ký hoạt động thành công", Toast.LENGTH_SHORT).show();

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

    private void updateQuantityInFirebase(String activityKey, int newCurrent, int total) {
        if (activityKey != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference("activities")
                    .child(activityKey);
            ref.child("quantity").setValue(newCurrent + "/" + total)
                    .addOnSuccessListener(aVoid -> Log.d("DetailEventActivity", "Quantity updated"))
                    .addOnFailureListener(e -> Log.e("DetailEventActivity", "Update failed", e));
        }
    }
}