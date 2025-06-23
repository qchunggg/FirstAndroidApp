package com.example.firstandroidapp;

public class UserModel {
    public String email;
    public String fullName;
    public long createdAt;
    public boolean isAdmin;
    public String studentId;
    public String className;
    public String department;
    public String phone;

    // Constructor rỗng (yêu cầu cho Firebase)
    public UserModel() {
    }

    // Constructor đầy đủ (tùy chọn, dùng để khởi tạo đối tượng)
    public UserModel(String email, String fullName, long createdAt, boolean isAdmin,
                     String studentId, String className, String department, String phone) {
        this.email = email;
        this.fullName = fullName;
        this.createdAt = createdAt;
        this.isAdmin = isAdmin;
        this.studentId = studentId;
        this.className = className;
        this.department = department;
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
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