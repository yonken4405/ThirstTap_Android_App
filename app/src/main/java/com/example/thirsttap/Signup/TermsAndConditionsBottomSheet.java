package com.example.thirsttap.Signup;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import com.example.thirsttap.R;

public class TermsAndConditionsBottomSheet extends BottomSheetDialogFragment {
    private ProgressBar loader;
    private WebView webView;
    private ImageButton closeButton;
    private RelativeLayout topPanel;
    private ConstraintLayout constraintLayout;
    private LinearLayout termsLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.terms_and_conditions_fragment, container, false);
        loader = view.findViewById(R.id.loader);
        webView = view.findViewById(R.id.termsWebView);
        closeButton = view.findViewById(R.id.closeButton);
        topPanel = view.findViewById(R.id.topPanel);
        constraintLayout = view.findViewById(R.id.constraintLayout);
        termsLayout = view.findViewById(R.id.terms_layout);


        //setup for bottomsheet
        termsLayout.setBackgroundResource(R.drawable.rounded_botsheet);
        topPanel.setVisibility(View.GONE);
        constraintLayout.setVisibility(View.VISIBLE);

        // Enable JavaScript if needed
        webView.getSettings().setJavaScriptEnabled(true);

        // Set a WebViewClient to handle events
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                loader.setVisibility(View.GONE); // Hide loader when content is ready
                webView.setVisibility(View.VISIBLE);
                closeButton.setVisibility(View.VISIBLE); // Show close button
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();

                // Check if the URL is a mailto link
                if (url.startsWith("mailto:")) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                    emailIntent.setData(Uri.parse(url)); // Only email apps should handle this
                    try {
                        startActivity(emailIntent);
                    } catch (ActivityNotFoundException e) {
                        // Handle the case where no email app is installed
                        Toast.makeText(view.getContext(), "No email client found", Toast.LENGTH_SHORT).show();
                    }
                    return true; // Indicates that the WebView should not load the URL
                }

                // For all other URLs, let the WebView load them
                view.loadUrl(url);
                return false; // Indicates that the WebView should load the URL
            }
        });

        // Load Terms and Conditions HTML
        webView.loadUrl("https://thirsttap.scarlet2.io/Backend/terms_and_conditions.html");

        // Close button logic
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                SignupBottomSheet signUpBottomSheet = new SignupBottomSheet();
                signUpBottomSheet.show(getParentFragmentManager(), "SignUpBottomSheet");
            }
        }); // Dismiss the BottomSheet

        return view;
    }
}
