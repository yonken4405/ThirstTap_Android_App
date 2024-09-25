package com.example.thirsttap.OrderPage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.thirsttap.HomePage.HomeFragment;
import com.example.thirsttap.MainActivity;
import com.example.thirsttap.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StationSelection extends Fragment implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;

    ImageButton backBtn;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private Marker currentMarker;
    private String stationAddress,stationName, stationId;
    private String userId, email, name, phoneNum;
    private Button orderButton;
    OrderViewModel orderViewModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_station_selection, container, false);

        orderViewModel = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);

        // Retrieve user profile data from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_profile", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userid", "default_userid");
        email = sharedPreferences.getString("email", "default_email");
        name = sharedPreferences.getString("name", "default_name");
        phoneNum = sharedPreferences.getString("phone_num", "default_phone_num");
        boolean isNewUser = sharedPreferences.getBoolean("isNewUser", false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        fetchStations(); // Fetch stations when the fragment is created
        backBtn = view.findViewById(R.id.back_button);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
            }
        });


        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }

        googleMap.setOnMarkerClickListener(marker -> {
            // Check if the clicked marker is the user's marker
            if (marker.equals(currentMarker)) {
                return false; // Ignore click events for the user's marker
            }
            // Otherwise, handle clicks on station markers
            showStationDetails(marker); // Show station details
            return true; // Indicate that we've handled the click
        });
    }

//    private void addStationMarkers(double latitude, double longitude, String stationName, String stationAddress) {
//        LatLng stationLocation = new LatLng(latitude, longitude);
//        MarkerOptions markerOptions = new MarkerOptions()
//                .position(stationLocation)
//                .title(stationName);
//
//        Marker marker = googleMap.addMarker(markerOptions);
//        // Set the station name and address as a tag using a JSONObject
//        JSONObject stationInfo = new JSONObject();
//        try {
//            stationInfo.put("name", stationName);
//            stationInfo.put("address", stationAddress);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        marker.setTag(stationInfo.toString()); // Store the JSON string in the tag
//    }




    private void fetchStations() {
        String stationUrl = "https://thirsttap.scarlet2.io/Backend/fetchStations.php"; // Replace with your actual URL

        StringRequest stringRequest = new StringRequest(Request.Method.GET, stationUrl,
                response -> {
                    try {
                        JSONArray stationsArray = new JSONArray(response);
                        for (int i = 0; i < stationsArray.length(); i++) {
                            JSONObject station = stationsArray.getJSONObject(i);
                            double latitude = station.getDouble("latitude");
                            double longitude = station.getDouble("longitude");
                            stationName = station.getString("name");
                            stationAddress = station.getString("station_address");
                            String contactNumber = station.getString("contact_number");
                            String email = station.getString("email");
                            String openingHours = station.getString("opening_hours");
                            stationId = station.getString("station_id");
                            addStationMarkers(latitude, longitude, stationName, stationAddress, contactNumber, email, openingHours, stationId);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Error parsing station data", Toast.LENGTH_SHORT).show();
                    }
                    Log.d("Response", response);

                },
                error -> {
                    Log.e("VolleyError", error.toString());
                    Toast.makeText(requireContext(), "Error fetching stations", Toast.LENGTH_SHORT).show();
                });

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(stringRequest);
    }

    private void addStationMarkers(double latitude, double longitude, String stationName, String stationAddress, String contactNumber, String email, String openingHours, String stationId) {
        LatLng stationLocation = new LatLng(latitude, longitude);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(stationLocation)
                .title(stationName);

        Marker marker = googleMap.addMarker(markerOptions);
        marker.setTag(new String[]{stationName, stationAddress, contactNumber, email, openingHours, stationId}); // Store additional data
    }


    private void showStationDetails(Marker marker) {
        String[] stationDetails = (String[]) marker.getTag();
        BottomSheetDialog stationDetailsDialog = new BottomSheetDialog(requireContext());
        View view = getLayoutInflater().inflate(R.layout.station_profile_bottom_sheet, null);

        TextView stationNameTextView = view.findViewById(R.id.station_name);
        stationNameTextView.setText(stationDetails[0]);

        TextView stationAddressTv = view.findViewById(R.id.station_address);
        stationAddressTv.setText(stationDetails[1]);

        TextView contactNumberTv = view.findViewById(R.id.contact_number);
        contactNumberTv.setText(stationDetails[2]);

        TextView emailTv = view.findViewById(R.id.email);
        emailTv.setText(stationDetails[3]);

        TextView openingHoursTv = view.findViewById(R.id.opening_hours);
        openingHoursTv.setText(stationDetails[4]);

        orderButton = view.findViewById(R.id.order_button);
        orderButton.setOnClickListener(v -> {

//            // Prepare the station details to pass to the next fragment
//            Bundle bundle = new Bundle();
//            bundle.putString("station_name", stationDetails[0]); // Station Name
//            bundle.putString("station_address", stationDetails[1]); // Station Address
//            bundle.putString("station_schedule", stationDetails[4]); // Station Opening Hours
//            bundle.putString("station_id", stationDetails[5]); // Station Id

            if (orderViewModel != null) {
                orderViewModel.setStationData(stationDetails[0], stationDetails[1], stationDetails[4], stationDetails[5]);
            } else {
                // Handle the case where orderViewModel is null
                Log.e("StationSelection", "OrderViewModel is null");
            }






            Log.d("stationselect stationid", stationDetails[5]);

            // Create and set up the OrderFragment
            OrderFragment fragment = new OrderFragment();
            //fragment.setArguments(bundle); // Pass the bundle to OrderFragment
            getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();

            stationDetailsDialog.dismiss(); // Dismiss the dialog after navigating
        });

        stationDetailsDialog.setContentView(view);

        stationDetailsDialog.show();
    }



    private void proceedToOrder(String stationName) {
        OrderFragment orderFragment = new OrderFragment();
        Bundle bundle = new Bundle();
        bundle.putString("station_name", stationName);
        orderFragment.setArguments(bundle);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, orderFragment)
                .addToBackStack(null)
                .commit();
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return; // Permission not granted, exit the method
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 14f));

                    // Update marker position or create a new marker with a custom icon
                    if (currentMarker != null) {
                        currentMarker.setPosition(currentLatLng);
                    } else {
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(currentLatLng)
                                .title("Your location")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.loc_icon_user)); // Use your custom drawable
                        currentMarker = googleMap.addMarker(markerOptions);
                    }
                } else {
                    Toast.makeText(requireContext(), "Unable to get location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
