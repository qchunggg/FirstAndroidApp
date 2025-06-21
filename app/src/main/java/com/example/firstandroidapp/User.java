package com.example.firstandroidapp;

public class User {
    private String name;
    private String studentId;
    private String className;
    private String department;
    private String accumulatedPoints;
    private String phoneNumber;
    private String semesterRank;
    private String yearRank;
    private String avatarUrl;

    public User() {
        // Constructor rỗng để Firebase có thể ánh xạ dữ liệu
    }

    public User(String name, String studentId, String className, String department, String accumulatedPoints,
                String phoneNumber, String semesterRank, String yearRank, String avatarUrl) {
        this.name = name;
        this.studentId = studentId;
        this.className = className;
        this.department = department;
        this.accumulatedPoints = accumulatedPoints;
        this.phoneNumber = phoneNumber;
        this.semesterRank = semesterRank;
        this.yearRank = yearRank;
        this.avatarUrl = avatarUrl;
    }

    // Getter và Setter cho các thuộc tính
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getAccumulatedPoints() {
        return accumulatedPoints;
    }

    public void setAccumulatedPoints(String accumulatedPoints) {
        this.accumulatedPoints = accumulatedPoints;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSemesterRank() {
        return semesterRank;
    }

    public void setSemesterRank(String semesterRank) {
        this.semesterRank = semesterRank;
    }

    public String getYearRank() {
        return yearRank;
    }

    public void setYearRank(String yearRank) {
        this.yearRank = yearRank;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
