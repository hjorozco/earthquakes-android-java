package com.weebly.hectorjorozco.earthquakes.utils;

import android.content.Context;
import android.os.Build;
import android.util.TypedValue;
import android.widget.Button;

import com.weebly.hectorjorozco.earthquakes.R;

import java.util.Objects;

public class UiUtils {

    public static void setupAlertDialogButtonBackground(Context context, Button button){ ;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TypedValue outValue = new TypedValue();
            context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
            button.setBackgroundResource(outValue.resourceId);
        } else {
            button.setBackground(context.getResources().getDrawable(R.drawable.touch_selector));
        }
    }

}
