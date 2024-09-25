package com.example.thirsttap.OrderHistoryPage;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.thirsttap.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OngoingOrdersFragment extends Fragment {

    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    private String orderStatus, orderId, amount, customerName, deliveryDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders_list, container, false);


        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(orderList);
        recyclerView.setAdapter(orderAdapter);

        // Fetch orders
        fetchOrders("ongoing");  // Only fetch ongoing orders


        return view;
    }

    private void fetchOrders(String status) {
        String url = "https://thirsttap.scarlet2.io/Backend/fetchOrderHistory.php?status=" + status;

        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                orderId = jsonObject.getString("orderid");
                                amount = jsonObject.getString("total_price");
                                customerName = jsonObject.getString("name");
                                deliveryDate = jsonObject.getString("delivery_date");

                                // Pass null or a default value for orderStatus since it's not in the JSON
                                orderList.add(new Order("Ongoing", orderId, "₱" + amount, customerName, deliveryDate));

                                Log.d("Order", "Fetched order: " + orderId);
                            }

                            orderAdapter = new OrderAdapter(orderList);
                            recyclerView.setAdapter(orderAdapter);
                            orderAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            Log.e("Volley", "JSON parsing error: " + e.getMessage());
                        }

                        Log.d("OrdersList", response.toString());
                        Log.d("OrdersList", orderList.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Volley request failed: " + error.getMessage());
                    }
                });

        queue.add(jsonArrayRequest);
    }
}

