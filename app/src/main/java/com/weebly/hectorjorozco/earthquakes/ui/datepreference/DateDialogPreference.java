package com.weebly.hectorjorozco.earthquakes.ui.datepreference;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.preference.DialogPreference;

import com.weebly.hectorjorozco.earthquakes.R;
import com.weebly.hectorjorozco.earthquakes.utils.WordsUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

// Class that handles the value of the DialogPreference that is saved on SharedPreferences

public class DateDialogPreference extends DialogPreference {


    private long mDateInMilliseconds;
    private long mMinimumDateInMilliseconds;
    private long mMaximumDateInMilliseconds;


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
    public void setDateInMilliseconds(long dateInMilliseconds, boolean setByDefinedDateRange) {

        // If the "to" date was custom saved by the user, then add 11 hours and 59 minutes to it to cover
        // that whole day.
        if(!setByDefinedDateRange &&
                getKey().equals(getContext().getString(R.string.search_preference_end_date_key))) {
            long millisecondsOnOneDay = TimeUnit.DAYS.toMillis(1);
            dateInMilliseconds = dateInMilliseconds + (millisecondsOnOneDay) - (1000*60);
        }

        mDateInMilliseconds = dateInMilliseconds;
        persistLong(dateInMilliseconds);
        setSummary(dateSummaryFormatter().format(new Date(mDateInMilliseconds)));
    }


    // Reads the preference default value from xml attribute. Fallback value is set to 0.
    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return (long) a.getInt(index, 0);
    }


    // Set the initial value of the preference when the preference screen is shown
    @Override
    protected void onSetInitialValue(Object defaultValue) {
        setDateInMilliseconds(getPersistedLong(mDateInMilliseconds), false);
    }


    // Set the layout resource for the DialogPreference
    @Override
    public int getDialogLayoutResource() {
        return R.layout.preference_dialog_date;
    }

    /**
     * Produces the date formatter used for showing the date in the summary.
     *
     * @return the SimpleDateFormat used for summary dates
     */
    private static SimpleDateFormat dateSummaryFormatter() {

        SimpleDateFormat simpleDateFormat;
        if (WordsUtils.getLocaleLanguage().equals("es")) {
            simpleDateFormat = new SimpleDateFormat("d 'de' MMMM 'del' yyyy, hh:mm aaa", Locale.getDefault());
        } else {
            simpleDateFormat = new SimpleDateFormat("MMMM d, yyyy hh:mm aaa", Locale.getDefault());
        }

        return simpleDateFormat;
    }


    long getMinimumDateInMilliseconds(){
        return mMinimumDateInMilliseconds;
    }

    public void setMinimumDateInMilliseconds(long minimumDateInMilliseconds){
        mMinimumDateInMilliseconds = minimumDateInMilliseconds;
    }

    long getMaximumDateInMilliseconds(){
        return mMaximumDateInMilliseconds;
    }

    public void setMaximumDateInMilliseconds(long maximumDateInMilliseconds){
        mMaximumDateInMilliseconds = maximumDateInMilliseconds;
    }

}
