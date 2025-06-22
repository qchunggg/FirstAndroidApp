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

    public ActivityManageAdapter(List<ActivityModel> activityList) {
        this.activityList = activityList;
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
        holder.tvDate.setText(activity.getTime());
        // TODO: xử lý thêm icon Sửa/Xoá nếu muốn
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
            tvTitle = itemView.findViewById(R.id.tvTitle); // cần chỉnh lại id trong XML
            tvDate = itemView.findViewById(R.id.tvDate);   // cần chỉnh lại id trong XML
            ivEdit = itemView.findViewById(R.id.ivEdit);   // nếu có chức năng sửa
            ivDelete = itemView.findViewById(R.id.ivDelete); // nếu có chức năng xoá
        }
    }
}
