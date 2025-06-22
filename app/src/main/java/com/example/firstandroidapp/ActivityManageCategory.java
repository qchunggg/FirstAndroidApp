package com.example.firstandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ActivityManageCategory extends AppCompatActivity {

    private RecyclerView rvCategories;
    private CategoryAdapter adapter;
    private List<CategoryModel> categoryList;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_category);

        rvCategories = findViewById(R.id.rvCategories);
        rvCategories.setLayoutManager(new LinearLayoutManager(this));

        database = FirebaseDatabase.getInstance().getReference("categories");

        categoryList = new ArrayList<>();

        getCategoriesFromFirebase();

        adapter = new CategoryAdapter(categoryList);
        rvCategories.setAdapter(adapter);

        // Thêm sự kiện cho nút back
        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển về MainActivity
                Intent intent = new Intent(ActivityManageCategory.this, AdminActivity.class);
                startActivity(intent);
                finish(); // Đóng ActivityManageCategory để không quay lại khi nhấn back từ MainActivity
            }
        });

//        Thêm sự kiện cho nút Add
        ImageView ivAdd = findViewById(R.id.ivAdd);
        ivAdd.setOnClickListener(v -> showAddCategoryDialog());
    }

    private void getCategoriesFromFirebase() {
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                categoryList.clear();
                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                    String title = categorySnapshot.child("title").getValue(String.class);
                    Integer iconResId = categorySnapshot.child("iconResId").getValue(Integer.class);
                    if (iconResId == null) {
                        iconResId = 0;
                    }
                    Log.d("FirebaseData", "Title: " + title + ", iconResId: " + iconResId);
                    categoryList.add(new CategoryModel(title, iconResId));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseError", "Error fetching data", databaseError.toException());
            }
        });
    }

    private void showAddCategoryDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_category, null);

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        final android.app.AlertDialog dialog = builder.setView(dialogView).create();

        dialog.setOnShowListener(dialogInterface -> {
            // Cho phép làm mờ nền phía sau
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.dimAmount = 0.5f; // mức mờ: 0.0 -> 1.0
            dialog.getWindow().setAttributes(lp);

            // Đặt lại chiều rộng dialog (nếu cần)
            dialog.getWindow().setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.85),
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        });

        dialog.show();

        EditText etCategoryName = dialogView.findViewById(R.id.etCategoryName);
        GridLayout glIcons = dialogView.findViewById(R.id.glIcons);
        Button btnAdd = dialogView.findViewById(R.id.btnAddCategory);

        final int[] selectedIconResId = {R.drawable.ic_default_icon}; // Icon mặc định

        // 2. Gán sự kiện chọn icon
        for (int i = 0; i < glIcons.getChildCount(); i++) {
            View iconView = glIcons.getChildAt(i);
            if (iconView instanceof ImageButton) {
                int iconResId = ((ImageButton) iconView).getDrawable().getConstantState().hashCode(); // fallback hash
                iconView.setOnClickListener(v -> {
                    selectedIconResId[0] = (Integer) iconView.getTag();

                    // Reset background của tất cả icon
                    for (int j = 0; j < glIcons.getChildCount(); j++) {
                        View child = glIcons.getChildAt(j);
                        if (child instanceof ImageButton) {
                            child.setBackground(null);
                            child.setBackgroundResource(android.R.color.transparent);
                        }
                    }

                    // Tô viền cho icon được chọn
                    iconView.setBackgroundResource(R.drawable.bg_icon_selected);
                });
                // Thiết lập tag icon ID thủ công nếu chưa có
                int id = iconView.getId();
                if (id == R.id.icon1) iconView.setTag(R.drawable.ic_study);
                else if (id == R.id.icon2) iconView.setTag(R.drawable.ic_guitar);
                else if (id == R.id.icon3) iconView.setTag(R.drawable.ic_sport);
                else if (id == R.id.icon4) iconView.setTag(R.drawable.ic_setting);
                else if (id == R.id.icon5) iconView.setTag(R.drawable.ic_flag);
                else if (id == R.id.icon6) iconView.setTag(R.drawable.ic_present);
                else if (id == R.id.icon7) iconView.setTag(R.drawable.ic_volunteer_activity);
                else if (id == R.id.icon8) iconView.setTag(R.drawable.ic_music);

            }
        }

        // 3. Sự kiện nút "Thêm mới"
        btnAdd.setOnClickListener(view -> {
            String categoryName = etCategoryName.getText().toString().trim();

            if (categoryName.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên danh mục", Toast.LENGTH_SHORT).show();
                return;
            }

            int iconResId = selectedIconResId[0];

            CategoryModel newCategory = new CategoryModel(categoryName, iconResId);
            database.push().setValue(newCategory);

            Toast.makeText(this, "Đã thêm danh mục", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
    }
}