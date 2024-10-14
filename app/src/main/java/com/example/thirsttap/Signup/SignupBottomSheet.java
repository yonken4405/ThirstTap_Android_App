package com.example.thirsttap.Signup;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.thirsttap.AccountPage.TermsAndConditionsFragment;
import com.example.thirsttap.Login.LoginBottomSheet;
import com.example.thirsttap.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignupBottomSheet extends BottomSheetDialogFragment {
    private TextInputEditText signupEmail, signupPassword, confirmPassword, signupName, signupPhoneNum;
    private String email, password, confirmPass, name, phoneNum;
    private CheckBox cb;
    private TextInputLayout passError, conPassError, emailLayout, nameLayout, numLayout;
    private TextView errorMess, termsConditions, privacyPolicy;
    private ImageButton backBtn;
    private Button signupBtn;
    private ProgressBar loader;


    // URL for the signup endpoint
    String url_signup = "https://thirsttap.scarlet2.io/Backend/register.php"; // Ensure this is correct

    @SuppressLint("ResourceAsColor")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.signup_layout, container, false);
        loader = view.findViewById(R.id.loader);

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
        termsConditions = view.findViewById(R.id.terms_conditions);
        privacyPolicy = view.findViewById(R.id.privacy_policy);

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
                    cb.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.blueFont)));
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
                    cb.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.gray)));
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
            if (!validateFields()) {
                return;
            }
            signupUser(email, password, name, phoneNum);
        });

        termsConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the current BottomSheetDialog
                dismiss();

                loader.setVisibility(View.VISIBLE);

                TermsAndConditionsBottomSheet termsSheet = new TermsAndConditionsBottomSheet();
                termsSheet.show(getParentFragmentManager(), "Terms and Conditions");


            }
        });

        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the current BottomSheetDialog
                dismiss();

                loader.setVisibility(View.VISIBLE);

                TermsAndConditionsBottomSheet termsSheet = new TermsAndConditionsBottomSheet();
                termsSheet.show(getParentFragmentManager(), "Terms and Conditions");

                // Use FragmentTransaction's `runOnCommit` to hide the ProgressBar after the transaction is done
                getParentFragmentManager().beginTransaction().runOnCommit(new Runnable() {
                    @Override
                    public void run() {
                        // Hide the ProgressBar after fragment is loaded
                        loader.setVisibility(View.GONE);
                    }
                });
            }
        });

//        // Set up the ScrollView touch handling
//        NestedScrollView scrollView = view.findViewById(R.id.scrollView);
//        scrollView.setOnTouchListener((v, event) -> {
//            // Disable touch events on the scroll view to prevent dismissing the BottomSheet
//            return event.getAction() == MotionEvent.ACTION_MOVE;
//        });



        return view;
    }



    private boolean validateFields() {
        boolean isValid = true;

        // Reset error messages
        nameLayout.setError(null);
        emailLayout.setError(null);
        numLayout.setError(null);
        passError.setError(null);
        conPassError.setError(null);

        // Validate each field and track if any are invalid
        if (!validateName()) {
            isValid = false;
        }
        if (!validateEmail()) {
            isValid = false;
        }
        if (!validateNumber()) {
            isValid = false;
        }
        if (!validatePassword()) {
            isValid = false;
        }
        if (!confirmPassword()) {
            isValid = false;
        }

        return isValid;
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
        } else if (!Patterns.EMAIL_ADDRESS.matcher(val).matches()) {
            emailLayout.setErrorEnabled(true);
            emailLayout.setError("Please enter a valid email");
            return false;
        } else {
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
        } else if (!val.matches("(09)\\d{9}")) {
            numLayout.setErrorEnabled(true);
            numLayout.setError("Please enter a valid phone number (09XXXXXXXXX)");
            return false;
        } else {
            numLayout.setError(null);
            numLayout.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePassword() {
        String val = signupPassword.getText().toString().trim();
        String passwordVal = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$"; // Password regex

        if (val.isEmpty()) {
            passError.setErrorEnabled(true);
            passError.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(passwordVal)) {
            Log.d("passwordlength", "pass length not enough");
            passError.setErrorEnabled(true);  // Ensure error display is explicitly enabled
            passError.setError("Password must have at least:\n- 1 uppercase letter\n- 1 lowercase letter\n- 1 digit\n- 8 characters");
            return false;
        } else {
            passError.setError(null);
            passError.setErrorEnabled(false);  // Explicitly disable error message when valid
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
        } else if (!val.equals(val2)) {
            conPassError.setErrorEnabled(true);
            passError.setErrorEnabled(true);
            conPassError.setError("Passwords do not match");
            passError.setError("Passwords do not match");
            return false;
        } else {
            conPassError.setError(null);
            conPassError.setErrorEnabled(false);
//            passError.setErrorEnabled(false);
//            passError.setError(null);
            return true;
        }
    }

    private void signupUser(String email, String password, String name, String phoneNum) {
        loader.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_signup,
                response -> {
                    loader.setVisibility(View.GONE);
                    Log.d("SignupBottomSheet", "Server response: " + response);

                    try {
                        // Check if the response is a valid JSON object
                        if (response.trim().startsWith("{")) {
                            JSONObject jsonResponse = new JSONObject(response.trim());
                            String success = jsonResponse.getString("success");

                            if (success.equals("1")) {
                                dismiss();
                                Toast.makeText(getActivity(), "Signup successful!", Toast.LENGTH_SHORT).show();
                                showVerificationBottomSheet(email);

                            } else {
                                String message = jsonResponse.getString("message");
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Handle unexpected response format
                            Toast.makeText(getActivity(), "Unexpected response from server", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    loader.setVisibility(View.GONE);
                    Log.e("SignupBottomSheet", "Error: " + error.getMessage());
                    Toast.makeText(getActivity(), "Signup failed. Please try again later.", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Create a map for POST parameters
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                params.put("name", name);
                params.put("phone_num", phoneNum);
                return params;
            }
        };

        // Set timeout for the request
        RetryPolicy policy = new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);

        // Add the request to the RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    private void showVerificationBottomSheet(String email) {
        if (getParentFragmentManager() != null) {
            EmailVerificationBottomSheet verificationBottomSheet = EmailVerificationBottomSheet.newInstance(email, "signup");
            verificationBottomSheet.show(getParentFragmentManager(), "EmailVerificationBottomSheet");
        } else {
            Log.e("SignupBottomSheet", "ParentFragmentManager is null");
        }
    }

    private void showLoginBottomSheet() {
        LoginBottomSheet loginBottomSheet = new LoginBottomSheet();
        loginBottomSheet.show(getChildFragmentManager(), loginBottomSheet.getTag());
    }
}
