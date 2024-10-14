package com.example.thirsttap.AccountPage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.thirsttap.AddressesPage.AddressListFragment;
import com.example.thirsttap.ClientOnBoarding.OnboardingActivity;
import com.example.thirsttap.MainActivity;
import com.example.thirsttap.R;

public class AccountFragment extends Fragment {
    private Button editAccBtn, addressBtn, signoutBtn, partnerBtn, rateBtn, changePass, legalBtn;
    private ImageButton backBtn;
    private String userId, email, name, phoneNum;
    private TextView nameTv, emailTv;
    private SwitchCompat toggleSwitch;
    private ProgressBar loader;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.account_fragment, container, false);

        loader = view.findViewById(R.id.loader);

        editAccBtn = view.findViewById(R.id.edit_button);
        addressBtn = view.findViewById(R.id.addresses_button);
        signoutBtn = view.findViewById(R.id.signout_button);
        backBtn = view.findViewById(R.id.back_button);
        nameTv = view.findViewById(R.id.name);
        emailTv = view.findViewById(R.id.email);
        toggleSwitch = view.findViewById(R.id.toggleSwitch);
        partnerBtn = view.findViewById(R.id.partner_button);
        rateBtn = view.findViewById(R.id.rate_button);
        changePass = view.findViewById(R.id.change_pass);
        legalBtn = view.findViewById(R.id.legal_button);


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

        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePasswordFragment fragment = new ChangePasswordFragment();
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
            }
        });

        addressBtn.setOnClickListener(v -> {
            AddressListFragment fragment = new AddressListFragment();
            Bundle args = new Bundle();
            args.putString("sourceFragment", "profileFragment"); // Pass the source fragment
            fragment.setArguments(args);
            getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
        });

        partnerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WorkWithUsFragment fragment = new WorkWithUsFragment();
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
            }
        });

        rateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Example code to show FeedbackBottomSheetFragment
                FeedbackBottomSheet feedbackBottomSheet = new FeedbackBottomSheet();
                feedbackBottomSheet.show(getParentFragmentManager(), feedbackBottomSheet.getTag());

            }
        });

        legalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loader.setVisibility(View.VISIBLE);

                TermsAndConditionsFragment fragment = new TermsAndConditionsFragment();
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();

                // Use FragmentTransaction's `runOnCommit` to hide the ProgressBar after the transaction is done
                getParentFragmentManager().beginTransaction().runOnCommit(new Runnable() {
                    @Override
                    public void run() {
                        // Hide the ProgressBar after fragment is loaded
                        loader.setVisibility(View.GONE);
                    }
                });

            }
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

        // Set a listener for the toggle switch
        toggleSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                toggleSwitch.setTrackTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.blueFont)));
            } else {
                toggleSwitch.setTrackTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.gray)));
            }
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
