package com.weebly.hectorjorozco.earthquakes.utils;

import android.net.http.SslError;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import static android.view.View.GONE;

public class WebViewUtils {

    private static String sErrorMessage;
    private static boolean sReceivedError;

    public static WebViewClient setupWebViewClient(
            String errorMessage, TextView textView, WebView webView, ProgressBar progressBar, boolean usgsMap) {

        sReceivedError = false;
        sErrorMessage = errorMessage;

        return new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(GONE);
                if (!sReceivedError) {
                    textView.setVisibility(GONE);
                    webView.setVisibility(View.VISIBLE);
                } else {
                    textView.setVisibility(View.VISIBLE);
                    webView.setVisibility(GONE);
                    textView.setText(errorMessage);
                    sReceivedError = false;
                }

                // If the owner of this web client is the USGS map on the EarthquakeDetails activity
                // hide the "close" button and the bottom banner text from it.
                if (usgsMap) {
                    // hide element by class name
                    webView.loadUrl("javascript:(function() { " +
                            "document.getElementsByClassName('close-button mat-raised-button')[0].style.display='none'; })()");
                    webView.loadUrl("javascript:(function() { " +
                            "document.getElementsByClassName('leaflet-control-attribution leaflet-control')[0].style.display='none'; })()");
                }

            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                showErrorMessage(textView, webView, progressBar);
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                showErrorMessage(textView, webView, progressBar);

            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                showErrorMessage(textView, webView, progressBar);
            }

        };
    }

    private static void showErrorMessage(TextView textView, WebView webView, ProgressBar progressBar) {
        progressBar.setVisibility(GONE);
        webView.setVisibility(GONE);
        textView.setVisibility(View.VISIBLE);
        textView.setText(sErrorMessage);
        sReceivedError = true;
    }
}
