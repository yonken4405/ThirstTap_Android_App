package com.example.thirsttap.AccountPage;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.thirsttap.AddressesPage.Address;
import com.example.thirsttap.AddressesPage.AddressListFragment;
import com.example.thirsttap.ForgotPassword.ForgotPasswordFragment2;
import com.example.thirsttap.R;
import com.example.thirsttap.Signup.EmailVerificationBottomSheet;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditAccountFragment extends Fragment {
    private Button saveBtn;
    private ImageButton backBtn;
    private String userId, email, name, phoneNum;
    private TextInputEditText userEmail, userName, userPhoneNum;
    private String newEmail, newName, newPhoneNum;
    private TextInputLayout emailLayout, nameLayout, numLayout;
    private ProgressBar loader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_account_fragment, container, false);
        loader = view.findViewById(R.id.loader);

        saveBtn = view.findViewById(R.id.save_button);
        backBtn = view.findViewById(R.id.back_button);

        userEmail = view.findViewById(R.id.user_email);
        userName = view.findViewById(R.id.user_name);
        userPhoneNum = view.findViewById(R.id.user_phone_number);

        emailLayout = view.findViewById(R.id.email_layout);
        nameLayout = view.findViewById(R.id.name_layout);
        numLayout = view.findViewById(R.id.number_layout);

        // Retrieve user profile data from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_profile", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userid", "default_userid");
        email = sharedPreferences.getString("email", "default_email");
        name = sharedPreferences.getString("name", "default_name");
        phoneNum = sharedPreferences.getString("phone_num", "default_phone_num");

        SharedPreferences.Editor editor = sharedPreferences.edit();

        userEmail.setText(email);
        userName.setText(name);
        userPhoneNum.setText(phoneNum);

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
                newEmail = userEmail.getText().toString().trim();
                newName = userName.getText().toString().trim();
                newPhoneNum = userPhoneNum.getText().toString().trim();

                // Validate form fields
                if (!validateName() || !validateEmail() || !validateNumber()) {
                    return;
                }

                // Check if any data has changed
                boolean hasDataChanged = !newEmail.equals(email) || !newName.equals(name) || !newPhoneNum.equals(phoneNum);

                if (hasDataChanged) {
                    if (!newEmail.equals(email)) {
                        sendCode(email); // Send verification code for new email
                        EmailVerificationBottomSheet verificationBottomSheet = EmailVerificationBottomSheet.newInstance(email, "changeEmail");
                        verificationBottomSheet.show(getParentFragmentManager(), "EmailVerificationBottomSheet");

                        // Set the listener to handle verification success or failure
                        verificationBottomSheet.setVerificationListener(new EmailVerificationBottomSheet.VerificationListener() {
                            @Override
                            public void onVerificationSuccess(String email) {
                                // Email verified, now update the data
                                updateUserData(email, newEmail, newName, newPhoneNum);
                                editor.putString("email", newEmail);
                                editor.putString("name", newName);
                                editor.putString("phone_num", newPhoneNum);
                                editor.apply();

                                // Move to AccountFragment after successful update
                                AccountFragment fragment = new AccountFragment();
                                getParentFragmentManager().beginTransaction()
                                        .replace(R.id.fragment_container, fragment)
                                        .addToBackStack(null)
                                        .commit();
                            }

                            @Override
                            public void onVerificationFailure(String errorMessage) {
                                // Handle verification failure (e.g., show a toast or log)
                                Toast.makeText(getContext(), "Email verification failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        // No email change, directly update the data
                        editor.putString("email", newEmail);
                        editor.putString("name", newName);
                        editor.putString("phone_num", newPhoneNum);
                        editor.apply();
                        updateUserData(email, newEmail, newName, newPhoneNum);
                        AccountFragment fragment = new AccountFragment();
                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, fragment)
                                .addToBackStack(null)
                                .commit();
                    }
                } else {
                    Toast.makeText(getContext(), "No changes have been made!", Toast.LENGTH_SHORT).show();
                }
            }
        });







        return view;
    }

    private void updateUserData(String email, String newEmail, String newName, String newPhoneNum){
        loader.setVisibility(View.VISIBLE);
        String url = "https://thirsttap.scarlet2.io/Backend/updateUserData.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    loader.setVisibility(View.GONE);
                    try {
                        JSONObject jsonObject = new JSONObject(response.trim());
                        if (jsonObject.getString("success").equals("1")){
                            Toast.makeText(getContext(), "Information updated successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("EditAccountFragment", "Failed to update information");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("response update", response);
                },
                error -> {
                    loader.setVisibility(View.GONE);
                    Log.e("VolleyError", "Error: " + error.getMessage());
                }) {

            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("new_name", newName);
                params.put("new_email", newEmail);
                params.put("new_number", newPhoneNum);

                return params;
            }
        };
        Volley.newRequestQueue(getActivity().getApplicationContext()).add(stringRequest);
    }



    private boolean validateName() {
        String val = userName.getText().toString().trim();

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
        String val = userEmail.getText().toString().trim();

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
        String val = userPhoneNum.getText().toString().trim();

        if (val.isEmpty()) {
            numLayout.setErrorEnabled(true);
            numLayout.setError("Field cannot be empty");
            return false;
        } else if(!val.matches("(09)\\d{9}")){
            numLayout.setErrorEnabled(true);
            numLayout.setError("Please enter a valid phone number (09XXXXXXXXX)");
            return false;
        }else {
            numLayout.setError(null);
            numLayout.setErrorEnabled(false);
            return true;
        }
    }

    private void sendCode(String email) {
        loader.setVisibility(View.VISIBLE);
        String url_sendCode = "https://thirsttap.scarlet2.io/Backend/updateEmailCode.php";
        Log.d("SendCode", "Sending code for email: " + email);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_sendCode,
                response -> {
                    loader.setVisibility(View.GONE);
                    Log.d("SendCode", "Server response: " + response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response.trim());
                        String success = jsonResponse.optString("success", "0");

                        if ("1".equals(success)) {
                            Toast.makeText(getContext(), "Code sent successfully!", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(getContext(), "Sending failed", Toast.LENGTH_SHORT).show();

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
                params.put("newEmail", newEmail);
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
