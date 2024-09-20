package com.example.thirsttap.AddressesPage;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.example.thirsttap.R;
public class AddressSetupFragment extends Fragment {
    Button continueBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_address_setup_activity, container, false);


        continueBtn = view.findViewById(R.id.continue_button);

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a new instance of GoogleMaps with arguments
                GoogleMapsFragment fragment = new GoogleMapsFragment();
                //Bundle args = new Bundle();
                //args.putString("email", email);
                //fragment.setArguments(args);

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });



        return view;
    }




}