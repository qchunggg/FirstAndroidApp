package com.example.firstandroidapp;

import android.content.Context;
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
    private Context context; // 🟡 Thêm biến context

    public ActivityAdapter(Context context, List<ActivityModel> activitiesList) {
        this.context = context; // 🟡 Gán context từ constructor
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
            description = description.substring(0, 50) + "...";
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

        // Hiển thị quantity
        int current = activity.getCurrentQuantity();
        int total = activity.getTotalQuantity();
        holder.tvQuantity.setText(current + "/" + total);

        // Xử lý trạng thái hoạt động dựa trên thời gian
        LocalDate today = LocalDate.now(); // Ngày hiện tại: 23/06/2025
        LocalDate tomorrow = today.plusDays(1); // Ngày tiếp theo: 24/06/2025
        LocalDate startDate = LocalDate.parse(activity.getStartTime(), formatter);
        LocalDate endDate = LocalDate.parse(activity.getEndTime(), formatter);

        if (startDate.isAfter(today)) {
            holder.tvStatus.setText("Sắp diễn ra");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_blue);
        } else {
            holder.tvStatus.setText("Đang diễn ra");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_green);
        }


        // Xử lý hình ảnh thumbnail
        if (activity.getThumbnailResId() != 0) {
            holder.ivThumb.setImageResource(activity.getThumbnailResId());
        } else {
            holder.ivThumb.setImageResource(R.drawable.ic_photo); // Ảnh mặc định
        }

        // Xử lý sự kiện click vào nút Chi tiết
        holder.btnDetail.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailEventActivity.class);
            intent.putExtra("activity", activity); // activity là ActivityModel
            if (context instanceof ActivitiesActivity) {
                ((ActivitiesActivity) context).startActivityForResult(intent, 1);
            } else {
                context.startActivity(intent); // fallback cho Fragment hoặc SearchActivity
            }
            Log.d("ActivityAdapter", "btnDetail clicked: " + activity.getName());
        });


        Log.d("ActivityAdapter", "Binding item at position " + position + ", name: " + activity.getName() + ", visible: " + (holder.itemView.getVisibility() == View.VISIBLE));
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

    public void updateData(List<ActivityModel> newList) {
        activitiesList.clear();
        activitiesList.addAll(newList);
        notifyDataSetChanged();
    }
}
