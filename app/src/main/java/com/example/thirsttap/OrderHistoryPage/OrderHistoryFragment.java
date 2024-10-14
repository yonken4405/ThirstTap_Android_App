package com.example.thirsttap.OrderHistoryPage;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.thirsttap.AccountPage.AccountFragment;
import com.example.thirsttap.HomePage.HomeFragment;
import com.example.thirsttap.MainActivity;
import com.example.thirsttap.OrderPage.CheckOutFragment;
import com.example.thirsttap.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class OrderHistoryFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private OrderViewPagerAdapter viewPagerAdapter;
    private ImageButton backBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_history_fragment, container, false);

        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager2 = view.findViewById(R.id.view_pager);

        viewPagerAdapter = new OrderViewPagerAdapter(this);
        viewPager2.setAdapter(viewPagerAdapter);

        backBtn = view.findViewById(R.id.back_button);

        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText("Pending");
                        break;
                    case 1:
                        tab.setText("Ongoing");
                        break;
                    case 2:
                        tab.setText("Completed");
                        break;
                    case 3:
                        tab.setText("Canceled");
                        break;
                }
            }
        }).attach();

        backBtn.setOnClickListener(v -> {
            HomeFragment fragment = new HomeFragment();
            getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
        });

        return view;
    }
}

