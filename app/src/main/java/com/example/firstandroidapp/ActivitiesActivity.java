package com.example.firstandroidapp;

import android.content.Intent;
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
    private List<ActivityModel> fullActivityList;

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
        activityAdapter = new ActivityAdapter(this, activityList);
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
                        // Gán key từ snapshot
                        activity.setKey(snapshot.getKey());
                        fullActivityList.add(activity);

                        // Chỉ thêm hoạt động chưa diễn ra hoặc đang diễn ra
                        try {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                            LocalDate today = LocalDate.now();
                            LocalDate startDate = LocalDate.parse(activity.getStartTime(), formatter);
                            LocalDate endDate = LocalDate.parse(activity.getEndTime(), formatter);


                            String quantityStr = snapshot.child("quantity").getValue(String.class);
                            activity.setQuantity(quantityStr); // ⟶ để model tự tách current / total
                            Long current = snapshot.child("currentQuantity").getValue(Long.class);
                            if (current != null) {
                                activity.setCurrentQuantity(current.intValue());
                            }
                            DatabaseReference ref = snapshot.getRef();
                            if (!snapshot.hasChild("currentQuantity")) {
                                ref.child("currentQuantity").setValue(activity.getCurrentQuantity());
                            }

                            if ((startDate.isEqual(today) || startDate.isAfter(today)) &&
                                    !endDate.isBefore(today)) {
                                activityList.add(activity);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();  // Bỏ qua lỗi nếu ngày hoặc quantity không hợp lệ
                        }

                        Log.d("DataCheck", "Name: " + activity.getName()
                                + ", Start: " + activity.getStartTime()
                                + ", End: " + activity.getEndTime()
                                + ", Quantity: " + activity.getQuantity());

                    }
                }

                // Cập nhật RecyclerView
                activityAdapter.notifyDataSetChanged();
                Log.d(TAG, "Data loaded, activityList size: " + activityList.size());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            ActivityModel updatedActivity = (ActivityModel) data.getSerializableExtra("updatedActivity");
            if (updatedActivity != null) {
                for (int i = 0; i < activityList.size(); i++) {
                    ActivityModel activity = activityList.get(i);
                    if (activity.getKey().equals(updatedActivity.getKey())) {
                        activity.setCurrentQuantity(updatedActivity.getCurrentQuantity());
                        activity.setTotalQuantity(updatedActivity.getTotalQuantity());
                        activity.setQuantity(updatedActivity.getQuantity());  // ✅ đồng bộ lại chuỗi quantity nếu cần
                        activityList.set(i, activity);
                        break;
                    }
                }
                activityAdapter.notifyDataSetChanged();
            }
            Log.d("ActivitiesActivity", "onActivityResult received, updated: " + updatedActivity.getQuantity());
        }


    }

    private void filterActivitiesByCategory(int categoryIndex) {
        String[] categories = getResources().getStringArray(R.array.categories);
        String selectedCategory = categories[categoryIndex];

        List<ActivityModel> filteredList = new ArrayList<>();
        Log.d(TAG, "Filtering category: " + selectedCategory);

        for (ActivityModel activity : activityList) {
            boolean categoryMatch = selectedCategory.equals("Tất cả") || activity.getType().equals(selectedCategory);

            // Lọc theo ngày và số lượng
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate today = LocalDate.now();
            LocalDate startDate = LocalDate.parse(activity.getStartTime(), formatter);
            LocalDate endDate = LocalDate.parse(activity.getEndTime(), formatter);

            boolean isDateValid = (startDate.isEqual(today) || startDate.isAfter(today)) &&
                    !endDate.isBefore(today);

            if (categoryMatch && isDateValid) {
                filteredList.add(activity);
            }

        }

        // Cập nhật danh sách hoạt động đã lọc
        activityAdapter.updateData(filteredList);
    }
}

