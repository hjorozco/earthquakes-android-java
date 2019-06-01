package com.weebly.hectorjorozco.earthquakes.ui.datepreference;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TimePicker;

import androidx.preference.DialogPreference;
import androidx.preference.PreferenceDialogFragmentCompat;

import com.weebly.hectorjorozco.earthquakes.R;


// Class that handles the dialog

public class DatePreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat {

    private TimePicker mTimePicker;

    // Static method that creates a new instance of our TimePreferenceFragmentCompat.
    // To know to which preference this new dialog belongs, we add a String parameter with the key
    // of the preference to our method and pass it (inside a Bundle) to the dialog.
    public static DatePreferenceDialogFragmentCompat newInstance(
            String key) {
        final DatePreferenceDialogFragmentCompat
                fragment = new DatePreferenceDialogFragmentCompat();
        final Bundle b = new Bundle(1);
        b.putString(ARG_KEY, key);
        fragment.setArguments(b);
        return fragment;
    }


    // Now we need to do something with our TimePicker. We want that it always shows the time that
    // was stored in the SharedPreferences. We can access the TimePicker from our created layout,
    // after it was added to the dialog. We can do this in the onBindDialogView method.
    // The getPreference method returns the preference which opened the dialog.
    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        mTimePicker = view.findViewById(R.id.edit);

        // Exception when there is no TimePicker
        if (mTimePicker == null) {
            throw new IllegalStateException("Dialog view must contain" +
                    " a TimePicker with id 'edit'");
        }

        // Get the time from the related Preference
        Integer minutesAfterMidnight = null;
        DialogPreference preference = getPreference();
        if (preference instanceof DateDialogPreference) {
            minutesAfterMidnight =
                    ((DateDialogPreference) preference).getTime();
        }

        // Set the time to the TimePicker
        if (minutesAfterMidnight != null) {
            int hours = minutesAfterMidnight / 60;
            int minutes = minutesAfterMidnight % 60;
            boolean is24hour = DateFormat.is24HourFormat(getContext());

            mTimePicker.setIs24HourView(is24hour);
            mTimePicker.setCurrentHour(hours);
            mTimePicker.setCurrentMinute(minutes);
        }
    }


    // save the selected time when we click the OK button (positive result). For this, we override
    // the onDialogClosed method. First we calculate the minutes we want to save, and after that,
    // we get our related preference and call the setTime method we have defined there.
    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            // generate value to save
            int hours = mTimePicker.getCurrentHour();
            int minutes = mTimePicker.getCurrentMinute();
            int minutesAfterMidnight = (hours * 60) + minutes;

            // Get the related Preference and save the value
            DialogPreference preference = getPreference();
            if (preference instanceof DateDialogPreference) {
                DateDialogPreference dateDialogPreference =
                        ((DateDialogPreference) preference);
                // This allows the client to ignore the user value.
                if (dateDialogPreference.callChangeListener(
                        minutesAfterMidnight)) {
                    // Save the value
                    dateDialogPreference.setTime(minutesAfterMidnight);
                }
            }
        }
    }
}
