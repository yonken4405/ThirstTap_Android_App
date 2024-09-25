package com.example.thirsttap.OrderPage;

public class OrderItem {
    private final String waterType;
    private final String containerSize;
    private final String containerStatus;
    private int quantity;
    private final double pricePerItem;
    private final double newContainerPrice;

    // Constructor
    public OrderItem(String waterType, String containerSize, String containerStatus, int quantity, double pricePerItem, double newContainerPrice) {
        this.waterType = waterType;
        this.containerSize = containerSize;
        this.containerStatus = containerStatus;
        this.quantity = Math.max(quantity, 1); // Ensure at least 1 item
        this.pricePerItem = Math.max(pricePerItem, 0.0); // Ensure non-negative price
        this.newContainerPrice = Math.max(newContainerPrice, 0.0); // Ensure non-negative new container price
    }

    // Getters
    public String getWaterType() { return waterType; }
    public String getContainerSize() { return containerSize; }
    public String getContainerStatus() { return containerStatus; }
    public int getQuantity() { return quantity; }
    public double getPricePerItem() { return pricePerItem; }
    public double getNewContainerPrice() { return newContainerPrice; }

    // Setter for quantity
    public void setQuantity(int quantity) {
        this.quantity = Math.max(quantity, 1); // Ensure at least 1 item
    }

    // Method to calculate total price
    public double getTotalPrice() {
        double containerCost = containerStatus.equals("New Container") ? newContainerPrice : 0.0;
        return (pricePerItem + containerCost) * quantity;
    }
}
