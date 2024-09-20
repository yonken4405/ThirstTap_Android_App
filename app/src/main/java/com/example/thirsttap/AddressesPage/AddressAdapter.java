package com.example.thirsttap.AddressesPage;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.thirsttap.R;

import java.util.List;


public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    private static final int VIEW_TYPE_BUILDING = 0;
    private static final int VIEW_TYPE_HOUSE = 1;

    private List<BuildingAddress> buildingAddressList;
    private List<HouseAddress> houseAddressList;
    private OnAddressClickListener onAddressClickListener;

    public AddressAdapter(List<BuildingAddress> buildingAddressList, List<HouseAddress> houseAddressList, OnAddressClickListener listener) {
        this.buildingAddressList = buildingAddressList;
        this.houseAddressList = houseAddressList;
        this.onAddressClickListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < buildingAddressList.size()) {
            return VIEW_TYPE_BUILDING;
        } else {
            return VIEW_TYPE_HOUSE;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_BUILDING) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_item_building, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_item_house, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_BUILDING) {
            BuildingAddress buildingAddress = buildingAddressList.get(position);
            Log.d("AddressAdapter", "Binding BuildingAddress: " + buildingAddress.toString());
            holder.streetTextView.setText(buildingAddress.getStreet() + ", " + buildingAddress.getBuildingName() + ", " + buildingAddress.getUnitNumber());
            holder.cityTextView.setText(buildingAddress.getCity() + ", " + buildingAddress.getPostalCode());
            //holder.additionalInfoTextView.setText(buildingAddress.getAdditionalInfo());

            // Set visibility of TextView based on default status
            holder.defaultTextView.setVisibility(buildingAddress.isDefault() ? View.VISIBLE : View.GONE);

            holder.itemView.setOnClickListener(v -> {
                if (!buildingAddress.isDefault()) {
                    onAddressClickListener.onAddressClick(buildingAddress.getAddressId(), "building");
                }
            });
        } else {
            HouseAddress houseAddress = houseAddressList.get(position - buildingAddressList.size());
            Log.d("AddressAdapter", "Binding HouseAddress: " + houseAddress.toString());
            holder.streetTextView.setText(houseAddress.getStreet() + ", " + houseAddress.getHouseNumber());
            holder.cityTextView.setText(houseAddress.getCity() + ", " + houseAddress.getPostalCode());
           // holder.additionalInfoTextView.setText(houseAddress.getAdditionalInfo());

            // Set visibility of TextView based on default status
            holder.defaultTextView.setVisibility(houseAddress.isDefault() ? View.VISIBLE : View.GONE);

            holder.itemView.setOnClickListener(v -> {
                if (!houseAddress.isDefault()) {
                    onAddressClickListener.onAddressClick(houseAddress.getAddressId(), "house");
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return buildingAddressList.size() + houseAddressList.size();
    }

    public interface OnAddressClickListener {
        void onAddressClick(int addressId, String addressType);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView streetTextView;
        TextView cityTextView;
        //TextView additionalInfoTextView;
        TextView defaultTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            streetTextView = itemView.findViewById(R.id.address_street);
            cityTextView = itemView.findViewById(R.id.address_city);
            //additionalInfoTextView = itemView.findViewById(R.id.additional_info);
            defaultTextView = itemView.findViewById(R.id.address_default);
        }
    }
}