package com.example.thirsttap.AddressesPage;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.thirsttap.R;

public class EditAddressFragment extends Fragment {

    private EditText streetEditText, buildingEditText, unitEditText, cityEditText, postalCodeEditText;
    private Button saveButton;
    private AddressViewModel viewModel;
    private int addressId;

    public static EditAddressFragment newInstance(int addressId) {
        EditAddressFragment fragment = new EditAddressFragment();
        Bundle args = new Bundle();
        args.putInt("addressId", addressId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            addressId = getArguments().getInt("addressId");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_address, container, false);

        streetEditText = view.findViewById(R.id.edit_street);
        buildingEditText = view.findViewById(R.id.edit_building);
        unitEditText = view.findViewById(R.id.edit_unit);
        cityEditText = view.findViewById(R.id.edit_city);
        postalCodeEditText = view.findViewById(R.id.edit_postal_code);
        saveButton = view.findViewById(R.id.save_button);

        viewModel = new ViewModelProvider(this).get(AddressViewModel.class);

        // Load existing address details (this could be fetched from the ViewModel)
        loadAddressDetails();

        saveButton.setOnClickListener(v -> saveAddress());

        return view;
    }

    private void loadAddressDetails() {
        // Here, you would fetch the address details using the addressId and populate the EditText fields
        // For example, using the ViewModel to get the address details
        // viewModel.getAddressById(addressId).observe(getViewLifecycleOwner(), address -> {
        //     streetEditText.setText(address.getStreet());
        //     buildingEditText.setText(address.getBuildingName());
        //     unitEditText.setText(address.getUnitNumber());
        //     cityEditText.setText(address.getCity());
        //     postalCodeEditText.setText(address.getPostalCode());
        // });
    }

    private void saveAddress() {
        String street = streetEditText.getText().toString();
        String building = buildingEditText.getText().toString();
        String unit = unitEditText.getText().toString();
        String city = cityEditText.getText().toString();
        String postalCode = postalCodeEditText.getText().toString();

        if (TextUtils.isEmpty(street) || TextUtils.isEmpty(city)) {
            Toast.makeText(getContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Call your ViewModel to update the address in the database
        viewModel.updateAddress(addressId, street, building, unit, city, postalCode);
        Toast.makeText(getContext(), "Address updated successfully", Toast.LENGTH_SHORT).show();
        getParentFragmentManager().popBackStack(); // Go back to the previous fragment
    }
}
