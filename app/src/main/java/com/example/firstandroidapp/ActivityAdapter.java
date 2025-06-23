package com.example.firstandroidapp;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder> {

    private List<ActivityModel> activitiesList;

    public ActivityAdapter(List<ActivityModel> activitiesList) {
        this.activitiesList = activitiesList;
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_activity, parent, false);
        return new ActivityViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        ActivityModel activity = activitiesList.get(position);

        // Hiển thị mô tả rút gọn
        String description = activity.getDescription();
        if (description.length() > 50) {
            description = description.substring(0, 50) + "..."; // Thêm dấu ba chấm
        }
        holder.tvDesc.setText(description);

        // Hiển thị tên và loại hoạt động
        holder.tvName.setText(activity.getName());
        holder.tvType.setText(activity.getType());

        // Cập nhật thời gian chỉ là startTime
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            LocalDate startDate = LocalDate.parse(activity.getStartTime(), formatter);
            holder.tvTime.setText(startDate.format(formatter));  // Chỉ hiển thị startTime
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Xử lý số lượng (quantity)
        String quantity = activity.getQuantity();
        String[] quantityParts = quantity.split("/");

        // Kiểm tra nếu quantity không chứa dấu "/" (chỉ có current value)
        if (quantityParts.length < 2) {
            // Nếu quantity không có "/", giả sử total = current
            holder.tvQuantity.setText(quantity + "/" + quantity);  // Hiển thị 100/100 nếu quantity = "100"
        } else {
            int current = Integer.parseInt(quantityParts[0]);
            int total = Integer.parseInt(quantityParts[1]);
            holder.tvQuantity.setText(current + "/" + total);  // Hiển thị quantity dưới dạng current/total
        }

        // Xử lý trạng thái hoạt động dựa trên thời gian
        LocalDate today = LocalDate.now();
        LocalDate startDate = LocalDate.parse(activity.getStartTime(), formatter);
        LocalDate endDate = LocalDate.parse(activity.getEndTime(), formatter);

        if (startDate.isEqual(today) || (startDate.isBefore(today) && today.isBefore(endDate))) {
            holder.tvStatus.setText("Đang diễn ra");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_green);
        } else if (startDate.isAfter(today)) {
            holder.tvStatus.setText("Sắp diễn ra");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_blue);
        } else {
            holder.itemView.setVisibility(View.GONE);  // Ẩn item nếu đã kết thúc
        }

        // Kiểm tra số lượng và ẩn item nếu hết
        if (Integer.parseInt(quantityParts[0]) == 0) {
            holder.itemView.setVisibility(View.GONE);  // Ẩn item khi quantity = 0
        }

        // Xử lý hình ảnh thumbnail
        if (activity.getThumbnailResId() != 0) {
            holder.ivThumb.setImageResource(activity.getThumbnailResId());
        } else {
            holder.ivThumb.setImageResource(R.drawable.ic_photo); // Ảnh mặc định
        }

        // Xử lý sự kiện click vào nút Chi tiết
        holder.btnDetail.setOnClickListener(v -> {
            // Tạo Intent để chuyển sang DetailActivity (hoặc màn hình chi tiết hoạt động)
            Intent intent = new Intent(v.getContext(), DetailEventActivity.class);
            intent.putExtra("activity", activity);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return activitiesList.size();
    }

    public static class ActivityViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvType, tvDesc, tvTime, tvQuantity, tvStatus;
        ImageView ivThumb;
        Button btnDetail;

        public ActivityViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvType = itemView.findViewById(R.id.tvType);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            ivThumb = itemView.findViewById(R.id.ivThumb);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnDetail = itemView.findViewById(R.id.btnDetail);
        }
    }

    // Thêm phương thức để cập nhật quantity trong Firebase
    private void updateQuantityInFirebase(String activityKey, int newQuantity, int total) {
        DatabaseReference activityRef = FirebaseDatabase.getInstance().getReference("activities").child(activityKey);
        activityRef.child("quantity").setValue(newQuantity + "/" + total) // Giữ nguyên total
                .addOnSuccessListener(aVoid -> Log.d("ActivityAdapter", "Quantity updated to: " + newQuantity + "/" + total))
                .addOnFailureListener(e -> Log.e("ActivityAdapter", "Failed to update quantity", e));
    }
}
