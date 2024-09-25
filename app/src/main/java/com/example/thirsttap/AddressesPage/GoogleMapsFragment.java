package com.example.thirsttap.AddressesPage;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.thirsttap.HomePage.HomeFragment;
import com.example.thirsttap.Login.LoginBottomSheet;
import com.example.thirsttap.MainActivity;
import com.example.thirsttap.R;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.*;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GoogleMapsFragment extends Fragment implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private BottomSheetDialog bottomSheetDialog;
    private TextView loc;
    private Marker currentMarker;
    private String userId; // Example user ID
    private String url = "https://thirsttap.scarlet2.io/Backend/store_address.php";

    // Variables to store address details
    private double currentLatitude;
    private double currentLongitude;
    private String addressLine1;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String addressLine, house, additional, building, unit, street, barangay, structure;
    private boolean isNewUser;
    private String email;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_google_maps, container, false);


        // Retrieve user profile data from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_profile", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userid", "default_userid");
        email = sharedPreferences.getString("email", "default_email");
        String name = sharedPreferences.getString("name", "default_name");
        String phoneNum = sharedPreferences.getString("phone_num", "default_phone_num");
        isNewUser = sharedPreferences.getBoolean("isNewUser", false);

        // Use the retrieved data
        Log.d("AnotherFragment", "UserID: " + userId);
        Log.d("AnotherFragment", "Email: " + email);
        Log.d("AnotherFragment", "Name: " + name);
        Log.d("AnotherFragment", "PhoneNum: " + phoneNum);
        Log.d("AnotherFragment", "Is New User: " + isNewUser);

        // Initialize the map
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Set up the bottom sheet
        openBottomSheet1(view);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        // Check location permission
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }

        // Initialize marker with default position (0, 0) or make it null initially
        LatLng defaultLatLng = new LatLng(0, 0);
        currentMarker = googleMap.addMarker(new MarkerOptions().position(defaultLatLng).title("Drag the map to move the pin"));

        // Listen for camera idle (when user stops moving the map)
        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                // Get the current center position of the map
                LatLng target = googleMap.getCameraPosition().target;

                // Update class variables for latitude and longitude
                currentLatitude = target.latitude;
                currentLongitude = target.longitude;

                updateAddress(target.latitude, target.longitude);

                // Update marker position if it exists
                if (currentMarker != null) {
                    currentMarker.setPosition(target);
                }
            }
        });
    }

    // Get current location
    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f));

                    // Update marker position
                    if (currentMarker != null) {
                        currentMarker.setPosition(currentLatLng);
                    } else {
                        // In case the marker is not yet initialized
                        currentMarker = googleMap.addMarker(new MarkerOptions().position(currentLatLng).title("Drag to move the pin"));
                    }

                    // Update address
                    updateAddress(currentLatLng.latitude, currentLatLng.longitude);
                } else {
                    Toast.makeText(requireContext(), "Unable to get location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Update address based on latitude and longitude on the bottom sheet
    private void updateAddress(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(requireContext());
        try {
            List<android.location.Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                android.location.Address address = addresses.get(0);
                addressLine = address.getAddressLine(0);

//                // Extract the address components
//                String featureName = address.getFeatureName(); // Name of the feature, e.g., street
//                String subLocality = address.getSubLocality(); // Barangay or other sub-locality
//                String locality = address.getLocality(); // City or town
//                String adminArea = address.getAdminArea(); // Province
//                String postalCode = address.getPostalCode();
//                String country = address.getCountryName();

//                Log.d("Address", featureName+" "+subLocality+" "+ locality+" "+ adminArea+" "+postalCode +" "+country);


                // Update the bottom sheet with new details
                if (loc != null) {
                    loc.setText(addressLine);
                    country = address.getCountryName();

                    // Show the bottom sheet
                    if (bottomSheetDialog != null) {
                        bottomSheetDialog.show();
                    }

                    // Check if the address is in the Philippines
                    if ("Philippines".equalsIgnoreCase(country)) {
                        //addressLine1 = address.getAddressLine(0);
                        city = address.getLocality();
                        state = address.getAdminArea();
                        postalCode = address.getPostalCode();

                        // Send the address information to your PHP backend
                        //sendAddressToServer(latitude, longitude, addressLine1, city, state, postalCode, country);
                    } else {
                        Toast.makeText(requireContext(), "Address is not within the Philippines.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("GoogleMaps", "Bottom sheet views are not initialized properly.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error getting address", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendAddressToServer(double latitude, double longitude, String barangay, String street, String building, String unit, String house, String additional) {
        // Create a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        // Create a request body
        Map<String, String> params = new HashMap<>();
        params.put("latitude", String.format(Locale.US, "%.8f", latitude));
        params.put("longitude", String.format(Locale.US, "%.8f", longitude));
        params.put("barangay", barangay);
        params.put("street", street);
        params.put("building", building);
        params.put("unit", unit);
        params.put("house", house);
        params.put("additional", additional);
        params.put("city", city);
        params.put("state", state);
        params.put("postal_code", postalCode);
        params.put("country", country);
        params.put("type", structure != null ? structure : "default"); // Handle null
        params.put("user_id", userId); // Make sure to replace with actual user ID

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the response (which might not be JSON)
                        Log.d("Response", response);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                Toast.makeText(requireContext(), "Address saved successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                String message = jsonResponse.getString("message");
                                Toast.makeText(requireContext(), "Failed to save address: " + message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e("VolleyError", "Error saving address: " + e.toString());
                            e.printStackTrace();
                            Toast.makeText(requireContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Log.e("VolleyError", error.toString());
                        Toast.makeText(requireContext(), "Error saving address", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };

        Log.d("GoogleMaps", "Sending latitude: " + latitude + ", longitude: " + longitude);
        Log.d("CheckOutFragment", "Params: " + params.toString());

        // Add the request to the RequestQueue
        requestQueue.add(stringRequest);
    }


    private void openBottomSheet1(View view) {
        // Check if a BottomSheetDialog is already open and dismiss it
        if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
            bottomSheetDialog.dismiss();
        }

        bottomSheetDialog = new BottomSheetDialog(requireContext());
        bottomSheetDialog.setContentView(R.layout.address_fragment1); // Ensure this points to the correct layout


        // Initialize views from the bottom sheet
        View bottomSheetView = bottomSheetDialog.findViewById(R.id.bottom_sheet_layout);
        if (bottomSheetView != null) {
            loc = bottomSheetView.findViewById(R.id.location_text_view);

            TextView locationTitle = bottomSheetView.findViewById(R.id.location_title);
            Button confirmButton = bottomSheetView.findViewById(R.id.confirm_button);


            confirmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Dismiss the current bottom sheet
                    if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
                        bottomSheetDialog.dismiss();
                    }

                    // Open the second bottom sheet
                    openBottomSheet2(view);
                }
            });
        } else {
            Log.e("GoogleMaps", "Bottom sheet view not found.");
        }

        // Show the bottom sheet
        bottomSheetDialog.show();
    }

    private void openBottomSheet2(View view) {
        if (bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
            bottomSheetDialog.dismiss();
        }

        bottomSheetDialog = new BottomSheetDialog(requireContext());
        bottomSheetDialog.setContentView(R.layout.address_fragment2); // Ensure this points to the correct layout

        loc = bottomSheetDialog.findViewById(R.id.location_text_view);
        loc.setText(addressLine);

        View bottomSheetView = bottomSheetDialog.findViewById(R.id.bottom_sheet_layout);
        if (bottomSheetView != null) {
            TextInputEditText streetEt = bottomSheetView.findViewById(R.id.street_et);
            TextInputEditText barangayEt = bottomSheetView.findViewById(R.id.brgy_et);

            Button saveBtn = bottomSheetView.findViewById(R.id.save_button);
            RadioGroup locRg = bottomSheetView.findViewById(R.id.location_rg);
            FrameLayout fieldsContainer = bottomSheetView.findViewById(R.id.fields_container);
            ImageButton backBtn = bottomSheetView.findViewById(R.id.back_button);

            RadioButton defaultRadioButton = bottomSheetView.findViewById(R.id.radio_house);
            RadioButton buildingRadioButton = bottomSheetView.findViewById(R.id.radio_building);

            // Initialize structure to house by default
            structure = defaultRadioButton.getText().toString().trim().toLowerCase();
            locRg.check(defaultRadioButton.getId());

            View defaultView = getLayoutInflater().inflate(R.layout.address_mini_fragment2, fieldsContainer, false);

            fieldsContainer.addView(defaultView);
            locRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    fieldsContainer.removeAllViews();

                    View newView;
                    if (checkedId == R.id.radio_house) {
                        structure = defaultRadioButton.getText().toString().trim().toLowerCase();
                        newView = getLayoutInflater().inflate(R.layout.address_mini_fragment2, fieldsContainer, false);
                    } else if (checkedId == R.id.radio_building) {
                        structure = buildingRadioButton.getText().toString().trim().toLowerCase();
                        newView = getLayoutInflater().inflate(R.layout.address_mini_fragment1, fieldsContainer, false);
                    } else {
                        Toast.makeText(getContext(), "Please select a structure type", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    fieldsContainer.addView(newView);
                }
            });

            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String street = streetEt.getText().toString().trim();
                    String barangay = barangayEt.getText().toString().trim();

                    TextInputEditText houseEt = fieldsContainer.findViewById(R.id.house_et);
                    TextInputEditText buildingEt = fieldsContainer.findViewById(R.id.building_et);
                    TextInputEditText unitEt = fieldsContainer.findViewById(R.id.unit_et);
                    TextInputEditText additionalEt = fieldsContainer.findViewById(R.id.additional_et);

                    String house = houseEt != null ? houseEt.getText().toString().trim() : "";
                    String building = buildingEt != null ? buildingEt.getText().toString().trim() : "";
                    String unit = unitEt != null ? unitEt.getText().toString().trim() : "";
                    String additional = additionalEt != null ? additionalEt.getText().toString().trim() : "";

                    sendAddressToServer(currentLatitude, currentLongitude, barangay, street, building, unit, house, additional);
                    bottomSheetDialog.dismiss();

                    if (isNewUser) {
                        updateIsNewUserStatus(email); //update not a new user anymore
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                    } else {
                        AddressListFragment fragment = new AddressListFragment();
                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, fragment)
                                .addToBackStack(null)
                                .commit();
                    }

                }
            });

            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetDialog.dismiss();
                    openBottomSheet1(view);

                }
            });


        } else {
            Log.e("GoogleMaps", "Bottom sheet view not found.");
        }

        bottomSheetDialog.show();
    }

    private void updateIsNewUserStatus(String email) {
        String url_updateStatus = "https://thirsttap.scarlet2.io/Backend/updateUserStatus.php"; // Replace with your correct URL

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_updateStatus,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response.trim());
                        if (jsonResponse.getString("success").equals("1")) {
                            // Successfully updated is_new_user
                            Log.d("LoginBottomSheet", "is_new_user updated to 0");
                        } else {
                            Log.d("LoginBottomSheet", "Failed to update is_new_user");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.d("LoginBottomSheet", "Network error: " + error.getMessage());
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                return params;
            }
        };

        // Add the request to the request queue
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
