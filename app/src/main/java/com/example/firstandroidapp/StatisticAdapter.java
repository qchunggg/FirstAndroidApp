package com.example.firstandroidapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StatisticAdapter extends RecyclerView.Adapter<StatisticAdapter.ViewHolder> {

    private List<ActivityModel> activityList;

    public StatisticAdapter(List<ActivityModel> activityList) {
        this.activityList = activityList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvActivityName, tvActivityDate, tvActivityPoint;

        public ViewHolder(View view) {
            super(view);
            tvActivityName = view.findViewById(R.id.tvActivityName);
            tvActivityDate = view.findViewById(R.id.tvActivityDate);
            tvActivityPoint = view.findViewById(R.id.tvActivityPoint); // ⚠️ sửa lại đúng ID
        }
    }

    @NonNull
    @Override
    public StatisticAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_statistic, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StatisticAdapter.ViewHolder holder, int position) {
        ActivityModel activity = activityList.get(position);
        holder.tvActivityName.setText(activity.getName());
        holder.tvActivityDate.setText(activity.getStartTime());
        holder.tvActivityPoint.setText(activity.getCurrentQuantity() + " điểm");
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }
}