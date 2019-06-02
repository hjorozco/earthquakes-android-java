package com.weebly.hectorjorozco.earthquakes.ui.datepreference;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.DatePicker;

import androidx.preference.DialogPreference;
import androidx.preference.PreferenceDialogFragmentCompat;

import com.weebly.hectorjorozco.earthquakes.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


// Class that handles the DialogPreference dialog fragment shown to the user

public class DatePreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat {


    private DatePicker mDatePicker;


    /**
     * Creates a new instance of this fragment.
      * @param dateDialogPreferenceKey The key of the DialogPreference that will use this fragment
     * @return A new instance of this fragment.
     */
    public static DatePreferenceDialogFragmentCompat newInstance(
            String dateDialogPreferenceKey) {
        final DatePreferenceDialogFragmentCompat
                datePreferenceDialogFragmentCompat = new DatePreferenceDialogFragmentCompat();
        final Bundle bundle = new Bundle(1);
        bundle.putString(ARG_KEY, dateDialogPreferenceKey);
        datePreferenceDialogFragmentCompat.setArguments(bundle);
        return datePreferenceDialogFragmentCompat;
    }


    // Now we need to do something with our TimePicker. We want that it always shows the time that
    // was stored in the SharedPreferences. We can access the TimePicker from our created layout,
    // after it was added to the dialog. We can do this in the onBindDialogView method.
    // The getPreference method returns the preference which opened the dialog.
    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        mDatePicker = view.findViewById(R.id.date_picker);

        if (mDatePicker == null) {
            throw new IllegalStateException("Dialog view must contain a DatePicker with id 'date_picker'");
        }

        // Get the date in milliseconds from the related Preference
        Long dateInMilliseconds = null;
        DialogPreference preference = getPreference();
        if (preference instanceof DateDialogPreference) {
            dateInMilliseconds =
                    ((DateDialogPreference) preference).getDateInMilliseconds();
        }

        // Set the time to the DatePicker
        if (dateInMilliseconds != null) {

            Calendar calendar = new GregorianCalendar();
            calendar.setTime(new Date(dateInMilliseconds));
            mDatePicker.init(calendar.get(
                    Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    null);
        }
    }


    // save the selected time when we click the OK button (positive result). For this, we override
    // the onDialogClosed method. First we calculate the minutes we want to save, and after that,
    // we get our related preference and call the setDateInMilliseconds method we have defined there.
    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {

            // generate value to save
            Calendar calendar = new GregorianCalendar(mDatePicker.getYear(), mDatePicker.getMonth(), mDatePicker.getDayOfMonth());
            long dateInMilliseconds = calendar.getTimeInMillis();

            // Get the related Preference and save the value
            DialogPreference preference = getPreference();
            if (preference instanceof DateDialogPreference) {
                DateDialogPreference dateDialogPreference =
                        ((DateDialogPreference) preference);
                // This allows the client to ignore the user value.
                if (dateDialogPreference.callChangeListener(
                        dateInMilliseconds)) {
                    // Save the value
                    dateDialogPreference.setDateInMilliseconds(dateInMilliseconds);
                }
            }
        }
    }
}
