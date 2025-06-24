package com.example.firstandroidapp;

public class PointItem {
    private String userName;
    private String description;
    private String date;
    private String pointId;
    private int points;
    private String proofStatus;
    private String status;
    private String type;

    // Constructor, getter và setter
    public PointItem(String userName, String description, String date, String pointId,
                     int points, String proofStatus, String status, String type) {
        this.userName = userName;
        this.description = description;
        this.date = date;
        this.pointId = pointId;
        this.points = points;
        this.proofStatus = proofStatus;
        this.status = status;
        this.type = type;
    }

    // Getter và Setter cho các trường
    public String getPointId() {
        return pointId;
    }

    public void setPointId(String pointId) {
        this.pointId = pointId;
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
}
