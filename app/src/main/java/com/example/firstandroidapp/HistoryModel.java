package com.example.firstandroidapp;

public class HistoryModel {
    private String name;
    private String status;
    private String type;
    private String description;
    private String date;
    private int points;
    private String proofStatus;

    private String activityId; // ✅ Thêm field này


    // Constructor có tham số (7 tham số)
    public HistoryModel(String name, String status, String type, String description, String date, int points, String proofStatus) {
        this.name = name;
        this.status = status;
        this.type = type;
        this.description = description;
        this.date = date;
        this.points = points;
        this.proofStatus = proofStatus;
    }

    public HistoryModel() {
    }

    // Getter và Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }
}
