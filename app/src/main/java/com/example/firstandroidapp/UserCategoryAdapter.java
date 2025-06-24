package com.example.firstandroidapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class UserCategoryAdapter extends RecyclerView.Adapter<UserCategoryAdapter.ViewHolder> {

    private List<CategoryModel> fullList = new ArrayList<>();
    private List<CategoryModel> displayList = new ArrayList<>();
    private boolean isExpanded = false;
    private final Context context;

    public UserCategoryAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<CategoryModel> data) {
        fullList = data;
        updateDisplayList();
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
        updateDisplayList();
    }

    private void updateDisplayList() {
        if (isExpanded || fullList.size() <= 4) {
            displayList = fullList;
        } else {
            displayList = fullList.subList(0, 4);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_participated_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserCategoryAdapter.ViewHolder holder, int position) {
        CategoryModel model = displayList.get(position);
        holder.tvCategoryName.setText(model.getTitle());

        int iconRes = model.getIconResId() != 0 ? model.getIconResId() : R.drawable.ic_default_icon;
        holder.imgCategoryIcon.setImageResource(iconRes);
    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCategoryIcon;
        TextView tvCategoryName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCategoryIcon = itemView.findViewById(R.id.imgCategoryIcon);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
        }
    }
}