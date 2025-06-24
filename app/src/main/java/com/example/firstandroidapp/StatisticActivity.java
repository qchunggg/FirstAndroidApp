package com.example.firstandroidapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Spinner;

import com.google.android.material.button.MaterialButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;

public class StatisticActivity extends AppCompatActivity {

    private RecyclerView rvActivities;
    private StatisticAdapter adapter;
    private List<ActivityModel> activityList = new ArrayList<>();

    private TextView tvTotalPoints;
    private GraphView graphView;

    private Spinner spinnerType, spinnerPeriod;
    private MaterialButton btnApplyFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics);
        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> finish()); // hoặc onBackPressed()



        // Ánh xạ View
        rvActivities = findViewById(R.id.rvRecentActivities);
        tvTotalPoints = findViewById(R.id.tvTotalPoints);
        graphView = findViewById(R.id.graph);

        spinnerPeriod = findViewById(R.id.spinner_period);
        spinnerType = findViewById(R.id.spinner_type);
        btnApplyFilter = findViewById(R.id.btn_apply_filter);

        // Setup RecyclerView
        rvActivities.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StatisticAdapter(activityList);
        rvActivities.setAdapter(adapter);

        // Load dữ liệu ban đầu
        loadActivitiesFromFirebase();

        // Xử lý lọc khi bấm nút "Áp dụng bộ lọc"
        btnApplyFilter.setOnClickListener(view -> applyFilter());
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(
                this, R.array.categories, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);

        ArrayAdapter<CharSequence> periodAdapter = ArrayAdapter.createFromResource(
                this, R.array.months, android.R.layout.simple_spinner_item);
        periodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPeriod.setAdapter(periodAdapter);

    }

    private void loadActivitiesFromFirebase() {
        FirebaseDatabase.getInstance().getReference("history")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        activityList.clear();
                        int totalPoint = 0;
                        List<DataPoint> graphPoints = new ArrayList<>();
                        int index = 0;

                        for (DataSnapshot data : snapshot.getChildren()) {
                            String name = data.child("name").getValue(String.class);
                            String date = data.child("date").getValue(String.class);
                            Long points = data.child("points").getValue(Long.class); // Firebase lưu số dạng Long

                            if (name != null && date != null && points != null) {
                                ActivityModel activity = new ActivityModel();
                                activity.setName(name);
                                activity.setStartTime(date);
                                activity.setCurrentQuantity(points.intValue());

                                activityList.add(activity);
                                totalPoint += points;
                                graphPoints.add(new DataPoint(index++, points));
                            }
                        }

                        adapter.notifyDataSetChanged();
                        tvTotalPoints.setText(String.valueOf(totalPoint));
                        drawGraph(graphPoints);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(StatisticActivity.this, "Không thể tải dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void drawGraph(List<DataPoint> points) {
        graphView.removeAllSeries();
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points.toArray(new DataPoint[0]));
        graphView.addSeries(series);
    }

    private void applyFilter() {
        String selectedType = spinnerType.getSelectedItem().toString().trim();
        String selectedPeriod = spinnerPeriod.getSelectedItem().toString().trim();

        // Có thể lọc theo thời gian, ở đây ví dụ đơn giản chỉ lọc theo loại hoạt động
        List<ActivityModel> filtered = new ArrayList<>();
        List<DataPoint> graphPoints = new ArrayList<>();
        int totalPoint = 0;
        int i = 0;

        for (ActivityModel activity : activityList) {
            boolean matchType = selectedType.equals("Tất cả") || activity.getType().equalsIgnoreCase(selectedType);

            if (matchType) {
                filtered.add(activity);
                totalPoint += activity.getCurrentQuantity();
                graphPoints.add(new DataPoint(i++, activity.getCurrentQuantity()));
            }
        }

        adapter = new StatisticAdapter(filtered);
        rvActivities.setAdapter(adapter);
        tvTotalPoints.setText(String.valueOf(totalPoint));
        drawGraph(graphPoints);
    }
}
