package com.example.firstandroidapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ActivityManageAdapter extends RecyclerView.Adapter<ActivityManageAdapter.ActivityViewHolder> {

    private List<ActivityModel> activityList;
    private OnActivityActionListener actionListener;  // Khai báo actionListener

    // Constructor nhận vào actionListener
    public ActivityManageAdapter(List<ActivityModel> activityList, OnActivityActionListener actionListener) {
        this.activityList = activityList;
        this.actionListener = actionListener;  // Khởi tạo actionListener
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_manager_activity, parent, false);
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        ActivityModel activity = activityList.get(position);

        holder.tvTitle.setText(activity.getName());
        // Hiển thị thời gian bắt đầu và kết thúc
        String timeRange = activity.getStartTime() + " - " + activity.getEndTime();
        holder.tvDate.setText(timeRange);

        holder.ivDelete.setOnClickListener(v -> {
            // Gọi phương thức xóa khi nhấn nút xóa
            if (actionListener != null) {
                actionListener.onDelete(activity);  // Truyền activity vào phương thức onDelete
            }
        });

        holder.ivEdit.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onEdit(activity);  // Truyền activity vào phương thức onEdit
            }
        });
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }

    public static class ActivityViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvDate;
        ImageView ivEdit, ivDelete;

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDate = itemView.findViewById(R.id.tvDate);  // Hiển thị thời gian
            ivEdit = itemView.findViewById(R.id.ivEdit);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }

    // Interface để lắng nghe sự kiện xóa và sửa
    public interface OnActivityActionListener {
        void onDelete(ActivityModel activity);
        void onEdit(ActivityModel activity);
    }
}
