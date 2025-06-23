package com.example.firstandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PointActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PointAdapter adapter;
    private List<PointItem> itemList;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.point);

        recyclerView = findViewById(R.id.recyclerView);

        // Khởi tạo Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Khởi tạo danh sách
        itemList = new ArrayList<>();

        // Lấy dữ liệu từ Firebase
        mDatabase.child("points").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Duyệt qua các điểm trong Firebase và thêm vào itemList
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String pointId = snapshot.getKey();  // Lấy ID của điểm
                    String userName = snapshot.child("userName").getValue(String.class);
                    String description = snapshot.child("description").getValue(String.class);
                    String date = snapshot.child("date").getValue(String.class);

                    itemList.add(new PointItem(userName, description, date,pointId));  // Lưu thêm pointId
                }

                // Cài đặt Adapter và LayoutManager cho RecyclerView
                adapter = new PointAdapter(PointActivity.this, itemList);
                recyclerView.setLayoutManager(new LinearLayoutManager(PointActivity.this));
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Firebase", "loadPost:onCancelled", databaseError.toException());
            }
        });

        // Thêm sự kiện cho nút quay lại (ic_back)
        ImageView ivBack = findViewById(R.id.ic_back);
        ivBack.setOnClickListener(v -> {
            // Khi nhấn vào nút quay lại, chuyển đến AdminActivity
            Intent intent = new Intent(PointActivity.this, AdminActivity.class);
            startActivity(intent);
        });
    }

    // Hàm để hiển thị Popup khi nhấn Từ chối
    private void showDeclinePopup(final String pointKey, final int position) {
        // Tạo lớp phủ mờ (dim overlay)
        View dimOverlay = new View(this);
        dimOverlay.setBackgroundColor(ContextCompat.getColor(this, R.color.dim_overlay));  // Màu mờ (xám)
        dimOverlay.setAlpha(0.6f); // Đặt độ mờ cho lớp phủ

        // Inflating layout của Popup
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.dialog_refuse_manager_activity, null);

        // Tạo PopupWindow với layout vừa lấy
        PopupWindow popupWindow = new PopupWindow(
                popupView,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        // Lấy layout gốc và thêm lớp phủ mờ vào layout chính
        LinearLayout layout = findViewById(android.R.id.content);  // Layout gốc (toàn bộ màn hình)
        layout.addView(dimOverlay); // Thêm lớp phủ mờ vào layout

        // Cài đặt cho PopupWindow
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0);

        // Ánh xạ các thành phần trong popup layout
        EditText edtReason = popupView.findViewById(R.id.edtReason);
        Button btnConfirm = popupView.findViewById(R.id.btnConfirm);
        ImageView ivClose = popupView.findViewById(R.id.ivClose);

        // Xử lý khi nhấn "Xác nhận"
        btnConfirm.setOnClickListener(v -> {
            String reason = edtReason.getText().toString();
            // Xử lý lý do từ chối, ví dụ lưu vào Firebase hoặc xử lý khác
            popupWindow.dismiss(); // Đóng popup sau khi xác nhận
            layout.removeView(dimOverlay); // Xóa lớp phủ mờ khi popup bị đóng

            // Xóa dữ liệu trên Firebase sau khi xác nhận lý do từ chối
            deletePointFromFirebase(pointKey, position);  // Gọi phương thức xóa từ Firebase
        });

        // Đóng Popup khi nhấn nút đóng
        ivClose.setOnClickListener(v -> {
            popupWindow.dismiss();
            layout.removeView(dimOverlay); // Xóa lớp phủ mờ khi đóng popup
        });
    }

    // Xóa dữ liệu từ Firebase
    public void deletePointFromFirebase(String pointId, final int position) {
        if (position >= 0 && position < itemList.size()) {
            mDatabase.child("points").child(pointId).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        // Cập nhật RecyclerView sau khi xóa
                        itemList.remove(position);
                        adapter.notifyItemRemoved(position);

                        // Kiểm tra nếu tất cả các phần tử trong "points" đã bị xóa
                        if (itemList.isEmpty()) {
                            // Nếu "points" không còn dữ liệu, thêm một giá trị mặc định
                            mDatabase.child("points").setValue("default");  // Tạo giá trị mặc định cho points
                        }

                        Toast.makeText(PointActivity.this, "Xóa thành công", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(PointActivity.this, "Lỗi khi xóa: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Log.e("PointActivity", "Invalid position: " + position);
        }
    }



}
