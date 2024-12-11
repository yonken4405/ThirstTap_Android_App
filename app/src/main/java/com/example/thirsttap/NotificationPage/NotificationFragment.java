package com.example.thirsttap.NotificationPage;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.thirsttap.HomePage.HomeFragment;
import com.example.thirsttap.OrderHistoryPage.OrderHistoryFragment;
import com.example.thirsttap.OrderPage.StationSelection;
import com.example.thirsttap.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<Notification> notificationList;
    private ProgressBar loader;
    private ImageButton backBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        loader = view.findViewById(R.id.loader);
        recyclerView = view.findViewById(R.id.recycler_view);
        backBtn = view.findViewById(R.id.back_button);
        notificationList = new ArrayList<>();
        adapter = new NotificationAdapter(notificationList, this::markAsRead); // Pass the markAsRead method
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Fetch notifications
        if (getArguments() != null) {
            int userId = getArguments().getInt("userId"); // Pass userId to fragment
            fetchNotifications(userId);
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment fragment = new HomeFragment();
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
            }
        });

        return view;
    }

    private void fetchNotifications(int userId) {
        loader.setVisibility(View.VISIBLE);
        String url = "https://thirsttap.scarlet2.io/Backend/fetchNotifications.php"; // Update to the correct API endpoint
        String finalUrl = url + "?user_id=" + userId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, finalUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loader.setVisibility(View.GONE);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String status = jsonResponse.getString("status");

                            if ("success".equals(status)) {
                                JSONArray jsonArray = jsonResponse.getJSONArray("notifications");
                                notificationList.clear();

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    Notification notification = new Notification(
                                            jsonObject.getInt("notif_id"),
                                            jsonObject.getInt("orderid"),
                                            jsonObject.getInt("userid"),
                                            jsonObject.getString("message"),
                                            jsonObject.getString("status"),
                                            jsonObject.getString("created_at")
                                    );

                                    notificationList.add(notification);
                                }

                                // Notify the adapter of data changes
                                adapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loader.setVisibility(View.GONE);
                        error.printStackTrace(); // Log the error
                    }
                });

        // Add the request to the queue
        Volley.newRequestQueue(getContext()).add(stringRequest);
    }

    private void markAsRead(int notifId) {
        loader.setVisibility(View.VISIBLE);
        String url = "https://thirsttap.scarlet2.io/Backend/markNotificationsRead.php"; // Create a new PHP script for this purpose

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    loader.setVisibility(View.GONE);
                    OrderHistoryFragment fragment = new OrderHistoryFragment();
                    getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
                },
                error -> {
                    loader.setVisibility(View.GONE);
                    // Optionally handle the error
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("notif_id", String.valueOf(notifId));
                return params;
            }
        };

        // Add the request to the queue
        Volley.newRequestQueue(getContext()).add(stringRequest);
    }
}
