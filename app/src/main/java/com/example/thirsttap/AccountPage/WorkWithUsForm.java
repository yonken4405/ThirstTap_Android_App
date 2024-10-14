package com.example.thirsttap.AccountPage;

import com.android.volley.DefaultRetryPolicy;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.thirsttap.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WorkWithUsForm extends Fragment {

    private static final int PICK_IMAGE_REQUEST_ID = 1;
    private static final int PICK_IMAGE_REQUEST_PERMIT = 2;
    private static final int PICK_IMAGE_REQUEST_CERT = 3;
    private int currentImageRequest;
    private String imageBase64ID, imageBase64Permit, imageBase64Cert;
    private EditText companyEditText, firstNameEditText, lastNameEditText, emailEditText, mobileEditText;
    private EditText streetEditText, unitEditText, cityEditText, regionEditText, commentsEditText;
    private ImageView idPreview, permitPreview, certPreview;
    private Uri imageUri;
    private ImageButton backBtn;
    private ProgressBar loader;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.work_with_us_form, container, false);
        loader = view.findViewById(R.id.loader);

        // Initialize EditText and ImageView elements
        companyEditText = view.findViewById(R.id.company);
        firstNameEditText = view.findViewById(R.id.first_name);
        lastNameEditText = view.findViewById(R.id.last_name);
        emailEditText = view.findViewById(R.id.email);
        mobileEditText = view.findViewById(R.id.mobile);
        streetEditText = view.findViewById(R.id.street);
        unitEditText = view.findViewById(R.id.unit);
        cityEditText = view.findViewById(R.id.city);
        regionEditText = view.findViewById(R.id.region);
        commentsEditText = view.findViewById(R.id.comments);
        idPreview = view.findViewById(R.id.image_preview_id);
        permitPreview = view.findViewById(R.id.image_preview_permit);
        certPreview = view.findViewById(R.id.image_preview_cert);
        backBtn = view.findViewById(R.id.back_button);

        // Set onClickListeners for the buttons
        Button selectImageButton = view.findViewById(R.id.select_image_button);
        selectImageButton.setOnClickListener(v -> {
            currentImageRequest = PICK_IMAGE_REQUEST_ID;
            selectImage();
        });

        Button selectImageButton2 = view.findViewById(R.id.select_image_button2);
        selectImageButton2.setOnClickListener(v -> {
            currentImageRequest = PICK_IMAGE_REQUEST_PERMIT;
            selectImage();
        });

        Button selectImageButton3 = view.findViewById(R.id.select_image_button3);
        selectImageButton3.setOnClickListener(v -> {
            currentImageRequest = PICK_IMAGE_REQUEST_CERT;
            selectImage();
        });

        // Initialize and set onClickListener for the submit button
        Button submitButton = view.findViewById(R.id.submit_button);
        submitButton.setOnClickListener(v -> submitForm());

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WorkWithUsFragment fragment = new WorkWithUsFragment();
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
            }
        });


        return view;
    }

    // Method to handle image selection
    private void selectImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ uses READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_MEDIA_IMAGES}, currentImageRequest);
            } else {
                openFileChooser();
            }
        } else {
            // Android versions below 13 use READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, currentImageRequest);
            } else {
                openFileChooser();
            }
        }
    }

    // Open the file chooser to select an image
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), currentImageRequest);
    }

    // Handle the selected image result
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            try {
                // Convert selected image to Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);

                // Convert bitmap to base64 string
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                String imageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);

                // Switch case to handle each image request type
                switch (currentImageRequest) {
                    case PICK_IMAGE_REQUEST_ID:
                        idPreview.setImageURI(imageUri);
                        idPreview.setVisibility(View.VISIBLE);
                        imageBase64ID = imageBase64; // Store base64 for ID image
                        break;

                    case PICK_IMAGE_REQUEST_PERMIT:
                        permitPreview.setImageURI(imageUri);
                        permitPreview.setVisibility(View.VISIBLE);
                        imageBase64Permit = imageBase64; // Store base64 for permit image
                        break;

                    case PICK_IMAGE_REQUEST_CERT:
                        certPreview.setImageURI(imageUri);
                        certPreview.setVisibility(View.VISIBLE);
                        imageBase64Cert = imageBase64; // Store base64 for certification image
                        break;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Handle permission request results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openFileChooser();
        } else {
            Toast.makeText(requireContext(), "Permission denied!", Toast.LENGTH_SHORT).show();
        }
    }

    // Validate fields before submitting the form
    private boolean validateFields() {
        if (companyEditText.getText().toString().trim().isEmpty()) {
            companyEditText.setError("Company name is required");
            return false;
        }
        // Add more validation as needed
        return true;
    }

    // Submit form data to the server
    private void submitForm() {
        if (validateFields()) {
            String company = companyEditText.getText().toString().trim();
            String firstName = firstNameEditText.getText().toString().trim();
            String lastName = lastNameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String mobile = mobileEditText.getText().toString().trim();
            String street = streetEditText.getText().toString().trim();
            String unit = unitEditText.getText().toString().trim();
            String city = cityEditText.getText().toString().trim();
            String region = regionEditText.getText().toString().trim();
            String comments = commentsEditText.getText().toString().trim();

            loader.setVisibility(View.VISIBLE);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://thirsttap.scarlet2.io/Backend/emailPartnerApplication.php",
                    response -> {
                        loader.setVisibility(View.GONE);
                        // Log the raw response for debugging
                        Log.d("Response", "Response: " + response);
                        try {
                            // Try to parse the response as JSON
                            JSONObject jsonResponse = new JSONObject(response);
                            String status = jsonResponse.getString("status");
                            String message = jsonResponse.getString("message");
                            if (status.equals("success")) {
                                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                                AccountFragment fragment = new AccountFragment();
                                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
                            } else {
                                Toast.makeText(requireContext(), "Submission failed: " + message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            // Handle JSON parsing error
                            Log.e("JSONError", "Error parsing JSON: " + e.getMessage());
                            Toast.makeText(requireContext(), "Error parsing response. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        loader.setVisibility(View.GONE);
                        // Handle network errors
                        Toast.makeText(requireContext(), "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("company", company);
                    params.put("first_name", firstName);
                    params.put("last_name", lastName);
                    params.put("email", email);
                    params.put("mobile", mobile);
                    params.put("street", street);
                    params.put("unit", unit);
                    params.put("city", city);
                    params.put("region", region);
                    params.put("comments", comments);
                    params.put("image_id", imageBase64ID);
                    params.put("image_permit", imageBase64Permit);
                    params.put("image_cert", imageBase64Cert);
                    return params;
                }
            };

            // Set a retry policy for the request
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));

            // Add the request to the Volley request queue
            RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
            requestQueue.add(stringRequest);
        }
    }

}

