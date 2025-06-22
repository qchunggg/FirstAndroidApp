package com.example.firstandroidapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<CategoryModel> categoryList;

    public CategoryAdapter(List<CategoryModel> categoryList) {
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategoryModel category = categoryList.get(position);


        holder.tvCategoryName.setText(category.getTitle());

        // Sử dụng ic_default_icon khi iconResId = 0, ngược lại dùng iconResId từ dữ liệu
        if (category.getIconResId() == 0) {
            holder.ivIcon.setImageResource(R.drawable.ic_default_icon);
            Log.d("IconDebug", "Using ic_default_icon");
        } else {
            holder.ivIcon.setImageResource(category.getIconResId());
            Log.d("IconDebug", "Using custom icon: " + category.getIconResId());
        }

        holder.ivEdit.setOnClickListener(v -> {
            if (editClickListener != null) {
                editClickListener.onEditClick(categoryList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName;
        ImageView ivIcon;

        ImageView ivEdit;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvTitle);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            ivEdit = itemView.findViewById(R.id.ivEdit);
        }
    }


    public interface OnEditClickListener {
        void onEditClick(CategoryModel category);
    }

    private OnEditClickListener editClickListener;

    public CategoryAdapter(List<CategoryModel> categoryList, OnEditClickListener listener) {
        this.categoryList = categoryList;
        this.editClickListener = listener;
    }
}