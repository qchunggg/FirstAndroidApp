package com.example.firstandroidapp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RatingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RankingAdapter adapter;
    private List<RankingItem> rankingList;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rating);

        // Ánh xạ RecyclerView
        recyclerView = findViewById(R.id.recyclerViewRanking);

        // Cài đặt RecyclerView
        recyclerView.setHasFixedSize(true);  // Cải thiện hiệu suất nếu kích thước item không thay đổi
        recyclerView.setLayoutManager(new LinearLayoutManager(this));  // Sử dụng LinearLayoutManager

        // Khởi tạo danh sách dữ liệu
        rankingList = new ArrayList<>();

        // Khởi tạo Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference("ranking");

        // Lắng nghe dữ liệu từ Firebase
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                rankingList.clear(); // Xóa dữ liệu cũ

                // Duyệt qua tất cả các mục trong Firebase
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    RankingItem item = snapshot.getValue(RankingItem.class);
                    rankingList.add(item);  // Thêm dữ liệu vào danh sách
                }

                // Gán adapter sau khi đã có dữ liệu
                if (adapter == null) {
                    adapter = new RankingAdapter(rankingList);  // Tạo mới adapter khi chưa có
                    recyclerView.setAdapter(adapter);  // Gán adapter cho RecyclerView
                } else {
                    // Cập nhật dữ liệu cho adapter nếu đã tồn tại
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(RatingActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
