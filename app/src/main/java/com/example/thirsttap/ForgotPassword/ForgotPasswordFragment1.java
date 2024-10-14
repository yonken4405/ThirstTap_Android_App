package com.example.thirsttap.ForgotPassword;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.thirsttap.R;
import com.example.thirsttap.Login.LoginFragment;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotPasswordFragment1 extends Fragment {
    private String url_sendCode = "https://thirsttap.scarlet2.io/Backend/forgotPasswordCode.php";
    private Button requestBtn;
    private TextInputEditText inputEmail;
    private ImageButton backBtn;
    private String email;
    private ProgressBar loader;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.forgot_pass_layout1, container, false);
        loader = view.findViewById(R.id.loader);

        requestBtn = view.findViewById(R.id.request_button);
        backBtn = view.findViewById(R.id.back_button);
        inputEmail = view.findViewById(R.id.request_email);

        requestBtn.setOnClickListener(v -> {
            email = inputEmail.getText().toString().trim();
            if (!email.isEmpty()) {
                requestBtn.setEnabled(false); // Disable the button initially
                sendCode(email);
            } else {
                Toast.makeText(getContext(), "Please enter an email address", Toast.LENGTH_SHORT).show();
            }
        });

        backBtn.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), LoginFragment.class));
        });

        return view;
    }

    private void sendCode(String email) {
        loader.setVisibility(View.VISIBLE);
        Log.d("SendCode", "Sending code for email: " + email);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_sendCode,
                response -> {
                    loader.setVisibility(View.GONE);
                    Log.d("SendCode", "Server response: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response.trim());
                        String success = jsonResponse.optString("success", "0");
                        //String message = jsonResponse.optString("message", "Unknown error");

                        if ("1".equals(success)) {
                            Toast.makeText(getContext(), "Code sent successfully!", Toast.LENGTH_SHORT).show();

                            // Create a new instance of ForgotPasswordFragment2 with arguments
                            ForgotPasswordFragment2 fragment = new ForgotPasswordFragment2();
                            Bundle args = new Bundle();
                            args.putString("email", email);
                            fragment.setArguments(args);

                            // Navigate to ForgotPasswordFragment2
                            getParentFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, fragment)
                                    .addToBackStack(null)
                                    .commit();
                        } else {
                            Toast.makeText(getContext(), "Sending failed", Toast.LENGTH_SHORT).show();
                            requestBtn.setEnabled(true);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error parsing server response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    loader.setVisibility(View.GONE);
                    Log.e("SendCode", "Network error: " + error.getMessage());
                    Toast.makeText(getContext(), "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                return params;
            }
        };

        //limit resending for once every 5 secs.
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,  // Timeout in milliseconds
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, // Set this to 0 to disable retries
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }


}
