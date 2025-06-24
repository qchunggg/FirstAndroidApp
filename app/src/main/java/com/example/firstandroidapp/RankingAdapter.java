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
        // Inflate item layout cho RecyclerView
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rating, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RankingItem item = rankingList.get(position);

        // Hiển thị thứ hạng
        holder.tvRank.setText(String.valueOf(position + 1));

        // Hiển thị tên người dùng và ID
        holder.tvUserName.setText(item.getUserName());
        holder.tvUserId.setText(item.getUserId());

        // Lấy điểm dựa trên loại điểm được chọn và hiển thị
        int points = getPointsByType(item, pointsType);
        holder.tvPoints.setText(points + " điểm"); // Hiển thị điểm kèm "điểm"
    }

    @Override
    public int getItemCount() {
        return rankingList.size();
    }

    private int getPointsByType(RankingItem item, String pointsType) {
        // Lấy điểm tương ứng với loại điểm (points1, points2, points3)
        int points = 0;
        switch (pointsType) {
            case "points1":
                points = item.getPoints1();
                break;
            case "points2":
                points = item.getPoints2();
                break;
            case "points3":
                points = item.getPoints3();
                break;
            default:
                points = 0;
                break;
        }
        return points;
    }

    // ViewHolder để lưu các tham chiếu đến các view trong item của RecyclerView
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
