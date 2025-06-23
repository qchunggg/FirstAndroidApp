package com.example.firstandroidapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ActivitiesActivity extends AppCompatActivity {

    private static final String TAG = "ActivitiesActivity";
    private Spinner spinnerCategory;
    private RecyclerView rvActivities;
    private ActivityAdapter activityAdapter;
    private List<ActivityModel> activityList;
    private List<ActivityModel> fullActivityList;  // Danh sách đầy đủ

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate started");

        setContentView(R.layout.activities);

        // Ánh xạ view
        spinnerCategory = findViewById(R.id.spinnerCategory);
        rvActivities = findViewById(R.id.rvActivities);

        // Khởi tạo danh sách và dữ liệu mẫu
        activityList = new ArrayList<>();
        fullActivityList = new ArrayList<>();
        activityAdapter = new ActivityAdapter(activityList);
        rvActivities.setLayoutManager(new LinearLayoutManager(this));
        rvActivities.setAdapter(activityAdapter);

        // Cài đặt danh mục spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.categories,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Lọc khi chọn danh mục
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                filterActivitiesByCategory(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Không làm gì
            }
        });

        Log.d(TAG, "onCreate finished");

        // Tải dữ liệu Firebase
        loadActivitiesData();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Lắng nghe sự thay đổi dữ liệu từ Firebase
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("activities");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fullActivityList.clear();
                activityList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ActivityModel activity = snapshot.getValue(ActivityModel.class);
                    if (activity != null) {
                        fullActivityList.add(activity);

                        // Chỉ thêm hoạt động chưa diễn ra hoặc đang diễn ra
                        try {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                            LocalDate today = LocalDate.now();
                            LocalDate activityDate = LocalDate.parse(activity.getTime(), formatter);

                            if (!activityDate.isBefore(today)) {
                                activityList.add(activity);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();  // Bỏ qua lỗi nếu ngày không hợp lệ
                        }
                    }
                }

                // Cập nhật RecyclerView
                activityAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Firebase", "Failed to read value.", databaseError.toException());
            }
        });
    }

    private void loadActivitiesData() {
        Log.d(TAG, "Loading sample data");
        activityList.add(new ActivityModel("Hoạt động 1", "Tình nguyện", "Mô tả ngắn về hoạt động", "20/05","21/05", "45/50", R.drawable.ic_photo));
        activityList.add(new ActivityModel("Hoạt động 2", "Học tập", "Mô tả ngắn về hoạt động", "21/05","21/05", "30/50", R.drawable.ic_photo));
        activityAdapter = new ActivityAdapter(activityList);
        rvActivities.setAdapter(activityAdapter);
    }

    private void filterActivitiesByCategory(int categoryIndex) {
        String[] categories = getResources().getStringArray(R.array.categories);
        String selectedCategory = categories[categoryIndex];

        List<ActivityModel> filteredList = new ArrayList<>();
        Log.d(TAG, "Filtering category: " + selectedCategory);

        for (ActivityModel activity : activityList) {
            if (selectedCategory.equals("Tất cả") || activity.getType().equals(selectedCategory)) {
                filteredList.add(activity);
            }
        }

        activityAdapter = new ActivityAdapter(filteredList);
        rvActivities.setAdapter(activityAdapter);
    }
}
