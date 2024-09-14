package com.example.thirsttap.Login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.thirsttap.ForgotPassword.ForgotPasswordActivity;
import com.example.thirsttap.MainActivity;
import com.example.thirsttap.R;
import com.example.thirsttap.Signup.SignupBottomSheet;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginBottomSheet extends BottomSheetDialogFragment {
    TextInputEditText email, password;
    Button loginBtn, googleBtn, forgotPassBtn;
    TextView signUp;
    String url_login = "https://scarlet2.io/Yankin/ThirstTap/login.php"; // Ensure this is correct
    TextInputLayout loginPass, loginEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_layout, container, false);

        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        loginBtn = view.findViewById(R.id.login_button);
        googleBtn = view.findViewById(R.id.google_button);
        signUp = view.findViewById(R.id.sign_up);
        loginPass = view.findViewById(R.id.password_layout);
        loginEmail = view.findViewById(R.id.email_layout);
        forgotPassBtn = view.findViewById(R.id.forgot_pass_button);

        forgotPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });


        // LOGIN: Signing in
        loginBtn.setOnClickListener(v -> {
            String emailInput = email.getText().toString().trim();
            String passwordInput = password.getText().toString().trim();

            // Input validation
            if (!validateEmail() || !validatePassword()) {
                return;
            }

            // Perform network request for authentication
            authenticateUser(emailInput, passwordInput);

        });

        signUp.setOnClickListener(v -> {
            dismiss();
            SignupBottomSheet signUpBottomSheet = new SignupBottomSheet();
            signUpBottomSheet.show(getParentFragmentManager(), "SignUpBottomSheet");
        });

        googleBtn.setOnClickListener(v -> {
            // Handle Google login
        });

        return view;
    }

    private boolean validateEmail() {
        String val = email.getText().toString().trim();

        if (val.isEmpty()) {
            loginEmail.setErrorEnabled(true);
            loginEmail.setError("Field cannot be empty");
            return false;
        } else {
            loginEmail.setError(null);
            loginEmail.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePassword() {
        String val = password.getText().toString().trim();
        if (val.isEmpty()) {
            loginPass.setError("Field cannot be empty");
            return false;
        } else {
            loginPass.setErrorEnabled(false);
            loginPass.setError(null);
            return true;
        }
    }

    private void authenticateUser(String email, String password) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_login,
                response -> {
                    Log.d("LoginBottomSheet", "Server response: " + response);
                    try {
                        // Directly parse the response as JSON
                        JSONObject jsonResponse = new JSONObject(response);
                        String success = jsonResponse.getString("success");

                        if (success.equalsIgnoreCase("1")) {
                            if (jsonResponse.has("token")) {
                                String token = jsonResponse.getString("token");
                                saveToken(token);

                                // Start MainActivity
                                if (getActivity() != null) {
                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    getActivity().runOnUiThread(() -> {
                                        startActivity(intent);
                                        dismiss();
                                    });
                                }
                            } else {
                                Log.d("LoginBottomSheet", "Token not found in response");
                            }
                        } else {
                            String message = jsonResponse.optString("message", "Authentication failed");
                            // Set error state for password input
                            loginPass.setErrorEnabled(true);
                            loginPass.setError(message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(getContext(), "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    error.printStackTrace(); // Print stack trace for debugging
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };

        // Add the request to the RequestQueue
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(stringRequest);
    }


    private void saveToken(String token) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("auth_token", token);
        editor.apply();
    }
}
