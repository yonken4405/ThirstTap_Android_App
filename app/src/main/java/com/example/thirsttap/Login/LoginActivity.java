package com.example.thirsttap.Login;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;

import androidx.fragment.app.FragmentManager;
import com.example.thirsttap.R;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button startBtn = findViewById(R.id.start_button);
        startBtn.setOnClickListener(v -> showLoginSheet());
    }

    private void showLoginSheet() {
        LoginBottomSheet loginBottomSheet = new LoginBottomSheet();
        FragmentManager fragmentManager = getSupportFragmentManager(); // Use this for AppCompatActivity
        loginBottomSheet.show(fragmentManager, "LoginBottomSheet");
    }

}


