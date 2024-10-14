package com.example.thirsttap.OrderPage;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.thirsttap.AddressesPage.Address;
import com.example.thirsttap.AddressesPage.AddressListFragment;
import com.example.thirsttap.HomePage.HomeFragment;
import com.example.thirsttap.R;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;

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

import android.text.Editable;
import android.text.TextWatcher;


public class CheckOutFragment extends Fragment {
    List<OrderItem> orderItems;
    private ImageButton backBtn, plusBtn, minusBtn;
    private Button addOrderButton, placeOrderBtn;
    private OrderViewModel orderViewModel;
    private LinearLayout orderItemsLayout;
    private TextView totalPriceTextView, merchSubtotal, totalPayment, changeDate, date, clearBtn;
    private int count;
    private double totalAmount = 0;
    boolean isNewContainer = true;
    private double totalPriceToPay = 0;
    private String formattedDate2;
    private String userId, email, name, phoneNum;
    private TextView stationAddressTextView, stationNameTextView, stationScheduleTextView, deliveryAddress, itemPriceTv, containerPriceTv;
    private String stationName, stationAddress, stationSchedule, stationId, paymentMethod, chosenAddress, defaultAddressId, currentDate ;
    private LinearLayout addressLayout;
    private int addressId;
    private EditText quantityTextView;
    private RadioButton codRadio, gcashRadio, cardRadio;
    private ProgressBar loader;
    private TextInputEditText additionalInfo;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.checkout_fragment, container, false);

        loader = view.findViewById(R.id.loader);

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
        clearBtn = view.findViewById(R.id.clear_button);
        deliveryAddress = view.findViewById(R.id.delivery_address);
        addressLayout = view.findViewById(R.id.address_ll);
        additionalInfo = view.findViewById(R.id.additional_et);

        // Retrieve user profile data from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_profile", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userid", "default_userid");
        email = sharedPreferences.getString("email", "default_email");
        name = sharedPreferences.getString("name", "default_name");
        phoneNum = sharedPreferences.getString("phone_num", "default_phone_num");
        addressId = sharedPreferences.getInt("chosen_address_id", -1); // -1 indicates it was not found
        chosenAddress = sharedPreferences.getString("chosen_address", "default_address");

        //set default date to be current date
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d", Locale.ENGLISH);
        String formattedDate = sdf.format(calendar.getTime());

        // Observe the selected date from the ViewModel
        orderViewModel.getSelectedDate().observe(getViewLifecycleOwner(), value -> {
            if (value == null || value.isEmpty()) {
                // Set current date as the default
                date.setText(formattedDate);

                // Prepare the date for uploading to the database in the format "YYYY-MM-DD"
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                formattedDate2 = sdf2.format(calendar.getTime());  // This will give you "2024-10-01"
            } else {
                // Use the selected date
                formattedDate2 = value;
                date.setText(formattedDate2);
                Log.d("datez",value );
            }
        });


        if (addressId != -1) {
            // The addressId exists; you can use it
            Log.d("AddressID", "Retrieved Address ID: " + addressId + chosenAddress);
        } else {
            // Handle the case where the addressId was not found
            Log.d("AddressID", "Address ID not found in SharedPreferences.");
            // You might want to set a default value or inform the user
        }

        deliveryAddress.setText(chosenAddress);

        fetchAddressDisplay();//set default address

        // Display the station data on the UI
        stationNameTextView = view.findViewById(R.id.store_name);
        stationScheduleTextView = view.findViewById(R.id.time);

        orderViewModel.getStationName().observe(getViewLifecycleOwner(), value -> {
            stationName = value;
            stationNameTextView.setText(value);
        });

        orderViewModel.getStationSchedule().observe(getViewLifecycleOwner(), value -> {
            stationSchedule = value;
            stationScheduleTextView.setText(stationSchedule);
            Log.d("sched", stationSchedule);
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

        addressLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddressListFragment fragment = new AddressListFragment();
                Bundle args = new Bundle();
                args.putString("sourceFragment", "checkOutFragment"); // Pass the source fragment
                fragment.setArguments(args);
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
            }
        });


        // Observe the order items list
        orderViewModel.getOrderItems().observe(getViewLifecycleOwner(), orderItems -> {
            // Clear previous views
            orderItemsLayout.removeAllViews();
            totalAmount = 0;  // Reset totalAmount to zero before calculating

            for (OrderItem item : orderItems) {

                // Inflate a new item view for each order item
                View itemView = LayoutInflater.from(getContext()).inflate(R.layout.order_item_view, orderItemsLayout, false);

                TextView waterTypeTextView = itemView.findViewById(R.id.order_water_type);
                TextView containerSizeTextView = itemView.findViewById(R.id.order_container_size);
                quantityTextView = itemView.findViewById(R.id.order_quantity);
                itemPriceTv = itemView.findViewById(R.id.order_item_price);
                containerPriceTv = itemView.findViewById(R.id.new_container_price);
                totalPriceTextView = itemView.findViewById(R.id.total);
                plusBtn = itemView.findViewById(R.id.plus_button);
                minusBtn = itemView.findViewById(R.id.minus_button);

                quantityTextView.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String newQuantityText = s.toString();
                        int newQuantity = 1; // Default to 1

                        if (!newQuantityText.isEmpty()) {
                            try {
                                newQuantity = Integer.parseInt(newQuantityText);
                                if (newQuantity < 1) {
                                    newQuantity = 1; // Enforce minimum quantity
                                }
                            } catch (NumberFormatException e) {
                                newQuantity = 1; // Reset to minimum if input is invalid
                                // Optionally, inform the user with a Toast or similar
                            }
                        }

                        item.setQuantity(newQuantity);
                        orderViewModel.updateOrderItem(item);
                        calculateTotal();
                    }

                    @Override
                    public void afterTextChanged(Editable s) { }
                });



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

            // Update the subtotal and total payment
            merchSubtotal.setText(String.valueOf(totalAmount));
            totalPayment.setText(String.valueOf(totalAmount+ 100));
        });


        addOrderButton.setOnClickListener(v -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new OrderFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialog();
            }
        });



        codRadio = view.findViewById(R.id.cod_radio);
        gcashRadio = view.findViewById(R.id.gcash_radio);
        cardRadio = view.findViewById(R.id.card_radio);

        // Observe the selected date from the ViewModel
        orderViewModel.getPaymentOption().observe(getViewLifecycleOwner(), value -> {
            if (value == null || value.isEmpty()) {

            } else {
                switch (value) {
                    case "COD":
                        // Handle COD selection
                        paymentMethod = "COD";
                        gcashRadio.setChecked(false);
                        cardRadio.setChecked(false);
                        codRadio.setChecked(true);
                        break;
                    case "Gcash":
                        // Handle Gcash selection
                        paymentMethod = "Gcash";
                        gcashRadio.setChecked(true);
                        codRadio.setChecked(false);
                        cardRadio.setChecked(false);
                        break;
                    case "Card":
                        paymentMethod = "Card";
                        cardRadio.setChecked(true);
                        gcashRadio.setChecked(false);
                        codRadio.setChecked(false);
                        break;
                }
            }
        });


        codRadio.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Handle COD selection
                gcashRadio.setChecked(false);
                cardRadio.setChecked(false);
                paymentMethod = "COD";
                OrderViewModel.setPaymentOption(paymentMethod);
            }
        });

        gcashRadio.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Handle Gcash selection
                codRadio.setChecked(false);
                cardRadio.setChecked(false);
                paymentMethod = "Gcash";
                OrderViewModel.setPaymentOption(paymentMethod);
            }
        });

        cardRadio.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Handle Gcash selection
                codRadio.setChecked(false);
                gcashRadio.setChecked(false);
                paymentMethod = "Card";
                OrderViewModel.setPaymentOption(paymentMethod);
            }
        });





        try {
            placeOrderBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   showOrderConfirmationDialog();

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

    private void calculateTotal() {
        totalAmount = 0; // Reset totalAmount to zero
        // Iterate through each child in orderItemsLayout
        for (int i = 0; i < orderItemsLayout.getChildCount(); i++) {
            View itemView = orderItemsLayout.getChildAt(i);
            EditText quantityTextView = itemView.findViewById(R.id.order_quantity);
            TextView itemPriceTv = itemView.findViewById(R.id.order_item_price);
            TextView containerPriceTv = itemView.findViewById(R.id.new_container_price);
            TextView totalPriceTextView = itemView.findViewById(R.id.total);

            // Get the quantity entered by the user
            String quantityText = quantityTextView.getText().toString();
            int quantity = 0;

            // Parse the quantity safely
            if (!quantityText.isEmpty()) {
                quantity = Integer.parseInt(quantityText);
            }

            // Assuming you have a way to get the price for the order item
            double pricePerUnit = Double.parseDouble(itemPriceTv.getText().toString()) + Double.parseDouble(containerPriceTv.getText().toString()); // Adjust this based on your logic
            double totalPrice = quantity * pricePerUnit;

            // Update total price for this item
            totalPriceTextView.setText(String.valueOf(totalPrice));

            // Add to the overall total
            totalAmount += totalPrice;
        }

        // Update the overall subtotal and total payment
        merchSubtotal.setText(String.valueOf(totalAmount));
        totalPayment.setText(String.valueOf(totalAmount + 100)); // Add additional costs if necessary
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

                    // Prepare the date for uploading to the database in the format "YYYY-MM-DD"
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    formattedDate2 = sdf2.format(selectedDate.getTime());  // This will give you "2024-10-01"

                    // Set the selected date in the OrderViewModel
                    OrderViewModel.setSelectedDate(formattedDate); // Store selected date
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
        loader.setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(getContext());

        String url = "https://thirsttap.scarlet2.io/Backend/createOrder.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response checkout", response);
                        orderViewModel.setSelectedDate(null);
                        loader.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Error placing order!", Toast.LENGTH_SHORT).show();
                Log.d("response checkout", String.valueOf(error));
                loader.setVisibility(View.GONE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                params.put("delivery_date", formattedDate2);
                params.put("station_name", stationName);
                params.put("station_id", stationId); // Use totalPriceToPay here as well
                params.put("delivery_address", chosenAddress);
                params.put("payment_method", paymentMethod);
                params.put("address_id", String.valueOf(addressId));
                params.put("total_price", totalPayment.getText().toString().trim()); // Use totalPriceToPay here
                String additionalInfoText = additionalInfo.getText().toString().trim();
                if (!additionalInfoText.isEmpty()) {
                    params.put("additional_info", additionalInfoText);
                } else {
                    params.put("additional_info", "");
                }

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
                            orderItem.put("total_price", item.getTotalPrice()); // Use totalPriceToPay here as well

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




    private void initializeItemView(OrderItem item, EditText quantityTextView, TextView totalPriceTextView) {
        // Set up the item view with initial values
        quantityTextView.setText(String.valueOf(item.getQuantity()));
        totalPriceTextView.setText(String.valueOf(item.getTotalPrice()));
    }

    private void setItemClickListeners(OrderItem item, EditText quantityTextView, TextView totalPriceTextView, ImageButton plusBtn, ImageButton minusBtn) {
        plusBtn.setOnClickListener(v -> {
            int currentQuantity = item.getQuantity(); // Get current quantity from OrderItem
            currentQuantity++; // Increment the quantity
            item.setQuantity(currentQuantity); // Update the OrderItem
            quantityTextView.setText(String.valueOf(currentQuantity)); // Update the UI
            calculateTotal(); // Recalculate the total
        });

        minusBtn.setOnClickListener(v -> {
            int currentQuantity = item.getQuantity(); // Get current quantity from OrderItem
            currentQuantity--; // Decrement the quantity
            if (currentQuantity > 0) { // Ensure the quantity doesn't go below 1
                item.setQuantity(currentQuantity); // Update the OrderItem
                quantityTextView.setText(String.valueOf(currentQuantity)); // Update the UI
                calculateTotal(); // Recalculate the total
            } else {
                // If the quantity is 1, show the confirmation dialog
                showConfirmationDialog(item, quantityTextView, totalPriceTextView);
            }
        });
    }

    private void showConfirmationDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Clear Cart")
                .setMessage("Are you sure you want to clear your cart?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    orderViewModel.clearCart();
                })
                .setNegativeButton("No", (dialog, which) -> {

                })
                .show();
    }


    private void showConfirmationDialog(OrderItem item, EditText quantityTextView, TextView totalPriceTextView) {
        new AlertDialog.Builder(getContext())
                .setTitle("Remove Item")
                .setMessage("Are you sure you want to remove this item?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Calculate the price to subtract from the total
                    //double pricePerItem = item.getPricePerItem() + item.getNewContainerPrice();

                    double pricePerItem = Double.parseDouble(totalPriceTextView.getText().toString());
                    //double totalPriceOfItem = pricePerItem * item.getQuantity(); // Calculate total price for the quantity

                    // Update totalAmount
                    totalAmount = totalAmount - pricePerItem; // Subtract the item's total price from totalAmount

                    // Remove item from order
                    orderViewModel.removeOrderItem(item); // Ensure this method exists
                    item.setQuantity(0); // Update item quantity to 0

                    // Update UI
                    orderItemsLayout.removeView((View) quantityTextView.getParent()); // Remove the item view
                    orderViewModel.removeItemsWithZeroQuantity();  // Ensure that any items with quantity zero are removed

                    // Update the subtotals
                    merchSubtotal.setText(String.valueOf(totalAmount)); // Update subtotal display
                    totalPayment.setText(String.valueOf(totalAmount+100));

                    //updateSubtotal(); // Update subtotal display and total payment
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // Reset quantity to 1
                    item.setQuantity(1);
                    quantityTextView.setText(String.valueOf(item.getQuantity()));
                    totalPriceTextView.setText(String.valueOf(item.getTotalPrice())); // Update total price accordingly
                })
                .show();
    }

    private void showOrderConfirmationDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Confirm Order")
                .setMessage("Are you sure you want to place this order?\n\nTotal Payment: ₱"+ totalPayment.getText().toString().trim())
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Check if the date has been selected
                    if (formattedDate2 == null || formattedDate2.isEmpty()) {
                        // Show a message to the user to select a delivery date
                        Toast.makeText(getContext(), "Please select a delivery date.", Toast.LENGTH_SHORT).show();
                        return; // Prevent the order from being placed
                    }

                    // Check if all required selections have been made
                    if (!gcashRadio.isChecked() && !codRadio.isChecked() && !cardRadio.isChecked()) {
                        // Payment method is not selected
                        Toast.makeText(getContext(), "Please select a payment method.", Toast.LENGTH_SHORT).show();
                        return; // Don't proceed to the next fragment
                    }

                    List<OrderItem> currentOrderItems = orderViewModel.getOrderItems().getValue();
                    int totalQuantity = 0;
                    for (OrderItem item : currentOrderItems){
                        totalQuantity += item.getQuantity();
                    }

                    if (totalQuantity < 3) {
                        // Payment method is not selected
                        Toast.makeText(getContext(), "Please buy at least 3 gallons.", Toast.LENGTH_SHORT).show();
                        return; // Don't proceed to the next fragment
                    }


                    // If the date is selected, proceed with placing the order
                    Log.d("CheckOutFragment", "Number of order items: " + orderViewModel.getOrderItems().getValue().size());

                    chosenAddress = deliveryAddress.getText().toString().trim();//extract chosen address

                    createOrder();  // Call the method to place the order


                    OrderConfirmationPopup popupFragment = new OrderConfirmationPopup();
                    popupFragment.show(getParentFragmentManager(), "full_screen_popup");

                })
                .setNegativeButton("No", (dialog, which) -> {

                })
                .show();
    }


    private void updateSubtotal() {
        totalPriceToPay = totalAmount + 100;
        merchSubtotal.setText(String.valueOf(totalAmount)); // Update subtotal display
        totalPayment.setText(String.valueOf(totalPriceToPay));

    }

    public void fetchAddressDisplay() {
        loader.setVisibility(View.VISIBLE);
        String url = "https://thirsttap.scarlet2.io/Backend/fetchDefaultAddress.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loader.setVisibility(View.GONE);
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            // Assuming you get the following fields from the server response
                            String barangay = jsonObject.getString("barangay");
                            String street = jsonObject.getString("street");
                            String building = jsonObject.getString("building");
                            String unit = jsonObject.getString("unit");
                            String houseNum = jsonObject.getString("house_num");
                            String additional = jsonObject.getString("additional");
                            String city = jsonObject.getString("city");
                            String state = jsonObject.getString("state");
                            String postal = jsonObject.getString("postal_code");
                            defaultAddressId = jsonObject.getString("address_id");


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.d("Response", response);
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                loader.setVisibility(View.GONE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId); // Pass user ID if necessary
                return params;
            }
        };

        Volley.newRequestQueue(getContext()).add(stringRequest);
    }

}
