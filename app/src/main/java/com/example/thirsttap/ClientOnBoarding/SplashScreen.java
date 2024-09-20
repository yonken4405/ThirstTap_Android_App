package com.example.thirsttap.ClientOnBoarding;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.thirsttap.R;

public class SplashScreen extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);


        // Make the status bar transparent
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);


        // Hide navigation and status bar icons
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION   // Hides bottom nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN        // Hides the status bar icons
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY  // Keeps UI hidden after interaction
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // Ensures content is laid out behind the status bar
        );

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent iHome = new Intent(getApplicationContext(), OnboardingActivity.class);
                startActivity(iHome);
            }
        }, 3000);
    }
}
