package com.example.thirsttap;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.view.animation.Animation; // Correct import

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.thirsttap.AccountPage.AccountFragment;
import com.example.thirsttap.AddressesPage.AddressListFragment;
import com.example.thirsttap.HomePage.HomeFragment;
import com.example.thirsttap.OrderHistoryPage.Order;
import com.example.thirsttap.OrderHistoryPage.OrderHistoryFragment;
import com.example.thirsttap.OrderPage.OrderFragment;
import com.example.thirsttap.OrderPage.StationSelection;

public class MainActivity extends AppCompatActivity {
    LinearLayout home, history, order, account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hide the navigation bar
        hideBottomNav();

        // Handle window insets to avoid content overlapping with the status bar
        View rootView = findViewById(R.id.fragment_container);
        rootView.setOnApplyWindowInsetsListener((view, insets) -> {
            int statusBarHeight = insets.getSystemWindowInsetTop();
            // Adjust your layout or content here based on statusBarHeight
            return view.onApplyWindowInsets(insets);
        });

        // Initialize navigation items
        home = findViewById(R.id.nav_home);
        history = findViewById(R.id.nav_history);
        order = findViewById(R.id.nav_order);
        account = findViewById(R.id.nav_account);

        // Add click listener for each item
        home.setOnClickListener(v -> navigateToFragment(new HomeFragment(), "HOME_FRAGMENT"));
        history.setOnClickListener(v -> navigateToFragment(new OrderHistoryFragment(), "HISTORY_FRAGMENT"));
        order.setOnClickListener(v -> navigateToFragment(new StationSelection(), "ORDER_FRAGMENT"));
        account.setOnClickListener(v -> navigateToFragment(new AccountFragment(), "ACCOUNT_FRAGMENT"));

        // Add a back stack listener to handle navigation button colors
        getSupportFragmentManager().addOnBackStackChangedListener(this::updateNavigationBar);

        // Load the first fragment on activity launch
        if (savedInstanceState == null) {
            navigateToFragment(new HomeFragment(), "HOME_FRAGMENT");
        }
    }

    private void navigateToFragment(Fragment fragment, String tag) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment, tag)
                .addToBackStack(null)
                .commit();
    }

    private void updateNavigationBar() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (currentFragment instanceof HomeFragment) {
            animateSelected(home);
        } else if (currentFragment instanceof OrderHistoryFragment) {
            animateSelected(history);
        } else if (currentFragment instanceof StationSelection) {
            animateSelected(order);
        } else if (currentFragment instanceof AccountFragment) {
            animateSelected(account);
        }
    }

    private void animateSelected(LinearLayout selectedItem) {
        selectedItem.setBackgroundColor(getResources().getColor(R.color.blueFont));
        selectedItem.setElevation(10f);

        ScaleAnimation scaleUp = new ScaleAnimation(
                1f, 1.2f,
                1f, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        scaleUp.setDuration(300);
        scaleUp.setFillAfter(true);
        selectedItem.startAnimation(scaleUp);

        resetOtherItems(selectedItem);
    }

    private void resetOtherItems(LinearLayout selectedItem) {
        LinearLayout[] items = {home, history, order, account};
        for (LinearLayout item : items) {
            if (item != selectedItem) {
                item.setBackgroundColor(getResources().getColor(R.color.lightBlue));
                item.setElevation(6f);

                ScaleAnimation scaleDown = new ScaleAnimation(
                        1.2f, 1f,
                        1.2f, 1f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f
                );
                scaleDown.setDuration(300);
                scaleDown.setFillAfter(true);
                item.startAnimation(scaleDown);
            }
        }
    }

    private void hideBottomNav() {
        // Hides the navigation bar
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide navigation bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }


}



