package com.example.firstandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

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
}