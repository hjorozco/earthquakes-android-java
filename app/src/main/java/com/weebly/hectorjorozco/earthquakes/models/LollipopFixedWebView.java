package com.weebly.hectorjorozco.earthquakes.models;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.WebView;

public class LollipopFixedWebView extends WebView {

    public LollipopFixedWebView(Context context) {
        super(getFixedContext(context));
    }

    public LollipopFixedWebView(Context context, AttributeSet attrs) {
        super(getFixedContext(context), attrs);
    }

    public LollipopFixedWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(getFixedContext(context), attrs, defStyleAttr);
    }

    // Fix for Android Lollipop WebView problem
    private static Context getFixedContext(Context context) {
        if (Build.VERSION.SDK_INT == 21 || Build.VERSION.SDK_INT == 22) // Android Lollipop 5.0 & 5.1
            return context.createConfigurationContext(new Configuration());
        return context;
    }
}
