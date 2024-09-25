package com.example.thirsttap.AddressesPage;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddressViewModel extends AndroidViewModel {
    private MutableLiveData<List<BuildingAddress>> buildingAddresses;
    private MutableLiveData<List<HouseAddress>> houseAddresses;
    private RequestQueue requestQueue;

    public AddressViewModel(@NonNull Application application) {
        super(application);
        buildingAddresses = new MutableLiveData<>();
        houseAddresses = new MutableLiveData<>();
        // Use application context for Volley request queue initialization
        requestQueue = Volley.newRequestQueue(application.getApplicationContext());
        fetchAddresses();  // You can call fetchAddresses here or trigger it from Fragment
    }

    public LiveData<List<BuildingAddress>> getBuildingAddresses() {
        return buildingAddresses;
    }

    public LiveData<List<HouseAddress>> getHouseAddresses() {
        return houseAddresses;
    }

    public void updateDefaultAddress(int addressId, String addressType) {
        String url = "https://thirsttap.scarlet2.io/Backend/updateDefaultAddress.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    // Refresh the addresses after updating the default address
                    fetchAddresses();
                },
                error -> {
                    Log.e("VolleyError", "Error: " + error.getMessage());
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("address_id", String.valueOf(addressId));
                params.put("address_type", addressType);
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    public void fetchAddresses() {
        // Retrieve userId from SharedPreferences
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("user_profile", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userid", "default_userid");

        // Append userId to the URL as a query parameter
        String url = "https://scarlet2.io/Yankin/ThirstTap/fetchAddress.php?userId=" + userId;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d("Response", response.toString());
                    List<BuildingAddress> buildings = new ArrayList<>();
                    List<HouseAddress> houses = new ArrayList<>();
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject addressObject = response.getJSONObject(i);
                            int addressId = addressObject.optInt("address_id", -1);
                            String type = addressObject.optString("type", "");
                            boolean isDefault = addressObject.optString("is_default", "0").equals("1");

                            if (type.equals("building")) {
                                buildings.add(new BuildingAddress(
                                        addressObject.optString("street", ""),
                                        addressObject.optString("building", ""),
                                        addressObject.optString("unit", ""),
                                        addressObject.optString("additional", ""),
                                        addressObject.optString("city", ""),
                                        addressObject.optString("postal_code", ""),
                                        isDefault, addressId
                                ));
                            } else if (type.equals("house")) {
                                houses.add(new HouseAddress(
                                        addressObject.optString("street", ""),
                                        addressObject.optString("house_num", ""),
                                        addressObject.optString("additional", ""),
                                        addressObject.optString("city", ""),
                                        addressObject.optString("postal_code", ""),
                                        isDefault, addressId
                                ));
                            }
                        }
                        // Update LiveData with the fetched data
                        buildingAddresses.setValue(buildings);
                        houseAddresses.setValue(houses);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.e("VolleyError", "Error: " + error.getMessage());
                }
        );

        // Add the request to the Volley request queue
        requestQueue.add(jsonArrayRequest);
    }

    public void updateAddress(int addressId, String street, String building, String unit, String city, String postalCode) {
        String url = "https://scarlet2.io/Yankin/ThirstTap/updateAddress.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    // Optionally handle the response
                    Log.d("UpdateResponse", response);
                    fetchAddresses(); // Refresh the addresses after updating
                },
                error -> {
                    Log.e("VolleyError", "Error: " + error.getMessage());
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("address_id", String.valueOf(addressId));
                params.put("street", street);
                params.put("building", building);
                params.put("unit", unit);
                params.put("city", city);
                params.put("postal_code", postalCode);
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    public void deleteAddress(int addressId) {
        String url = "https://scarlet2.io/Yankin/ThirstTap/deleteAddress.php"; // Replace with your delete URL

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    // Optionally handle the response
                    Log.d("DeleteResponse", response);
                    fetchAddresses(); // Refresh the addresses after deletion
                },
                error -> {
                    Log.e("VolleyError", "Error: " + error.getMessage());
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("address_id", String.valueOf(addressId));
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }


}
