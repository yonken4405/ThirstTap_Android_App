package com.example.thirsttap.OrderPage;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.thirsttap.HomePage.HomeFragment;
import com.example.thirsttap.MainActivity;
import com.example.thirsttap.R;

public class OrderFragment extends Fragment {

    private ImageButton backBtn, plusBtn, minusBtn;
    private TextView counter, roundPrice, slimPrice, smallSlimPrice, quantityTv;
    private int count, quantity;
    private Button proceedBtn;
    private String  waterType, containerSize, containerStatus;
    private double gallonPrice, newContainerPrice;
    private OrderViewModel orderViewModel;
    private TextView stationAddressTextView, stationNameTextView;
    private String stationName, stationAddress, stationSchedule;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_fragment, container, false);

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
        RadioButton titleAlkaline = view.findViewById(R.id.radio_alkaline);
        RadioButton titleDistilled = view.findViewById(R.id.radio_distilled);

        // Retrieve the passed data from the Bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            stationName = bundle.getString("station_name");
            stationAddress = bundle.getString("station_address");
            stationSchedule = bundle.getString("station_schedule");

            // Display the station data on the UI
            stationNameTextView = view.findViewById(R.id.store_name);
            stationAddressTextView = view.findViewById(R.id.store_address);

            //Set the name and address based on chosen station
            stationNameTextView.setText(stationName);
            stationAddressTextView.setText(stationAddress);

        }


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
        RadioButton titleRound = view.findViewById(R.id.radio_round);
        RadioButton titleSlim = view.findViewById(R.id.radio_slim);
        RadioButton titleSmallSlim = view.findViewById(R.id.radio_small_slim);

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
        RadioButton newContainer = view.findViewById(R.id.new_container_radio);
        RadioButton exContainer = view.findViewById(R.id.ex_container_radio);
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

                // Check if all required selections have been made
                if (waterTypeGroup.getCheckedRadioButtonId() == -1) {
                    // Water type is not selected
                    // You can show a message to the user, e.g. using a Toast
                    Toast.makeText(getContext(), "Please select a water type.", Toast.LENGTH_SHORT).show();
                    return; // Don't proceed to the next fragment
                }

                if (sizeGroup.getCheckedRadioButtonId() == -1) {
                    // Container size is not selected
                    Toast.makeText(getContext(), "Please select a container size.", Toast.LENGTH_SHORT).show();
                    return; // Don't proceed to the next fragment
                }

                if (containerGroup.getCheckedRadioButtonId() == -1) {
                    // Container status is not selected
                    Toast.makeText(getContext(), "Please select a container status.", Toast.LENGTH_SHORT).show();
                    return; // Don't proceed to the next fragment
                }

                // If all selections are made, proceed with the logic
                quantity = Integer.parseInt(quantityTv.getText().toString()); // Get number of gallons

                OrderItem newItem = new OrderItem(waterType, containerSize, containerStatus, quantity, gallonPrice, newContainerPrice);
                orderViewModel.addOrderItem(newItem);

                // Prepare the station details to pass to the next fragment
                Bundle bundle = new Bundle();
                bundle.putString("station_name", stationName); // Station Name
                bundle.putString("station_address", stationAddress); // Station Address
                bundle.putString("station_schedule", stationSchedule); // Station Opening Hours

                // Proceed to the checkout fragment
                CheckOutFragment fragment = new CheckOutFragment();
                fragment.setArguments(bundle);
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
            }
        });


        return view;
    }


}
