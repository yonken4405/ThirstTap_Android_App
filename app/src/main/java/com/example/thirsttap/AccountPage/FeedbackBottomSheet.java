package com.example.thirsttap.AccountPage;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.thirsttap.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.HashMap;
import java.util.Map;

public class FeedbackBottomSheet extends BottomSheetDialogFragment {

    private RatingBar ratingBar;
    private EditText feedbackText;
    private Button submitFeedback;
    private RequestQueue requestQueue;
    private String userId, email, name, phoneNum;
    private ProgressBar loader;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feedback_form, container, false);
        loader = view.findViewById(R.id.loader);

        // Retrieve user profile data from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_profile", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userid", "default_userid");
        email = sharedPreferences.getString("email", "default_email");
        name = sharedPreferences.getString("name", "default_name");
        phoneNum = sharedPreferences.getString("phone_num", "default_phone_num");

        ratingBar = view.findViewById(R.id.ratingBar);
        feedbackText = view.findViewById(R.id.feedbackText);
        submitFeedback = view.findViewById(R.id.submitFeedback);

        requestQueue = Volley.newRequestQueue(getContext());

        submitFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Debugging - Log values before submission
                Log.d("Feedback", "User ID: " + userId);
                Log.d("Feedback", "Rating: " + ratingBar.getRating());
                Log.d("Feedback", "Feedback Text: " + feedbackText.getText().toString());

                submitFeedback();
            }
        });

        return view;
    }

    private void submitFeedback() {
        final float rating = ratingBar.getRating();
        final String feedback = feedbackText.getText().toString();
        loader.setVisibility(View.VISIBLE);
        String url = "https://thirsttap.scarlet2.io/Backend/submitFeedback.php";  // Replace with your PHP server URL

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loader.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Feedback submitted successfully!", Toast.LENGTH_SHORT).show();
                        // Optionally reset fields after submission
                        ratingBar.setRating(0);
                        feedbackText.setText("");
                        dismiss();  // Dismiss the BottomSheet after submission
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loader.setVisibility(View.GONE);
                        error.printStackTrace();  // Print detailed error to logcat
                        Toast.makeText(getContext(), "Error submitting feedback!", Toast.LENGTH_SHORT).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("userid", String.valueOf(userId));  // Pass the user_id
                params.put("rating", String.valueOf(rating));
                params.put("feedback", feedback);
                return params;
            }

        };

        requestQueue.add(stringRequest);
    }
}
