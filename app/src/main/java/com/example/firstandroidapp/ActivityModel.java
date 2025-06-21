package com.example.firstandroidapp;

public class ActivityModel {

    private String name;
    private String type;
    private String description;
    private String time;
    private String quantity;
    private int thumbnailResId;

    public ActivityModel(String name, String type, String description, String time, String quantity, int thumbnailResId) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.time = time;
        this.quantity = quantity;
        this.thumbnailResId = thumbnailResId;
    }

    // Nếu không truyền ảnh, mặc định là 0
    public ActivityModel(String name, String type, String description, String time, String quantity) {
        this(name, type, description, time, quantity, 0);
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getQuantity() { return quantity; }
    public void setQuantity(String quantity) { this.quantity = quantity; }

    public int getThumbnailResId() { return thumbnailResId; }
    public void setThumbnailResId(int thumbnailResId) { this.thumbnailResId = thumbnailResId; }
}
