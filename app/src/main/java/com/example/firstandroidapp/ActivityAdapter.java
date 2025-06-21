package com.example.firstandroidapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        if (description.length() > 50) { // Giới hạn độ dài mô tả
            description = description.substring(0, 50) + "..."; // Thêm dấu ba chấm
        }
        holder.tvDesc.setText(description);

        holder.tvName.setText(activity.getName());
        holder.tvType.setText(activity.getType());
        holder.tvTime.setText(activity.getTime());
        holder.tvQuantity.setText(activity.getQuantity());

        // Kiểm tra trước khi split
        String quantity = activity.getQuantity();
        String shortQuantity = quantity;
        if (quantity.contains("/")) {
            String[] quantitySplit = quantity.split("/");
            shortQuantity = quantitySplit[0] + "/" + quantitySplit[1].substring(0, 2);  // Chỉ lấy 2 chữ số đầu của tổng
        }
        holder.tvQuantity.setText(shortQuantity);  // Hiển thị số lượng ngắn gọn

        // Xử lý hình ảnh thumbnail
        if (activity.getThumbnailResId() != 0) {
            holder.ivThumb.setImageResource(activity.getThumbnailResId());
        } else {
            holder.ivThumb.setImageResource(R.drawable.ic_photo); // Ảnh mặc định
        }

        // Xử lý sự kiện click vào nút Chi tiết
        holder.btnDetail.setOnClickListener(v -> {
            // Tạo Intent để chuyển sang DetailEventActivity
            Intent intent = new Intent(v.getContext(), DetailEventActivity.class);
            // Truyền các dữ liệu qua Intent
            intent.putExtra("name", activity.getName());
            intent.putExtra("description", activity.getDescription());
            intent.putExtra("time", activity.getTime());
            intent.putExtra("quantity", activity.getQuantity());
            intent.putExtra("location", activity.getLocation());
            intent.putExtra("eventOrganizer", activity.getEventOrganizer());  // Truyền thêm thông tin tổ chức sự kiện
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return activitiesList.size();
    }

    public static class ActivityViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvType, tvDesc, tvTime, tvQuantity;
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
            btnDetail = itemView.findViewById(R.id.btnDetail);
        }
    }
}
