package com.example.thirsttap.AddressesPage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thirsttap.AccountPage.AccountFragment;
import com.example.thirsttap.HomePage.HomeFragment;
import com.example.thirsttap.R;

import java.util.ArrayList;
import java.util.List;

public class AddressListFragment extends Fragment implements AddressAdapter.OnAddressClickListener {
    private RecyclerView recyclerView;
    private AddressAdapter adapter;
    private List<BuildingAddress> buildingAddressList;
    private List<HouseAddress> houseAddressList;
    private AddressViewModel viewModel;
    private Button addBtn;
    private ImageButton backBtn;
    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.address_list_fragment, container, false);

        recyclerView = view.findViewById(R.id.recycler_addresses);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        addBtn = view.findViewById(R.id.add_button);
        backBtn = view.findViewById(R.id.back_button);

        buildingAddressList = new ArrayList<>();
        houseAddressList = new ArrayList<>();

        adapter = new AddressAdapter(buildingAddressList, houseAddressList, this);
        recyclerView.setAdapter(adapter);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_profile", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userid", "default_userid");

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

        addBtn.setOnClickListener(v -> {
            GoogleMapsFragment fragment = new GoogleMapsFragment();
            getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
        });

        backBtn.setOnClickListener(v -> {
            AccountFragment fragment = new AccountFragment();
            getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
        });

        return view;
    }

    private void updateDefaultAddress(int addressId, String addressType) {
        viewModel.updateDefaultAddress(addressId, addressType);
    }

    @Override
    public void onEditClick(int addressId) {
        EditAddressFragment fragment = EditAddressFragment.newInstance(addressId);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDeleteClick(int addressId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete Address")
                .setMessage("Are you sure you want to delete this address?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    viewModel.deleteAddress(addressId);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onAddressClick(int addressId, String addressType) {
        // Show confirmation dialog before making the address default
        new AlertDialog.Builder(getContext())
                .setTitle("Set as Default Address")
                .setMessage("Are you sure you want to make this address the default?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    updateDefaultAddress(addressId, addressType);
                })
                .setNegativeButton("No", null)
                .show();
    }
}
