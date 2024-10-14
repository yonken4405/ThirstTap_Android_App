package com.example.thirsttap.OrderPage;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class OrderViewModel extends ViewModel {
    private static final MutableLiveData<String> waterType = new MutableLiveData<>();
    private static final MutableLiveData<String> containerSize = new MutableLiveData<>();
    private static final MutableLiveData<String> containerStatus = new MutableLiveData<>();
    private static final MutableLiveData<Integer> quantity = new MutableLiveData<>();
    private static final MutableLiveData<Double> pricePerItem = new MutableLiveData<>();
    private static final MutableLiveData<Double> newContainerPrice = new MutableLiveData<>();
    private static final MutableLiveData<List<OrderItem>> orderItems = new MutableLiveData<>(new ArrayList<>());
    private static final MutableLiveData<String> selectedDate = new MutableLiveData<>();
    private static final MutableLiveData<String> paymentOption = new MutableLiveData<>();


    private MutableLiveData<String> stationName = new MutableLiveData<>();
    private MutableLiveData<String> stationAddress = new MutableLiveData<>();
    private MutableLiveData<String> stationSchedule = new MutableLiveData<>();
    private MutableLiveData<String> stationId = new MutableLiveData<>();

    public OrderViewModel() {
        // Initialize selectedDate with the current date if not already set
        if (selectedDate.getValue() == null || selectedDate.getValue().isEmpty()) {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d", Locale.ENGLISH);
            String currentDate = sdf.format(calendar.getTime());
            selectedDate.setValue(currentDate);  // Set the current date as the default
        }
    }

    // Method to add a new order item
    public static void addOrderItem(OrderItem orderItem) {
        List<OrderItem> currentList = orderItems.getValue();
        if (currentList != null) {
            currentList.add(orderItem);
            orderItems.setValue(currentList);  // Update the list in LiveData
        }
    }

    // Method to remove a specific order item
    public void removeOrderItem(OrderItem item) {
        List<OrderItem> currentList = orderItems.getValue();
        if (currentList != null && currentList.contains(item)) {
            currentList.remove(item);  // Remove the specified item
            orderItems.setValue(currentList);  // Update the list in LiveData
        }
        Log.d("list of orders", currentList.toString());
    }

    // Update the removeItemsWithZeroQuantity method to simply return current items
    public void removeItemsWithZeroQuantity() {
        List<OrderItem> currentList = orderItems.getValue();
        if (currentList != null) {
            Iterator<OrderItem> iterator = currentList.iterator();
            while (iterator.hasNext()) {
                OrderItem item = iterator.next();
                if (item.getQuantity() == 0) {
                    iterator.remove();  // Remove item if quantity is 0
                }
            }
            orderItems.setValue(currentList);  // Update the list after removal
        }
    }

    // Getter and Setter methods for station data
    public void setStationData(String name, String address, String schedule, String id) {
        stationName.setValue(name);
        stationAddress.setValue(address);
        stationSchedule.setValue(schedule);
        stationId.setValue(id);
    }

    public void updateOrderItem(OrderItem updatedItem) {
        List<OrderItem> currentList = orderItems.getValue();
        if (currentList == null) {
            Log.e("OrderViewModel", "Current order items list is null.");
            return; // Handle null list case as appropriate
        }

        for (int i = 0; i < currentList.size(); i++) {
            OrderItem item = currentList.get(i);
            if (item.getId() == updatedItem.getId()) {
                // Only update if the values are actually different
                if (!item.equals(updatedItem)) { // You may need to implement equals in OrderItem
                    currentList.set(i, updatedItem);
                    orderItems.setValue(currentList);
                }
                break;
            }
        }
    }


    // Getter and Setter for the selected date
    public static LiveData<String> getSelectedDate() {
        return selectedDate;
    }

    public static void setSelectedDate(String date) {
        selectedDate.setValue(date);
    }

    // Getter and Setter for the payment option
    public static LiveData<String> getPaymentOption() {
        return paymentOption;
    }

    public static void setPaymentOption(String option) {
        paymentOption.setValue(option);
    }

    // Method to clear the cart
    public void clearCart() {
        orderItems.setValue(new ArrayList<>());  // Reset the list
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



    // Getter for the list of items
    public LiveData<List<OrderItem>> getOrderItems() {
        return orderItems;
    }

    public List<OrderItem> getCurrentOrderItems() {
        List<OrderItem> currentList = orderItems.getValue();
        return currentList != null ? new ArrayList<>(currentList) : new ArrayList<>();
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


