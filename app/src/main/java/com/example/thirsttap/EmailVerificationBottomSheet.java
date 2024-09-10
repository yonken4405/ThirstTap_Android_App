package com.example.thirsttap;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
public class EmailVerificationBottomSheet extends BottomSheetDialogFragment {

    private String url_verify = "http://192.168.20.170/ThirstTap/verify.php";
    private EditText[] otpFields;
    private Button verifyButton;
    private ImageButton backBtn;
    private String email, digit1, digit2, digit3, digit4, code;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_email_verification, container, false);

        verifyButton = view.findViewById(R.id.verify_button);
        backBtn = view.findViewById(R.id.back_button);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                showLoginBottomSheet();
            }
        });

        //automate textfield transfer
        otpFields = new EditText[4];
        otpFields[0] = view.findViewById(R.id.digit1);
        otpFields[1] = view.findViewById(R.id.digit2);
        otpFields[2] = view.findViewById(R.id.digit3);
        otpFields[3] = view.findViewById(R.id.digit4);
        setupOtpFields();

        //get email from signup
        if (getArguments() != null) {
            email = getArguments().getString("email");
        }

        verifyButton.setOnClickListener(v -> {

            digit1 = otpFields[0].getText().toString().trim();
            digit2 = otpFields[1].getText().toString().trim();
            digit3 = otpFields[2].getText().toString().trim();
            digit4 = otpFields[3].getText().toString().trim();
            code =  digit1 + digit2 + digit3 + digit4;
            //code = getOtpCode();
            if (!code.isEmpty() && email != null) {
                verifyEmail(code, email);
            } else {
                Toast.makeText(getContext(), "Please enter the verification code", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private String getOtpCode() {
        StringBuilder codeBuilder = new StringBuilder();
        for (EditText field : otpFields) {
            codeBuilder.append(field.getText().toString().trim());
        }
        return codeBuilder.toString();
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
                            Toast.makeText(getContext(), "Email verified successfully!", Toast.LENGTH_SHORT).show();
                            showLoginBottomSheet();
                        } else {
                            Toast.makeText(getContext(), "Verification failed", Toast.LENGTH_SHORT).show();
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

    private void showLoginBottomSheet() {
        if (getParentFragmentManager() != null) {
            LoginBottomSheet loginBottomSheet = new LoginBottomSheet();
            loginBottomSheet.show(getParentFragmentManager(), "LoginBottomSheet");
        } else {
            Log.e("SignupBottomSheet", "ParentFragmentManager is null");
        }
    }

    public static EmailVerificationBottomSheet newInstance(String email) {
        EmailVerificationBottomSheet fragment = new EmailVerificationBottomSheet();
        Bundle args = new Bundle();
        args.putString("email", email);
        fragment.setArguments(args);
        return fragment;
    }
}

