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
    private String  waterType, containerSize, containerStatus, isSelectedSize;
    private double gallonPrice, newContainerPrice, fetchedNewContainerPrice;
    private OrderViewModel orderViewModel;
    private TextView stationAddressTextView, stationNameTextView;
    private String stationName, stationAddress, stationSchedule, stationId;
    private RadioButton titleSpring, titleAlkaline, titlePurified, titleMineral, titleRound, titleSlim, titleSmallSlim, newContainer, exContainer;
    private LinearLayout newContainerDesc, exContainerDesc, loader, priceRound, priceSlim, priceSmallSlim;
    private boolean isWaterTypeEnabled, isRound5GalEnabled , isSlim5GalEnabled , isSlim2_5GalEnabled ,isNewContainerEnabled ,isExchangeContainerEnabled;


    // Use a HashMap to store prices for different water types
    private Map<String, Double> priceMap = new HashMap<>();
    private Map<String, Boolean> availabilityMap = new HashMap<>();



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
        LinearLayout descSpring = view.findViewById(R.id.desc_spring);
        LinearLayout descAlkaline = view.findViewById(R.id.desc_alkaline);
        LinearLayout descPurified = view.findViewById(R.id.desc_purified);
        LinearLayout descMineral = view.findViewById(R.id.desc_mineral);
        titleSpring = view.findViewById(R.id.radio_spring); // Added for Spring water
        titleAlkaline = view.findViewById(R.id.radio_alkaline);
        titlePurified = view.findViewById(R.id.radio_purified);
        titleMineral = view.findViewById(R.id.radio_mineral);
        titleRound = view.findViewById(R.id.radio_round);
        titleSlim = view.findViewById(R.id.radio_slim);
        titleSmallSlim = view.findViewById(R.id.radio_small_slim);

        RadioGroup containerGroup = view.findViewById(R.id.container_group);
        newContainerDesc = view.findViewById(R.id.new_container_desc);
        exContainerDesc = view.findViewById(R.id.exchange_container_desc);
        newContainer = view.findViewById(R.id.new_container_radio);
        exContainer = view.findViewById(R.id.ex_container_radio);


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

        priceRound = view.findViewById(R.id.price_round);
        priceSlim = view.findViewById(R.id.price_slim);
        priceSmallSlim = view.findViewById(R.id.price_small_slim);

