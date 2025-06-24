package com.example.firstandroidapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.PopupWindow;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Collections;
import java.util.List;

public class PointAdapter extends RecyclerView.Adapter<PointAdapter.PointViewHolder> {

    private List<PointItem> pointList;
    private Context context;

    // Constructor nhận vào danh sách các điểm và context
    public PointAdapter(Context context, List<PointItem> pointList) {
        this.context = context;
        this.pointList = pointList;
    }

    @NonNull
    @Override
    public PointViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflating layout item_point.xml
        View view = LayoutInflater.from(context).inflate(R.layout.item_point, parent, false);
        return new PointViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PointViewHolder holder, int position) {
        PointItem pointItem = pointList.get(position);

        // Gán giá trị vào các thành phần giao diện
        holder.userName.setText(pointItem.getUserName());
        holder.description.setText(pointItem.getDescription());
        holder.date.setText(pointItem.getDate());
        holder.tvPoints.setText("Điểm: " + pointItem.getPoints());


        // Sự kiện cho nút "Từ chối"
        holder.btnDecline.setOnClickListener(v -> {
            // Gọi hàm showDeclinePopup khi nhấn nút "Từ chối"
            showRejectPopup(pointItem.getUserId(), pointItem.getPointId(), position);
        });

        // Sự kiện cho nút "Xác nhận"
        holder.btnConfirm.setOnClickListener(v -> {
            // Gọi hàm showConfirmPopup khi nhấn nút "Xác nhận"
            showConfirmPopup(pointItem.getUserId(), pointItem.getPointId(), position);
        });
    }

    @Override
    public int getItemCount() {
        return pointList.size();
    }

    // ViewHolder để giữ các thành phần giao diện của mỗi item
    public static class PointViewHolder extends RecyclerView.ViewHolder {
        TextView userName, description, date,tvPoints;
        Button btnDecline, btnConfirm; // Nút "Từ chối" và "Xác nhận"

        public PointViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.tvUserName);
            description = itemView.findViewById(R.id.tvDescription);
            date = itemView.findViewById(R.id.tvDate);
            tvPoints = itemView.findViewById(R.id.tvPoints);
            btnDecline = itemView.findViewById(R.id.btnDecline); // Nút "Từ chối"
            btnConfirm = itemView.findViewById(R.id.btnConfirm); // Nút "Xác nhận"
        }
    }

    // Phương thức để hiển thị Popup khi nhấn nút "Từ chối"
    private void showRejectPopup(final String userId, final String pointId, final int position) {
        // Inflating layout của Popup
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.dialog_refuse_manager_activity, null);

        // Tạo PopupWindow với layout vừa lấy
        PopupWindow popupWindow = new PopupWindow(
                popupView,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        // Cài đặt cho PopupWindow
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(((AppCompatActivity) context).findViewById(android.R.id.content), android.view.Gravity.CENTER, 0, 0);

        // Ánh xạ các thành phần trong popup layout
        EditText edtReason = popupView.findViewById(R.id.edtReason);
        Button btnConfirm = popupView.findViewById(R.id.btnConfirm);
        ImageView ivClose = popupView.findViewById(R.id.ivClose);

        // Xử lý khi nhấn "Xác nhận"
        btnConfirm.setOnClickListener(v -> {
            String reason = edtReason.getText().toString();
            if (!reason.isEmpty()) {
                // Cập nhật trạng thái trên Firebase
                updateStatusInFirebase(userId, pointId, "Bị từ chối");

                // Xóa đối tượng khỏi danh sách và cập nhật RecyclerView
                pointList.remove(position); // Xóa đối tượng khỏi danh sách
                notifyItemRemoved(position); // Cập nhật RecyclerView

                // Hiển thị thông báo
                Toast.makeText(context, "Lý do từ chối: " + reason, Toast.LENGTH_SHORT).show();
            } else {
                // Hiển thị thông báo nếu không nhập lý do
                Toast.makeText(context, "Vui lòng nhập lý do", Toast.LENGTH_SHORT).show();
            }

            popupWindow.dismiss(); // Đóng popup sau khi xác nhận
        });

        // Đóng Popup khi nhấn nút đóng
        ivClose.setOnClickListener(v -> popupWindow.dismiss());
    }

    // Phương thức để hiển thị Popup khi nhấn nút "Xác nhận"
    private void showConfirmPopup(final String userId, final String pointId, final int position) {
        // Inflating layout của Popup
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.dialog_drowse_manager_activity, null);

        // Tạo PopupWindow với layout vừa lấy
        PopupWindow popupWindow = new PopupWindow(
                popupView,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        // Cài đặt cho PopupWindow
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(((AppCompatActivity) context).findViewById(android.R.id.content), android.view.Gravity.CENTER, 0, 0);

        // Ánh xạ các thành phần trong popup layout
        Button btnAccept = popupView.findViewById(R.id.btnAccept);
        Button btnCancel = popupView.findViewById(R.id.btnReject);

        // Xử lý khi nhấn "Chấp nhận"
        btnAccept.setOnClickListener(v -> {
            // Xử lý khi nhấn "Chấp nhận"

            // Thực hiện hành động xác nhận (Ví dụ: Cấp điểm, etc)
            showNewPopup(userId, pointId, position);  // Hiển thị popup mới
            popupWindow.dismiss(); // Đóng popup sau khi xác nhận
        });

        // Đóng Popup khi nhấn nút "Hủy"
        btnCancel.setOnClickListener(v -> popupWindow.dismiss());
    }

    // Phương thức hiển thị popup mới khi nhấn "Chấp nhận"
    private void showNewPopup(final String userId, final String pointId, final int position) {
        // Inflating layout của Popup mới
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View newPopupView = inflater.inflate(R.layout.dialog_identify_manager_activity, null);

        // Tạo PopupWindow cho layout mới
        PopupWindow newPopupWindow = new PopupWindow(
                newPopupView,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        // Cài đặt cho PopupWindow
        newPopupWindow.setFocusable(true);
        newPopupWindow.setOutsideTouchable(true);
        newPopupWindow.showAtLocation(((AppCompatActivity) context).findViewById(android.R.id.content), android.view.Gravity.CENTER, 0, 0);

        // Ánh xạ các thành phần trong popup mới
        Button btnConfirmPopup = newPopupView.findViewById(R.id.btnConfirm);
        Button btnClosePopup = newPopupView.findViewById(R.id.btnCancel);

        // Xử lý khi nhấn "Đóng"
        btnConfirmPopup.setOnClickListener(v -> {
            // Cập nhật trạng thái trên Firebase
            updateStatusInFirebase(userId, pointId, "Đã xác nhận");

            // Xóa object từ Firebase và cập nhật lại RecyclerView
            ((PointActivity) context).deletePointFromFirebase(pointId, position);

            newPopupWindow.dismiss();  // Đóng popup mới khi nhấn "Đóng"
        });

        btnClosePopup.setOnClickListener(v -> newPopupWindow.dismiss());
    }


    // Phương thức cập nhật trạng thái trong Firebase
    private void updateStatusInFirebase(String userId, String pointId, String newStatus) {
        // Lấy tham chiếu đến đối tượng trong Firebase, sử dụng cả userId và pointId
        DatabaseReference pointRef = FirebaseDatabase.getInstance().getReference("history")
                .child(userId) // Sử dụng userId để xác định đúng đối tượng
                .child(pointId); // Sau đó dùng pointId để cập nhật đúng mục trong userId

        // Cập nhật chỉ trường 'status' trong đối tượng mà không tạo thêm object mới
        pointRef.updateChildren(Collections.singletonMap("status", newStatus))
                .addOnSuccessListener(aVoid -> {
                    // Thực hiện hành động khi cập nhật thành công
                    Log.d("Firebase", "Trạng thái đã được cập nhật thành công cho pointId: " + pointId);
                })
                .addOnFailureListener(e -> {
                    // Xử lý khi có lỗi
                    Log.e("Firebase", "Lỗi khi cập nhật trạng thái cho pointId: " + pointId, e);
                });
    }
}
