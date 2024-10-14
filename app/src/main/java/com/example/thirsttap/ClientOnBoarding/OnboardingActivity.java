package com.example.thirsttap.ClientOnBoarding;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.*;

import com.example.thirsttap.AccountPage.TermsAndConditionsFragment;
import com.example.thirsttap.Login.LoginFragment;
import com.example.thirsttap.R;

import com.google.android.material.tabs.*;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private TabLayoutMediator tabLayoutMediator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);

        // Adapter for the ViewPager2
        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0:
                        return OnboardingPageFragment.newInstance("Select from a wide range of distributor filtration methods, containers, and sizes.", R.drawable.onboarding_image1);
                    case 1:
                        return OnboardingPageFragment.newInstance("Select between different suppliers for your best price and value.", R.drawable.onboarding_image2);
                    case 2:
                        return OnboardingPageFragment.newInstance("Your trusted supplier will now approve your order. Satisfied with your order? Let us know!", R.drawable.onboarding_image3);
                    case 3:
                        return new LoginFragment(); // New fragment for the 4th page
                    case 4:
                        return new TermsAndConditionsFragment(); // Add this for Terms and Conditions
                    default:
                        return null;
                }
            }

            @Override
            public int getItemCount() {
                return 4; // Number of pages
            }
        });

        // Connect TabLayout with ViewPager2
        tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == viewPager.getCurrentItem()) {
                        tab.setIcon(ContextCompat.getDrawable(this, R.drawable.active_dot)); // Active dot icon
                    } else {
                        tab.setIcon(R.drawable.inactive_dot); // Inactive dot icon
                    }
                });

        tabLayoutMediator.attach();

        // Add page transformer to the ViewPager2
        viewPager.setPageTransformer((page, position) -> {
            page.setAlpha(0.5f + (1 - Math.abs(position)) * 0.5f); // Fade effect
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < tabLayout.getTabCount(); i++) {
                    TabLayout.Tab tab = tabLayout.getTabAt(i);
                    if (tab != null) {
                        if (i == position) {
                            tab.setIcon(R.drawable.active_dot);
                        } else {
                            tab.setIcon(R.drawable.inactive_dot);
                        }
                    }
                }

                // Hide or show the TabLayout based on the page position
                if (position == 3) {
                    tabLayout.setVisibility(View.GONE); // Hide TabLayout on 4th page
                } else {
                    tabLayout.setVisibility(View.VISIBLE); // Show TabLayout on other pages
                }
            }
        });


    }
}

