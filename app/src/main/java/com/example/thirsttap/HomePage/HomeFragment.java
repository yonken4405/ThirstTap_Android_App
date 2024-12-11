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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.thirsttap.AccountPage.WorkWithUsFragment;
import com.example.thirsttap.AddressesPage.Address;
import com.example.thirsttap.AddressesPage.AddressListFragment;
import com.example.thirsttap.NotificationPage.Notification;
import com.example.thirsttap.NotificationPage.NotificationFragment;
import com.example.thirsttap.OrderPage.OrderFragment;
import com.example.thirsttap.OrderPage.StationSelection;
import com.example.thirsttap.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {
    private Button orderNow;
    private TextView addressDisplay, nameDisplay;
    private String userId, email, name, phoneNum;
    private LinearLayout ad1;
    private ProgressBar loader;
    private ImageButton notifBtn;
    private TextView notificationCounter;


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
        notifBtn = view.findViewById(R.id.notification_btn);
        notificationCounter = view.findViewById(R.id.notification_counter); // Adjust this ID to match your layout
        notificationCounter.setVisibility(View.GONE); // Initially hide the counter


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

        fetchNotifications(Integer.parseInt(userId));


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


        notifBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the notification fragment

                NotificationFragment fragment = new NotificationFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("userId", Integer.parseInt(userId)); // Pass userId to the fragment
                fragment.setArguments(bundle);
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
            }
        });



        return view;
    }

    private void updateNotificationCounter(int unreadCount) {
        if (unreadCount > 0) {
            notificationCounter.setVisibility(View.VISIBLE);
            notificationCounter.setText(String.valueOf(unreadCount)); // Show the count
        } else {
            notificationCounter.setVisibility(View.GONE); // Hide if no unread notifications
        }
    }

    private void fetchNotifications(int userId) {
        loader.setVisibility(View.VISIBLE);
        String url = "https://thirsttap.scarlet2.io/Backend/fetchNotifications.php"; // Replace with your API endpoint
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + "?user_id=" + userId, // Change 'userId' to 'user_id'
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("HomeFragment", "Before processing response");
                        loader.setVisibility(View.GONE);
                        try {
                            // Check if the response indicates an error
                            if (response.contains("error")) {
                                Log.e("HomeFragment", "Error in response: " + response);
                                return;
                            }

                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.getString("status").equals("success")) {
                                JSONArray jsonArray = jsonResponse.getJSONArray("notifications");
                                int unreadCount = 0; // Counter for unread notifications
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
                                    if (notification.getStatus().equals("sent")) {
                                        unreadCount++; // Increment if notification is unread
                                    }
                                }
                                updateNotificationCounter(unreadCount); // Update the bell icon counter
                            } else {
                                Log.e("HomeFragment", "Error in response: " + jsonResponse.getString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("HomeFragment", "JSON parsing error: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Log.e("HomeFragment", "Volley error: " + error.getMessage());
                        loader.setVisibility(View.GONE);
                    }
                });

        // Add the request to the queue
        Volley.newRequestQueue(getContext()).add(stringRequest);
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
