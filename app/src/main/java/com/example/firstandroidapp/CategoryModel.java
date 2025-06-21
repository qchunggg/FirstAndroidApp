package com.example.firstandroidapp;

public class CategoryModel {

    private String title;
    private int iconResId;


    public CategoryModel(){}

    public CategoryModel(String title, int iconResId) {
        this.title = title;
        this.iconResId = iconResId;
    }

    public String getTitle() {
        return title;
    }

    public int getIconResId() {
        return iconResId;
    }
}