//        waterTypeGroup.setOnCheckedChangeListener((group, checkedId) -> {
//            // Hide all description layouts first
//            descSpring.setVisibility(View.GONE);
//            descAlkaline.setVisibility(View.GONE);
//            descPurified.setVisibility(View.GONE);
//            descMineral.setVisibility(View.GONE);
//
//            isSelectedSize.equals("Slim2_5gal");
//
//            // Check if isSelectedSize is null to prevent NullPointerException
//            if (isSelectedSize != null) {
//                priceRound.setVisibility(isSelectedSize.equals("5gal") ? View.VISIBLE : View.GONE);
//                priceSlim.setVisibility(isSelectedSize.equals("Slim5gal") ? View.VISIBLE : View.GONE);
//                priceSmallSlim.setVisibility(isSelectedSize.equals("Slim2_5gal") ? View.VISIBLE : View.GONE);
//            }
//
//
//            // Reset the text of all RadioButtons
//            titleSpring.setText("Spring");
//            titleAlkaline.setText("Alkaline");
//            titlePurified.setText("Purified");
//            titleMineral.setText("Mineral");
//
//
//            // Show description layout based on the selected radio button
//            if (checkedId == R.id.radio_spring) {
//                descSpring.setVisibility(View.VISIBLE);
//                titleSpring.setText("");
//                waterType = "spring"; // Get water type
//            } else if (checkedId == R.id.radio_alkaline) {
//                descAlkaline.setVisibility(View.VISIBLE);
//                titleAlkaline.setText("");
//                waterType = "alkaline";
//            } else if (checkedId == R.id.radio_purified) {
//                descPurified.setVisibility(View.VISIBLE);
//                titlePurified.setText("");
//                waterType = "purified";
//            } else if (checkedId == R.id.radio_mineral) {
//                descMineral.setVisibility(View.VISIBLE);
//                titleMineral.setText("");
//                waterType = "mineral";
//            }
//
//            // Update prices based on the new water type
//            updatePrices(waterType);
//
//            // Update visibility of container options
//            updateContainerOptionsVisibility(waterType);
//
//
//
//        });

        RadioGroup sizeGroup = view.findViewById(R.id.size_container);


        waterTypeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // Hide all description layouts
            descSpring.setVisibility(View.GONE);
            descAlkaline.setVisibility(View.GONE);
            descPurified.setVisibility(View.GONE);
            descMineral.setVisibility(View.GONE);

            // Reset size-related visibility and state
            isSelectedSize = null;
            sizeGroup.clearCheck();
            priceRound.setVisibility(View.GONE);
            priceSlim.setVisibility(View.GONE);
            priceSmallSlim.setVisibility(View.GONE);

            // Use if-else instead of switch-case
            if (checkedId == R.id.radio_spring) {
                descSpring.setVisibility(View.VISIBLE);
                titleSpring.setText("");
                waterType = "spring";
            } else if (checkedId == R.id.radio_alkaline) {
                descAlkaline.setVisibility(View.VISIBLE);
                titleAlkaline.setText("");
                waterType = "alkaline";
            } else if (checkedId == R.id.radio_purified) {
                descPurified.setVisibility(View.VISIBLE);
                titlePurified.setText("");
                waterType = "purified";
            } else if (checkedId == R.id.radio_mineral) {
                descMineral.setVisibility(View.VISIBLE);
                titleMineral.setText("");
                waterType = "mineral";
            } else {
                waterType = null;
            }

            // Update prices and visibility based on the new water type
            updatePrices(waterType);

            // Reset container options visibility
            updateContainerOptionsVisibility(waterType);
        });





