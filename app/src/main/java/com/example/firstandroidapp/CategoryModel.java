package com.example.firstandroidapp;

public class CategoryModel {

    private String title;
    private int iconResId;

    private String key;
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

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
