package com.example.firstandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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
import java.util.Collections;
import java.util.List;

public class RatingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RankingAdapter adapter;
    private List<RankingItem> rankingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rating);

        // Ánh xạ các view
        recyclerView = findViewById(R.id.recyclerViewRanking);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        rankingList = new ArrayList<>();
        adapter = new RankingAdapter(rankingList, "points1");  // Mặc định sẽ hiển thị points1
        recyclerView.setAdapter(adapter);

        // Khởi tạo các nút để chuyển đổi loại điểm
        Button btnPoints1 = findViewById(R.id.btnPoints1);
        Button btnPoints2 = findViewById(R.id.btnPoints2);
        Button btnPoints3 = findViewById(R.id.btnPoints3);

        // Gắn sự kiện click cho từng nút
        btnPoints1.setOnClickListener(v -> {
            Log.d("RankingActivity", "Points1 button clicked");
            loadRankingData("points1");  // Gọi load với points1
        });

        btnPoints2.setOnClickListener(v -> {
            Log.d("RankingActivity", "Points2 button clicked");
            loadRankingData("points2");  // Gọi load với points2
        });

        btnPoints3.setOnClickListener(v -> {
            Log.d("RankingActivity", "Points3 button clicked");
            loadRankingData("points3");  // Gọi load với points3
        });

        // Gắn sự kiện click cho ivBack
        ImageView ivBack = findViewById(R.id.ivBack); // Cập nhật ID cho phù hợp
        ivBack.setOnClickListener(v -> {
            // Quay lại MainActivity khi nhấn nút quay lại
            Intent intent = new Intent(RatingActivity.this, MainActivity.class);
            startActivity(intent);
            finish();  // Đóng Activity hiện tại (RatingActivity)
        });
    }

    private void loadRankingData(String pointsType) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("ranking");

        // Log điểm loại được lấy từ Firebase
        Log.d("RankingActivity", "Loading data for: " + pointsType);

        mDatabase.orderByChild(pointsType).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                rankingList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    RankingItem rankingItem = snapshot.getValue(RankingItem.class);
                    if (rankingItem != null) {
                        rankingList.add(rankingItem);
                    }
                }

                // Sort the list in descending order by the selected points type
                Collections.sort(rankingList, (item1, item2) -> Integer.compare(getPointsByType(item2, pointsType), getPointsByType(item1, pointsType)));

                // Log lại để kiểm tra xem danh sách đã có dữ liệu chưa
                Log.d("RankingActivity", "Updated list size: " + rankingList.size());

                // Cập nhật dữ liệu cho Adapter
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
                Log.e("RankingActivity", "Error: " + databaseError.getMessage());
            }
        });
    }

    private int getPointsByType(RankingItem item, String pointsType) {
        // Log điểm từng loại để kiểm tra
        int points = 0;
        switch (pointsType) {
            case "points1":
                points = item.getPoints1();
                Log.d("RankingActivity", "points1: " + points);
                break;
            case "points2":
                points = item.getPoints2();
                Log.d("RankingActivity", "points2: " + points);
                break;
            case "points3":
                points = item.getPoints3();
                Log.d("RankingActivity", "points3: " + points);
                break;
        }
        return points;
    }
}
