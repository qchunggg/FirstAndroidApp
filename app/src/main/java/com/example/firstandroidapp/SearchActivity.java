package com.example.firstandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView rvSearchResults;
    private ImageButton btnBack;
    private TextInputEditText etSearch;
    private List<ActivityModel> activityList;
    private ActivityAdapter activityAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        rvSearchResults = findViewById(R.id.rvSearchResults);
        btnBack = findViewById(R.id.btnBack);
        etSearch = findViewById(R.id.etSearch);

        // Cài đặt RecyclerView
        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
        activityList = new ArrayList<>(); // Sẽ nhận dữ liệu từ Intent hoặc Firebase
        activityAdapter = new ActivityAdapter(activityList);
        rvSearchResults.setAdapter(activityAdapter);

        // Xử lý nút Back
        btnBack.setOnClickListener(v -> finish());

        // Lọc danh sách theo thời gian thực
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterSearchResults(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Nhận dữ liệu từ Intent (từ ActivitiesActivity)
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("activityList")) {
            activityList = (List<ActivityModel>) intent.getSerializableExtra("activityList");
            activityAdapter = new ActivityAdapter(new ArrayList<>(activityList));
            rvSearchResults.setAdapter(activityAdapter);
        }
    }

    private void filterSearchResults(String query) {
        List<ActivityModel> filteredList = new ArrayList<>();
        if (query.isEmpty()) {
            filteredList.addAll(activityList);
        } else {
            String filterPattern = query.toLowerCase().trim();
            for (ActivityModel activity : activityList) {
                if (activity.getName().toLowerCase().contains(filterPattern)) {
                    filteredList.add(activity);
                }
            }
        }
        activityAdapter = new ActivityAdapter(filteredList);
        rvSearchResults.setAdapter(activityAdapter);
    }
}
