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

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private HistoryAdapter historyAdapter;
    private ArrayList<HistoryModel> activityList;
    private ArrayList<HistoryModel> fullActivityList;
    private DatabaseReference databaseReference;
    private Spinner spinnerState; // Declare Spinner

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.history, container, false);

        // Khởi tạo RecyclerView
        recyclerView = view.findViewById(R.id.rvHistory);
        activityList = new ArrayList<>();
        fullActivityList = new ArrayList<>();
        historyAdapter = new HistoryAdapter(activityList);
        recyclerView.setAdapter(historyAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Khởi tạo Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("history");

        // Khởi tạo Spinner
        spinnerState = view.findViewById(R.id.spinnerState);

        // Lấy dữ liệu từ Firebase và cập nhật Spinner
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fullActivityList.clear();
                activityList.clear();
                Set<String> categories = new HashSet<>();

                // Fetch history data and categories
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    HistoryModel history = snapshot.getValue(HistoryModel.class);
                    if (history != null) {
                        fullActivityList.add(history);
                        String category = history.getStatus().replace("\"", "");
                        categories.add(category);
                    }
                }

                // Add "Tất cả" and other categories to list
                ArrayList<String> categoryList = new ArrayList<>();
                categoryList.add("Tất cả");
                categoryList.addAll(categories);

                // Update Spinner with category list
                if (getContext() != null) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            getContext(),
                            R.layout.spinner_dropdown_item,
                            categoryList
                    );
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    spinnerState.setAdapter(adapter);
                }

                // Display all activities by default
                activityList.addAll(fullActivityList);
                historyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.out.println("Database error: " + error.getMessage());
            }
        });

        // Lắng nghe sự kiện chọn item
        spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = parent.getItemAtPosition(position).toString();
                filterActivities(selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing when nothing is selected
            }
        });

        return view;
    }

    // Filter activities based on selected category
    private void filterActivities(String category) {
        activityList.clear();
        if (category.equals("Tất cả")) {
            activityList.addAll(fullActivityList);
        } else {
            for (HistoryModel activity : fullActivityList) {
                if (activity.getStatus().replace("\"", "").equals(category)) {
                    activityList.add(activity);
                }
            }
        }
        historyAdapter.notifyDataSetChanged();
    }
}
