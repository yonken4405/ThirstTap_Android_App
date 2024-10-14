package com.example.thirsttap.AddressesPage;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
    private String sourceFragment;


    public AddressAdapter(List<BuildingAddress> buildingAddressList, List<HouseAddress> houseAddressList, OnAddressClickListener listener, String sourceFragment) {
        this.buildingAddressList = buildingAddressList;
        this.houseAddressList = houseAddressList;
        this.onAddressClickListener = listener;
        this.sourceFragment = sourceFragment;

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
            String chosenAddress = buildingAddress.getUnitNumber() + " " +buildingAddress.getBuildingName() + " " + buildingAddress.getStreet() + ", " + buildingAddress.getBarangay() + ", " + buildingAddress.getCity() + ", " + buildingAddress.getPostalCode();
            holder.streetTextView.setText(chosenAddress);

            holder.defaultTextView.setVisibility(buildingAddress.isDefault() ? View.VISIBLE : View.GONE);

            holder.itemView.setOnClickListener(v -> {
                holder.streetTextView.setText(chosenAddress);
                // Change: Only change the addressId if the address is tapped
                SharedPreferences sharedPreferences = v.getContext().getSharedPreferences("user_profile", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("chosen_address_id", buildingAddress.getAddressId());  // Update addressId based on user selection
                editor.putString("chosen_address", chosenAddress);
                editor.apply();
                Log.d("AddressAdapter", "Stored Address ID: " + buildingAddress.getAddressId());

                onAddressClickListener.onAddressClick(buildingAddress.getAddressId(), "building", sourceFragment);
            });

            //hide if just choosing address for checkout
            if (sourceFragment.equals("checkOutFragment")){
                holder.editBtn.setVisibility(View.GONE);
                holder.deleteBtn.setVisibility(View.GONE);
            } else {
                holder.editBtn.setVisibility(View.VISIBLE);
                holder.deleteBtn.setVisibility(View.VISIBLE);
            }

            holder.editBtn.setOnClickListener(v -> {
                onAddressClickListener.onEditClick(buildingAddress.getAddressId());
            });

            holder.deleteBtn.setOnClickListener(v -> {
                onAddressClickListener.onDeleteClick(buildingAddress.getAddressId());
            });
        } else {
            HouseAddress houseAddress = houseAddressList.get(position - buildingAddressList.size());
            String chosenAddress = houseAddress.getHouseNumber() + " " + houseAddress.getStreet() + ", " + houseAddress.getBarangay() + ", " + houseAddress.getCity() + ", " + houseAddress.getPostalCode();
            holder.streetTextView.setText(chosenAddress);

            holder.defaultTextView.setVisibility(houseAddress.isDefault() ? View.VISIBLE : View.GONE);

            holder.itemView.setOnClickListener(v -> {
                // Change: Only change the addressId if the address is tapped
                SharedPreferences sharedPreferences = v.getContext().getSharedPreferences("user_profile", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("chosen_address_id", houseAddress.getAddressId());  // Update addressId based on user selection
                editor.putString("chosen_address", chosenAddress);
                editor.apply();

                onAddressClickListener.onAddressClick(houseAddress.getAddressId(), "house", sourceFragment);
            });

            //hide if just choosing address for checkout
            if (sourceFragment.equals("checkOutFragment")){
                holder.editBtn.setVisibility(View.GONE);
                holder.deleteBtn.setVisibility(View.GONE);
            } else {
                holder.editBtn.setVisibility(View.VISIBLE);
                holder.deleteBtn.setVisibility(View.VISIBLE);
            }

            holder.editBtn.setOnClickListener(v -> {
                onAddressClickListener.onEditClick(houseAddress.getAddressId());
            });

            holder.deleteBtn.setOnClickListener(v -> {
                onAddressClickListener.onDeleteClick(houseAddress.getAddressId());
            });
        }
    }

    @Override
    public int getItemCount() {
        return buildingAddressList.size() + houseAddressList.size();
    }

    public interface OnAddressClickListener {
        void onAddressClick(int addressId, String addressType, String sourceFragment);
        void onEditClick(int addressId);
        void onDeleteClick(int addressId);
        Context getContext(); // Add getContext method to allow SharedPreferences access
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView streetTextView;
        TextView defaultTextView;
        ImageButton editBtn, deleteBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            streetTextView = itemView.findViewById(R.id.address_street);
            defaultTextView = itemView.findViewById(R.id.address_default);
            editBtn = itemView.findViewById(R.id.edit_btn);
            deleteBtn = itemView.findViewById(R.id.delete_btn);
        }
    }


}
