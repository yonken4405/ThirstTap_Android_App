package com.example.thirsttap;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCM";
    private static final String CHANNEL_ID = "your_channel_id"; // Define the channel ID
    private static final String SERVER_URL = "https://thirsttap.scarlet2.io/Backend/saveFcm.php"; // Replace with your server URL

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        createNotificationChannel();

        // Handle FCM messages here.
        String title = remoteMessage.getNotification() != null ? remoteMessage.getNotification().getTitle() : "New Notification";
        String body = remoteMessage.getNotification() != null ? remoteMessage.getNotification().getBody() : "You have a new message!";
        showNotification(title, body);
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "New token: " + token);
        storeTokenInPreferences(token);

        // Retrieve the userId from SharedPreferences or your preferred method
        SharedPreferences sharedPreferences = getSharedPreferences("user_profile", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("user_id", null); // Assuming you store userId in SharedPreferences
        if (userId != null) {
            sendTokenToServer(token, userId, this);
        }
    }

    private void storeTokenInPreferences(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_profile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("fcm_token", token);
        editor.apply();
    }

    private void showNotification(String title, String messageBody) {
        Log.d(TAG, "Showing notification: Title: " + title + ", Body: " + messageBody);

        // Create an Intent to redirect to your specific fragment
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("fragmentToOpen", "OrderHistoryFragment"); // Specify the fragment to open
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody)) // To show the full text
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI); // Set default notification sound

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(0, notificationBuilder.build()); // Notify with a specific ID
            Log.d(TAG, "Notification sent");
        } else {
            Log.e(TAG, "Notification Manager is null");
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH); // Ensure it's set to HIGH
            channel.setDescription("Channel description"); // Optional description
            channel.setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI, null); // Set sound for the channel
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void sendTokenToServer(String token, String userId, Context context) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Token sent to server: " + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error sending token to server: " + error.getMessage());
                // Log additional error details if needed
                if (error.networkResponse != null) {
                    Log.e(TAG, "Error response code: " + error.networkResponse.statusCode);
                    Log.e(TAG, "Error response data: " + new String(error.networkResponse.data));
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("fcm_token", token);
                params.put("user_id", userId);
                return params;
            }
        };

        Volley.newRequestQueue(context.getApplicationContext()).add(stringRequest);
    }
}
