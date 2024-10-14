package com.example.thirsttap.AccountPage;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import com.example.thirsttap.R;

public class WorkWithUsFragment extends Fragment {
    Button formBtn;
    ImageButton backBtn;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.work_with_us_fragment, container, false);

        formBtn = view.findViewById(R.id.form_button);
        backBtn = view.findViewById(R.id.back_button);


        formBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WorkWithUsForm fragment = new WorkWithUsForm();
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountFragment fragment = new AccountFragment();
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
            }
        });

       return view;
    }



}
