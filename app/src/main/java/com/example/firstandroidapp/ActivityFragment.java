package com.example.firstandroidapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ActivityFragment extends Fragment {

    private RecyclerView recyclerView;
    private ActivityAdapter activityAdapter;
    private ArrayList<ActivityModel> activityList;
    private ArrayList<ActivityModel> fullActivityList;
    private DatabaseReference databaseReference;
    private Spinner spinnerCategory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activities, container, false);

        // Khởi tạo RecyclerView
        recyclerView = view.findViewById(R.id.rvActivities);
        activityList = new ArrayList<>();
        fullActivityList = new ArrayList<>();
        activityAdapter = new ActivityAdapter(activityList);
        recyclerView.setAdapter(activityAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Khởi tạo Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("activities");

        // Khởi tạo Spinner
        spinnerCategory = view.findViewById(R.id.spinnerCategory);

        // Lấy dữ liệu từ Firebase và cập nhật Spinner
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fullActivityList.clear();
                activityList.clear();
                Set<String> categories = new HashSet<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ActivityModel activity = snapshot.getValue(ActivityModel.class);
                    if (activity != null) {
                        fullActivityList.add(activity);
                        String type = activity.getType().replace("\"", "");
                        categories.add(type);
                    }
                }

                // Thêm "Tất cả" và các category vào danh sách
                ArrayList<String> categoryList = new ArrayList<>();
                categoryList.add("Tất cả");
                categoryList.addAll(categories);

                // Cập nhật Spinner với layout tùy chỉnh
                if (getContext() != null) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            getContext(),
                            R.layout.spinner_dropdown_item,
                            categoryList
                    );
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    spinnerCategory.setAdapter(adapter);
                }

                activityList.addAll(fullActivityList);
                activityAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.out.println("Database error: " + error.getMessage());
            }
        });

        // Lắng nghe sự kiện chọn item
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = parent.getItemAtPosition(position).toString();
                filterActivities(selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không làm gì khi không chọn
            }
        });

        return view;
    }

    // Hàm lọc danh sách hoạt động
    private void filterActivities(String category) {
        activityList.clear();
        if (category.equals("Tất cả")) {
            activityList.addAll(fullActivityList);
        } else {
            for (ActivityModel activity : fullActivityList) {
                if (activity.getType().replace("\"", "").equals(category)) {
                    activityList.add(activity);
                }
            }
        }
        activityAdapter.notifyDataSetChanged();
    }
}