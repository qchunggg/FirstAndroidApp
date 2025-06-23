package com.example.firstandroidapp;

import java.io.Serializable;

public class ActivityModel implements Serializable {
    private String key;
    private String name;
    private String type;
    private String description;
    private String startTime;
    private String endTime;
    private String quantity;
    private int thumbnailResId;
    private String eventOrganizer;  // Thêm trường eventOrganizer

    private String location;

    // Constructor
    public ActivityModel() {
    }

    // Constructor cho item_activity (chỉ cần thông tin cơ bản)
    public ActivityModel(String name, String type, String description, String startTime, String endTime,String quantity, int thumbnailResId) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.quantity = quantity;
        this.thumbnailResId = thumbnailResId;
        this.eventOrganizer = "Toàn trường";  // Giá trị mặc định
        this.location = "Hội trường T45";  // Giá trị mặc định
    }


    // Constructor cho detail_event (cần thông tin đầy đủ)
    public ActivityModel(String name, String type, String description, String startTime, String endTime, String quantity, String eventOrganizer, String location) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.quantity = quantity;
        this.thumbnailResId = 0;  // Đặt giá trị mặc định cho thumbnailResId
        this.eventOrganizer = eventOrganizer;
        this.location = location;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public int getThumbnailResId() {
        return thumbnailResId;
    }

    public void setThumbnailResId(int thumbnailResId) {
        this.thumbnailResId = thumbnailResId;
    }

    public String getEventOrganizer() {
        return eventOrganizer;
    }

    public void setEventOrganizer(String eventOrganizer) {
        this.eventOrganizer = eventOrganizer;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
