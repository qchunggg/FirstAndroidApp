package com.example.firstandroidapp;

import java.io.Serializable;

public class ActivityModel implements Serializable {
    private String key;
    private String name;
    private String type;
    private String description;
    private String startTime;
    private String endTime;
    private String quantity; // Giữ nguyên để tương thích với Firebase và admin
    private int thumbnailResId;
    private String eventOrganizer;
    private String location;

    private int points;

    // Thêm hai trường mới
    private int currentQuantity; // Số lượng đã đăng ký
    private int totalQuantity;   // Số lượng tối đa

    // Constructor mặc định (dùng khi lấy từ Firebase)
    public ActivityModel() {
    }

    // Constructor cho item_activity (chỉ cần thông tin cơ bản)
    public ActivityModel(String name, String type, String description, String startTime, String endTime, String quantity, int thumbnailResId) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.quantity = quantity;
        this.thumbnailResId = thumbnailResId;
        this.eventOrganizer = "Toàn trường";  // Giá trị mặc định
        this.location = "Hội trường T45";    // Giá trị mặc định
        // Phân tách và gán giá trị
        initializeQuantities();
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
        // Phân tách và gán giá trị
        initializeQuantities();
    }

    // Phương thức riêng để khởi tạo currentQuantity và totalQuantity
    private void initializeQuantities() {
        if (quantity != null && !quantity.isEmpty()) {
            try {
                // Nếu quantity là một số duy nhất (như "100" từ admin), lấy làm totalQuantity
                this.totalQuantity = Integer.parseInt(quantity);
                this.currentQuantity = 0; // Mặc định chưa ai đăng ký
            } catch (NumberFormatException e) {
                // Nếu quantity có định dạng "current/total" (trong tương lai), phân tách
                String[] parts = quantity.split("/");
                if (parts.length == 2) {
                    this.currentQuantity = Integer.parseInt(parts[0]);
                    this.totalQuantity = Integer.parseInt(parts[1]);
                } else {
                    this.totalQuantity = 0; // Giá trị mặc định nếu lỗi
                    this.currentQuantity = 0;
                }
            }
        } else {
            this.totalQuantity = 0;
            this.currentQuantity = 0;
        }
    }

    // Getter and Setter methods
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

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
        // Cập nhật lại currentQuantity và totalQuantity khi quantity thay đổi
        initializeQuantities();
    }

    public int getCurrentQuantity() {
        return currentQuantity;
    }

    public void setCurrentQuantity(int currentQuantity) {
        this.currentQuantity = currentQuantity;
        this.quantity = currentQuantity + "/" + totalQuantity; // Cập nhật quantity cho client
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
        this.quantity = currentQuantity + "/" + totalQuantity; // Cập nhật quantity cho client
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

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    // Tạm xử lý nếu Firebase trả về cả "point" và "points"
    public void setPointsFromFirebase(Integer pointValue) {
        if (pointValue != null) {
            this.points = pointValue;
        }
    }
}
