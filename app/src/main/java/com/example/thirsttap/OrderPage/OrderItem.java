package com.example.thirsttap.OrderPage;

public class OrderItem {
    private static int idCounter = 0; // Static counter for unique IDs
    private final int id; // Unique ID
    private final String waterType;
    private final String containerSize;
    private final String containerStatus;
    private int quantity;
    private final double pricePerItem;
    private final double newContainerPrice;

    // Constructor
    public OrderItem(String waterType, String containerSize, String containerStatus, int quantity, double pricePerItem, double newContainerPrice) {
        this.id = ++idCounter; // Increment and assign unique ID
        this.waterType = waterType;
        this.containerSize = containerSize;
        this.containerStatus = containerStatus;
        this.quantity = Math.max(quantity, 1); // Ensure at least 1 item
        this.pricePerItem = Math.max(pricePerItem, 0.0); // Ensure non-negative price
        this.newContainerPrice = Math.max(newContainerPrice, 0.0); // Ensure non-negative new container price
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        OrderItem that = (OrderItem) obj;
        return id == that.id &&
                quantity == that.quantity &&
                Double.compare(that.pricePerItem, pricePerItem) == 0 &&
                Double.compare(that.newContainerPrice, newContainerPrice) == 0 &&
                waterType.equals(that.waterType) &&
                containerSize.equals(that.containerSize) &&
                containerStatus.equals(that.containerStatus);
    }


    // Getters
    public int getId() { return id; } // Getter for id as int
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
