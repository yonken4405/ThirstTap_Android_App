package com.example.thirsttap.OrderPage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.thirsttap.MainActivity;
import com.example.thirsttap.R;

public class OrderFragment extends AppCompatActivity {

    ImageButton backBtn, plusBtn, minusBtn;
    TextView counter;
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_fragment);

        backBtn = findViewById(R.id.back_button);
        plusBtn = findViewById(R.id.plus_button);
        minusBtn = findViewById(R.id.minus_button);
        counter = findViewById(R.id.counter_tv);
        count = 1;

        RadioGroup waterTypeGroup = findViewById(R.id.water_type_container);
        LinearLayout descAlkaline = findViewById(R.id.desc_alkaline);
        LinearLayout descDistilled = findViewById(R.id.desc_distilled);
        RadioButton titleAlkaline = findViewById(R.id.radio_alkaline);
        RadioButton titleDistilled = findViewById(R.id.radio_distilled);


        waterTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Hide all description layouts first
                descAlkaline.setVisibility(View.GONE);
                descDistilled.setVisibility(View.GONE);

                // Reset the text of all RadioButtons
                titleAlkaline.setText("Alkaline");  // Ensure text is reset
                titleDistilled.setText("Distilled");  // Ensure text is reset

                // Show description layout based on the selected radio button
                if (checkedId == R.id.radio_alkaline) {
                    descAlkaline.setVisibility(View.VISIBLE);
                    titleAlkaline.setText("");  // Hide text by setting it to an empty string
                } else if (checkedId == R.id.radio_distilled) {
                    descDistilled.setVisibility(View.VISIBLE);
                    titleDistilled.setText("");  // Hide text by setting it to an empty string
                }
            }
        });

        RadioGroup sizeGroup = findViewById(R.id.size_container);
        LinearLayout priceRound = findViewById(R.id.price_round);
        LinearLayout priceSlim = findViewById(R.id.price_slim);
        LinearLayout priceSmallSlim = findViewById(R.id.price_small_slim);
        RadioButton titleRound = findViewById(R.id.radio_round);
        RadioButton titleSlim = findViewById(R.id.radio_slim);
        RadioButton titleSmallSlim = findViewById(R.id.radio_small_slim);

        sizeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Hide all description layouts first
                priceRound.setVisibility(View.GONE);
                priceSlim.setVisibility(View.GONE);
                priceSmallSlim.setVisibility(View.GONE);

                // Reset the text of all RadioButtons
                titleRound.setText("5.00 Gal Round");
                titleSlim.setText("5.00 Gal Slim");
                titleSmallSlim.setText("2.50 Gal Slim");

                if (checkedId == R.id.radio_round) {
                    priceRound.setVisibility(View.VISIBLE);
                    titleRound.setText("");
                } else if (checkedId == R.id.radio_slim) {
                    priceSlim.setVisibility(View.VISIBLE);
                    titleSlim.setText("");
                } else if (checkedId == R.id.radio_small_slim) {
                    priceSmallSlim.setVisibility(View.VISIBLE);
                    titleSmallSlim.setText("");
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderFragment.this, MainActivity.class);
                startActivity(intent);
            }
        });

        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count > 0){
                    count += 1;
                    counter.setText(String.valueOf(count));
                }

            }
        });

        minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count > 1){
                    count -= 1;
                    counter.setText(String.valueOf(count));
                }
            }
        });

    }
}
