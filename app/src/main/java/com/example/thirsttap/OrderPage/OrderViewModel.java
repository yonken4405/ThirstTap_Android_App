package com.example.thirsttap.OrderPage;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class OrderViewModel extends ViewModel {
    private static final MutableLiveData<String> waterType = new MutableLiveData<>();
    private static final MutableLiveData<String> containerSize = new MutableLiveData<>();
    private static final MutableLiveData<String> containerStatus = new MutableLiveData<>();
    private static final MutableLiveData<Integer> quantity = new MutableLiveData<>();
    private static final MutableLiveData<Double> pricePerItem = new MutableLiveData<>();
    private static final MutableLiveData<Double> newContainerPrice = new MutableLiveData<>();
    private static final MutableLiveData<List<OrderItem>> orderItems = new MutableLiveData<>(new ArrayList<>());

    private MutableLiveData<String> stationName = new MutableLiveData<>();
    private MutableLiveData<String> stationAddress = new MutableLiveData<>();
    private MutableLiveData<String> stationSchedule = new MutableLiveData<>();
    private MutableLiveData<String> stationId = new MutableLiveData<>();

    // Method to add a new order item
    public static void addOrderItem(OrderItem orderItem) {
        List<OrderItem> currentList = orderItems.getValue();
        if (currentList != null) {
            currentList.add(orderItem);
            orderItems.setValue(currentList);  // Update the list in LiveData
        }
    }

    // Getter and Setter methods for station data
    public void setStationData(String name, String address, String schedule, String id) {
        stationName.setValue(name);
        stationAddress.setValue(address);
        stationSchedule.setValue(schedule);
        stationId.setValue(id);
    }

    public LiveData<String> getStationName() {
        return stationName;
    }

    public LiveData<String> getStationAddress() {
        return stationAddress;
    }

    public LiveData<String> getStationSchedule() {
        return stationSchedule;
    }

    public LiveData<String> getStationId() {
        return stationId;
    }


    // Method to clear the cart
    public void clearCart() {
        orderItems.setValue(new ArrayList<>());  // Reset the list
    }

    // Getter for the list of items
    public LiveData<List<OrderItem>> getOrderItems() {
        return orderItems;
    }

    // Getters and Setters
    public static LiveData<String> getWaterType() {
        return waterType;
    }

    public static void setWaterType(String type) {
        waterType.setValue(type);
    }

    public static LiveData<String> getContainerSize() {
        return containerSize;
    }

    public static void setContainerSize(String size) {
        containerSize.setValue(size);
    }

    public static LiveData<String> getContainerStatus() {
        return containerStatus;
    }

    public static void setContainerStatus(String status) {
        containerStatus.setValue(status);
    }

    public static LiveData<Integer> getQuantity() {
        return quantity;
    }

    public static void setQuantity(int qty) {
        quantity.setValue(qty);
    }

    public static LiveData<Double> getPricePerItem() {
        return pricePerItem;
    }

    public static void setPricePerItem(double price) {
        pricePerItem.setValue(price);
    }

    public static LiveData<Double> getNewContainerPrice() {
        return newContainerPrice;
    }

    public static void setNewContainerPrice(double price) {
        newContainerPrice.setValue(price);
    }
}


