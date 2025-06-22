package com.example.firstandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

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
        setContentView(R.layout.manager_activity); // Giao diện chứa RecyclerView và nút thao tác

        // Ánh xạ view
        rvActivities = findViewById(R.id.rvActivities);
        ivBack = findViewById(R.id.ivBack);
        btnAdd = findViewById(R.id.btnAdd);

        // Cấu hình RecyclerView
        rvActivities.setLayoutManager(new LinearLayoutManager(this));
        activityList = new ArrayList<>();
        adapter = new ActivityManageAdapter(activityList);
        rvActivities.setAdapter(adapter);


        // Truy cập Firebase Database
        database = FirebaseDatabase.getInstance().getReference("activities");
        loadActivitiesFromFirebase();

        // Quay lại trang admin
        ivBack.setOnClickListener(v -> {
            startActivity(new Intent(ActivitiesManageActivity.this, AdminActivity.class));
            finish();
        });

        // Chức năng thêm hoạt động mới (mở màn khác nếu bạn có)
        /*
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(ActivitiesManageActivity.this, AddActivityActivity.class);
            startActivity(intent);
        });
        */
    }

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
}
