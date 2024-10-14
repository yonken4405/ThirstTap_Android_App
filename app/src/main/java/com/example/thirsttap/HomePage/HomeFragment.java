package com.example.thirsttap.HomePage;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.thirsttap.AccountPage.WorkWithUsFragment;
import com.example.thirsttap.AddressesPage.Address;
import com.example.thirsttap.AddressesPage.AddressListFragment;
import com.example.thirsttap.OrderPage.OrderFragment;
import com.example.thirsttap.OrderPage.StationSelection;
import com.example.thirsttap.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {
    private Button orderNow;
    private TextView addressDisplay, nameDisplay;
    private String userId, email, name, phoneNum;
    private LinearLayout ad1;
    private ProgressBar loader;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.home_screen_fragment, container, false);
        loader = view.findViewById(R.id.loader);

        orderNow = view.findViewById(R.id.order_now_button);
        addressDisplay = view.findViewById(R.id.address_display);
        nameDisplay = view.findViewById(R.id.user_name);
        ad1 = view.findViewById(R.id.partner_ad);


        // Retrieve user profile data from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_profile", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userid", "default_userid");
        email = sharedPreferences.getString("email", "default_email");
        name = sharedPreferences.getString("name", "default_name");
        phoneNum = sharedPreferences.getString("phone_num", "default_phone_num");
        boolean isNewUser = sharedPreferences.getBoolean("isNewUser", false);
        Log.d("userData", userId+" "+email+" "+ name +" "+phoneNum +" "+isNewUser);

        fetchAddressDisplay();//set the address to the default one
        nameDisplay.setText(name);


        addressDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddressListFragment fragment = new AddressListFragment();
                Bundle args = new Bundle();
                args.putString("sourceFragment", "homeFragment"); // Pass the source fragment
                fragment.setArguments(args);
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
            }
        });


        orderNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StationSelection fragment = new StationSelection();
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
            }
        });

        ad1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WorkWithUsFragment fragment = new WorkWithUsFragment();
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
            }
        });



        return view;
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

                            // Create an Address object
                            Address address = new Address(
                                    barangay, street, building, unit, houseNum, additional, city, state, postal
                            );

                            // Format the address using the formatAddress method
                            String formattedAddress = Address.defaultAddress(address);

                            // Update the TextView with the formatted address
                            addressDisplay.setText(formattedAddress);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.d("Response", response);
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loader.setVisibility(View.GONE);
                error.printStackTrace();
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
