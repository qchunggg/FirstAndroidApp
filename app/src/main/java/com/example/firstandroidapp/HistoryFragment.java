package com.example.firstandroidapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private HistoryAdapter historyAdapter;
    private ArrayList<HistoryModel> activityList;
    private ArrayList<HistoryModel> fullActivityList;
    private Spinner spinnerState; // Declare Spinner
    private android.app.ProgressDialog progressDialog;

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

        ImageView ivMenu = view.findViewById(R.id.ivMenu);
        ivMenu.setOnClickListener(v -> showPopupMenu(v));

        // Khởi tạo Firebase
        loadUserRegisteredActivities();

        // Khởi tạo Spinner
        spinnerState = view.findViewById(R.id.spinnerState);

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

        progressDialog = new android.app.ProgressDialog(getContext());
        progressDialog.setMessage("Đang đăng xuất...");
        progressDialog.setCancelable(false);

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

    private void loadUserRegisteredActivities() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userActRef = FirebaseDatabase.getInstance()
                .getReference("userActivities")
                .child(userId);

        userActRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                fullActivityList.clear();
                activityList.clear();
                Set<String> categories = new HashSet<>();

                for (DataSnapshot actSnap : snapshot.getChildren()) {
                    String activityId = actSnap.getKey();

                    FirebaseDatabase.getInstance().getReference("activities").child(activityId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnap) {
                                    if (dataSnap.exists()) {
                                        String name = dataSnap.child("name").getValue(String.class);
                                        String desc = dataSnap.child("description").getValue(String.class);
                                        String type = dataSnap.child("type").getValue(String.class);
                                        String date = dataSnap.child("startTime").getValue(String.class);
                                        Integer points = dataSnap.child("points").getValue(Integer.class);

                                        HistoryModel model = new HistoryModel(
                                                name != null ? name : "",
                                                "Đã đăng ký",
                                                type != null ? type : "",
                                                desc != null ? desc : "",
                                                date != null ? date : "",
                                                points != null ? points : 0,
                                                "Chưa nộp minh chứng"
                                        );

                                        fullActivityList.add(model);
                                        categories.add("Đã đăng ký");
                                        activityList.add(model);
                                        historyAdapter.notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError error) {
                                    // Handle error if needed
                                }
                            });
                }

                // Cập nhật spinner
                if (getContext() != null) {
                    ArrayList<String> categoryList = new ArrayList<>();
                    categoryList.add("Tất cả");
                    categoryList.addAll(categories);

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            getContext(),
                            R.layout.spinner_dropdown_item,
                            categoryList
                    );
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    spinnerState.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    private void showPopupMenu(View anchor) {
        View popupView = LayoutInflater.from(getContext()).inflate(R.layout.nav_menu, null);

        int popupWidth = (int) (getResources().getDisplayMetrics().widthPixels * 0.5);

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
                progressDialog.show(); // Hiện hiệu ứng loading

                logoutLayout.postDelayed(() -> {
                    FirebaseAuth.getInstance().signOut();

                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    progressDialog.dismiss(); // Tắt loading
                    startActivity(intent);
                }, 1300); // Loading trong 1.3 giây
            });
        }
    }

}
