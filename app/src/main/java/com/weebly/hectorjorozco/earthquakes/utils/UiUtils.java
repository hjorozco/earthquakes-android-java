package com.weebly.hectorjorozco.earthquakes.utils;

import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.Button;

import androidx.core.content.res.ResourcesCompat;

import com.weebly.hectorjorozco.earthquakes.R;

import static com.weebly.hectorjorozco.earthquakes.utils.QueryUtils.formatOffsetDistance;


public class UiUtils {

    public static void setupAlertDialogButtonBackground(Context context, Button button){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TypedValue outValue = new TypedValue();
            context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
            button.setBackgroundResource(outValue.resourceId);
        } else {
            // If Android 19
            button.setBackground(ResourcesCompat.getDrawable
                    (context.getResources(), R.drawable.touch_selector, null));
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


    public static String changeLocationOffsetFromMilesToKilometers(String locationOffset, Context context){
        int kmPosition = locationOffset.indexOf(context.getString(R.string.kilometers_text));
        String kilometersStringValue = locationOffset.substring(0, kmPosition - 1);
        float kilometersValue = Float.parseFloat(kilometersStringValue);
        float milesValue = UiUtils.getMilesFromKilometers(kilometersValue);
        String milesStringValue = formatOffsetDistance(milesValue);
        String locationOffsetPart1 = milesStringValue + " " + context.getString(R.string.miles_text);
        String locationOffsetPart2 = locationOffset.substring(kmPosition + 2);
        return locationOffsetPart1 + locationOffsetPart2;
    }

}
