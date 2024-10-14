package com.example.thirsttap.OrderHistoryPage;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class OrderViewPagerAdapter extends FragmentStateAdapter {

    public OrderViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new PendingOrdersFragment();
            case 1:
                return new OngoingOrdersFragment();
            case 2:
                return new CompletedOrdersFragment();
            case 3:
                return new CancelledOrdersFragment();
            default:
                return new PendingOrdersFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}

