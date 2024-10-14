package com.example.thirsttap.AccountPage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.thirsttap.ClientOnBoarding.OnboardingActivity;
import com.example.thirsttap.R;
import com.example.thirsttap.Signup.EmailVerificationBottomSheet;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangePasswordFragment extends Fragment {
    private Button saveBtn;
    private ImageButton backBtn;
    private String userId, email, name, phoneNum;
    private TextInputEditText oldPass, newPass, conPass;
    private String oldPassword, newPassword, confirmNewPassword;
    private TextInputLayout oldError, newError, conError;
    private ProgressBar loader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.change_password_fragment, container, false);
        loader = view.findViewById(R.id.loader);

        saveBtn = view.findViewById(R.id.save_button);
        backBtn = view.findViewById(R.id.back_button);

        oldPass = view.findViewById(R.id.old_password);
        newPass = view.findViewById(R.id.new_password);
        conPass = view.findViewById(R.id.confirm_password);

        oldError = view.findViewById(R.id.old_error);
        newError = view.findViewById(R.id.new_error);
        conError = view.findViewById(R.id.confirm_password_error);

        // Retrieve user profile data from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_profile", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userid", "default_userid");
        email = sharedPreferences.getString("email", "default_email");
        name = sharedPreferences.getString("name", "default_name");
        phoneNum = sharedPreferences.getString("phone_num", "default_phone_num");

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountFragment fragment = new AccountFragment();
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldPassword = oldPass.getText().toString().trim();
                newPassword = newPass.getText().toString().trim();
                confirmNewPassword = conPass.getText().toString().trim();

                // Validate all inputs and show errors at once if needed
                String validationErrors = validateInputs(oldPassword, newPassword, confirmNewPassword);
                if (!validationErrors.isEmpty()) {
                    Toast.makeText(getContext(), validationErrors, Toast.LENGTH_LONG).show();
                } else {
                    // Show confirmation dialog if all validations pass
                    showConfirmationDialog();
                }
            }
        });

        return view;
    }

    private void showConfirmationDialog() {
        // Validate old password before showing dialog
        if (!validateOldPassword(oldPassword)) {
            return; // Exit if old password is invalid
        }

        new AlertDialog.Builder(getContext())
                .setTitle("Confirm Change Password")
                .setMessage("Are you sure you want to change your password?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Proceed with password change
                        if (validatePassword(newPassword) && confirmPassword(newPassword, confirmNewPassword)) {
                            changePassword(userId, oldPassword, newPassword);

                        }
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private boolean validateOldPassword(String oldPassword) {
        if (oldPassword.isEmpty()) {
            oldError.setError("Field cannot be empty");
            return false;
        } else {
            oldError.setError(null);
            return true;
        }
    }

    private void changePassword(String userId, String oldPassword, String newPassword) {
        loader.setVisibility(View.VISIBLE);
        String url = "https://thirsttap.scarlet2.io/Backend/updatePassword.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    loader.setVisibility(View.GONE);
                    try {
                        JSONObject jsonObject = new JSONObject(response.trim());
                        if (jsonObject.getString("status").equals("success")) {
                            Toast.makeText(getContext(), "Password changed successfully! You must log in again.", Toast.LENGTH_SHORT).show();
                            signOut();
                            // Navigate back or perform other actions
                        } else {
                            oldError.setError("Current password is incorrect");
                            Log.d("ChangePasswordFragment", "Failed to change password");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("response change password", response);
                },
                error -> {
                    loader.setVisibility(View.GONE);

                    Log.e("VolleyError", "Error: " + error.getMessage());
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                params.put("old_password", oldPassword);
                params.put("new_password", newPassword);
                return params;
            }
        };

        // Set retry policy if necessary
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000, // timeout in milliseconds
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(getActivity().getApplicationContext()).add(stringRequest);
    }

    public void signOut() {
        // Clear user data from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_profile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Remove all user data
        editor.apply();

        // Navigate to the login fragment
        Intent intent = new Intent(getContext(), OnboardingActivity.class);
        startActivity(intent);
    }

    private String validateInputs(String oldPassword, String newPassword, String confirmNewPassword) {
        StringBuilder errors = new StringBuilder();

        // Validate old password
        if (!validateOldPassword(oldPassword)) {
            errors.append("Current password is incorrect\n");
        }

        // Validate new password
        if (!validatePassword(newPassword)) {
            errors.append("New password does not meet the requirements\n");
        }

        // Validate confirm password
        if (!confirmPassword(newPassword, confirmNewPassword)) {
            errors.append("Passwords do not match\n");
        }

        return errors.toString();
    }


    private boolean validatePassword(String password) {
        String passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$";
        if (password.isEmpty()) {
            newError.setError("Field cannot be empty");
            return false;
        } else if (!password.matches(passwordPattern)) {
            newError.setError("Password must have at least:\n- 1 uppercase letter\n- 1 lowercase letter\n- 1 digit\n- 8 characters");
            return false;
        } else {
            newError.setError(null);
            return true;
        }
    }

    private boolean confirmPassword(String newPassword, String confirmNewPassword) {
        if (confirmNewPassword.isEmpty()) {
            conError.setError("Field cannot be empty");
            return false;
        } else if (!newPassword.equals(confirmNewPassword)) {
            conError.setError("Passwords do not match");
            return false;
        } else {
            conError.setError(null);
            return true;
        }
    }
}
