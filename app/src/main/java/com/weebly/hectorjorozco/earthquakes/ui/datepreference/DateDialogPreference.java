package com.weebly.hectorjorozco.earthquakes.ui.datepreference;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.preference.DialogPreference;

import com.weebly.hectorjorozco.earthquakes.R;

// Class that handles the values of the preference

public class DateDialogPreference extends DialogPreference {

    private int mTime;
    // The Layout ID of the layout resource to be displayed by this DialogPreference
    private int mDialogLayoutResId = R.layout.pref_dialog_time;


    // Four constructors
    public DateDialogPreference(Context context) {
        this(context, null);
    }

    public DateDialogPreference(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.dialogPreferenceStyle);
    }

    public DateDialogPreference(Context context, AttributeSet attrs,
                                int defStyleAttr) {
        this(context, attrs, defStyleAttr, defStyleAttr);
    }

    public DateDialogPreference(Context context, AttributeSet attrs,
                                int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        // Do custom stuff here
        // ...
        // read attributes etc.
    }


    public int getTime() {
        return mTime;
    }


    // Save the time int value to Shared Preferences and updates the preference summary
    public void setTime(int time) {
        mTime = time;
        persistInt(time);
        setSummary(String.valueOf(mTime));
    }


    // Reads the preference default value from xml attribute. Fallback value is set to 0.
    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, 0);
    }


    @Override
    protected void onSetInitialValue(Object defaultValue) {
        setTime(getPersistedInt(mTime));
    }


    // Set the layout resource for the DialogPreference
    @Override
    public int getDialogLayoutResource() {
        return mDialogLayoutResId;
    }

}
