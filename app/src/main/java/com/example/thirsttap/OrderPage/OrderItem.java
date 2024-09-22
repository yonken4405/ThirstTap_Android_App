package com.example.thirsttap.OrderPage;

public class OrderItem {
    private String waterType;
    private String containerSize;
    private String containerStatus;
    private int quantity;
    private double pricePerItem;
    private double newContainerPrice;

    // Constructor
    public OrderItem(String waterType, String containerSize, String containerStatus, int quantity, double pricePerItem, double newContainerPrice) {
        this.waterType = waterType;
        this.containerSize = containerSize;
        this.containerStatus = containerStatus;
        this.quantity = quantity;
        this.pricePerItem = pricePerItem;
        this.newContainerPrice = newContainerPrice;
    }

    // Getters
    public String getWaterType() { return waterType; }
    public String getContainerSize() { return containerSize; }
    public String getContainerStatus() { return containerStatus; }
    public int getQuantity() { return quantity; }
    public double getPricePerItem() { return pricePerItem; }
    public double getNewContainerPrice() { return newContainerPrice; }

    public double getTotalPrice() {
        return (pricePerItem + (containerStatus.equals("New Container") ? newContainerPrice : 0)) * quantity;
    }
}


