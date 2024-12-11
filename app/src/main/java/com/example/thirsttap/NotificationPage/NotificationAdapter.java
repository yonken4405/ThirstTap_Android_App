package com.example.thirsttap.NotificationPage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thirsttap.R;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private List<Notification> notifications;
    private OnNotificationClickListener onNotificationClickListener;

    public NotificationAdapter(List<Notification> notifications, OnNotificationClickListener listener) {
        this.notifications = notifications;
        this.onNotificationClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        holder.messageTextView.setText(notification.getMessage());

        // Set text color based on read/unread status
        if (notification.getStatus().equals("sent")) {
            holder.messageTextView.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.josiahBlack)); // read
        } else {
            holder.messageTextView.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.gray)); // read
        }

        // Handle notification click to mark as read
        holder.itemView.setOnClickListener(v -> {
            onNotificationClickListener.onNotificationClick(notification.getNotifId());
            // Optionally remove the notification from the list after it is clicked
            notifications.remove(position);
            notifyItemRemoved(position);
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public interface OnNotificationClickListener {
        void onNotificationClick(int notifId);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView messageTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.message_text_view);
        }
    }
}
