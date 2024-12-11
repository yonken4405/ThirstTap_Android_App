package com.example.thirsttap.OrderHistoryPage;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.thirsttap.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PendingOrdersFragment extends Fragment {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    private String userId;
    private ProgressBar loader;
    private TextInputEditText searchEditText;
    private ImageView arrowAscending;
    private ImageView arrowDescending;
    private boolean isAscending = true; // Track the current sort order

    private String currentStatus = "Pending"; // Store the current status
    private String currentSortOrder = "desc";  // Store the current sort order

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders_list, container, false);
        loader = view.findViewById(R.id.loader);
        searchEditText = view.findViewById(R.id.transaction_et);

        // Initialize TextInputLayout and retrieve the current end icon
        TextInputLayout textInputLayout = view.findViewById(R.id.et_search);
//
//        // Initialize arrow views
//        arrowAscending = view.findViewById(R.id.arrow_ascending);
//        arrowDescending = view.findViewById(R.id.arrow_descending);

        // Retrieve user profile data from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_profile", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userid", "default_userid");

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(orderList);
        recyclerView.setAdapter(orderAdapter);

        fetchOrders(currentStatus, currentSortOrder);  // Default sort order ascending

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(false);
            fetchOrders(currentStatus, currentSortOrder);
        });

//        // Set up arrow click listeners
//        arrowAscending.setOnClickListener(v -> {
//            currentSortOrder = "asc";
//            fetchOrders(currentStatus, currentSortOrder);
//            arrowAscending.setVisibility(View.GONE);
//            arrowDescending.setVisibility(View.VISIBLE);
//        });
//
//        arrowDescending.setOnClickListener(v -> {
//            currentSortOrder = "desc";
//            fetchOrders(currentStatus, currentSortOrder);
//            arrowAscending.setVisibility(View.VISIBLE);
//            arrowDescending.setVisibility(View.GONE);
//        });

        // Set up arrow click listeners to change the icon dynamically
        textInputLayout.setEndIconOnClickListener(v -> {
            if (currentSortOrder.equals("asc")) {
                currentSortOrder = "desc";
                textInputLayout.setEndIconDrawable(R.drawable.ascending_arrow);  // Change to ascending arrow
            } else {
                currentSortOrder = "asc";
                textInputLayout.setEndIconDrawable(R.drawable.descending_arrow);  // Change to descending arrow
            }
            fetchOrders(currentStatus, currentSortOrder);
        });

        // Set up search listener
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterOrders(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void fetchOrders(String status, String sortOrder) {
        loader.setVisibility(View.VISIBLE);

        String url = "https://thirsttap.scarlet2.io/Backend/fetchOrderHistory.php?status=" + status + "&userid=" + userId + "&sort=" + sortOrder;

        RequestQueue queue = Volley.newRequestQueue(getContext());
        orderList.clear();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    loader.setVisibility(View.GONE);
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
                            String orderId = jsonObject.getString("orderid");
                            String amount = jsonObject.getString("total_price");
                            String customerName = jsonObject.getString("name");
                            String deliveryDate = jsonObject.getString("delivery_date");
                            String deliveryAddress = jsonObject.getString("delivery_address");

                            orderList.add(new Order("Pending", orderId, "₱" + amount, customerName, deliveryDate, deliveryAddress));
                        }

                        orderAdapter.notifyDataSetChanged();  // Notify the adapter about the new data

                    } catch (JSONException e) {
                        Log.e("Volley", "JSON parsing error: " + e.getMessage());
                    }
                },
                error -> {
                    loader.setVisibility(View.GONE);
                    Log.e("Volley", "Volley request failed: " + error.getMessage());
                });

        queue.add(jsonArrayRequest);
    }

    private void filterOrders(String query) {
        if (query.isEmpty()) {
            // If the query is empty, fetch all orders again
            fetchOrders(currentStatus, currentSortOrder);
        } else {
            List<Order> filteredList = new ArrayList<>();
            for (Order order : orderList) {
                if (order.getOrderId().toLowerCase().contains(query.toLowerCase()) ||
                        order.getDeliveryDate().toLowerCase().contains(query.toLowerCase()) ||
                        order.getDeliveryAddress().toLowerCase().contains(query.toLowerCase()) ||
                        order.getAmount().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(order);
                }
            }
            orderAdapter.updateOrderList(filteredList);  // Update the adapter with the filtered list
        }
    }
}
