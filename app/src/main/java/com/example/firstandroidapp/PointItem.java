package com.example.firstandroidapp;

public class PointItem {
    private String userId;  // Thêm trường userId
    private String userName;
    private String description;
    private String date;
    private String pointId;
    private int points;
    private String proofStatus;
    private String status;
    private String type;
    private String name;

    // Constructor
    public PointItem(String userId, String userName, String description, String date, String pointId, int points, String proofStatus, String status, String type, String name) {
        this.userId = userId;  // Lưu userId
        this.userName = userName;
        this.description = description;
        this.date = date;
        this.pointId = pointId;
        this.points = points;
        this.proofStatus = proofStatus;
        this.status = status;
        this.type = type;
        this.name = name;
    }

    // Getter and Setter methods
    public String getUserId() {
        return userId;  // Getter for userId
    }

    public void setUserId(String userId) {
        this.userId = userId;  // Setter for userId
    }

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

    public String getPointId() {
        return pointId;
    }

    public void setPointId(String pointId) {
        this.pointId = pointId;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getProofStatus() {
        return proofStatus;
    }

    public void setProofStatus(String proofStatus) {
        this.proofStatus = proofStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
