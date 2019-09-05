package com.weebly.hectorjorozco.earthquakes.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;

import com.weebly.hectorjorozco.earthquakes.R;
import com.weebly.hectorjorozco.earthquakes.utils.QueryUtils;

import static android.view.View.GONE;

public class EarthquakeWebPageActivity extends AppCompatActivity {

    public static final String EARTHQUAKE_URL_EXTRA_KEY = "EARTHQUAKE_URL_EXTRA_KEY";

    private TextView mTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earthquake_web_page);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mTextView = findViewById(R.id.activity_earthquake_web_page_text_view);

        mTextView.setOnClickListener(v -> showEarthquakeWebsite());

        showEarthquakeWebsite();
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void showEarthquakeWebsite() {
        WebView webView = findViewById(R.id.activity_earthquake_web_page_web_view);
        ProgressBar progressBar = findViewById(R.id.activity_earthquake_web_page_progress_bar);
        if (QueryUtils.internetConnection(this)) {
            mTextView.setVisibility(GONE);
            progressBar.setVisibility(View.VISIBLE);
            webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    if (newProgress==100){
                        progressBar.setVisibility(GONE);
                        webView.setVisibility(View.VISIBLE);
                    }
                }
            });
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.loadUrl(getIntent().getStringExtra(EARTHQUAKE_URL_EXTRA_KEY));
        } else {
            mTextView.setVisibility(View.VISIBLE);
            webView.setVisibility(GONE);
            progressBar.setVisibility(GONE);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}

