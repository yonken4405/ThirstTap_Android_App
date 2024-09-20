package com.example.thirsttap.AddressesPage;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.thirsttap.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddressListFragment extends Fragment {
    private RecyclerView recyclerView;
    private AddressAdapter adapter;
    private List<BuildingAddress> buildingAddressList;
    private List<HouseAddress> houseAddressList;
    private RequestQueue requestQueue;
    private AddressViewModel viewModel; // Assuming ViewModel is used
    private Button addBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.address_list_fragment, container, false);

        recyclerView = view.findViewById(R.id.recycler_addresses);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        addBtn = view.findViewById(R.id.add_button);

        buildingAddressList = new ArrayList<>();
        houseAddressList = new ArrayList<>();

        adapter = new AddressAdapter(buildingAddressList, houseAddressList, this::updateDefaultAddress);
        recyclerView.setAdapter(adapter);

        // Initialize Volley request queue
        requestQueue = Volley.newRequestQueue(getContext());

        // Initialize ViewModel and observe address data
        viewModel = new ViewModelProvider(this).get(AddressViewModel.class);
        viewModel.getBuildingAddresses().observe(getViewLifecycleOwner(), addresses -> {
            buildingAddressList.clear();
            buildingAddressList.addAll(addresses);
            adapter.notifyDataSetChanged();
        });

        viewModel.getHouseAddresses().observe(getViewLifecycleOwner(), addresses -> {
            houseAddressList.clear();
            houseAddressList.addAll(addresses);
            adapter.notifyDataSetChanged();
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Trigger data fetch from ViewModel
                //viewModel.fetchAddresses(); // Ensure fetchAddresses is public in ViewModel
                GoogleMapsFragment fragment = new GoogleMapsFragment();
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
            }
        });

        return view;
    }

    private void updateDefaultAddress(int addressId, String addressType) {
        // Use ViewModel to update the default address
        viewModel.updateDefaultAddress(addressId, addressType);
    }
}

