package com.example.firstandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ActivitiesManageActivity extends AppCompatActivity {

    private RecyclerView rvActivities;
    private ActivityManageAdapter adapter;
    private List<ActivityModel> activityList;
    private DatabaseReference database;
    private ImageView ivBack;
    private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manager_activity);

        // Ánh xạ view
        rvActivities = findViewById(R.id.rvActivities);
        ivBack = findViewById(R.id.ivBack);
        btnAdd = findViewById(R.id.btnAdd);

        // Cấu hình RecyclerView
        rvActivities.setLayoutManager(new LinearLayoutManager(this));
        activityList = new ArrayList<>();

        // Tạo adapter và truyền actionListener vào
        adapter = new ActivityManageAdapter(activityList, activity -> {
            // Xử lý sự kiện xóa khi người dùng nhấn nút xóa
            showDeleteConfirmationDialog(activity);
        });
        rvActivities.setAdapter(adapter);

        // Truy cập Firebase Database
        database = FirebaseDatabase.getInstance().getReference("activities");
        loadActivitiesFromFirebase();

        // Quay lại trang admin
        ivBack.setOnClickListener(v -> {
            startActivity(new Intent(ActivitiesManageActivity.this, AdminActivity.class));
            finish();
        });

        btnAdd.setOnClickListener(v -> {
            // Logic để thêm hoạt động mới
        });
    }

    // Tải danh sách hoạt động từ Firebase
    private void loadActivitiesFromFirebase() {
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                activityList.clear();
                for (DataSnapshot item : snapshot.getChildren()) {
                    ActivityModel activity = item.getValue(ActivityModel.class);
                    if (activity != null) {
                        activityList.add(activity);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Firebase", "Failed to load activities", error.toException());
            }
        });
    }

    // Hiển thị dialog xác nhận xóa
    private void showDeleteConfirmationDialog(ActivityModel activity) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_remove_category, null);

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        final android.app.AlertDialog dialog = builder.setView(dialogView).create();

        // Nút "Hủy"
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        // Nút "Đồng ý" - Xóa hoạt động khỏi Firebase
        Button btnConfirm = dialogView.findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(v -> {
            String key = activity.getKey();  // Lấy key của hoạt động
            if (key != null) {
                deleteActivityFromFirebase(key);
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Không tìm thấy key của hoạt động", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    // Xóa hoạt động khỏi Firebase
    private void deleteActivityFromFirebase(String activityKey) {
        if (activityKey != null) {
            database.child(activityKey).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(ActivitiesManageActivity.this, "Xóa hoạt động thành công", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ActivitiesManageActivity.this, "Xóa không thành công: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(ActivitiesManageActivity.this, "Không tìm thấy key của hoạt động", Toast.LENGTH_SHORT).show();
        }
    }

}
