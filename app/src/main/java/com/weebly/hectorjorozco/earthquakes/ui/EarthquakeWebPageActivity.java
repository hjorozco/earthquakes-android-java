package com.weebly.hectorjorozco.earthquakes.ui;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.weebly.hectorjorozco.earthquakes.R;
import com.weebly.hectorjorozco.earthquakes.utils.QueryUtils;
import com.weebly.hectorjorozco.earthquakes.utils.WebViewUtils;

import static android.view.View.GONE;

public class EarthquakeWebPageActivity extends AppCompatActivity {

    public static final String EARTHQUAKE_URL_EXTRA_KEY = "EARTHQUAKE_URL_EXTRA_KEY";

    private TextView mTextView;
    private WebView mWebView;
    private String mEarthquakeUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earthquake_web_page);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mEarthquakeUrl = getIntent().getStringExtra(EARTHQUAKE_URL_EXTRA_KEY);

        mTextView = findViewById(R.id.activity_earthquake_web_page_text_view);
        mWebView = findViewById(R.id.activity_earthquake_web_page_web_view);

        mTextView.setOnClickListener(v -> showEarthquakeWebsite());

        showEarthquakeWebsite();
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void showEarthquakeWebsite() {
        ProgressBar progressBar = findViewById(R.id.activity_earthquake_web_page_progress_bar);

        if (QueryUtils.internetConnection(this)) {
            progressBar.setVisibility(View.VISIBLE);
            mTextView.setVisibility(GONE);
            mWebView.setVisibility(View.GONE);
            mWebView.setWebViewClient(WebViewUtils.setupWebViewClient(
                    getString(R.string.activity_earthquake_report_loading_error_message),
                    mTextView, mWebView, progressBar));
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.getSettings().setDomStorageEnabled(true);
            mWebView.loadUrl(mEarthquakeUrl);
        } else {
            mTextView.setVisibility(View.VISIBLE);
            mTextView.setText(getString(R.string.activity_earthquake_report_no_internet_message));
            mWebView.setVisibility(GONE);
            progressBar.setVisibility(GONE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_earthquake_web_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_activity_earthquake_web_page_action_chrome:
                QueryUtils.openWebPageInGoogleChrome(this, mEarthquakeUrl);
                break;
            case android.R.id.home:
                if (mWebView != null && mWebView.canGoBack()) {
                    mWebView.goBack();
                } else {
                    onBackPressed();
                }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}

