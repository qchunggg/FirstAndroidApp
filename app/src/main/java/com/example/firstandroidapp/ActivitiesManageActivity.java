package com.example.firstandroidapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ActivitiesManageActivity extends AppCompatActivity {

    private RecyclerView rvActivities;
    private ActivityManageAdapter adapter;
    private List<ActivityModel> activityList;
    private DatabaseReference database;
    private ImageView ivBack;
    private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manager_activity);

        // Ánh xạ view
        rvActivities = findViewById(R.id.rvActivities);
        ivBack = findViewById(R.id.ivBack);
        btnAdd = findViewById(R.id.btnAdd);

        // Cấu hình RecyclerView
        rvActivities.setLayoutManager(new LinearLayoutManager(this));
        activityList = new ArrayList<>();

        // Tạo adapter và truyền actionListener vào
        adapter = new ActivityManageAdapter(activityList, new ActivityManageAdapter.OnActivityActionListener() {
            @Override
            public void onDelete(ActivityModel activity) {
                showDeleteConfirmationDialog(activity);
            }

            @Override
            public void onEdit(ActivityModel activity) {
                showEditActivityDialog(activity);
            }
        });

        rvActivities.setAdapter(adapter);

        // Truy cập Firebase Database
        database = FirebaseDatabase.getInstance().getReference("activities");
        loadActivitiesFromFirebase();

        // Quay lại trang admin
        ivBack.setOnClickListener(v -> {
            startActivity(new Intent(ActivitiesManageActivity.this, AdminActivity.class));
            finish();
        });

        // Mở dialog thêm hoạt động mới
        btnAdd.setOnClickListener(v -> showAddActivityDialog());
    }

    // Tải danh sách hoạt động từ Firebase
    private void loadActivitiesFromFirebase() {
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                activityList.clear();
                for (DataSnapshot item : snapshot.getChildren()) {
                    ActivityModel activity = item.getValue(ActivityModel.class);
                    if (activity != null) {
                        activityList.add(activity);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Firebase", "Failed to load activities", error.toException());
            }
        });
    }

    // Hiển thị dialog xác nhận xóa
    private void showDeleteConfirmationDialog(ActivityModel activity) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_remove_category, null);
        AlertDialog dialog = new AlertDialog.Builder(this).setView(dialogView).create();

        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnConfirm = dialogView.findViewById(R.id.btnConfirm);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            String key = activity.getKey();
            if (key != null) {
                deleteActivityFromFirebase(key);
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Không tìm thấy key của hoạt động", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    // Xóa hoạt động khỏi Firebase
    private void deleteActivityFromFirebase(String activityKey) {
        if (activityKey != null) {
            database.child(activityKey).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Xóa hoạt động thành công", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Xóa không thành công: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Không tìm thấy key của hoạt động", Toast.LENGTH_SHORT).show();
        }
    }

    // Hiển thị dialog thêm hoạt động
    private void showAddActivityDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.add_manager_activity, null);
        AlertDialog dialog = new AlertDialog.Builder(this).setView(dialogView).create();

        EditText etName = dialogView.findViewById(R.id.et_ten_hoat_dong);
        EditText etDescription = dialogView.findViewById(R.id.et_mo_ta);
        Spinner tvCategory = dialogView.findViewById(R.id.spinner_category);
        EditText etTimeStart = dialogView.findViewById(R.id.et_time_start);
        EditText etTimeEnd = dialogView.findViewById(R.id.et_time_end);
        EditText etLocation = dialogView.findViewById(R.id.et_location);
        EditText etReward = dialogView.findViewById(R.id.et_reward);
        EditText etQuantity = dialogView.findViewById(R.id.et_participation_limit);
        Button btnUpdate = dialogView.findViewById(R.id.btn_update);
        ImageView btnBack = dialogView.findViewById(R.id.btn_back);

        List<CategoryModel> categoryList = new ArrayList<>();
        ArrayAdapter<CategoryModel> categoryAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categoryList
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tvCategory.setAdapter(categoryAdapter);

        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference("categories");
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                for (DataSnapshot item : snapshot.getChildren()) {
                    CategoryModel category = item.getValue(CategoryModel.class);
                    if (category != null) {
                        category.setKey(item.getKey());
                        categoryList.add(category);
                    }
                }
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ActivitiesManageActivity.this, "Lỗi khi tải danh mục", Toast.LENGTH_SHORT).show();
            }
        });

        ImageView timePickerStart = dialogView.findViewById(R.id.time_picker_start);
        ImageView timePickerEnd = dialogView.findViewById(R.id.time_picker_end);
        timePickerStart.setOnClickListener(v -> showDatePickerDialog(etTimeStart));
        timePickerEnd.setOnClickListener(v -> showDatePickerDialog(etTimeEnd));
        btnBack.setOnClickListener(v -> dialog.dismiss());

        btnUpdate.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String desc = etDescription.getText().toString().trim();
            String timeStart = etTimeStart.getText().toString().trim();
            String timeEnd = etTimeEnd.getText().toString().trim();
            String location = etLocation.getText().toString().trim();
            String reward = etReward.getText().toString().trim();
            String quantity = etQuantity.getText().toString().trim();

            if (name.isEmpty() || desc.isEmpty() || timeStart.isEmpty() || timeEnd.isEmpty() || categoryList.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            int point = 0;
            try {
                point = Integer.parseInt(reward);
            } catch (NumberFormatException e) {
                point = 0;
            }

            CategoryModel selectedCategory = (CategoryModel) tvCategory.getSelectedItem();
            String categoryTitle = selectedCategory.getTitle();

            String key = database.push().getKey();
            ActivityModel activity = new ActivityModel(name, categoryTitle, desc, timeStart, timeEnd, quantity, 0);
            activity.setKey(key);
            activity.setLocation(location);
            activity.setPoints(point);
            activity.setEventOrganizer("Tự động");

            database.child(key).setValue(activity)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Thêm hoạt động thành công", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Lỗi khi thêm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        dialog.show();
    }






    // Hiển thị DatePickerDialog để chọn ngày
    private void showDatePickerDialog(EditText dateEditText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Hiển thị DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(ActivitiesManageActivity.this,
                (view, year1, month1, dayOfMonth) -> {
                    String date = String.format("%02d/%02d/%04d", dayOfMonth, month1 + 1, year1); // Tháng bắt đầu từ 0
                    dateEditText.setText(date);
                }, year, month, day);
        datePickerDialog.show();
    }


    private void showEditActivityDialog(ActivityModel activity) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.design_manager_activity, null);
        AlertDialog dialog = new AlertDialog.Builder(this).setView(dialogView).create();

        EditText etName = dialogView.findViewById(R.id.et_ten_hoat_dong);
        EditText etDescription = dialogView.findViewById(R.id.et_mo_ta);
        Spinner tvCategory = dialogView.findViewById(R.id.spinner_category);
        EditText etTimeStart = dialogView.findViewById(R.id.et_time_start);
        EditText etTimeEnd = dialogView.findViewById(R.id.et_time_end);
        EditText etLocation = dialogView.findViewById(R.id.et_location);
        EditText etReward = dialogView.findViewById(R.id.et_reward);
        EditText etQuantity = dialogView.findViewById(R.id.et_participation_limit);
        Button btnUpdate = dialogView.findViewById(R.id.btn_update);
        ImageView btnBack = dialogView.findViewById(R.id.btn_back);

        etName.setText(activity.getName());
        etDescription.setText(activity.getDescription());
        etTimeStart.setText(activity.getStartTime());
        etTimeEnd.setText(activity.getEndTime());
        etLocation.setText(activity.getLocation());
        etQuantity.setText(activity.getQuantity());
        etReward.setText(String.valueOf(activity.getPoints()));  // ✅ Gán giá trị point
        btnUpdate.setText("Cập nhật");

        List<CategoryModel> categoryList = new ArrayList<>();
        ArrayAdapter<CategoryModel> categoryAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categoryList
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tvCategory.setAdapter(categoryAdapter);

        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference("categories");
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                int selectedIndex = 0, i = 0;
                for (DataSnapshot item : snapshot.getChildren()) {
                    CategoryModel category = item.getValue(CategoryModel.class);
                    if (category != null) {
                        category.setKey(item.getKey());
                        categoryList.add(category);
                        if (category.getTitle().equals(activity.getType())) {
                            selectedIndex = i;
                        }
                        i++;
                    }
                }
                categoryAdapter.notifyDataSetChanged();
                tvCategory.setSelection(selectedIndex);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ActivitiesManageActivity.this, "Lỗi khi tải danh mục", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(v -> dialog.dismiss());

        ImageView timePickerStart = dialogView.findViewById(R.id.time_picker_start);
        ImageView timePickerEnd = dialogView.findViewById(R.id.time_picker_end);
        timePickerStart.setOnClickListener(v -> showDatePickerDialog(etTimeStart));
        timePickerEnd.setOnClickListener(v -> showDatePickerDialog(etTimeEnd));

        btnUpdate.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String desc = etDescription.getText().toString().trim();
            String timeStart = etTimeStart.getText().toString().trim();
            String timeEnd = etTimeEnd.getText().toString().trim();
            String location = etLocation.getText().toString().trim();
            String quantity = etQuantity.getText().toString().trim();
            String reward = etReward.getText().toString().trim();

            if (name.isEmpty() || desc.isEmpty() || timeStart.isEmpty() || timeEnd.isEmpty() || categoryList.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            int point = 0;
            try {
                point = Integer.parseInt(reward);
            } catch (NumberFormatException e) {
                point = 0;
            }

            CategoryModel selectedCategory = (CategoryModel) tvCategory.getSelectedItem();
            String categoryTitle = selectedCategory.getTitle();

            activity.setName(name);
            activity.setDescription(desc);
            activity.setType(categoryTitle);
            activity.setStartTime(timeStart);
            activity.setEndTime(timeEnd);
            activity.setLocation(location);
            activity.setQuantity(quantity);
            activity.setPoints(point);  // ✅ cập nhật point mới

            String key = activity.getKey();
            if (key != null) {
                database.child(key).setValue(activity)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });

        dialog.show();
    }



}
