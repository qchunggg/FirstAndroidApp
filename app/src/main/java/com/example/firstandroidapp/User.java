package com.example.firstandroidapp;

public class User {
    private String name;
    private String studentId;
    private String className;
    private String department;
    private String phone;

    // Constructor mặc định (Firebase yêu cầu có constructor mặc định)
    public User() {
    }

    // Constructor với tất cả các tham số
    public User(String name, String studentId, String className, String department, String phone) {
        this.name = name;
        this.studentId = studentId;
        this.className = className;
        this.department = department;
        this.phone = phone;
    }

    // Getter và Setter cho tất cả các trường

    public String getUserName() {
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
