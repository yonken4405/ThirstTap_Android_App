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
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.thirsttap.AccountPage.AccountFragment;
import com.example.thirsttap.HomePage.HomeFragment;
import com.example.thirsttap.OrderPage.CheckOutFragment;
import com.example.thirsttap.R;

import java.util.ArrayList;
import java.util.List;

public class AddressListFragment extends Fragment implements AddressAdapter.OnAddressClickListener {
    private ProgressBar loader;
    private RecyclerView recyclerView;
    private AddressAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<BuildingAddress> buildingAddressList;
    private List<HouseAddress> houseAddressList;
    private AddressViewModel viewModel;
    private Button addBtn;
    private ImageButton backBtn;
    private String userId;
    private String sourceFragment;
    private BuildingAddress defaultBuildingAddress;
    private HouseAddress defaultHouseAddress;
    private TextView deliveryAddressTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.address_list_fragment, container, false);
        loader = view.findViewById(R.id.loader);

        recyclerView = view.findViewById(R.id.recycler_addresses);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        addBtn = view.findViewById(R.id.add_button);
        backBtn = view.findViewById(R.id.back_button);

        buildingAddressList = new ArrayList<>();
        houseAddressList = new ArrayList<>();
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sourceFragment = getArguments().getString("sourceFragment"); // Get the source fragment
        }

//        // Change: Retrieve the default address ID from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_profile", Context.MODE_PRIVATE);

        // Change: Pass the default address ID to the adapter
        adapter = new AddressAdapter(buildingAddressList, houseAddressList, this, sourceFragment);
        recyclerView.setAdapter(adapter);

        userId = sharedPreferences.getString("userid", "default_userid");

        viewModel = new ViewModelProvider(this).get(AddressViewModel.class);
        // Observe ViewModel data or loading state
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                loader.setVisibility(View.VISIBLE);
            } else {
                loader.setVisibility(View.GONE);
            }
        });
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

        //hide if just choosing address for checkout
        if (sourceFragment.equals("checkOutFragment")){
            addBtn.setVisibility(View.GONE);
        } else {
            addBtn.setVisibility(View.VISIBLE);
        }

        addBtn.setOnClickListener(v -> {
            GoogleMapsFragment fragment = new GoogleMapsFragment();
            getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
        });


        backBtn.setOnClickListener(v -> {
            //change route if choosing address for checkout
            if (sourceFragment.equals("checkOutFragment")){
                CheckOutFragment fragment = new CheckOutFragment();
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
            } else {
                AccountFragment fragment = new AccountFragment();
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
            }
        });

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        // Set up swipe refresh layout
        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(false);
            viewModel.fetchAddresses();
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
    public void onAddressClick(int addressId, String addressType, String sourceFragment) {

        if ("checkOutFragment".equals(sourceFragment)) {
            // Navigate back to SubMain
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, new CheckOutFragment())
                    .addToBackStack(null)
                    .commit();
        } else {
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




}
