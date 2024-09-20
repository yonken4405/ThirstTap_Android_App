package com.example.thirsttap.AddressesPage;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import android.app.Application;
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
        requestQueue = Volley.newRequestQueue(application.getApplicationContext());
        fetchAddresses();
    }

    public LiveData<List<BuildingAddress>> getBuildingAddresses() {
        return buildingAddresses;
    }

    public LiveData<List<HouseAddress>> getHouseAddresses() {
        return houseAddresses;
    }

    public void updateDefaultAddress(int addressId, String addressType) {
        String url = "https://scarlet2.io/Yankin/ThirstTap/updateDefaultAddress.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    fetchAddresses(); // Refresh the address list
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
        String url = "https://scarlet2.io/Yankin/ThirstTap/fetchAddress.php";

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

        requestQueue.add(jsonArrayRequest);
    }
}
