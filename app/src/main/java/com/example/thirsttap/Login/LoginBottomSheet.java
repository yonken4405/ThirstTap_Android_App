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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.thirsttap.AddressesPage.AddressActivity;
import com.example.thirsttap.ClientOnBoarding.OnboardingActivity;
import com.example.thirsttap.ForgotPassword.ForgotPasswordActivity;
import com.example.thirsttap.MainActivity;
import com.example.thirsttap.R;
import com.example.thirsttap.Signup.EmailVerificationBottomSheet;
import com.example.thirsttap.Signup.SignupBottomSheet;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginBottomSheet extends BottomSheetDialogFragment {
    private TextInputEditText email, password;
    private Button loginBtn, googleBtn, forgotPassBtn;
    private TextView signUp;
    private String url_login = "https://thirsttap.scarlet2.io/Backend/login.php"; // Ensure this is correct
    private String url_resendCode = "https://thirsttap.scarlet2.io/Backend/resendCode.php";
    private TextInputLayout loginPass, loginEmail;
    private boolean isNewUser;
    private ProgressBar loader;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_layout, container, false);
        loader = view.findViewById(R.id.loader);

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
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            getActivity().runOnUiThread(() -> {
                startActivity(intent);
                dismiss();
            });
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
        loader.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_login,
                response -> {
                    loader.setVisibility(View.GONE);
                    Log.d("LoginBottomSheet", "Server response: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String success = jsonResponse.getString("success");

                        if (success.equalsIgnoreCase("1")) {
                            if (jsonResponse.has("token")) {
                                String token = jsonResponse.getString("token");
                                saveToken(token);

                                // Extract user data
                                JSONObject userData = jsonResponse.getJSONObject("user_data");
                                String userid = userData.getString("userid");
                                String userEmail = userData.getString("email");
                                String userName = userData.getString("name");
                                String userPhoneNum = userData.getString("phone_num");
                                // Parse is_new_user as a boolean
                                isNewUser = userData.getInt("is_new_user") == 1;

                                // Save the user's profile data
                                saveUserProfile(userEmail, userName, userPhoneNum, userid, isNewUser);

                                // Start the appropriate activity based on the user's status
                                if (isNewUser) {
                                    // Navigate to setup or onboarding screen for new users
                                    Intent intent = new Intent(getActivity(), AddressActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    dismiss();
                                } else {
                                    // Start MainActivity for returning users
                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    dismiss();
                                }
                            } else {
                                Log.d("LoginBottomSheet", "Token not found in response");
                            }
                        } else {
                            String message = jsonResponse.optString("message", "Authentication failed");
                            // Check for specific unverified user message
                            if (message.contains("Unverified user")) {
                                dismiss();
                                showVerificationBottomSheet(email);
                            } else {
                                loginPass.setErrorEnabled(true);
                                loginPass.setError(message);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    loader.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
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


    private void saveUserProfile(String email, String name, String phoneNum, String userid, boolean isNewUser) {
        // You can use SharedPreferences to store user profile data locally
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_profile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userid", userid);
        editor.putString("email", email);
        editor.putString("name", name);
        editor.putString("phone_num", phoneNum);
        editor.putBoolean("isNewUser", isNewUser); // Save it as a boolean
        editor.apply();
    }


    private void saveToken(String token) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("auth_token", token);
        editor.apply();
    }

    private void showVerificationBottomSheet(String email) {
        if (getParentFragmentManager() != null) {
            resendCode(email);
            EmailVerificationBottomSheet verificationBottomSheet = EmailVerificationBottomSheet.newInstance(email, "forgotPassword");
            verificationBottomSheet.show(getParentFragmentManager(), "EmailVerificationBottomSheet");
        } else {
            Log.e("SignupBottomSheet", "ParentFragmentManager is null");
        }
    }

    private void resendCode(String email) {
        loader.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_resendCode,
                response -> {
                    loader.setVisibility(View.GONE);
                    Log.d("ResendCode", "Server response: " + response);

                    try {
                        JSONObject jsonResponse = new JSONObject(response.trim());
                        if ("1".equals(jsonResponse.optString("success", "0"))) {
                            if (isAdded()) { // Check if fragment is added to its activity
                                Toast.makeText(getContext(), "Code resent successfully!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if (isAdded()) {
                                Toast.makeText(getContext(), "Resending failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    loader.setVisibility(View.GONE);
                    if (isAdded()) {
                        Toast.makeText(getContext(), "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                return params;
            }
        };

        // Limit resending for once every 5 secs.
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,  // Timeout in milliseconds
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, // Set this to 0 to disable retries
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }




}
