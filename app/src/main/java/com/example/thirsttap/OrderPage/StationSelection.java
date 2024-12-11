package com.example.thirsttap.OrderPage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.VectorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
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
            if (marker.equals(currentMarker) || marker.equals(defaultAddressMarker)) {
                return false; // Ignore click events for these markers
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
                    Log.d("fetchStations", "Response: " + response); // Log the full response
                    try {
                        JSONArray stationsArray = new JSONArray(response);
                        for (int i = 0; i < stationsArray.length(); i++) {
                            JSONObject station = stationsArray.getJSONObject(i);
                            // Log each station's details
                            Log.d("fetchStations", "Processing station: " + station.toString());

                            double latitude = station.getDouble("latitude");
                            double longitude = station.getDouble("longitude");
                            String stationName = station.getString("station_name");
                            String stationAddress = station.getString("station_address");
                            String contactNumber = station.optString("contact_number", "N/A"); // Use optString for optional fields
                            String email = station.optString("email", "N/A"); // Use optString for optional fields
                            JSONArray scheduleArray = station.optJSONArray("schedule");

                            String openingHours = createOpeningHoursString(scheduleArray);
                            String stationId = station.getString("station_id");

                            // Log the generated opening hours string
                            Log.d("fetchStations", "Opening hours for " + stationName + ": " + openingHours);

                            // Determine if the station is closed today
                            boolean isClosedToday = isStationClosedToday(scheduleArray);

                            // Pass all required information to addStationMarkers
                            addStationMarkers(latitude, longitude, stationName, stationAddress, contactNumber, email, openingHours, stationId, isClosedToday);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Error parsing station data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    loader.setVisibility(View.GONE);
                    Log.e("fetchStations", "Volley error: " + error.toString());
                    Toast.makeText(requireContext(), "Error fetching stations", Toast.LENGTH_SHORT).show();
                });

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(stringRequest);
    }

    // Method to check if the station is closed today
    private boolean isStationClosedToday(JSONArray scheduleArray) throws JSONException {
        if (scheduleArray == null || scheduleArray.length() == 0) {
            return true; // Assume closed if no schedule
        }

        // Get the current day of the week (0 = Monday, 1 = Tuesday, ..., 6 = Sunday)
        Calendar calendar = Calendar.getInstance();
        int todayIndex = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7; // Adjusting to match your indexing (Monday = 0)

        JSONObject todaySchedule = scheduleArray.getJSONObject(todayIndex);
        String isClosedStr = todaySchedule.optString("is_closed", "0");

        if ("1".equals(isClosedStr)) {
            return true; // The station is closed for the entire day
        }

        // Parse opening and closing times
        String openingTimeStr = todaySchedule.optString("opening_time", "00:00:00");
        String closingTimeStr = todaySchedule.optString("closing_time", "00:00:00");

        // Get the current time
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String currentTimeStr = timeFormat.format(calendar.getTime());

        try {
            // Parse the times into Date objects
            Date currentTime = timeFormat.parse(currentTimeStr);
            Date openingTime = timeFormat.parse(openingTimeStr);
            Date closingTime = timeFormat.parse(closingTimeStr);

            // Compare the current time with the opening and closing times
            if (currentTime != null && (currentTime.before(openingTime) || currentTime.after(closingTime))) {
                return true; // Station is closed outside its operating hours
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return true; // Assume closed if there's an error in parsing times
        }

        return false; // The station is open
    }


    private String createOpeningHoursString(JSONArray scheduleArray) throws JSONException {
        if (scheduleArray == null || scheduleArray.length() == 0) {
            return "No schedule available"; // Handle empty or null schedules
        }

        Map<String, String> scheduleMap = new LinkedHashMap<>();
        // Create a mapping of full day names to their shorthand versions
        Map<String, String> dayMap = new HashMap<>();
        dayMap.put("monday", "Mon");
        dayMap.put("tuesday", "Tue");
        dayMap.put("wednesday", "Wed");
        dayMap.put("thursday", "Thu");
        dayMap.put("friday", "Fri");
        dayMap.put("saturday", "Sat");
        dayMap.put("sunday", "Sun");

        for (int i = 0; i < scheduleArray.length(); i++) {
            JSONObject daySchedule = scheduleArray.getJSONObject(i);
            String dayOfWeek = daySchedule.optString("day_of_week", null);
            String isClosedStr = daySchedule.optString("is_closed", "0");
            boolean isClosed = "1".equals(isClosedStr); // Convert to boolean

            if (dayOfWeek == null) {
                Log.w("createOpeningHoursString", "Day of week is null for entry: " + daySchedule.toString());
                continue; // Skip this entry if day of week is null
            }

            // Convert the full day name to shorthand
            String shortDay = dayMap.getOrDefault(dayOfWeek, dayOfWeek);

            if (isClosed) {
                scheduleMap.put(shortDay, "Closed");
            } else {
                String openingTime = formatTime(daySchedule.optString("opening_time", "00:00:00"));
                String closingTime = formatTime(daySchedule.optString("closing_time", "00:00:00"));
                scheduleMap.put(shortDay, openingTime + " - " + closingTime);
            }
        }

        // Create a compressed schedule string
        return compressSchedule(scheduleMap);
    }

    private String compressSchedule(Map<String, String> scheduleMap) {
        StringBuilder openingHours = new StringBuilder();
        List<String> daysList = new ArrayList<>(scheduleMap.keySet());
        String previousOpeningHours = "";
        int startRange = -1; // Initialize to -1 to indicate no range started

        for (int i = 0; i < daysList.size(); i++) {
            String currentDay = daysList.get(i);
            String currentOpeningHours = scheduleMap.get(currentDay);

            if (previousOpeningHours.equals(currentOpeningHours)) {
                // If current hours are the same as previous, continue the range
                continue;
            } else {
                // If different and previous is not the same as "Closed"
                if (startRange != -1 && !previousOpeningHours.equals("Closed")) {
                    if (startRange == i - 1) {
                        openingHours.append(daysList.get(startRange)).append(": ").append(previousOpeningHours).append("\n");
                    } else {
                        openingHours.append(getDayRange(startRange, i - 1)).append(": ").append(previousOpeningHours).append("\n");
                    }
                }
                // Reset for the new range
                startRange = i; // Start a new range
                previousOpeningHours = currentOpeningHours;
            }
        }

        // Handle the last range
        if (startRange != -1) { // Only proceed if a range was started
            if (!previousOpeningHours.equals("Closed")) {
                if (startRange == daysList.size() - 1) {
                    openingHours.append(daysList.get(startRange)).append(": ").append(previousOpeningHours);
                } else {
                    openingHours.append(getDayRange(startRange, daysList.size() - 1)).append(": ").append(previousOpeningHours);
                }
            } else {
                // Handle the case for "Closed" days
                openingHours.append(getDayRange(startRange, daysList.size() - 1)).append(": Closed");
            }
        }

        return openingHours.toString().trim(); // Remove trailing newline
    }

    // Generate a string for the range of days
    private String getDayRange(int startIndex, int endIndex) {
        String[] daysOfWeek = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        if (startIndex == endIndex) {
            return daysOfWeek[startIndex]; // Same day
        } else {
            return daysOfWeek[startIndex] + " - " + daysOfWeek[endIndex]; // Range
        }
    }

    // Helper method to format time from 24-hour to 12-hour format
    private String formatTime(String time) {
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        String minutes = parts[1];
        String period = "AM";

        if (hours >= 12) {
            period = "PM";
            if (hours > 12) {
                hours -= 12;
            }
        } else if (hours == 0) {
            hours = 12; // Midnight case
        }

        return String.format("%d:%s %s", hours, minutes, period);
    }


    // Modify addStationMarkers method to include isClosedToday parameter
    private void addStationMarkers(double latitude, double longitude, String stationName, String stationAddress, String contactNumber, String email, String openingHours, String stationId, boolean isClosedToday) {
        LatLng stationLocation = new LatLng(latitude, longitude);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(stationLocation)
                .title(stationName)
                .icon(getBitmapDescriptorFromVector(R.drawable.baseline_location_pin_24));

        Marker marker = googleMap.addMarker(markerOptions);
        marker.setTag(new String[]{stationName, stationAddress, contactNumber, email, openingHours, stationId, String.valueOf(isClosedToday)}); // Store isClosedToday
    }


    // Update showStationDetails to reflect open/closed status
    private void showStationDetails(Marker marker) {
        String[] stationDetails = (String[]) marker.getTag();

        if (stationDetails == null) {
            // Handle the case where the tag is null (this should not happen for valid station markers)
            Log.e("StationSelection", "Marker tag is null for marker: " + marker.getTitle());
            return; // Exit the method if the station details are not available
        }

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

        TextView stationStatus = view.findViewById(R.id.status);

        orderButton = view.findViewById(R.id.order_button);

        // Get today's date and check if the station is closed today
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        String today = dateFormat.format(calendar.getTime()).toLowerCase();

        // Determine if the station is closed today
        boolean isClosedToday = Boolean.parseBoolean(stationDetails[6]); // Assuming this is correct

        if (isClosedToday) {
            stationStatus.setText("Closed");
            stationStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.red));
            orderButton.setEnabled(false); // Disable the button
            orderButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.InputText)); // Change button color to gray
            Toast.makeText(requireContext(), "The station is closed today.", Toast.LENGTH_SHORT).show();
        } else {
            stationStatus.setText("Open");
            stationStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.blueFont));
            orderButton.setEnabled(true); // Enable the button
            orderButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blueFont)); // Change button color to gray
        }


        orderButton.setOnClickListener(v -> {
            if (orderViewModel != null) {
                orderViewModel.setStationData(stationDetails[0], stationDetails[1], stationDetails[4], stationDetails[5]);
            } else {
                Log.e("StationSelection", "OrderViewModel is null");
            }

            Log.d("stationselect stationid", stationDetails[5]);

            OrderFragment fragment = new OrderFragment();
            getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();

            orderViewModel.clearCart();
            stationDetailsDialog.dismiss();
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
