package com.example.thirsttap.OrderHistoryPage;

public class Order {
    private String orderStatus;
    private String orderId;
    private String amount;
    private String customerName;
    private String deliveryDate;

    // Constructor
    public Order(String orderStatus, String orderId, String amount, String customerName, String deliveryDate) {
        this.orderStatus = orderStatus;
        this.orderId = orderId;
        this.amount = amount;
        this.customerName = customerName;
        this.deliveryDate = deliveryDate;
    }

    // Getters and Setters
    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }


    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
}

