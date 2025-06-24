package com.example.firstandroidapp;

import android.app.Activity;
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
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

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
    private ActivityResultLauncher<Intent> detailLauncher;
    private android.app.ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activities, container, false);

        // Ánh xạ view
        recyclerView = view.findViewById(R.id.rvActivities);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        ivSearch = view.findViewById(R.id.ivSearch);

        activityList = new ArrayList<>();
        fullActivityList = new ArrayList<>();

        activityAdapter = new ActivityAdapter(requireContext(), activityList);
        activityAdapter.setOnActivityClickListener(activity -> {
            Intent intent = new Intent(requireContext(), DetailEventActivity.class);
            Log.d("Fragment", "activity.getKey() before gửi sang Detail: " + activity.getKey());
            intent.putExtra("activity", activity);
            detailLauncher.launch(intent); // dùng launcher để nhận lại updatedActivity
        });

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
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate today = LocalDate.now();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ActivityModel activity = snapshot.getValue(ActivityModel.class);
                    Long points = snapshot.child("points").getValue(Long.class);
                    if (points != null) {
                        activity.setPoints(points.intValue());
                    }

                    if (activity != null) {
                        activity.setKey(snapshot.getKey());

                        String quantityStr = snapshot.child("quantity").getValue(String.class);
                        activity.setQuantity(quantityStr);
                        Long current = snapshot.child("currentQuantity").getValue(Long.class);
                        if (current != null) {
                            activity.setCurrentQuantity(current.intValue());
                        }
                        DatabaseReference ref = snapshot.getRef();
                        if (!snapshot.hasChild("currentQuantity")) {
                            ref.child("currentQuantity").setValue(activity.getCurrentQuantity());
                        }

                        try {
                            LocalDate startDate = LocalDate.parse(activity.getStartTime(), formatter);
                            LocalDate endDate = LocalDate.parse(activity.getEndTime(), formatter);

                            if ((startDate.isEqual(today) || startDate.isAfter(today)) &&
                                    !endDate.isBefore(today)) {

                                fullActivityList.add(activity);  // ✔️ Chỉ thêm cái hợp lệ
                                activityList.add(activity);     // ✔️ Cho hiển thị mặc định ban đầu
                                String cleanType = activity.getType().trim().replace("\"", "");
                                activity.setType(cleanType); // chuẩn hóa 1 lần duy nhất
                                categories.add(cleanType);

                            }
                        } catch (Exception e) {
                            Log.e("ActivityFragment", "Lỗi xử lý activity: " + activity.getName(), e);
                        }
                    }
                }
                activityAdapter.notifyDataSetChanged();

                // Cập nhật danh mục spinner
                ArrayList<String> categoryList = new ArrayList<>();
                categoryList.add("Tất cả");
                categoryList.addAll(categories);

                if (getContext() != null) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_dropdown_item, categoryList);
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    spinnerCategory.setAdapter(adapter);
                }

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

        detailLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        ActivityModel updatedActivity = (ActivityModel) result.getData().getSerializableExtra("updatedActivity");
                        if (updatedActivity != null) {
                            for (int i = 0; i < activityList.size(); i++) {
                                if (activityList.get(i).getKey().equals(updatedActivity.getKey())) {
                                    activityList.set(i, updatedActivity);
                                    activityAdapter.notifyItemChanged(i);
                                    break;
                                }
                            }
                            for (int i = 0; i < fullActivityList.size(); i++) {
                                if (fullActivityList.get(i).getKey().equals(updatedActivity.getKey())) {
                                    fullActivityList.set(i, updatedActivity);
                                    break;
                                }
                            }

                        }
                    }
                }
        );

        progressDialog = new android.app.ProgressDialog(getContext());
        progressDialog.setMessage("Đang đăng xuất...");
        progressDialog.setCancelable(false);

        return view;
    }

    private void filterActivities(String category) {
        activityList.clear();
        for (ActivityModel activity : fullActivityList) {
            if (category.equals("Tất cả") || activity.getType().equals(category)) {
                activityList.add(activity);
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
