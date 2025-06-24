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

        // Lấy dữ liệu từ Firebase "history" và duyệt qua tất cả các userId
        mDatabase.child("history").addListenerForSingleValueEvent(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Duyệt qua các userId trong bảng "history"
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String userId = snapshot.getKey(); // userId chính là key của mỗi mục trong "history"

                    // Lấy thông tin sự kiện từ "history"
                    for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                        String pointId = eventSnapshot.getKey();  // Lấy ID của lịch sử
                        String description = eventSnapshot.child("description").getValue(String.class);
                        String date = eventSnapshot.child("date").getValue(String.class);
                        Integer points = eventSnapshot.child("points").exists() ? eventSnapshot.child("points").getValue(Integer.class) : 0;
                        String status = eventSnapshot.child("status").getValue(String.class);
                        String proofStatus = eventSnapshot.child("proofStatus").getValue(String.class);
                        String type = eventSnapshot.child("type").getValue(String.class);
                        String name = eventSnapshot.child("name").getValue(String.class);

                        final Integer tempPoints = (points == null) ? 0 : points;

                        // Truy cập vào bảng "users" để lấy userName qua userId
                        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot userSnapshot) {
                                // Lấy tên người dùng từ bảng "users"
                                String fullName = userSnapshot.child("fullName").getValue(String.class);
                                if (fullName == null) {
                                    fullName = "Unknown";  // Nếu không có tên, gán giá trị mặc định là "Unknown"
                                }

                                // Lọc theo status "Chưa xác nhận"
                                if ("Chưa xác nhận".equals(status)) {
                                    // Thêm sự kiện vào danh sách
                                    itemList.add(new PointItem(userId, fullName, description, date, pointId, tempPoints, proofStatus, status, type, name));
                                }

                                // Cập nhật RecyclerView sau khi đã lấy tất cả dữ liệu
                                if (itemList.size() == dataSnapshot.getChildrenCount()) {
                                    adapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e("Firebase", "Error fetching user data", databaseError.toException());
                            }
                        });
                    }
                }

                // Cài đặt Adapter và LayoutManager cho RecyclerView chỉ khi dữ liệu đã được lấy đầy đủ
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
    private void showDeclinePopup(final String userId, final String pointId, final int position) {
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
            deletePointFromFirebase(pointId, position);  // Gọi phương thức xóa từ Firebase
        });

        // Đóng Popup khi nhấn nút đóng
        ivClose.setOnClickListener(v -> {
            popupWindow.dismiss();
            layout.removeView(dimOverlay); // Xóa lớp phủ mờ khi đóng popup
        });
    }

    // Xóa dữ liệu từ Firebase
    public void deletePointFromFirebase(String pointId, final int position) {
        Log.d("DeletePoint", "Attempting to delete pointId: " + pointId);
        if (position >= 0 && position < itemList.size()) {
            // Xóa mục từ Firebase
            mDatabase.child("history").child(pointId).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        // Nếu xóa thành công, cập nhật RecyclerView
                        itemList.remove(position);  // Xóa item khỏi danh sách
                        adapter.notifyItemRemoved(position);

                        // Kiểm tra lại dữ liệu sau khi xóa
                        mDatabase.child("history").child(pointId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.exists()) {
                                    Log.d("DeletePoint", "Successfully deleted from Firebase.");
                                } else {
                                    Log.d("DeletePoint", "Data still exists after deletion attempt.");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e("DeletePoint", "Error checking data: " + databaseError.getMessage());
                            }
                        });

                        // Kiểm tra nếu tất cả các phần tử trong "history" đã bị xóa
                        if (itemList.isEmpty()) {
                            // Nếu "history" không còn dữ liệu, thêm một giá trị mặc định
                            mDatabase.child("history").setValue("default");
                        }


                    })
                    .addOnFailureListener(e -> {
                        // Nếu xảy ra lỗi khi xóa, thông báo lỗi
                        Log.e("DeletePoint", "Error deleting point: " + e.getMessage());
                        Toast.makeText(PointActivity.this, "Lỗi khi xóa: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Log.e("PointActivity", "Invalid position: " + position);
        }
    }

}
