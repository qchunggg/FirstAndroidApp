package com.example.firstandroidapp;

public class RankingItem {
    private String userName;
    private String userId;
    private int points1;
    private int points2;
    private int points3;

    // Constructor mặc định cho Firebase
    public RankingItem() {}

    public RankingItem(String userName, String userId, int points1, int points2, int points3) {
        this.userName = userName;
        this.userId = userId;
        this.points1 = points1;
        this.points2 = points2;
        this.points3 = points3;
    }

    // Getter methods
    public String getUserName() {
        return userName;
    }

    public String getUserId() {
        return userId;
    }

    public int getPoints1() {
        return points1;
    }

    public int getPoints2() {
        return points2;
    }

    public int getPoints3() {
        return points3;
    }

    // Setter methods (optional)
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setPoints1(int points1) {
        this.points1 = points1;
    }

    public void setPoints2(int points2) {
        this.points2 = points2;
    }

    public void setPoints3(int points3) {
        this.points3 = points3;
    }

    // Override toString() method for easier logging
    @Override
    public String toString() {
        return "RankingItem{" +
                "userName='" + userName + '\'' +
                ", userId='" + userId + '\'' +
                ", points1=" + points1 +
                ", points2=" + points2 +
                ", points3=" + points3 +
                '}';
    }
}
