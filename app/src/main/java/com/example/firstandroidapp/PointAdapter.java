package com.example.firstandroidapp;

import android.content.Context;
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

        // Sự kiện cho nút "Từ chối"
        holder.btnDecline.setOnClickListener(v -> {
            // Gọi hàm showDeclinePopup khi nhấn nút "Từ chối"
            showRejectPopup(pointItem.getPointId(), position);
        });

        // Sự kiện cho nút "Xác nhận"
        holder.btnConfirm.setOnClickListener(v -> {
            // Gọi hàm showConfirmPopup khi nhấn nút "Xác nhận"
            showConfirmPopup(pointItem.getPointId(), position);
        });
    }

    @Override
    public int getItemCount() {
        return pointList.size();
    }

    // ViewHolder để giữ các thành phần giao diện của mỗi item
    public static class PointViewHolder extends RecyclerView.ViewHolder {
        TextView userName, description, date;
        Button btnDecline, btnConfirm; // Nút "Từ chối" và "Xác nhận"

        public PointViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.tvUserName);
            description = itemView.findViewById(R.id.tvDescription);
            date = itemView.findViewById(R.id.tvDate);
            btnDecline = itemView.findViewById(R.id.btnDecline); // Nút "Từ chối"
            btnConfirm = itemView.findViewById(R.id.btnConfirm); // Nút "Xác nhận"
        }
    }

    // Phương thức để hiển thị Popup khi nhấn nút "Từ chối"
    private void showRejectPopup(final String pointId, final int position) {
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
            // Xử lý lý do từ chối, ví dụ lưu vào Firebase hoặc xử lý khác
            Toast.makeText(context, "Lý do từ chối: " + reason, Toast.LENGTH_SHORT).show();

            // Xóa dữ liệu từ Firebase và cập nhật lại RecyclerView
            ((PointActivity) context).deletePointFromFirebase(pointId, position);
            popupWindow.dismiss(); // Đóng popup sau khi xác nhận
        });

        // Đóng Popup khi nhấn nút đóng
        ivClose.setOnClickListener(v -> popupWindow.dismiss());
    }

    // Phương thức để hiển thị Popup khi nhấn nút "Xác nhận"
    private void showConfirmPopup(final String pointId, final int position) {
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
            showNewPopup(pointId, position);  // Hiển thị popup mới
            popupWindow.dismiss(); // Đóng popup sau khi xác nhận
        });

        // Đóng Popup khi nhấn nút "Hủy"
        btnCancel.setOnClickListener(v -> popupWindow.dismiss());
    }

    // Phương thức hiển thị popup mới khi nhấn "Chấp nhận"
    private void showNewPopup(final String pointId, final int position) {
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
            // Xóa object từ Firebase và cập nhật lại RecyclerView
            ((PointActivity) context).deletePointFromFirebase(pointId, position);
            newPopupWindow.dismiss();  // Đóng popup mới khi nhấn "Đóng"
        });

        btnClosePopup.setOnClickListener(v -> newPopupWindow.dismiss());

    }
}
