package com.example.firstandroidapp;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView rvHistory;
    private HistoryAdapter historyAdapter;
    private List<HistoryModel> activityList;
    private Spinner spinnerState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);  // Use your layout for this Activity

        // Initialize views
        rvHistory = findViewById(R.id.rvHistory);
        spinnerState = findViewById(R.id.spinnerState);

        // Set up RecyclerView
        rvHistory.setLayoutManager(new LinearLayoutManager(this));

        // Create sample data
        activityList = new ArrayList<>();
        createSampleData();
        historyAdapter = new HistoryAdapter(activityList);
        rvHistory.setAdapter(historyAdapter);

        // Set up Spinner with categories
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.activity_states, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerState.setAdapter(spinnerAdapter);

        // Spinner item selection listener
        spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                filterActivitiesByState(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });
    }

    // Sample data for activities
    private void createSampleData() {
        activityList.add(new HistoryModel("Hoạt động 1", "Đã hoàn thành", "Tình nguyện", "Mô tả ngắn về hoạt động 1", "10/06/2025", 30, "Chưa có minh chứng"));
        activityList.add(new HistoryModel("Hoạt động 2", "Chưa hoàn thành", "Học tập", "Mô tả ngắn về hoạt động 2", "11/06/2025", 25, "Đã nộp minh chứng"));
        activityList.add(new HistoryModel("Hoạt động 3", "Đang tiến hành", "Thể thao", "Mô tả ngắn về hoạt động 3", "12/06/2025", 20, "Chưa nộp minh chứng"));
        activityList.add(new HistoryModel("Hoạt động 4", "Đã hoàn thành", "Văn hóa", "Mô tả ngắn về hoạt động 4", "13/06/2025", 40, "Đã nộp minh chứng"));
        historyAdapter.notifyDataSetChanged();
    }

    // Filter activities based on selected state
    private void filterActivitiesByState(int stateIndex) {
        String[] states = getResources().getStringArray(R.array.activity_states);
        String selectedState = states[stateIndex];

        List<HistoryModel> filteredList = new ArrayList<>();
        for (HistoryModel activity : activityList) {
            if (activity.getStatus().equals(selectedState) || selectedState.equals("Tất cả")) {
                filteredList.add(activity);
            }
        }

        // Update the adapter with the filtered list
        historyAdapter = new HistoryAdapter(filteredList);
        rvHistory.setAdapter(historyAdapter);
    }


}
