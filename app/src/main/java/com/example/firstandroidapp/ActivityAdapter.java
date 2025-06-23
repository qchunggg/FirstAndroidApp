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
        if (description.length() > 50) { // Giới hạn độ dài mô tả
            description = description.substring(0, 50) + "..."; // Thêm dấu ba chấm
        }
        holder.tvDesc.setText(description);

        holder.tvName.setText(activity.getName());
        holder.tvType.setText(activity.getType());

        // Hiển thị thời gian bắt đầu và kết thúc
        String time = activity.getStartTime() + " - " + activity.getEndTime();
        holder.tvTime.setText(time);

        holder.tvQuantity.setText(activity.getQuantity());

        // Xử lý trạng thái dựa theo ngày
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate today = LocalDate.now();

        try {
            LocalDate activityDate = LocalDate.parse(activity.getTime(), formatter);

            if (activityDate.isEqual(today)) {
                holder.tvStatus.setText("Đang diễn ra");
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_green);
            } else if (activityDate.isAfter(today)) {
                holder.tvStatus.setText("Sắp diễn ra");
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_blue); // bạn cần tạo file này
            } else {
                // Ẩn item bằng cách đặt chiều cao = 0, hoặc xử lý ở bước lọc (nên làm ở bước 3)
                holder.itemView.setVisibility(View.GONE);
                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            }
        } catch (Exception e) {
            e.printStackTrace(); // Tránh crash nếu định dạng sai
        }

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
            intent.putExtra("startTime", activity.getStartTime());  // Truyền thời gian bắt đầu
            intent.putExtra("endTime", activity.getEndTime());      // Truyền thời gian kết thúc
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
}
