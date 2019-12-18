package com.weebly.hectorjorozco.earthquakes.utils;

import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.Button;

import com.weebly.hectorjorozco.earthquakes.R;


public class UiUtils {

    public static void setupAlertDialogButtonBackground(Context context, Button button){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TypedValue outValue = new TypedValue();
            context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
            button.setBackgroundResource(outValue.resourceId);
        } else {
            button.setBackground(context.getResources().getDrawable(R.drawable.touch_selector));
        }
    }


    public static int getPxFromDp(Context context, float dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }


    public static float getMilesFromKilometers(float kilometers){
        return kilometers * (float) 0.621371;
    }


    public static float getKilometersFromMiles(float miles){
        return miles * (float) 1.609344;
    }

}
