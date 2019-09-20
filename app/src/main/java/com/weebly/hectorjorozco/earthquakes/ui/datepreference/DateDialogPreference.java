package com.weebly.hectorjorozco.earthquakes.ui.datepreference;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.preference.DialogPreference;

import com.weebly.hectorjorozco.earthquakes.R;
import com.weebly.hectorjorozco.earthquakes.utils.WordsUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

// Class that handles the value of the DialogPreference that is saved on SharedPreferences

public class DateDialogPreference extends DialogPreference {


    private long mDateInMilliseconds;
    private long mMinimumDateInMilliseconds;
    private long mMaximumDateInMilliseconds;
    private boolean toDateChangedManuallyFlag = false;


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


    public long getDateInMilliseconds() {
        return mDateInMilliseconds;
    }


    // Save the DateInMilliseconds long value to Shared Preferences and updates the preference summary
    public void setDateInMilliseconds(long dateInMilliseconds) {

        // If the "to" date was changed manually, then add 23 hours and 59 minutes to it to cover
        // that whole day.
        if (toDateChangedManuallyFlag) {
            dateInMilliseconds += (TimeUnit.DAYS.toMillis(1)) - (1000 * 60);
            toDateChangedManuallyFlag = false;
        }

        mDateInMilliseconds = dateInMilliseconds;
        persistLong(dateInMilliseconds);
        setSummary(WordsUtils.displayedDateFormatter().format(new Date(mDateInMilliseconds)));
    }


    // Reads the preference default value from xml attribute. Fallback value is set to 0.
    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return (long) a.getInt(index, 0);
    }


    // Set the initial value of the preference when the preference screen is shown
    @Override
    protected void onSetInitialValue(Object defaultValue) {
        toDateChangedManuallyFlag = false;

        // The first time the preference is created defaultValue is not null
        long defaultValueLong;
        if (defaultValue!=null){
            defaultValueLong = (long) defaultValue;
        } else {
            defaultValueLong = mDateInMilliseconds;
        }
        setDateInMilliseconds(getPersistedLong(defaultValueLong));
    }


    // Set the layout resource for the DialogPreference
    @Override
    public int getDialogLayoutResource() {
        return R.layout.dialog_preference_date;
    }


    long getMinimumDateInMilliseconds() {
        return mMinimumDateInMilliseconds;
    }

    public void setMinimumDateInMilliseconds(long minimumDateInMilliseconds) {
        mMinimumDateInMilliseconds = minimumDateInMilliseconds;
    }

    long getMaximumDateInMilliseconds() {
        return mMaximumDateInMilliseconds;
    }

    public void setMaximumDateInMilliseconds(long maximumDateInMilliseconds) {
        mMaximumDateInMilliseconds = maximumDateInMilliseconds;
    }

    public void setToDateChangedManuallyFlag(boolean value) {
        toDateChangedManuallyFlag = value;
    }

}
