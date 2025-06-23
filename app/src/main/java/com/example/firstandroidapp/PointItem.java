package com.example.firstandroidapp;

public class PointItem {
    private String userName;
    private String description;
    private String date;
    private String pointId;  // Mới thêm pointId

    // Constructor, getter và setter
    public PointItem(String userName, String description, String date, String pointId) {
        this.userName = userName;
        this.description = description;
        this.date = date;
        this.pointId = pointId;  // Lưu pointId
    }

    // Getter và Setter cho pointId
    public String getPointId() {
        return pointId;
    }

    public void setPointId(String pointId) {
        this.pointId = pointId;
    }

    // Getter và Setter cho các trường còn lại
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
