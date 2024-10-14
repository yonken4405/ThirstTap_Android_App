package com.example.thirsttap.OrderHistoryPage;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thirsttap.R;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orderList;

    public OrderAdapter(List<Order> orderList) {
        this.orderList = orderList != null ? orderList : new ArrayList<>(); // Avoid null reference
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        // Check for valid position
        if (position < 0 || position >= orderList.size()) {
            Log.e("OrderAdapter", "Invalid position: " + position + " for list size: " + orderList.size());
            return; // Early return to prevent exception
        }

        Order order = orderList.get(position);
        Log.d("OrderAdapter", "Binding order: " + order.getOrderId() + " at position: " + position);

        if (order != null) {
            holder.orderStatus.setText(order.getOrderStatus() != null ? order.getOrderStatus() : "N/A");
            holder.orderId.setText(order.getOrderId() != null ? order.getOrderId() : "N/A");
            holder.amount.setText(order.getAmount() != null ? order.getAmount() : "N/A");
            holder.customerName.setText(order.getCustomerName() != null ? order.getCustomerName() : "N/A");
            holder.deliveryDate.setText(order.getDeliveryDate() != null ? order.getDeliveryDate() : "N/A");
            holder.deliveryAddress.setText(order.getDeliveryAddress() != null ? order.getDeliveryAddress() : "N/A");
        } else {
            Log.e("OrderAdapter", "Order is null at position: " + position);
        }
    }

    public void updateOrderList(List<Order> newList) {
        if (newList != null) {
            orderList.clear();
            orderList.addAll(newList);
        } else {
            orderList.clear(); // Clear the list if newList is null
        }
        notifyDataSetChanged(); // Notify the adapter of the change
    }

    @Override
    public int getItemCount() {
        return orderList.size(); // Return the size of the list
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderStatus, orderId, amount, customerName, deliveryDate, deliveryAddress;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderStatus = itemView.findViewById(R.id.tv_order_status);
            orderId = itemView.findViewById(R.id.tv_order_id);
            amount = itemView.findViewById(R.id.tv_order_amount);
            customerName = itemView.findViewById(R.id.tv_customer_name);
            deliveryDate = itemView.findViewById(R.id.tv_delivery_date);
            deliveryAddress = itemView.findViewById(R.id.tv_customer_address);
        }
    }
}


