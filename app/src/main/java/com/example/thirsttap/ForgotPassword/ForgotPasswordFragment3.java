package com.example.thirsttap.ForgotPassword;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.thirsttap.Login.LoginActivity;
import com.example.thirsttap.R;

public class ForgotPasswordFragment3 extends Fragment {

    private ImageButton backBtn;
    private Button saveBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.forgot_pass_layout3, container, false);

        backBtn = view.findViewById(R.id.back_button);
        saveBtn = view.findViewById(R.id.save_button);

        backBtn.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        saveBtn.setOnClickListener(v -> {
            // After saving, go to LoginActivity
            startActivity(new Intent(getActivity(), LoginActivity.class));
        });

        return view;
    }
}
