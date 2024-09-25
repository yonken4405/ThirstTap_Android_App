package com.example.thirsttap.OrderPage;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.thirsttap.HomePage.HomeFragment;
import com.example.thirsttap.R;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Date;

public class CheckOutFragment extends Fragment {
    List<OrderItem> orderItems;
    private ImageButton backBtn, plusBtn, minusBtn;
    private Button addOrderButton, placeOrderBtn;
    private OrderViewModel orderViewModel;
    private LinearLayout orderItemsLayout;
    private TextView totalPriceTextView, merchSubtotal, totalPayment, changeDate, date;
    private int count;
    private double totalAmount = 0;
    boolean isNewContainer = true;
    private double totalPriceToPay = 0;
    private String formattedDate2;
    private String userId, email, name, phoneNum;
    private TextView stationAddressTextView, stationNameTextView, stationScheduleTextView;
    private String stationName, stationAddress, stationSchedule, stationId;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.checkout_fragment, container, false);

        orderItems = new ArrayList<>();
        orderViewModel = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);

        orderItemsLayout = view.findViewById(R.id.order_summary_container); // A LinearLayout where order items will be listed

        addOrderButton = view.findViewById(R.id.add_order_button);
        merchSubtotal = view.findViewById(R.id.merchandise_subtotal_tv);
        totalPayment = view.findViewById(R.id.total_payment_tv);
        placeOrderBtn = view.findViewById(R.id.place_order_btn);
        changeDate = view.findViewById(R.id.change_btn);
        date = view.findViewById(R.id.date);
        backBtn = view.findViewById(R.id.back_button);


        // Retrieve user profile data from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_profile", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userid", "default_userid");
        email = sharedPreferences.getString("email", "default_email");
        name = sharedPreferences.getString("name", "default_name");
        phoneNum = sharedPreferences.getString("phone_num", "default_phone_num");



        // Display the station data on the UI
        stationNameTextView = view.findViewById(R.id.store_name);
        stationScheduleTextView = view.findViewById(R.id.time);

        orderViewModel.getStationName().observe(getViewLifecycleOwner(), value -> {
            stationNameTextView.setText(value);
            stationName = value;
        });

        orderViewModel.getStationSchedule().observe(getViewLifecycleOwner(), value -> {
            stationScheduleTextView.setText(stationSchedule);
            stationSchedule = value;
        });

        orderViewModel.getStationAddress().observe(getViewLifecycleOwner(), value -> {
            stationAddress = value;
        });

        orderViewModel.getStationId().observe(getViewLifecycleOwner(), value -> {
            stationId = value;
        });






        changeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });


        // Observe the order items list
        orderViewModel.getOrderItems().observe(getViewLifecycleOwner(), orderItems -> {
            orderItemsLayout.removeAllViews();  // Clear the layout before adding items
            totalAmount = 0;

            for (OrderItem item : orderItems) {

                // Inflate a new item view for each order item
                View itemView = LayoutInflater.from(getContext()).inflate(R.layout.order_item_view, orderItemsLayout, false);

                TextView waterTypeTextView = itemView.findViewById(R.id.order_water_type);
                TextView containerSizeTextView = itemView.findViewById(R.id.order_container_size);
                TextView quantityTextView = itemView.findViewById(R.id.order_quantity);
                totalPriceTextView = itemView.findViewById(R.id.total);
                plusBtn = itemView.findViewById(R.id.plus_button);
                minusBtn = itemView.findViewById(R.id.minus_button);




                //hide the new container price if exchange container is chosen
                TextView conStat = itemView.findViewById(R.id.container_status_tv);
                TextView conStat2 = itemView.findViewById(R.id.new_container_price);
                if(item.getNewContainerPrice() == 0){
                    conStat.setText("Exchange Container: ₱");
                    conStat2.setText(String.valueOf(item.getNewContainerPrice()));
                    isNewContainer = false;
                }


                waterTypeTextView.setText(item.getWaterType());
                containerSizeTextView.setText(item.getContainerSize());
                quantityTextView.setText(String.valueOf(item.getQuantity()));
                totalPriceTextView.setText(String.valueOf(item.getTotalPrice()));



                // Initialize item view
                initializeItemView(item, quantityTextView, totalPriceTextView);

                // Add click listeners
                setItemClickListeners(item, quantityTextView, totalPriceTextView, plusBtn, minusBtn);

                // Add the item view to the layout
                orderItemsLayout.addView(itemView);
                totalAmount += item.getTotalPrice(); // Accumulate subtotal

                Log.d("CheckOutFragment", "Order Items: " + orderItems.toString());

            }

            merchSubtotal.setText(String.valueOf(totalAmount));

            totalAmount = totalAmount + 100;//100for the fees
            totalPayment.setText(String.valueOf(totalAmount));
        });


        addOrderButton.setOnClickListener(v -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new OrderFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });


        try {
            placeOrderBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Check if the date has been selected
                    if (formattedDate2 == null || formattedDate2.isEmpty()) {
                        // Show a message to the user to select a delivery date
                        Toast.makeText(getContext(), "Please select a delivery date.", Toast.LENGTH_SHORT).show();
                        return; // Prevent the order from being placed
                    }


                    // If the date is selected, proceed with placing the order
                    Log.d("CheckOutFragment", "Number of order items: " + orderViewModel.getOrderItems().getValue().size());

                    createOrder();  // Call the method to place the order
                }
            });

            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OrderFragment fragment = new OrderFragment();
                    getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
                }
            });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        return view;
    }

    public void showDatePicker() {
        // Get the current date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year1, month1, dayOfMonth) -> {
                    // Create a Calendar object with the selected date
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year1, month1, dayOfMonth);

                    // Format the date to "Tuesday, September 17"
                    SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d", Locale.ENGLISH);
                    String formattedDate = sdf.format(selectedDate.getTime());  // getTime() used on Calendar

                    // Use the formatted date string
                    date.setText(formattedDate);

                    //to be uploaded to the database
                    SimpleDateFormat sdf2 = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
                    formattedDate2 = sdf2.format(selectedDate.getTime());  // getTime() used on Calendar
                }, year, month, day);



        // Set the minimum date to today
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

        // Set the maximum date to 3 days from now
        calendar.add(Calendar.DAY_OF_MONTH, 3);
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());

        // Show the date picker dialog
        datePickerDialog.show();
    }


    private void createOrder() {
        RequestQueue queue = Volley.newRequestQueue(getContext());

        String url = "https://thirsttap.scarlet2.io/Backend/createOrder.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getContext(), "Order placed successfully!", Toast.LENGTH_SHORT).show();
                        Log.d("response checkout", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Error placing order!", Toast.LENGTH_SHORT).show();
                Log.d("response checkout", String.valueOf(error));
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                params.put("delivery_date", formattedDate2);
                params.put("station_name", stationName);
                params.put("station_id", stationId); // Use totalPriceToPay here as well
                // Calculate totalPriceToPay here
                totalPriceToPay = totalAmount + 100;
                params.put("total_price", String.valueOf(totalPriceToPay)); // Use totalPriceToPay here

                JSONArray orderItemsArray = new JSONArray();
                List<OrderItem> currentOrderItems = orderViewModel.getOrderItems().getValue();
                if (currentOrderItems != null) {
                    for (OrderItem item : currentOrderItems) {
                        JSONObject orderItem = new JSONObject();
                        try {
                            orderItem.put("water_type", item.getWaterType());
                            orderItem.put("gallon_size", item.getContainerSize());
                            orderItem.put("quantity", item.getQuantity());
                            orderItem.put("is_new_container", item.getNewContainerPrice() != 0);
                            orderItem.put("total_price", String.valueOf(totalPriceToPay)); // Use totalPriceToPay here as well



                            orderItemsArray.put(orderItem);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                params.put("order_items", orderItemsArray.toString());
                Log.d("CheckOutFragment", "Params: " + params.toString());

                return params;
            }
        };

        queue.add(stringRequest);
    }




    private void initializeItemView(OrderItem item, TextView quantityTextView, TextView totalPriceTextView) {
        // Set up the item view with initial values
        quantityTextView.setText(String.valueOf(item.getQuantity()));
        totalPriceTextView.setText(String.valueOf(item.getTotalPrice()));
    }

    private void setItemClickListeners(OrderItem item, TextView quantityTextView, TextView totalPriceTextView, ImageButton plusBtn, ImageButton minusBtn) {
        // Initialize count based on the order
        int[] count = {item.getQuantity()};

        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count[0] += 1; // Increment count
                item.setQuantity(count[0]); // Update the quantity in the OrderItem object
                quantityTextView.setText(String.valueOf(count[0])); // Update displayed quantity

                double pricePerItem = item.getPricePerItem() + item.getNewContainerPrice(); // Get price
                double updatedPrice = pricePerItem * count[0]; // Calculate updated price
                totalPriceTextView.setText(String.valueOf(updatedPrice)); // Update item's total price

                // Update the subtotal based on the new item's total price
                totalAmount += item.getPricePerItem() + item.getNewContainerPrice(); // Add the item's price to the subtotal
                updateSubtotal(); // Call a method to update subtotal display
            }
        });

        minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count[0] > 1) {
                    double pricePerItem = item.getPricePerItem() + item.getNewContainerPrice(); // Get price

                    count[0] -= 1; // Decrement count
                    item.setQuantity(count[0]); // Update the quantity in the OrderItem object
                    quantityTextView.setText(String.valueOf(count[0])); // Update displayed quantity

                    double updatedPrice = pricePerItem * count[0]; // Calculate updated price
                    totalPriceTextView.setText(String.valueOf(updatedPrice)); // Update item's total price

                    // Update the subtotal
                    totalAmount -= item.getPricePerItem() + item.getNewContainerPrice(); // Subtract the item's price from the subtotal
                    updateSubtotal(); // Call a method to update subtotal display
                }
            }
        });
    }



    private void updateSubtotal() {
        totalPriceToPay = totalAmount + 100;
        merchSubtotal.setText(String.valueOf(totalAmount)); // Update subtotal display
        totalPayment.setText(String.valueOf(totalPriceToPay));

    }
}
