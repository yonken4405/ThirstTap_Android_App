package com.example.thirsttap;

import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.view.animation.Animation; // Correct import

import androidx.appcompat.app.AppCompatActivity;

import com.example.thirsttap.AccountPage.AccountFragment;
import com.example.thirsttap.AddressesPage.AddressListFragment;
import com.example.thirsttap.HomePage.HomeFragment;
import com.example.thirsttap.OrderPage.OrderFragment;
import com.example.thirsttap.OrderPage.StationSelection;

public class MainActivity extends AppCompatActivity {
    LinearLayout home, history, order, account;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hide the navigation bar
        hideSystemUI();

        // Handle window insets to avoid content overlapping with the status bar
        View rootView = findViewById(R.id.fragment_container); // Replace with the ID of your root view
        rootView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View view, WindowInsets insets) {
                int statusBarHeight = insets.getSystemWindowInsetTop();
                // Adjust your layout or content here based on statusBarHeight
                return view.onApplyWindowInsets(insets);
            }
        });

        // Load the first fragment on activity launch
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        home = findViewById(R.id.nav_home);
        history = findViewById(R.id.nav_history);
        order = findViewById(R.id.nav_order);
        account = findViewById(R.id.nav_account);

        // Add click listener for each item
        home.setOnClickListener(v -> {
            animateSelected(home);
            HomeFragment fragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
        });

        history.setOnClickListener(v -> {
            animateSelected(history);
            StationSelection fragment = new StationSelection();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
        });

        order.setOnClickListener(v -> {
            animateSelected(order);
            StationSelection fragment = new StationSelection();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
        });

        account.setOnClickListener(v -> {
            animateSelected(account);
            AccountFragment addressListFragment = new AccountFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, addressListFragment).addToBackStack(null).commit();
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void animateSelected(LinearLayout selectedItem) {
        // Set background color to dark blue
        selectedItem.setBackgroundColor(getResources().getColor(R.color.blueFont));

        // Set elevation to raise the selected item
        selectedItem.setElevation(10f); // Higher elevation for the selected item

        ScaleAnimation scaleUp = new ScaleAnimation(
                1f, 1.2f, // Start and end X scaling
                1f, 1.2f, // Start and end Y scaling
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleUp.setDuration(300);
        scaleUp.setFillAfter(true);

        // Animate the selected item
        selectedItem.startAnimation(scaleUp);

        // Optionally reset other items to normal size
        resetOtherItems(selectedItem);
    }

    private void resetOtherItems(LinearLayout selectedItem) {
        LinearLayout[] items = {home, history, order, account};
        for (LinearLayout item : items) {
            if (item != selectedItem) {
                // Reset background color to default
                item.setBackgroundColor(getResources().getColor(R.color.lightBlue)); // Replace with your default color

                // Reset elevation to normal
                item.setElevation(6f); // Lower elevation for unselected items

                ScaleAnimation scaleDown = new ScaleAnimation(
                        1.2f, 1f,
                        1.2f, 1f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                scaleDown.setDuration(300);
                scaleDown.setFillAfter(true);
                item.startAnimation(scaleDown);
            }
        }
    }


}