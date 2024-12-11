package com.example.thirsttap.NotificationPage;

public class Notification {
    private int notifId;
    private int orderId;
    private int userId;
    private String message;
    private String status; // 'sent' or 'read'
    private String createdAt;

    // Constructor
    public Notification(int notifId, int orderId, int userId, String message, String status, String createdAt) {
        this.notifId = notifId;
        this.orderId = orderId;
        this.userId = userId;
        this.message = message;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters
    public int getNotifId() {
        return notifId;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
