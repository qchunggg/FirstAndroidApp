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
    private String currentPointsType = "points1";  // Mặc định là points1

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rating);

        // Ánh xạ các view
        recyclerView = findViewById(R.id.recyclerViewRanking);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        rankingList = new ArrayList<>();

        // Khởi tạo các nút để chuyển đổi loại điểm
        Button btnPoints1 = findViewById(R.id.btnPoints1);
        Button btnPoints2 = findViewById(R.id.btnPoints2);
        Button btnPoints3 = findViewById(R.id.btnPoints3);

        // Gắn sự kiện click cho từng nút
        btnPoints1.setOnClickListener(v -> {
            currentPointsType = "points1";  // Cập nhật pointsType khi nhấn btnPoints1
            Log.d("RankingActivity", "Đã chọn: Học kỳ");
            updateAdapter(currentPointsType);  // Cập nhật adapter với points1
        });

        btnPoints2.setOnClickListener(v -> {
            currentPointsType = "points2";  // Cập nhật pointsType khi nhấn btnPoints2
            Log.d("RankingActivity", "Đã chọn: Năm học");
            updateAdapter(currentPointsType);  // Cập nhật adapter với points2
        });

        btnPoints3.setOnClickListener(v -> {
            currentPointsType = "points3";  // Cập nhật pointsType khi nhấn btnPoints3
            Log.d("RankingActivity", "Đã chọn: Toàn thời gian");
            updateAdapter(currentPointsType);  // Cập nhật adapter với points3
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

    // Phương thức cập nhật lại adapter với loại điểm mới
    private void updateAdapter(String pointsType) {
        // Tạo lại adapter mỗi khi pointsType thay đổi
        adapter = new RankingAdapter(rankingList, pointsType);  // Tạo lại adapter với pointsType mới
        recyclerView.setAdapter(adapter);  // Cập nhật RecyclerView với adapter mới

        // Tải lại dữ liệu từ Firebase với loại điểm mới
        loadRankingData(pointsType);
    }

    private void loadRankingData(String pointsType) {
        Log.d("RankingActivity", "Đang tải dữ liệu cho loại điểm: " + pointsType);
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("ranking");

        mDatabase.orderByChild(pointsType).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<RankingItem> newRankingList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    RankingItem rankingItem = snapshot.getValue(RankingItem.class);
                    if (rankingItem != null) {
                        newRankingList.add(rankingItem);  // Thêm vào danh sách mới
                    }
                }

                // Sắp xếp danh sách mới theo thứ tự giảm dần theo loại điểm đã chọn
                Collections.sort(newRankingList, (item1, item2) -> Integer.compare(getPointsByType(item2, pointsType), getPointsByType(item1, pointsType)));

                // Kiểm tra nếu danh sách mới có thay đổi
                if (!rankingList.equals(newRankingList)) {
                    rankingList.clear();
                    rankingList.addAll(newRankingList);

                    // Log để xác nhận sau khi sắp xếp
                    Log.d("RankingActivity", "Danh sách đã sắp xếp: " + rankingList.size());

                    // Cập nhật RecyclerView bằng adapter
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi
                Log.e("RankingActivity", "Lỗi: " + databaseError.getMessage());
            }
        });
    }

    private int getPointsByType(RankingItem item, String pointsType) {
        // Lấy điểm tương ứng với loại điểm (points1, points2, points3)
        int points = 0;
        switch (pointsType) {
            case "points1":
                points = item.getPoints1();
                break;
            case "points2":
                points = item.getPoints2();
                break;
            case "points3":
                points = item.getPoints3();
                break;
            default:
                points = 0;
                break;
        }
        return points;
    }
}
