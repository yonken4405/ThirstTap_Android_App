package com.example.thirsttap.OrderHistoryPage;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thirsttap.R;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orderList;

    public OrderAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        Log.d("OrderAdapter", "Binding order: " + order.getOrderId() + " at position: " + position);

        if (order != null) {
            holder.orderStatus.setText(order.getOrderStatus() != null ? order.getOrderStatus() : "N/A");
            holder.orderId.setText(order.getOrderId() != null ? order.getOrderId() : "N/A");
            holder.amount.setText(order.getAmount() != null ? order.getAmount() : "N/A");
            holder.customerName.setText(order.getCustomerName() != null ? order.getCustomerName() : "N/A");
            holder.deliveryDate.setText(order.getDeliveryDate() != null ? order.getDeliveryDate() : "N/A");
        } else {
            Log.e("OrderAdapter", "Order is null at position: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView orderStatus, orderId, amount, customerName, deliveryDate;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            orderStatus = itemView.findViewById(R.id.tv_order_status);
            orderId = itemView.findViewById(R.id.tv_order_id);
            amount = itemView.findViewById(R.id.tv_order_amount);
            customerName = itemView.findViewById(R.id.tv_customer_name);
            deliveryDate = itemView.findViewById(R.id.tv_delivery_date);
        }
    }
}

