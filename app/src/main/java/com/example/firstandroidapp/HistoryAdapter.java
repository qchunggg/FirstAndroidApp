package com.example.firstandroidapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private List<HistoryModel> activityList;

    public HistoryAdapter(List<HistoryModel> activityList) {
        this.activityList = activityList;
    }

    public interface OnSubmitProofClickListener {
        void onSubmitProofClicked(HistoryModel activity, int position);
    }

    private OnSubmitProofClickListener listener;

    public void setOnSubmitProofClickListener(OnSubmitProofClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);  // layout của từng item
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryModel activity = activityList.get(position);

        holder.tvActivityName.setText(activity.getName());
        holder.tvActivityStatus.setText(activity.getStatus());
        holder.tvActivityType.setText(activity.getType());
        holder.tvActivityDesc.setText(activity.getDescription());
        holder.tvActivityDate.setText(activity.getDate());
        holder.tvActivityPoints.setText(activity.getPoints() + " Điểm");
        holder.tvProofStatus.setText(activity.getProofStatus());

        // 👇 Thêm điều kiện hiển thị nút "Nộp minh chứng"
        if ("Đã đăng ký".equals(activity.getStatus()) &&
                "Chưa nộp minh chứng".equals(activity.getProofStatus())) {
            holder.btnSubmitProof.setVisibility(View.VISIBLE);
        } else {
            holder.btnSubmitProof.setVisibility(View.GONE);
        }

        // 👇 Xử lý khi bấm nút (sẽ dùng ở bước sau)
        holder.btnSubmitProof.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSubmitProofClicked(activity, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }

    public void updateData(List<HistoryModel> newList) {
        activityList = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvActivityName, tvActivityStatus, tvActivityType, tvActivityDesc, tvActivityDate, tvActivityPoints, tvProofStatus, btnSubmitProof;

        public ViewHolder(View itemView) {
            super(itemView);
            tvActivityName = itemView.findViewById(R.id.tvActivityName);
            tvActivityStatus = itemView.findViewById(R.id.tvActivityStatus);
            tvActivityType = itemView.findViewById(R.id.tvActivityType);
            tvActivityDesc = itemView.findViewById(R.id.tvActivityDesc);
            tvActivityDate = itemView.findViewById(R.id.tvActivityDate);
            tvActivityPoints = itemView.findViewById(R.id.tvActivityPoints);
            tvProofStatus = itemView.findViewById(R.id.tvProofStatus);
            btnSubmitProof = itemView.findViewById(R.id.btnSubmitProof);
        }
    }
}

