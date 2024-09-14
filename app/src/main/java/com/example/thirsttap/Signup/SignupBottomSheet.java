package com.example.thirsttap.Signup;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.thirsttap.Login.LoginBottomSheet;
import com.example.thirsttap.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignupBottomSheet extends BottomSheetDialogFragment {
    TextInputEditText signupEmail, signupPassword, confirmPassword, signupName, signupPhoneNum;
    String email, password, confirmPass, name, phoneNum;
    CheckBox cb;
    TextInputLayout passError, conPassError, emailLayout, nameLayout, numLayout;
    TextView errorMess;
    ImageButton backBtn;
    Button signupBtn;

    // URL for the signup endpoint
    String url_signup = "https://scarlet2.io/Yankin/ThirstTap/register.php"; // Ensure this is correct

    @SuppressLint("ResourceAsColor")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.signup_layout, container, false);

        signupBtn = view.findViewById(R.id.signup_button);
        signupEmail = view.findViewById(R.id.signup_email);
        signupPassword = view.findViewById(R.id.signup_password);
        confirmPassword = view.findViewById(R.id.confirm_password);
        signupName = view.findViewById(R.id.signup_name);
        signupPhoneNum = view.findViewById(R.id.signup_phone_number);
        cb = view.findViewById(R.id.checkbox);
        passError = view.findViewById(R.id.password_error);
        conPassError = view.findViewById(R.id.confirm_password_error);
        errorMess = view.findViewById(R.id.error_message);
        backBtn = view.findViewById(R.id.back_button);
        emailLayout = view.findViewById(R.id.email_layout);
        nameLayout = view.findViewById(R.id.name_layout);
        numLayout = view.findViewById(R.id.number_layout);




        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                showLoginBottomSheet();
            }
        });

        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                email = signupEmail.getText().toString().trim();
                password = signupPassword.getText().toString().trim();
                confirmPass = confirmPassword.getText().toString().trim();
                name = signupName.getText().toString().trim();
                phoneNum = signupPhoneNum.getText().toString().trim();

                if (isChecked) {
                    if (!name.isEmpty() && !email.isEmpty() && !phoneNum.isEmpty() && !password.isEmpty() && !confirmPass.isEmpty()) {
                        signupBtn.setBackgroundColor(getResources().getColor(R.color.blueFont));
                        signupBtn.setEnabled(true);
                        errorMess.setVisibility(View.GONE);
                    }
                } else {
                    signupBtn.setBackgroundColor(getResources().getColor(R.color.InputBoxOutline));
                    signupBtn.setEnabled(false);
                    errorMess.setText("Please agree to the terms and conditions");
                    errorMess.setVisibility(View.VISIBLE);
                }
            }
        });

        signupBtn.setOnClickListener(v -> {
            email = signupEmail.getText().toString().trim();
            password = signupPassword.getText().toString().trim();
            confirmPass = confirmPassword.getText().toString().trim();
            name = signupName.getText().toString().trim();
            phoneNum = signupPhoneNum.getText().toString().trim();

            // Proceed with signup if form is valid
            if (!validateName() || !validateEmail() || !validateNumber() || !validatePassword() || !confirmPassword()) {
                return;
            }
            signupUser(email, password, name, phoneNum);
        });


        return view;
    }

    private boolean validateName() {
        String val = signupName.getText().toString().trim();

        if (val.isEmpty()) {
            nameLayout.setErrorEnabled(true);
            nameLayout.setError("Field cannot be empty");
            return false;
        } else {
            nameLayout.setError(null);
            nameLayout.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateEmail() {
        String val = signupEmail.getText().toString().trim();

        if (val.isEmpty()) {
            emailLayout.setErrorEnabled(true);
            emailLayout.setError("Field cannot be empty");
            return false;
        } else if(!Patterns.EMAIL_ADDRESS.matcher(val).matches()){
            emailLayout.setErrorEnabled(true);
            emailLayout.setError("Please enter a valid email");
            return false;
        }else {
            emailLayout.setError(null);
            emailLayout.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateNumber() {
        String val = signupPhoneNum.getText().toString().trim();

        if (val.isEmpty()) {
            numLayout.setErrorEnabled(true);
            numLayout.setError("Field cannot be empty");
            return false;
        } else if(!val.matches("(09)\\d{9}")){
            numLayout.setErrorEnabled(true);
            numLayout.setError("Please enter a valid phone number");
            return false;
        }else {
            numLayout.setError(null);
            numLayout.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePassword() {
        String val = signupPassword.getText().toString().trim();
        String passwordVal = "^" + //Matches the beginning of the string.
                "(?=.*[A-Z])" + //Positive lookahead assertion for at least one uppercase letter.
                "(?=.*[a-z])" + //Positive lookahead assertion for at least one lowercase letter.
                "(?=.*\\d)" + //Positive lookahead assertion for at least one digit.
                ".{8,}" + //At least 8 digits
                // + //Positive lookahead assertion for at least one special character.
                //"[A-Za-z\\d!@#$%^&*]{8,}" + //Matches a string containing uppercase letters, lowercase letters, digits, and special characters with a minimum length of 8.
                "$"; //Matches the end of the string.
        if (val.isEmpty()) {
            passError.setError("Field cannot be empty");
            return false;
        } else if(!val.matches(passwordVal)){
            passError.setErrorEnabled(true);
            passError.setError("Password must have at least: \n- 1 uppercase letter \n- 1 lowercase letter \n- 1 digit \n- 8 characters");
            return false;
        } else {
            passError.setErrorEnabled(false);
            passError.setError(null);
            return true;
        }
    }

    private boolean confirmPassword() {
        String val = signupPassword.getText().toString().trim();
        String val2 = confirmPassword.getText().toString().trim();

        if (val2.isEmpty()) {
            conPassError.setErrorEnabled(true);
            conPassError.setError("Field cannot be empty");
            return false;
        } else if(!val.equals(val2)){
            conPassError.setErrorEnabled(true);
            passError.setErrorEnabled(true);
            conPassError.setError("Passwords do not match");
            passError.setError("Passwords do not match");
            return false;
        } else {
            conPassError.setError(null);
            conPassError.setErrorEnabled(false);
            passError.setErrorEnabled(false);
            passError.setError(null);
            return true;
        }
    }

    private void signupUser(String email, String password, String name, String phoneNum) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_signup,
                response -> {

                    Log.d("SignupBottomSheet", "Server response: " + response);

                    try {
                        // Check if the response is a valid JSON object
                        if (response.trim().startsWith("{")) {
                            JSONObject jsonResponse = new JSONObject(response.trim());
                            String success = jsonResponse.optString("success", "0");

                            if ("1".equals(success)) {
                                onSignupSuccess();
                            } else {
                                String message = jsonResponse.optString("message", "Signup failed");
                                onSignupFailure(message);
                            }
                        } else {
                            // Handle cases where the response is not a JSON object
                            Log.e("SignupBottomSheet", "Invalid response format: " + response);
                            showToast("Invalid response from server");
                        }
                    } catch (JSONException e) {
                        Log.e("SignupBottomSheet", "JSON parsing error: ", e);
                        showToast("Error: " + e.getMessage());
                    }
                },
                error -> {
                    Log.e("SignupBottomSheet", "Network error: ", error);
                    showToast("Network error: " + error.getMessage());
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                params.put("name", name);
                params.put("phone_num", phoneNum);
                return params;
            }
        };

        //extend default timeout in case of low signal
        int socketTimeout = 30000; // 30 seconds timeout
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void onSignupSuccess() {
        Log.d("SignupBottomSheet", "Signup successful");
        getActivity().runOnUiThread(() -> {
            showToast("Account created successfully");
            dismiss();
            showVerificationBottomSheet(email);
        });

    }

    private void onSignupFailure(String message) {
        Log.e("SignupBottomSheet", "Signup failed: " + message);
        showToast(message);
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }


    private void showLoginBottomSheet() {
        if (getParentFragmentManager() != null) {
            LoginBottomSheet loginBottomSheet = new LoginBottomSheet();
            loginBottomSheet.show(getParentFragmentManager(), "LoginBottomSheet");
        } else {
            Log.e("SignupBottomSheet", "ParentFragmentManager is null");
        }
    }

    private void showVerificationBottomSheet(String email) {
        if (getParentFragmentManager() != null) {
            EmailVerificationBottomSheet verificationBottomSheet = EmailVerificationBottomSheet.newInstance(email);
            verificationBottomSheet.show(getParentFragmentManager(), "EmailVerificationBottomSheet");
        } else {
            Log.e("SignupBottomSheet", "ParentFragmentManager is null");
        }
    }


}