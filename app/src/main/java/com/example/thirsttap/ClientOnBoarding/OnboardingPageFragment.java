package com.example.thirsttap.ClientOnBoarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.thirsttap.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

public class OnboardingPageFragment extends Fragment {
    private static final String ARG_TEXT = "argText";
    private static final String ARG_IMAGE_RES_ID = "argImageResId";
    private TextView skipButton, instructionsTv;
    private ImageView imageView;

    public static OnboardingPageFragment newInstance(String text, int imageResId) {
        OnboardingPageFragment fragment = new OnboardingPageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TEXT, text);
        args.putInt(ARG_IMAGE_RES_ID, imageResId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboarding_page, container, false);
        instructionsTv = view.findViewById(R.id.onboarding_text);
        imageView = view.findViewById(R.id.onboarding_image);
        skipButton = view.findViewById(R.id.skip_button);

        Bundle args = getArguments();
        if (args != null) {
            instructionsTv.setText(args.getString(ARG_TEXT));
            imageView.setImageResource(args.getInt(ARG_IMAGE_RES_ID));
        }

        skipButton.setOnClickListener(v -> {
            ViewPager2 viewPager = getActivity().findViewById(R.id.view_pager);
            if (viewPager != null) {
                viewPager.setCurrentItem(3, true); // Skip to the 4th page (index 3)
            }
        });

        return view;
    }
}
