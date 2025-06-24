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
    private static final int PICK_IMAGE_REQUEST = 1;
    private HistoryModel currentSubmittingActivity;
    private int submittingPosition = -1;
    private android.app.ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.history, container, false);

        // Khởi tạo RecyclerView
        recyclerView = view.findViewById(R.id.rvHistory);
        activityList = new ArrayList<>();
        fullActivityList = new ArrayList<>();
        historyAdapter = new HistoryAdapter(activityList);
        historyAdapter.setOnSubmitProofClickListener((activity, position) -> {
            // Giả lập luôn là đã chọn ảnh
            activity.setProofStatus("Đã nộp minh chứng");
            activity.setStatus("Chưa xác nhận");

            activityList.set(position, activity);
            historyAdapter.notifyItemChanged(position);
        });
        recyclerView.setAdapter(historyAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ImageView ivMenu = view.findViewById(R.id.ivMenu);
        ivMenu.setOnClickListener(v -> showPopupMenu(v));

        // Khởi tạo Firebase
        loadUserRegisteredActivities();

        // Khởi tạo Spinner
        spinnerState = view.findViewById(R.id.spinnerState);

        // Lắng nghe sự kiện chọn item
        spinnerState = view.findViewById(R.id.spinnerState);

        // GÁN adapter từ mảng strings.xml
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.activity_states,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerState.setAdapter(adapter);

        // Xử lý chọn spinner
        spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = parent.getItemAtPosition(position).toString();
                filterActivities(selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
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
        DatabaseReference historyRef = FirebaseDatabase.getInstance()
                .getReference("history")
                .child(userId);

        historyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                fullActivityList.clear();
                activityList.clear();

                for (DataSnapshot actSnap : snapshot.getChildren()) {
                    HistoryModel model = actSnap.getValue(HistoryModel.class);
                    if (model != null) {
                        fullActivityList.add(model);
                        activityList.add(model);
                    }
                }

                historyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Xử lý nếu cần
            }
        });
    }


    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn minh chứng"), PICK_IMAGE_REQUEST);
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
