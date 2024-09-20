package com.example.thirsttap.HomePage;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.thirsttap.AddressesPage.AddressListFragment;
import com.example.thirsttap.OrderPage.OrderFragment;
import com.example.thirsttap.R;

public class HomeFragment extends Fragment {
    private ImageButton homeBtn, orderBtn, profileBtn;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.home_screen_fragment, container, false);

        homeBtn = view.findViewById(R.id.home_btn);
        orderBtn = view.findViewById(R.id.order_btn);
        profileBtn = view.findViewById(R.id.profile_btn);

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddressListFragment addressListFragment = new AddressListFragment();
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, addressListFragment).addToBackStack(null).commit();
            }
        });

        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OrderFragment.class);
                startActivity(intent);

            }
        });







        return view;
    }


}
