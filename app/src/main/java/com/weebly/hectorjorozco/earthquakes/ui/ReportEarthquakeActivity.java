package com.weebly.hectorjorozco.earthquakes.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.weebly.hectorjorozco.earthquakes.R;
import com.weebly.hectorjorozco.earthquakes.utils.QueryUtils;
import com.weebly.hectorjorozco.earthquakes.utils.WordsUtils;

public class ReportEarthquakeActivity extends AppCompatActivity {

    public static final String REPORT_EARTHQUAKE_URL_EXTRA_KEY = "REPORT_EARTHQUAKE_URL_EXTRA_KEY";

    private TextView mTextView;
    private WebView mWebView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_earthquake);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mWebView = findViewById(R.id.activity_report_earthquake_web_view);
        mTextView = findViewById(R.id.activity_report_earthquake_text_view);

        mTextView.setOnClickListener(v -> showReportEarthquakeWebsite());

        showReportEarthquakeWebsite();
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void showReportEarthquakeWebsite() {
        if (QueryUtils.internetConnection(this)) {
            mTextView.setVisibility(View.GONE);
            mWebView.setVisibility(View.VISIBLE);
            mWebView.setWebChromeClient(new WebChromeClient());
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.getSettings().setDomStorageEnabled(true);
            mWebView.loadUrl(getIntent().getStringExtra(REPORT_EARTHQUAKE_URL_EXTRA_KEY));
        } else {
            mTextView.setVisibility(View.VISIBLE);
            mWebView.setVisibility(View.GONE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_report_earthquake, menu);
        return WordsUtils.getLocaleLanguage().equals("es");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_activity_report_earthquake_action_help:
                showHelpSnackBar();
                break;
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showHelpSnackBar() {
        Snackbar.make(findViewById(android.R.id.content),
                getString(R.string.activity_earthquake_report_snack_bar_text), Snackbar.LENGTH_LONG).show();

    }

}

