package com.example.thirsttap.AccountPage;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
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

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.thirsttap.R;
import com.google.android.material.bottomsheet.BottomSheetDragHandleView;

public class TermsAndConditionsFragment extends Fragment {
    private ProgressBar loader;
    private WebView webView;
    private ImageButton closeButton, backButton;
    private RelativeLayout topPanel;
    private ConstraintLayout constraintLayout;
    private LinearLayout termsLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.terms_and_conditions_fragment, container, false);
        loader = view.findViewById(R.id.loader);
        webView = view.findViewById(R.id.termsWebView);
        closeButton = view.findViewById(R.id.closeButton);
        topPanel = view.findViewById(R.id.topPanel);
        constraintLayout = view.findViewById(R.id.constraintLayout);
        termsLayout = view.findViewById(R.id.terms_layout);
        backButton = view.findViewById(R.id.back_button);

        //setup for accountfragment
        termsLayout.setBackgroundColor(Color.parseColor("#F0F8F7"));
        topPanel.setVisibility(View.VISIBLE);
        constraintLayout.setVisibility(View.GONE);

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
        backButton.setOnClickListener(v -> {
            AccountFragment fragment = new AccountFragment();
            getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
        });


    return view;
    }
}