//        // Inside the sizeGroup listener
//        sizeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                // Hide all description layouts first
//                priceRound.setVisibility(View.GONE);
//                priceSlim.setVisibility(View.GONE);
//                priceSmallSlim.setVisibility(View.GONE);
//
//                // Reset the text of all RadioButtons
//                titleRound.setText("5.00 Gal Round");
//                titleSlim.setText("5.00 Gal Slim");
//                titleSmallSlim.setText("2.50 Gal Slim");
//
//                if (checkedId == R.id.radio_round) {
//                    // Hide all description layouts first
//                    priceRound.setVisibility(isRound5GalEnabled ? View.VISIBLE : View.GONE);
//                    priceSlim.setVisibility(View.GONE);
//                    priceSmallSlim.setVisibility(View.GONE);
//
//                    checkedId = isRound5GalEnabled ? checkedId : 0;
//
//                    titleRound.setText("");
//                    containerSize = "5.00 Gal Round Container"; // Get container size
//                    gallonPrice = priceMap.get(waterType + "_5gallon");
//                    roundPrice.setText(String.valueOf(gallonPrice)); // Update price display
//
//
//                } else if (checkedId == R.id.radio_slim) {
//                    // Hide all description layouts first
//                    priceRound.setVisibility(View.GONE);
//                    priceSlim.setVisibility(isSlim5GalEnabled ? View.VISIBLE : View.GONE);
//                    priceSmallSlim.setVisibility(View.GONE);
//
//                    checkedId = isSlim5GalEnabled ? checkedId : 0;
//
//                    titleSlim.setText("");
//                    containerSize = "5.00 Gal Slim Container";
//                    gallonPrice = priceMap.get(waterType + "_slim5gallon");
//                    slimPrice.setText(String.valueOf(gallonPrice)); // Update price display
//                } else if (checkedId == R.id.radio_small_slim) {
//                    // Hide all description layouts first
//                    priceRound.setVisibility(View.GONE);
//                    priceSlim.setVisibility(View.GONE);
//                    priceSmallSlim.setVisibility(isSlim2_5GalEnabled ? View.VISIBLE : View.GONE);
//
//                    checkedId = isSlim2_5GalEnabled ? checkedId : 0;
//
//                    titleSmallSlim.setText("");
//                    containerSize = "2.50 Gal Slim Container";
//                    gallonPrice = priceMap.get(waterType + "_slim2_5gallon");
//                    smallSlimPrice.setText(String.valueOf(gallonPrice)); // Update price display
//                } else {
//                    // Hide all description layouts first
//                    priceRound.setVisibility(View.GONE);
//                    priceSlim.setVisibility(View.GONE);
//                    priceSmallSlim.setVisibility(View.GONE);
//                }
//            }
//        });

        // Inside the sizeGroup listener
        sizeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // Hide all price layouts by default
            priceRound.setVisibility(View.GONE);
            priceSlim.setVisibility(View.GONE);
            priceSmallSlim.setVisibility(View.GONE);

            // Reset the text of all RadioButtons
            titleRound.setText("5.00 Gal Round");
            titleSlim.setText("5.00 Gal Slim");
            titleSmallSlim.setText("2.50 Gal Slim");

            // Use if-else instead of switch-case
            if (checkedId == R.id.radio_round) {
                if (isRound5GalEnabled) {
                    priceRound.setVisibility(View.VISIBLE);
                    isSelectedSize = "5gal";
                    titleRound.setText("");
                    containerSize = "5.00 Gal Round Container";

                    Double price = priceMap.get(waterType + "_5gallon");
                    gallonPrice = price != null ? price : 0.0; // Default to 0.0 if null
                    roundPrice.setText(String.valueOf(gallonPrice));
                } else {
                    isSelectedSize = null;
                }
            } else if (checkedId == R.id.radio_slim) {
                if (isSlim5GalEnabled) {
                    priceSlim.setVisibility(View.VISIBLE);
                    isSelectedSize = "Slim5gal";
                    titleSlim.setText("");
                    containerSize = "5.00 Gal Slim Container";

                    Double price = priceMap.get(waterType + "_slim5gallon");
                    gallonPrice = price != null ? price : 0.0;
                    slimPrice.setText(String.valueOf(gallonPrice));
                } else {
                    isSelectedSize = null;
                }
            } else if (checkedId == R.id.radio_small_slim) {
                if (isSlim2_5GalEnabled) {
                    priceSmallSlim.setVisibility(View.VISIBLE);
                    isSelectedSize = "Slim2_5gal";
                    titleSmallSlim.setText("");
                    containerSize = "2.50 Gal Slim Container";

                    Double price = priceMap.get(waterType + "_slim2_5gallon");
                    gallonPrice = price != null ? price : 0.0;
                    smallSlimPrice.setText(String.valueOf(gallonPrice));
                } else {
                    isSelectedSize = null;
                }
            } else {
                // Clear selection if no valid size is selected
                isSelectedSize = null;
            }
        });



        containerGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.new_container_radio) {
                    containerStatus = "New Container";//know if new gallons or old
                    newContainerPrice = fetchedNewContainerPrice;

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
                    count = Integer.valueOf(counter.getText().toString().trim());
                    counter.setText(String.valueOf(count+1));
                }

            }
        });

        minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count > 1){
                    count = Integer.valueOf(counter.getText().toString().trim());
                    counter.setText(String.valueOf(count-1));
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
                    Toast.makeText(getContext(), "Item added to cart!", Toast.LENGTH_SHORT).show();

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
        bundle.putString("station_name", stationName);
        bundle.putString("station_address", stationAddress);
        bundle.putString("station_schedule", stationSchedule);
        bundle.putString("station_id", stationId);
        bundle.putDouble("gallon_price", gallonPrice); // Add gallon price
        bundle.putDouble("new_container_price", newContainerPrice); // Add new container price

        // Proceed to the checkout fragment
        CheckOutFragment fragment = new CheckOutFragment();
        fragment.setArguments(bundle);
        getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
    }


    private void fetchWaterOptions(String stationId) {
        // Construct the URL for the API request
        String url = "https://thirsttap.scarlet2.io/Backend/fetchWaterOptions.php?station_id=" + stationId;

        // Show loader while fetching data
        loader.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, response -> {
            Log.d("Response", response);
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString("status");

                if (status.equals("success")) {
                    JSONArray optionsArray = jsonObject.getJSONArray("options");

                    // Clear any existing prices in the priceMap before fetching new options
                    priceMap.clear();

                    for (int i = 0; i < optionsArray.length(); i++) {
                        JSONObject option = optionsArray.getJSONObject(i);

                        String waterType = option.getString("water_type").toLowerCase(); // Ensure consistent casing

                        // Check for null before processing
                        if (waterType == null) {
                            Log.e("WaterTypeError", "Water type is null for index: " + i);
                            continue; // Skip this iteration if waterType is null
                        }

                        // Fetch and convert to boolean
                        isRound5GalEnabled = option.getInt("is_round_5gal_enabled") == 1;
                        isSlim5GalEnabled = option.getInt("is_slim_5gal_enabled") == 1;
                        isSlim2_5GalEnabled = option.getInt("is_slim_2_5gal_enabled") == 1;
                        isNewContainerEnabled = option.getInt("is_new_container_enabled") == 1;
                        isExchangeContainerEnabled = option.getInt("is_exchange_container_enabled") == 1;

                        // Store visibility states in the availabilityMap for different container types
                        availabilityMap.put(waterType + "_isRound5GalEnabled", isRound5GalEnabled);
                        availabilityMap.put(waterType + "_isSlim5GalEnabled", isSlim5GalEnabled);
                        availabilityMap.put(waterType + "_isSlim2_5GalEnabled", isSlim2_5GalEnabled);
                        availabilityMap.put(waterType + "_isNewContainerEnabled", isNewContainerEnabled);
                        availabilityMap.put(waterType + "_isExchangeContainerEnabled", isExchangeContainerEnabled);

                        // Fetch is_water_type_enabled as an integer and convert to boolean
                        int isWaterTypeEnabledInt = option.getInt("is_water_type_enabled");
                        isWaterTypeEnabled = (isWaterTypeEnabledInt == 1); // Convert to boolean

                        double price5Gallon = option.getDouble("price_5gallon");
                        double priceSlim5Gallon = option.getDouble("price_slim_5gallon");
                        double priceSlim2_5Gallon = option.getDouble("price_slim_2_5gallon");
                        double newContainerPrice = option.getDouble("new_container_price");

                        // Store prices in the priceMap for different container types
                        priceMap.put(waterType + "_5gallon", price5Gallon);
                        priceMap.put(waterType + "_slim5gallon", priceSlim5Gallon);
                        priceMap.put(waterType + "_slim2_5gallon", priceSlim2_5Gallon);
                        priceMap.put(waterType + "_newContainer", newContainerPrice);


                        // Store visibility state for each water type
                        updateWaterTypeVisibility(waterType, option.getInt("is_water_type_enabled") == 1);

                        // Update visibility of container options
                        updateContainerOptionsVisibility(waterType);
                    }


                    // Optionally, initialize the UI with a default water type
                    String defaultWaterType = "purified"; // Set default type
                    updatePrices(defaultWaterType);

                } else {
                    Toast.makeText(getContext(), "Failed to fetch water options", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            } finally {
                loader.setVisibility(View.GONE); // Hide loader
            }
        }, error -> {
            loader.setVisibility(View.GONE); // Hide loader on error
            Log.e("Error", error.toString());
            Toast.makeText(getContext(), "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        }) {
            // Using GET method, so no need for getParams()
        };

        // Add the request to the Volley request queue
        Volley.newRequestQueue(getContext()).add(stringRequest);
    }

    // Update visibility of container options based on their availability
    private void updateContainerOptionsVisibility(String waterType) {
        // Fetch availability for all container types from the availabilityMap
        Boolean isRound5GalEnabled = availabilityMap.get(waterType + "_isRound5GalEnabled");
        Boolean isSlim5GalEnabled = availabilityMap.get(waterType + "_isSlim5GalEnabled");
        Boolean isSlim2_5GalEnabled = availabilityMap.get(waterType + "_isSlim2_5GalEnabled");
        Boolean isNewContainerEnabled = availabilityMap.get(waterType + "_isNewContainerEnabled");
        Boolean isExchangeContainerEnabled = availabilityMap.get(waterType + "_isExchangeContainerEnabled");

        // Update size RadioButtons based on fetched booleans
        titleRound.setVisibility(isRound5GalEnabled != null && isRound5GalEnabled ? View.VISIBLE : View.GONE);
        titleSlim.setVisibility(isSlim5GalEnabled != null && isSlim5GalEnabled ? View.VISIBLE : View.GONE);
        titleSmallSlim.setVisibility(isSlim2_5GalEnabled != null && isSlim2_5GalEnabled ? View.VISIBLE : View.GONE);


        // Update container option RadioButtons based on fetched booleans
        newContainer.setVisibility(isNewContainerEnabled != null && isNewContainerEnabled ? View.VISIBLE : View.GONE);
        newContainerDesc.setVisibility(isNewContainerEnabled != null && isNewContainerEnabled ? View.VISIBLE : View.GONE);

        exContainer.setVisibility(isExchangeContainerEnabled != null && isExchangeContainerEnabled ? View.VISIBLE : View.GONE);
        exContainerDesc.setVisibility(isExchangeContainerEnabled != null && isExchangeContainerEnabled ? View.VISIBLE : View.GONE);
    }




    // Update prices based on selected water type
    private void updatePrices(String waterType) {
        // Fetch prices for all container types from the priceMap
        Double price5Gallon = priceMap.get(waterType + "_5gallon");
        Double priceSlim5Gallon = priceMap.get(waterType + "_slim5gallon");
        Double priceSlim2_5Gallon = priceMap.get(waterType + "_slim2_5gallon");
        Double newContainerPrice = priceMap.get(waterType + "_newContainer");

        // Update the UI with the prices if they are not null
        if (price5Gallon != null) {
            roundPrice.setText(String.valueOf(price5Gallon)); // Update price for round container
        } else {
            roundPrice.setText("N/A"); // Handle case when price is null
        }

        if (priceSlim5Gallon != null) {
            slimPrice.setText(String.valueOf(priceSlim5Gallon)); // Update price for slim container
        } else {
            slimPrice.setText("N/A"); // Handle case when price is null
        }

        if (priceSlim2_5Gallon != null) {
            smallSlimPrice.setText(String.valueOf(priceSlim2_5Gallon)); // Update price for small slim container
        } else {
            smallSlimPrice.setText("N/A"); // Handle case when price is null
        }

        if (newContainerPrice != null) {
            fetchedNewContainerPrice = newContainerPrice; // Assuming you have a TextView for new container price
            // You might want to update a TextView here as well
        } else {
            // Handle case when new container price is null
            fetchedNewContainerPrice = 0.0; // or however you want to handle it
        }
    }


    private void updateWaterTypeVisibility(String waterType, boolean isWaterTypeEnabled) {
        if (waterType.equals("spring")) {
            titleSpring.setVisibility(isWaterTypeEnabled ? View.VISIBLE : View.GONE);
        } else if (waterType.equals("alkaline")) {
            titleAlkaline.setVisibility(isWaterTypeEnabled ? View.VISIBLE : View.GONE);
        } else if (waterType.equals("purified")) {
            titlePurified.setVisibility(isWaterTypeEnabled ? View.VISIBLE : View.GONE);
        } else if (waterType.equals("mineral")) {
            titleMineral.setVisibility(isWaterTypeEnabled ? View.VISIBLE : View.GONE);
        }
    }



}
