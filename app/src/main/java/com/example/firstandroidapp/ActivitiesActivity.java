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

import java.util.ArrayList;
import java.util.List;

public class ActivitiesActivity extends AppCompatActivity {

    private Spinner spinnerCategory;
    private RecyclerView rvActivities;
    private ActivityAdapter activityAdapter;
    private List<ActivityModel> activityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activities);

        spinnerCategory = findViewById(R.id.spinnerCategory);
        rvActivities = findViewById(R.id.rvActivities);

        // Tạo ArrayAdapter từ mảng categories trong arrays.xml
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Khởi tạo RecyclerView
        rvActivities.setLayoutManager(new LinearLayoutManager(this));
        activityList = new ArrayList<>();
        activityAdapter = new ActivityAdapter(activityList);
        rvActivities.setAdapter(activityAdapter);
        Log.d("SPINNER_CHECK", "Adapter size: " + adapter.getCount());


        // Giả lập dữ liệu
        loadActivitiesData();

        // Lắng nghe sự kiện chọn danh mục
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                filterActivitiesByCategory(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });
    }

    private void loadActivitiesData() {
        // Dữ liệu mẫu (thực tế bạn sẽ lấy từ API hoặc CSDL)
        activityList.add(new ActivityModel("Hoạt động 1", "Tình nguyện", "Mô tả ngắn về hoạt động", "20/05", "45/50", R.drawable.ic_photo));
        activityList.add(new ActivityModel("Hoạt động 2", "Học tập", "Mô tả ngắn về hoạt động", "21/05", "30/50", R.drawable.ic_photo));
        activityAdapter.notifyDataSetChanged();
    }

    private void filterActivitiesByCategory(int categoryIndex) {
        String[] categories = getResources().getStringArray(R.array.categories);
        String selectedCategory = categories[categoryIndex];

        List<ActivityModel> filteredList = new ArrayList<>();
        for (ActivityModel activity : activityList) {
            if (activity.getType().equals(selectedCategory)) {
                filteredList.add(activity);
            }
        }
        activityAdapter = new ActivityAdapter(filteredList);
        rvActivities.setAdapter(activityAdapter);
    }
}
