package com.example.thirsttap.ForgotPassword;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.thirsttap.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotPasswordFragment2 extends Fragment {
    private String url_verify = "https://scarlet2.io/Yankin/ThirstTap/forgotPasswordVerify.php";
    private ImageButton backBtn;
    private Button verifyBtn;
    private EditText[] otpFields;
    private String code, email;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.forgot_pass_layout2, container, false);

        backBtn = view.findViewById(R.id.back_button);
        verifyBtn = view.findViewById(R.id.verify_button);

        // Retrieve the email from the forgotpassword fragment1
        Bundle args = getArguments();
        if (args != null) {
            email = args.getString("email");
        }

        otpFields = new EditText[4];
        otpFields[0] = view.findViewById(R.id.digit1);
        otpFields[1] = view.findViewById(R.id.digit2);
        otpFields[2] = view.findViewById(R.id.digit3);
        otpFields[3] = view.findViewById(R.id.digit4);
        setupOtpFields();

        backBtn.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack(); // Go back to ForgotPasswordFragment1
        });

        verifyBtn.setOnClickListener(v -> {
            code = otpFields[0].getText().toString().trim() +
                    otpFields[1].getText().toString().trim() +
                    otpFields[2].getText().toString().trim() +
                    otpFields[3].getText().toString().trim();

            verifyEmail(code, email);
            verifyBtn.setEnabled(false);


        });

        return view;
    }

    private void setupOtpFields() {
        for (int i = 0; i < otpFields.length; i++) {
            final int index = i;
            otpFields[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && index < otpFields.length - 1) {
                        otpFields[index + 1].requestFocus();
                    } else if (s.length() == 0 && index > 0) {
                        otpFields[index - 1].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void verifyEmail(String code, String email) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_verify,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response.trim());
                        if ("1".equals(jsonResponse.optString("success", "0"))) {
                            Toast.makeText(getContext(), "Email verification successful!", Toast.LENGTH_SHORT).show();

                            // Create a new instance of ForgotPasswordFragment3 with arguments
                            ForgotPasswordFragment3 fragment = new ForgotPasswordFragment3();
                            Bundle args = new Bundle();
                            args.putString("email", email);
                            fragment.setArguments(args);

                            // Navigate to ForgotPasswordFragment3
                            getParentFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, fragment)
                                    .addToBackStack(null)
                                    .commit();

                        } else {
                            Toast.makeText(getContext(), "Verification failed", Toast.LENGTH_SHORT).show();
                            verifyBtn.setEnabled(true);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(getContext(), "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("verification_code", code);
                params.put("email", email);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

}
