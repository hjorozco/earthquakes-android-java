package com.weebly.hectorjorozco.earthquakes.ui.sortbypreference;

import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import androidx.preference.DialogPreference;
import androidx.preference.PreferenceDialogFragmentCompat;

import com.weebly.hectorjorozco.earthquakes.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


// Class that handles the DialogPreference dialog fragment shown to the user

public class SortByPreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat {


    private DatePicker mDatePicker;


    /**
     * Creates a new instance of this fragment.
     *
     * @param dateDialogPreferenceKey The key of the DialogPreference that will use this fragment
     * @return A new instance of this fragment.
     */
    public static SortByPreferenceDialogFragmentCompat newInstance(
            String dateDialogPreferenceKey) {
        final SortByPreferenceDialogFragmentCompat
                sortByPreferenceDialogFragmentCompat = new SortByPreferenceDialogFragmentCompat();
        final Bundle bundle = new Bundle(1);
        bundle.putString(ARG_KEY, dateDialogPreferenceKey);
        sortByPreferenceDialogFragmentCompat.setArguments(bundle);
        return sortByPreferenceDialogFragmentCompat;
    }


    // Binds views in the content view of the dialog to data.
    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        mDatePicker = view.findViewById(R.id.date_picker);

        if (mDatePicker == null) {
            throw new IllegalStateException("Dialog view must contain a DatePicker with id 'date_picker'");
        }

        // Get the date in milliseconds from the preference that opened the dialog
        Long dateInMilliseconds = null;
        DialogPreference preference = getPreference();
        if (preference instanceof SortByDialogPreference) {
            dateInMilliseconds =
                    ((SortByDialogPreference) preference).getDateInMilliseconds();
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

            if (preference.getKey().equals(getString(R.string.search_preference_start_date_key))) {
                mDatePicker.setMaxDate(((SortByDialogPreference) preference).getMaximumDateInMilliseconds());
            } else {
                mDatePicker.setMinDate(((SortByDialogPreference) preference).getMinimumDateInMilliseconds());
            }
        }
    }


    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {

            // Generate the value to save
            Calendar calendar = new GregorianCalendar(mDatePicker.getYear(), mDatePicker.getMonth(), mDatePicker.getDayOfMonth());
            long dateInMilliseconds = calendar.getTimeInMillis();

            // Get the related Preference and save the value
            DialogPreference preference = getPreference();
            if (preference instanceof SortByDialogPreference) {
                SortByDialogPreference sortByDialogPreference =
                        ((SortByDialogPreference) preference);
                // This allows the client to ignore the user value.
                if (sortByDialogPreference.callChangeListener(
                        dateInMilliseconds)) {
                    if (sortByDialogPreference.getKey().equals(
                            getString(R.string.search_preference_end_date_key))) {
                        sortByDialogPreference.setToDateChangedManuallyFlag(true);
                    }
                    // Save the value and update the summary
                    sortByDialogPreference.setDateInMilliseconds(dateInMilliseconds);
                }
            }
        }
    }
}
