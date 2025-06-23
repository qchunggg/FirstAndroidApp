package com.example.firstandroidapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    private ImageView ivSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activities, container, false);

        // Ánh xạ view
        recyclerView = view.findViewById(R.id.rvActivities);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        ivSearch = view.findViewById(R.id.ivSearch);

        activityList = new ArrayList<>();
        fullActivityList = new ArrayList<>();

        activityAdapter = new ActivityAdapter(activityList);
        recyclerView.setAdapter(activityAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ImageView ivMenu = view.findViewById(R.id.ivMenu);
        ivMenu.setOnClickListener(v -> showPopupMenu(v));

        // Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("activities");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fullActivityList.clear();
                activityList.clear();
                Set<String> categories = new HashSet<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ActivityModel activity = snapshot.getValue(ActivityModel.class);
                    if (activity != null) {
                        try {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                            LocalDate today = LocalDate.now();
                            LocalDate startDate = LocalDate.parse(activity.getStartTime(), formatter); // Sửa ở đây
                            if (!startDate.isBefore(today)) {  // Lọc hoạt động chưa diễn ra hoặc đang diễn ra
                                fullActivityList.add(activity);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();  // Bỏ qua lỗi nếu ngày không hợp lệ
                        }
                        String type = activity.getType().replace("\"", "");
                        categories.add(type);
                    }
                }

                // Thêm danh mục vào Spinner
                ArrayList<String> categoryList = new ArrayList<>();
                categoryList.add("Tất cả");
                categoryList.addAll(categories);

                if (getContext() != null) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_dropdown_item, categoryList);
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    spinnerCategory.setAdapter(adapter);
                }

                activityList.addAll(fullActivityList);
                activityAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Firebase", "Failed to read value.", databaseError.toException());
            }
        });

        // Lọc khi chọn danh mục
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = parent.getItemAtPosition(position).toString();
                filterActivities(selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Gắn sự kiện click cho nút tìm kiếm
        ivSearch.setOnClickListener(v -> {
            Log.d("ActivityFragment", "Search icon clicked");
            Intent intent = new Intent(getContext(), SearchActivity.class);
            intent.putExtra("activityList", new ArrayList<>(fullActivityList));
            startActivity(intent);
        });

        return view;
    }

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

    private void showPopupMenu(View anchor) {
        View popupView = LayoutInflater.from(getContext()).inflate(R.layout.nav_menu, null);

        int popupWidth = (int) (getResources().getDisplayMetrics().widthPixels * 0.50);

        PopupWindow popupWindow = new PopupWindow(
                popupView,
                popupWidth,
                ViewGroup.LayoutParams.MATCH_PARENT,
                true
        );

        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80000000")));
        popupWindow.setAnimationStyle(R.style.PopupAnimation);

        popupWindow.showAtLocation(anchor.getRootView(), Gravity.START, 0, 0);

        LinearLayout logoutLayout = popupView.findViewById(R.id.logout);
        if (logoutLayout != null) {
            logoutLayout.setOnClickListener(v -> {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });
        }
    }
}
