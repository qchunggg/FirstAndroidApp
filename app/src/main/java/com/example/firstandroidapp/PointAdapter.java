package com.example.firstandroidapp;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

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

        holder.userName.setText(pointItem.getUserName());
        holder.description.setText(pointItem.getDescription());
        holder.date.setText(pointItem.getDate());
        holder.points.setText("Điểm: " + pointItem.getPoints());  // Gán điểm cho TextView tvPoints

        // Sự kiện cho nút "Từ chối"
        holder.btnDecline.setOnClickListener(v -> showRejectPopup(pointItem.getPointId(), position));

        // Sự kiện cho nút "Xác nhận"
        holder.btnConfirm.setOnClickListener(v -> showConfirmPopup(pointItem.getPointId(), position));
    }

    @Override
    public int getItemCount() {
        return pointList.size();
    }

    public static class PointViewHolder extends RecyclerView.ViewHolder {
        TextView userName, description, date, points;
        Button btnDecline, btnConfirm;

        public PointViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.tvUserName);
            description = itemView.findViewById(R.id.tvDescription);
            date = itemView.findViewById(R.id.tvDate);
            points = itemView.findViewById(R.id.tvPoints); // TextView hiển thị điểm
            btnDecline = itemView.findViewById(R.id.btnDecline);
            btnConfirm = itemView.findViewById(R.id.btnConfirm);
        }
    }

    // Phương thức để hiển thị Popup khi nhấn nút "Từ chối"
    private void showRejectPopup(final String pointId, final int position) {
        // Tạo lớp phủ mờ (dim overlay)
        View dimOverlay = new View(context);
        dimOverlay.setBackgroundColor(ContextCompat.getColor(context, R.color.dim_overlay));  // Màu mờ (xám)
        dimOverlay.setAlpha(0.6f); // Đặt độ mờ cho lớp phủ

        // Inflating layout của Popup
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.dialog_refuse_manager_activity, null);

        // Tạo PopupWindow với layout vừa lấy
        PopupWindow popupWindow = new PopupWindow(
                popupView,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        // Lấy layout gốc và thêm lớp phủ mờ vào layout chính
        LinearLayout layout = ((PointActivity) context).findViewById(android.R.id.content);  // Layout gốc
        layout.addView(dimOverlay); // Thêm lớp phủ mờ vào layout

        // Cài đặt cho PopupWindow
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(((PointActivity) context).findViewById(android.R.id.content), Gravity.CENTER, 0, 0);

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
            ((PointActivity) context).deletePointFromFirebase(pointId, position);  // Gọi phương thức xóa từ Firebase
        });

        // Đóng Popup khi nhấn nút đóng
        ivClose.setOnClickListener(v -> {
            popupWindow.dismiss();
            layout.removeView(dimOverlay); // Xóa lớp phủ mờ khi đóng popup
        });
    }

    // Phương thức để hiển thị Popup khi nhấn nút "Xác nhận"
    private void showConfirmPopup(final String pointId, final int position) {
        // Tạo Popup
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.dialog_drowse_manager_activity, null);

        PopupWindow popupWindow = new PopupWindow(
                popupView,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(((PointActivity) context).findViewById(android.R.id.content), Gravity.CENTER, 0, 0);

        Button btnAccept = popupView.findViewById(R.id.btnAccept);
        Button btnCancel = popupView.findViewById(R.id.btnReject);

        btnAccept.setOnClickListener(v -> {
            showNewPopup(pointId, position);  // Hiển thị popup mới
            popupWindow.dismiss();  // Đóng popup sau khi xác nhận
        });

        btnCancel.setOnClickListener(v -> popupWindow.dismiss());
    }

    private void showNewPopup(final String pointId, final int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View newPopupView = inflater.inflate(R.layout.dialog_identify_manager_activity, null);

        PopupWindow newPopupWindow = new PopupWindow(newPopupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        newPopupWindow.setFocusable(true);
        newPopupWindow.setOutsideTouchable(true);
        newPopupWindow.showAtLocation(((PointActivity) context).findViewById(android.R.id.content), Gravity.CENTER, 0, 0);

        Button btnConfirmPopup = newPopupView.findViewById(R.id.btnConfirm);
        Button btnClosePopup = newPopupView.findViewById(R.id.btnCancel);

        btnConfirmPopup.setOnClickListener(v -> {
            ((PointActivity) context).deletePointFromFirebase(pointId, position);
            newPopupWindow.dismiss();  // Đóng popup khi nhấn "Đóng"
        });

        btnClosePopup.setOnClickListener(v -> newPopupWindow.dismiss());
    }
}
