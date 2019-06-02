package com.weebly.hectorjorozco.earthquakes.ui.datepreference;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;

import androidx.preference.DialogPreference;

import com.weebly.hectorjorozco.earthquakes.R;

// Class that handles the value of the DialogPreference that is saved on SharedPreferences

public class DateDialogPreference extends DialogPreference {


    private long mDateInMilliseconds;


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
    }


    long getDateInMilliseconds() {
        return mDateInMilliseconds;
    }


    // Save the DateInMilliseconds long value to Shared Preferences and updates the preference summary
    void setDateInMilliseconds(long dateInMilliseconds) {
        mDateInMilliseconds = dateInMilliseconds;
        persistLong(dateInMilliseconds);
        setSummary(String.valueOf(mDateInMilliseconds));
    }


    // Reads the preference default value from xml attribute. Fallback value is set to 0.
    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return (long) a.getInt(index, 0);
    }


    // Set the initial value of the preference when the preference screen is shown
    @Override
    protected void onSetInitialValue(Object defaultValue) {
        setDateInMilliseconds(getPersistedLong(mDateInMilliseconds));
    }


    // Set the layout resource for the DialogPreference
    @Override
    public int getDialogLayoutResource() {
        return R.layout.preference_dialog_date;
    }

}
