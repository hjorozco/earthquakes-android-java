package com.weebly.hectorjorozco.earthquakes.ui;

import android.annotation.SuppressLint;
import android.os.Build;
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

import com.google.android.material.snackbar.Snackbar;
import com.weebly.hectorjorozco.earthquakes.R;
import com.weebly.hectorjorozco.earthquakes.utils.QueryUtils;
import com.weebly.hectorjorozco.earthquakes.utils.WebViewUtils;

import static android.view.View.GONE;

public class ReportEarthquakeActivity extends AppCompatActivity {

    public static final String REPORT_EARTHQUAKE_URL_EXTRA_KEY = "REPORT_EARTHQUAKE_URL_EXTRA_KEY";

    private TextView mTextView;
    private WebView mWebView;
    private String mReportEarthquakeUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_earthquake);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mReportEarthquakeUrl = getIntent().getStringExtra(REPORT_EARTHQUAKE_URL_EXTRA_KEY);

        mWebView = findViewById(R.id.activity_report_earthquake_web_view);
        mTextView = findViewById(R.id.activity_report_earthquake_text_view);

        mTextView.setOnClickListener(v -> showReportEarthquakeWebsite());

        showReportEarthquakeWebsite();
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void showReportEarthquakeWebsite() {
        ProgressBar progressBar = findViewById(R.id.activity_report_earthquake_progress_bar);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            QueryUtils.setupProgressBarForPreLollipop(progressBar, this);
        }

        if (QueryUtils.internetConnection(this)) {
            progressBar.setVisibility(View.VISIBLE);
            mTextView.setVisibility(View.GONE);
            mWebView.setVisibility(View.GONE);
            mWebView.setWebViewClient(WebViewUtils.setupWebViewClient(
                    getString(R.string.activity_earthquake_report_loading_error_message),
                    mTextView, mWebView, progressBar, false));
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.getSettings().setDomStorageEnabled(true);
            mWebView.loadUrl(mReportEarthquakeUrl);
        } else {
            mTextView.setVisibility(View.VISIBLE);
            mTextView.setText(getString(R.string.activity_earthquake_report_no_internet_message));
            mWebView.setVisibility(View.GONE);
            progressBar.setVisibility(GONE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_report_earthquake, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int menuItemId = item.getItemId();

        if (menuItemId == R.id.menu_activity_report_earthquake_action_help) {
            showHelpSnackBar();
        } else if (menuItemId == R.id.menu_activity_report_earthquake_action_chrome) {
            QueryUtils.openWebPageInGoogleChrome(this, mReportEarthquakeUrl);
        } else if (menuItemId ==android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }


    // When the user presses the back key on the phone
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.no_animation, R.anim.slide_down);
    }


    private void showHelpSnackBar() {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                getString(R.string.activity_earthquake_report_snack_bar_text), Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(getString(R.string.ok_text), v -> snackbar.dismiss());
        snackbar.show();
    }

}

