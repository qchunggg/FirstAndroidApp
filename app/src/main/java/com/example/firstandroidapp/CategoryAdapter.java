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
    private OnEditClickListener editClickListener;
    private OnCategoryActionListener actionListener;

    public CategoryAdapter(List<CategoryModel> categoryList,
                           OnEditClickListener editClickListener,
                           OnCategoryActionListener actionListener) {
        this.categoryList = categoryList;
        this.editClickListener = editClickListener;
        this.actionListener = actionListener;
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

        if (category.getIconResId() == 0) {
            holder.ivIcon.setImageResource(R.drawable.ic_default_icon);
        } else {
            holder.ivIcon.setImageResource(category.getIconResId());
        }

        holder.ivEdit.setOnClickListener(v -> {
            if (editClickListener != null) {
                editClickListener.onEditClick(category);
            }
        });

        holder.ivDelete.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onDelete(category, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName;
        ImageView ivIcon, ivEdit, ivDelete;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvTitle);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            ivEdit = itemView.findViewById(R.id.ivEdit);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }

    public interface OnEditClickListener {
        void onEditClick(CategoryModel category);
    }

    public interface OnCategoryActionListener {
        void onDelete(CategoryModel category, int position);
    }
}
