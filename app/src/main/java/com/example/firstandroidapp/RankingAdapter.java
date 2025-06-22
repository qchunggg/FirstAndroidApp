package com.example.firstandroidapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RankingViewHolder> {

    private List<RankingItem> rankingList;

    // Constructor
    public RankingAdapter(List<RankingItem> rankingList) {
        this.rankingList = rankingList;
    }

    @Override
    public RankingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout for each item
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rating, parent, false); // Ensure layout is correct
        return new RankingViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RankingViewHolder holder, int position) {
        // Get the current item
        RankingItem item = rankingList.get(position);

        // Update rank
        holder.tvRank.setText(String.valueOf(position + 1));  // Rank starts from 1

        holder.tvUserName.setText(item.getUserName());
        holder.tvUserId.setText(item.getUserId());
        holder.tvPoints.setText(String.valueOf(item.getPoints()));

        // Set profile picture (adjust based on actual resource)
        holder.ivProfilePic.setImageResource(R.drawable.ic_profile);  // Replace with actual image
    }

    @Override
    public int getItemCount() {
        return rankingList.size();  // Return the number of items in the list
    }

    // ViewHolder for each item
    public static class RankingViewHolder extends RecyclerView.ViewHolder {

        public TextView tvRank, tvUserName, tvUserId, tvPoints;
        public ImageView ivProfilePic;

        public RankingViewHolder(View itemView) {
            super(itemView);

            // Initialize views
            tvRank = itemView.findViewById(R.id.tvRank);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserId = itemView.findViewById(R.id.tvUserId);
            tvPoints = itemView.findViewById(R.id.tvPoints);
            ivProfilePic = itemView.findViewById(R.id.ivProfilePic);
        }
    }
}
