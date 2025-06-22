package com.example.firstandroidapp;

public class RankingItem {
    private String userName;
    private String userId;
    private int points;

    // Constructor
    public RankingItem(String userName, String userId, int points) {
        this.userName = userName;
        this.userId = userId;
        this.points = points;
    }

    // Getter and Setter methods
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
