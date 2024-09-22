package com.example.thirsttap.AccountPage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.thirsttap.AddressesPage.AddressListFragment;
import com.example.thirsttap.ClientOnBoarding.OnboardingActivity;
import com.example.thirsttap.Login.LoginFragment;
import com.example.thirsttap.MainActivity;
import com.example.thirsttap.R;

public class AccountFragment extends Fragment {
    Button editAccBtn, addressBtn, signoutBtn;
    ImageButton backBtn;
    String userId, email, name, phoneNum;
    TextView nameTv, emailTv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.account_fragment, container, false);

        editAccBtn = view.findViewById(R.id.edit_button);
        addressBtn = view.findViewById(R.id.addresses_button);
        signoutBtn = view.findViewById(R.id.signout_button);
        backBtn = view.findViewById(R.id.back_button);
        nameTv = view.findViewById(R.id.name);
        emailTv = view.findViewById(R.id.email);

        // Retrieve user profile data from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_profile", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userid", "default_userid");
        email = sharedPreferences.getString("email", "default_email");
        name = sharedPreferences.getString("name", "default_name");
        phoneNum = sharedPreferences.getString("phone_num", "default_phone_num");

        nameTv.setText(name);
        emailTv.setText(email);

        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);
        });

        editAccBtn.setOnClickListener(v -> {
            EditAccountFragment fragment = new EditAccountFragment();
            getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
        });

        addressBtn.setOnClickListener(v -> {
            AddressListFragment fragment = new AddressListFragment();
            getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
        });

        signoutBtn.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Sign Out")
                    .setMessage("Are you sure you want to sign out?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        signOut();
                    })
                    .setNegativeButton("No", null)
                    .show();


        });

        return view;
    }

    public void signOut() {
        // Clear user data from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_profile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Remove all user data
        editor.apply();

        // Navigate to the login fragment
        Intent intent = new Intent(getContext(), OnboardingActivity.class);
        startActivity(intent);
    }
}
