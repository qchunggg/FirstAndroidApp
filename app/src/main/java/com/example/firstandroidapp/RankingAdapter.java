package com.example.firstandroidapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.ViewHolder> {
    private List<RankingItem> rankingList;
    private String pointsType;

    public RankingAdapter(List<RankingItem> rankingList, String pointsType) {
        this.rankingList = rankingList;
        this.pointsType = pointsType;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rating, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RankingItem item = rankingList.get(position);

        holder.tvRank.setText(String.valueOf(position + 1));
        holder.tvUserName.setText(item.getUserName());
        holder.tvUserId.setText(item.getUserId());

        // Lấy điểm theo loại điểm và hiển thị
        int points = getPointsByType(item, pointsType);
        holder.tvPoints.setText(points + " điểm");
    }

    @Override
    public int getItemCount() {
        return rankingList.size();
    }

    private int getPointsByType(RankingItem item, String pointsType) {
        switch (pointsType) {
            case "points1":
                return item.getPoints1();
            case "points2":
                return item.getPoints2();
            case "points3":
                return item.getPoints3();
            default:
                return 0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRank, tvUserName, tvUserId, tvPoints;

        public ViewHolder(View view) {
            super(view);
            tvRank = view.findViewById(R.id.tvRank);
            tvUserName = view.findViewById(R.id.tvUserName);
            tvUserId = view.findViewById(R.id.tvUserId);
            tvPoints = view.findViewById(R.id.tvPoints);
        }
    }
}
