package com.example.thirsttap.ForgotPassword;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.thirsttap.Login.LoginFragment;
import com.example.thirsttap.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotPasswordFragment3 extends Fragment {

    private String url_verify = "https://thirsttap.scarlet2.io/Backend/forgotPasswordReset.php";
    private ImageButton backBtn;
    private Button saveBtn;
    private String email, password;
    TextInputEditText pass, confirmPass;
    TextInputLayout passError, conPassError;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.forgot_pass_layout3, container, false);

        backBtn = view.findViewById(R.id.back_button);
        saveBtn = view.findViewById(R.id.save_button);
        pass = view.findViewById(R.id.reset_password);
        confirmPass = view.findViewById(R.id.confirm_reset_pass);
        passError = view.findViewById(R.id.password_error);
        conPassError = view.findViewById(R.id.confirm_password_error);

        // Retrieve the email from the forgotpassword fragment1
        Bundle args = getArguments();
        if (args != null) {
            email = args.getString("email");
        }

        backBtn.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        saveBtn.setOnClickListener(v -> {
            // Proceed with signup if form is valid
            if (!validatePassword() || !confirmPassword()) {
                return;
            }

            saveBtn.setEnabled(false);
            password = pass.getText().toString().trim();
            Log.d("resetPassword", "Email: " + email);
            Log.d("resetPassword", "Password: " + password);
            resetPassword(email, password);


        });

        return view;
    }

    private void resetPassword(String email, String password) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_verify,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response.trim());
                        if ("1".equals(jsonResponse.optString("success", "0"))) {
                            //Toast.makeText(getContext(), "Email verification successful!", Toast.LENGTH_SHORT).show();

                            showPopup();

                        } else {
                            Toast.makeText(getContext(), "Password reset failed", Toast.LENGTH_SHORT).show();
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
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private boolean validatePassword() {
        String val = pass.getText().toString().trim();
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
        String val = pass.getText().toString().trim();
        String val2 = confirmPass.getText().toString().trim();

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

    private void showPopup() {
        // Inflate the popup_layout.xml
        LayoutInflater inflater = getLayoutInflater(); // Use getLayoutInflater() instead of getSystemService
        View popupView = inflater.inflate(R.layout.email_verified_popup, null);

        // Create the PopupWindow with the size specified in XML
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // Allows dismissal when tapping outside

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // Show the PopupWindow at the center of the parent view
        // Use getView() to get the fragment's root view instead of findViewById()
        popupWindow.showAtLocation(getView(), Gravity.CENTER, 0, 0);

        // Handle close button inside the popup
        Button closeButton = popupView.findViewById(R.id.popup_close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();

                // After closing the popup, navigate to the LoginFragment
                startActivity(new Intent(getActivity(), LoginFragment.class));
            }


        });

    }
}
