package com.example.thirsttap.OrderPage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.VectorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.thirsttap.AddressesPage.Address;
import com.example.thirsttap.HomePage.HomeFragment;
import com.example.thirsttap.MainActivity;
import com.example.thirsttap.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class StationSelection extends Fragment implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;

    ImageButton backBtn;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private Marker currentMarker, defaultAddressMarker;
    private String stationAddress,stationName, stationId;
    private String userId, email, name, phoneNum;
    private Button orderButton;
    private OrderViewModel orderViewModel;
    private Double defaultLatitude, defaultLongitude;
    private int defaultAddressId;
    private ProgressBar loader;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_station_selection, container, false);
        loader = view.findViewById(R.id.loader);

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

        fetchDefaultAddress();

        Log.d("defaultaddressid station", String.valueOf(defaultAddressId));


        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
            fetchDefaultAddress(); // Fetch and display the default address
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }

        googleMap.setOnMarkerClickListener(marker -> {
            // Check if the clicked marker is the user's marker
            if (marker.equals(currentMarker)) {
                return false; // Ignore click events for the user's marker
            } else if (marker.equals(defaultAddressMarker )) {
                return false; // Ignore click events for the user's marker
            }
            // Otherwise, handle clicks on station markers
            showStationDetails(marker); // Show station details
            return true; // Indicate that we've handled the click
        });
    }


    private void fetchStations() {
        loader.setVisibility(View.VISIBLE);

        String stationUrl = "https://thirsttap.scarlet2.io/Backend/fetchStations.php"; // Replace with your actual URL

        StringRequest stringRequest = new StringRequest(Request.Method.GET, stationUrl,
                response -> {
                    loader.setVisibility(View.GONE);
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
                    loader.setVisibility(View.GONE);
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
                .title(stationName)
                .icon(getBitmapDescriptorFromVector(R.drawable.baseline_location_pin_24)); // Use your custom drawable

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

            orderViewModel.clearCart();
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

                    // Create a marker for current location with a tag
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(currentLatLng)
                            .title("Your current location")
                            .icon(getBitmapDescriptorFromVector(R.drawable.baseline_person_pin_circle_24)); // Use your custom drawable
                    currentMarker = googleMap.addMarker(markerOptions);
                    currentMarker.setTag("current_location"); // Tag for current location marker
                } else {
                    Toast.makeText(requireContext(), "Unable to get location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchDefaultAddress() {
        loader.setVisibility(View.VISIBLE);
        String url = "https://thirsttap.scarlet2.io/Backend/fetchDefaultAddress.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loader.setVisibility(View.GONE);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            defaultLatitude = jsonObject.getDouble("latitude");
                            defaultLongitude = jsonObject.getDouble("longitude");
                            defaultAddressId = jsonObject.getInt("address_id");

                            // Now move the camera to the default address if the map is ready
                            if (googleMap != null) {
                                LatLng defaultAddress = new LatLng(defaultLatitude, defaultLongitude);
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultAddress, 14f));

                                // Optionally, add a marker for the default address
                                MarkerOptions markerOptions = new MarkerOptions()
                                        .position(defaultAddress)
                                        .title("Default Address");

                                defaultAddressMarker = googleMap.addMarker(markerOptions); // Change to defaultAddressMarker
                            }

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

                            // Store address ID in SharedPreferences here
                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_profile", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("chosen_address_id", defaultAddressId); // Store addressId
                            editor.putString("chosen_address", formattedAddress);
                            editor.apply(); // Save changes

                            Log.d("Response StationSelection", formattedAddress);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.d("Response StationSelection", response);
                        Log.d("Coordinates", "Latitude: " + defaultLatitude + ", Longitude: " + defaultLongitude);


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


    private BitmapDescriptor getBitmapDescriptorFromVector(int vectorResId) {
        VectorDrawable vectorDrawable = (VectorDrawable) AppCompatResources.getDrawable(requireContext(), vectorResId);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


}
