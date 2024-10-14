package com.example.thirsttap.OrderPage;

import android.app.AlertDialog;
import android.content.Intent;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.thirsttap.HomePage.HomeFragment;
import com.example.thirsttap.MainActivity;
import com.example.thirsttap.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderFragment extends Fragment {

    private ImageButton backBtn, plusBtn, minusBtn;
    private TextView roundPrice, slimPrice, smallSlimPrice, quantityTv;
    private int count, quantity;
    private EditText counter;
    private Button proceedBtn;
    private String  waterType, containerSize, containerStatus;
    private double gallonPrice, newContainerPrice;
    private OrderViewModel orderViewModel;
    private TextView stationAddressTextView, stationNameTextView;
    private String stationName, stationAddress, stationSchedule, stationId;
    private RadioButton titleAlkaline, titleDistilled, titleRound, titleSlim, titleSmallSlim, newContainer, exContainer;
    private LinearLayout newContainerDesc, exContainerDesc;
    private ProgressBar loader;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_fragment, container, false);
        loader = view.findViewById(R.id.loader);

        // Initialize the ViewModel
        orderViewModel = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);


        proceedBtn = view.findViewById(R.id.proceed_button);
        backBtn = view.findViewById(R.id.back_button);
        plusBtn = view.findViewById(R.id.plus_button);
        minusBtn = view.findViewById(R.id.minus_button);
        counter = view.findViewById(R.id.counter_tv);
        count = 1;
        roundPrice = view.findViewById(R.id.round_container_price);
        slimPrice = view.findViewById(R.id.slim_container_price);
        smallSlimPrice = view.findViewById(R.id.small_slim_container_price);
        quantityTv = view.findViewById(R.id.counter_tv);

        RadioGroup waterTypeGroup = view.findViewById(R.id.water_type_container);
        LinearLayout descAlkaline = view.findViewById(R.id.desc_alkaline);
        LinearLayout descDistilled = view.findViewById(R.id.desc_distilled);
        titleAlkaline = view.findViewById(R.id.radio_alkaline);
        titleDistilled = view.findViewById(R.id.radio_distilled);


        // Display the station data on the UI
        stationNameTextView = view.findViewById(R.id.store_name);
        stationAddressTextView = view.findViewById(R.id.store_address);

        orderViewModel.getStationName().observe(getViewLifecycleOwner(), value -> {
            stationNameTextView.setText(value);
            stationName = value;
        });

        orderViewModel.getStationSchedule().observe(getViewLifecycleOwner(), value -> {
            stationSchedule = value;
        });

        orderViewModel.getStationAddress().observe(getViewLifecycleOwner(), value -> {
            stationAddressTextView.setText(value);
            stationAddress = value;
        });

        orderViewModel.getStationId().observe(getViewLifecycleOwner(), value -> {
            stationId = value;
            fetchWaterOptions(value);
        });


        waterTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Hide all description layouts first
                descAlkaline.setVisibility(View.GONE);
                descDistilled.setVisibility(View.GONE);

                // Reset the text of all RadioButtons
                titleAlkaline.setText("Alkaline");  // Ensure text is reset
                titleDistilled.setText("Distilled");  // Ensure text is reset

                // Show description layout based on the selected radio button
                if (checkedId == R.id.radio_alkaline) {
                    descAlkaline.setVisibility(View.VISIBLE);
                    titleAlkaline.setText("");  // Hide text by setting it to an empty string
                    waterType = "Alkaline";//get water type
                } else if (checkedId == R.id.radio_distilled) {
                    descDistilled.setVisibility(View.VISIBLE);
                    titleDistilled.setText("");  // Hide text by setting it to an empty string
                    waterType = "Distilled";
                }
            }
        });

        RadioGroup sizeGroup = view.findViewById(R.id.size_container);
        LinearLayout priceRound = view.findViewById(R.id.price_round);
        LinearLayout priceSlim = view.findViewById(R.id.price_slim);
        LinearLayout priceSmallSlim = view.findViewById(R.id.price_small_slim);
        titleRound = view.findViewById(R.id.radio_round);
        titleSlim = view.findViewById(R.id.radio_slim);
        titleSmallSlim = view.findViewById(R.id.radio_small_slim);

        sizeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Hide all description layouts first
                priceRound.setVisibility(View.GONE);
                priceSlim.setVisibility(View.GONE);
                priceSmallSlim.setVisibility(View.GONE);

                // Reset the text of all RadioButtons
                titleRound.setText("5.00 Gal Round");
                titleSlim.setText("5.00 Gal Slim");
                titleSmallSlim.setText("2.50 Gal Slim");

                if (checkedId == R.id.radio_round) {
                    priceRound.setVisibility(View.VISIBLE);
                    titleRound.setText("");
                    containerSize = "5.00 Gal Round Container";//get container size
                    gallonPrice = Double.parseDouble(roundPrice.getText().toString());
                } else if (checkedId == R.id.radio_slim) {
                    priceSlim.setVisibility(View.VISIBLE);
                    titleSlim.setText("");
                    containerSize = "5.00 Gal Slim Container";
                    gallonPrice = Double.parseDouble(slimPrice.getText().toString());
                } else if (checkedId == R.id.radio_small_slim) {
                    priceSmallSlim.setVisibility(View.VISIBLE);
                    titleSmallSlim.setText("");
                    containerSize = "2.50 Gal Slim Container";
                    gallonPrice = Double.parseDouble(smallSlimPrice.getText().toString());
                }
            }
        });

        RadioGroup containerGroup = view.findViewById(R.id.container_group);
        newContainer = view.findViewById(R.id.new_container_radio);
        exContainer = view.findViewById(R.id.ex_container_radio);
        newContainerDesc = view.findViewById(R.id.new_container_desc);
        exContainerDesc = view.findViewById(R.id.exchange_container_desc);

        containerGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.new_container_radio) {
                    containerStatus = "New Container";//know if new gallons or old
                    newContainerPrice = 100;

                } else if (checkedId == R.id.ex_container_radio) {
                    containerStatus = "Exchange Container";
                    newContainerPrice = 0;
                }
            }
        });



        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StationSelection fragment = new StationSelection();
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
            }
        });

        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count > 0){
                    count += 1;
                    counter.setText(String.valueOf(count));
                }

            }
        });

        minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count > 1){
                    count -= 1;
                    counter.setText(String.valueOf(count));
                }
            }
        });



        proceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<OrderItem> items = orderViewModel.getCurrentOrderItems();
                Log.d("Current Order Items", items.toString());

                if (items == null || items.isEmpty()) {
                    // The cart is empty; user must create a new order item

                    // Check if all required selections have been made
                    if (waterTypeGroup.getCheckedRadioButtonId() == -1) {
                        // Water type is not selected
                        Toast.makeText(getContext(), "Please select a water type", Toast.LENGTH_SHORT).show();
                        return; // Don't proceed to the next fragment
                    }

                    if (sizeGroup.getCheckedRadioButtonId() == -1) {
                        // Container size is not selected
                        Toast.makeText(getContext(), "Please select a container size", Toast.LENGTH_SHORT).show();
                        return; // Don't proceed to the next fragment
                    }

                    if (containerGroup.getCheckedRadioButtonId() == -1) {
                        // Container status is not selected
                        Toast.makeText(getContext(), "Please select a container status", Toast.LENGTH_SHORT).show();
                        return; // Don't proceed to the next fragment
                    }

                    // All selections are made, proceed with adding a new order item
                    quantity = Integer.parseInt(quantityTv.getText().toString()); // Get number of gallons

                    // Create a new OrderItem with the selected details
                    OrderItem newItem = new OrderItem(waterType, containerSize, containerStatus, quantity, gallonPrice, newContainerPrice);

                    // Add the new item to the order
                    orderViewModel.addOrderItem(newItem);
                    Toast.makeText(getContext(), "New order added", Toast.LENGTH_SHORT).show();

                    goToCheckout();

                } else {
                    // The user has already added an item to the cart
                    // Ask if they want to add another item or proceed
                    if (waterTypeGroup.getCheckedRadioButtonId() == -1 || sizeGroup.getCheckedRadioButtonId() == -1 || containerGroup.getCheckedRadioButtonId() == -1) {
                        new AlertDialog.Builder(getContext())
                                .setTitle("No new order added")
                                .setMessage("You already have items in the cart. Do you want to proceed without adding a new order?")
                                .setPositiveButton("Proceed", (dialog, which) -> {
                                    // User chose to proceed without adding a new order
                                    Toast.makeText(getContext(), "Proceeding without adding new order", Toast.LENGTH_SHORT).show();

                                    goToCheckout();
                                })
                                .setNegativeButton("Cancel", (dialog, which) -> {
                                    // User canceled, do nothing and remain on the current screen
                                    dialog.dismiss();
                                })
                                .show();
                    } else {
                        // All selections are made, proceed with adding a new order item
                        quantity = Integer.parseInt(quantityTv.getText().toString()); // Get number of gallons

                        // Create a new OrderItem with the selected details
                        OrderItem newItem = new OrderItem(waterType, containerSize, containerStatus, quantity, gallonPrice, newContainerPrice);

                        // Add the new item to the order
                        orderViewModel.addOrderItem(newItem);
                        Toast.makeText(getContext(), "New order added", Toast.LENGTH_SHORT).show();

                        goToCheckout();
                    }

                }



            }
        });


        return view;
    }

    private void goToCheckout(){
        // Prepare the station details to pass to the next fragment
        Bundle bundle = new Bundle();
        bundle.putString("station_name", stationName); // Station Name
        bundle.putString("station_address", stationAddress); // Station Address
        bundle.putString("station_schedule", stationSchedule); // Station Opening Hours
        bundle.putString("station_id", stationId); // Station Id


        // Proceed to the checkout fragment
        CheckOutFragment fragment = new CheckOutFragment();
        fragment.setArguments(bundle);
        getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
    }




    private void fetchWaterOptions(String stationId) {
        loader.setVisibility(View.VISIBLE);

        String url = "https://thirsttap.scarlet2.io/Backend/fetchWaterOptions.php"; // Replace with your PHP script URL

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loader.setVisibility(View.GONE);
                        Log.d("fetchWaterOptions Response", response); // Log the raw response

                        // Check if the response is valid
                        if (response == null || response.isEmpty()) {
                            Log.e("Response Error", "Received an empty response");
                            return;
                        }

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            updateRadioButtons(jsonArray);
                            Log.d("stationId on orderFrag", response);
                        } catch (JSONException e) {
                            Log.e("JSON Error", "Error parsing JSON: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loader.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Error fetching options: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("station_id", stationId); // Pass the station ID to the PHP script
                return params;
            }
        };

        Log.d("stationId on orderFrag", stationId);
        Volley.newRequestQueue(getContext()).add(stringRequest);
    }


    private void updateRadioButtons(JSONArray jsonArray) throws JSONException {
        // Assuming you have RadioButtons for each type and size
        titleAlkaline.setVisibility(View.GONE);
        titleDistilled.setVisibility(View.GONE);
        titleRound.setVisibility(View.GONE);
        titleSlim.setVisibility(View.GONE);
        titleSmallSlim.setVisibility(View.GONE);
        newContainer.setVisibility(View.GONE);
        exContainer.setVisibility(View.GONE);
        newContainerDesc.setVisibility(View.GONE);
        exContainerDesc.setVisibility(View.GONE);

        for (int i = 0; i < jsonArray.length(); i++) {
            String option = jsonArray.getString(i);
            switch (option) {
                case "Alkaline Water":
                    titleAlkaline.setVisibility(View.VISIBLE);
                    break;
                case "Distilled Water":
                    titleDistilled.setVisibility(View.VISIBLE);
                    break;
                case "Round 5 Gallon":
                    titleRound.setVisibility(View.VISIBLE);
                    break;
                case "Slim 5 Gallon":
                    titleSlim.setVisibility(View.VISIBLE);
                    break;
                case "Slim 2.5 Gallon":
                    titleSmallSlim.setVisibility(View.VISIBLE);
                    break;
                case "New Container":
                    newContainer.setVisibility(View.VISIBLE);
                    newContainerDesc.setVisibility(View.VISIBLE);
                    break;
                case "Exchange Container":
                    exContainer.setVisibility(View.VISIBLE);
                    exContainerDesc.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }







}
