package com.weebly.hectorjorozco.earthquakes.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;

import com.weebly.hectorjorozco.earthquakes.R;
import com.weebly.hectorjorozco.earthquakes.ui.datepreference.DateDialogPreference;
import com.weebly.hectorjorozco.earthquakes.ui.datepreference.DatePreferenceDialogFragmentCompat;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;


public class SearchPreferencesFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {


    private static final int MAX_NUMBER_OF_EARTHQUAKES_EDIT_TEXT_LENGTH_FILTER = 10;
    private static final int LOCATION_EDIT_TEXT_LENGTH_FILTER = 50;

    private ListPreference mDateRangeListPreference;
    private DateDialogPreference mFromDateDialogPreference;
    private DateDialogPreference mToDateDialogPreference;
    private SeekBarPreference minimumMagnitudeSeekBarPreference;
    private SeekBarPreference maximumMagnitudeSeekBarPreference;

    // Used to flag when the "from" or "to" dates where changed by a predefined date range selected or by
    // the user changint the date individually
    private boolean mDateRangeChanged;

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    // Called during onCreate(Bundle) to supply the preferences for this fragment.
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        setPreferencesFromResource(R.xml.search_preferences_hierarchy, rootKey);

        setupMaxNumberOfEarthquakesEditTextPreference((EditTextPreference)
                findPreference(getString(R.string.search_preference_max_number_of_earthquakes_key)));

        setupLocationEditTextPreference((EditTextPreference)
                findPreference(getString(R.string.search_preference_location_key)));

        mDateRangeListPreference = findPreference(getString(R.string.search_preference_date_range_key));
        mFromDateDialogPreference = findPreference(getString(R.string.search_preference_start_date_key));
        mToDateDialogPreference = findPreference(getString(R.string.search_preference_end_date_key));

        minimumMagnitudeSeekBarPreference = findPreference(getString(R.string.search_preference_minimum_magnitude_key));
        maximumMagnitudeSeekBarPreference = findPreference(getString(R.string.search_preference_maximum_magnitude_key));

        setupDatePreferences();
        setupMinimumMagnitudeSeekBarPreference(minimumMagnitudeSeekBarPreference);
        setupMaximumMagnitudeSeekBarPreference(maximumMagnitudeSeekBarPreference);

    }


    // Called when a shared preference is changed, added, or removed.
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        Log.d("TESTING", "Preference changed");

        if (key.equals(getString(R.string.search_preference_date_range_key))) {

            mDateRangeChanged = true;

            updatePredefinedDateRanges();

        }

        if (key.equals(getString(R.string.search_preference_start_date_key))) {
            if (!mDateRangeChanged) {
                mDateRangeListPreference.setValue(getString(R.string.search_preference_date_range_custom_entry_value));
            }
            mToDateDialogPreference.setMinimumDateInMilliseconds(mFromDateDialogPreference.getDateInMilliseconds());

        } else if (key.equals(getString(R.string.search_preference_end_date_key))) {
            if (!mDateRangeChanged) {
                mDateRangeListPreference.setValue(getString(R.string.search_preference_date_range_custom_entry_value));
            }
            mDateRangeChanged = false;
            mFromDateDialogPreference.setMaximumDateInMilliseconds(mToDateDialogPreference.getDateInMilliseconds());

        } else if (key.equals(getString(R.string.search_preference_minimum_magnitude_key))) {
            minimumMagnitudeSeekBarPreference.setSummary(String.valueOf(minimumMagnitudeSeekBarPreference.getValue()));

        } else if (key.equals(getString(R.string.search_preference_maximum_magnitude_key))) {
            maximumMagnitudeSeekBarPreference.setSummary(String.valueOf(maximumMagnitudeSeekBarPreference.getValue()));
        }
    }


    // Called when a preference in the tree requests to display a dialog.
    @Override
    public void onDisplayPreferenceDialog(Preference preference) {

        if (preference instanceof DateDialogPreference) {
            // Display a DatePreferenceDialogFragmentCompat
            DialogFragment dialogFragment = DatePreferenceDialogFragmentCompat
                    .newInstance(preference.getKey());
            dialogFragment.setTargetFragment(this, 0);
            if (getFragmentManager() != null) {
                dialogFragment.show(getFragmentManager(),
                        "androidx.preference.PreferenceDialogFragmentCompat");
            }
        } else {
            // Call the super class method that handles the predefined DialogPreferences
            super.onDisplayPreferenceDialog(preference);
        }
    }


    private void setupMaxNumberOfEarthquakesEditTextPreference(EditTextPreference editTextPreference) {

        if (editTextPreference != null) {

            // Sets the input type to only whole numbers, filters the length and removes all leading zeros if any
            editTextPreference.setOnBindEditTextListener(
                    new EditTextPreference.OnBindEditTextListener() {
                        @Override
                        public void onBindEditText(@NonNull EditText editText) {
                            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(
                                    MAX_NUMBER_OF_EARTHQUAKES_EDIT_TEXT_LENGTH_FILTER)});

                            String maxNumberOfEarthquakesString = editText.getText().toString();
                            if (!maxNumberOfEarthquakesString.isEmpty()) {
                                editText.setText(String.valueOf(Integer.valueOf(maxNumberOfEarthquakesString)));
                            }

                            editText.selectAll();
                        }
                    });

            editTextPreference.setSummaryProvider(new Preference.SummaryProvider<EditTextPreference>() {
                @Override
                public CharSequence provideSummary(EditTextPreference preference) {

                    if (preference.getText().isEmpty()) {
                        return getString(R.string.search_preference_max_number_of_earthquakes_not_set_value);
                    } else {
                        int maxNumberOfEarthquakes = Integer.valueOf(preference.getText());
                        if (maxNumberOfEarthquakes == 0) {
                            return getString(R.string.search_preference_max_number_of_earthquakes_zero_value);
                        } else {
                            return String.valueOf(maxNumberOfEarthquakes);
                        }
                    }
                }
            });
        }
    }


    private void setupLocationEditTextPreference(EditTextPreference editTextPreference) {

        if (editTextPreference != null) {

            editTextPreference.setOnBindEditTextListener(
                    new EditTextPreference.OnBindEditTextListener() {
                        @Override
                        public void onBindEditText(@NonNull EditText editText) {
                            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(
                                    LOCATION_EDIT_TEXT_LENGTH_FILTER)});
                            editText.setText(editText.getText().toString().trim());
                            editText.selectAll();
                        }
                    });

            editTextPreference.setSummaryProvider(new Preference.SummaryProvider<EditTextPreference>() {
                @Override
                public CharSequence provideSummary(EditTextPreference preference) {
                    String editTextPreferenceText = preference.getText().trim();
                    if (TextUtils.isEmpty(editTextPreferenceText)) {
                        return getString(R.string.search_preference_location_not_set_value);
                    }
                    return editTextPreferenceText;
                }
            });

        }
    }


    private void setupDatePreferences() {

        updatePredefinedDateRanges();

        if (mFromDateDialogPreference != null && mToDateDialogPreference != null) {
            // Sets the maximum of the "from" date to be the "to" date.
            mFromDateDialogPreference.setMaximumDateInMilliseconds(mToDateDialogPreference.getDateInMilliseconds());
            // Sets the minimum of the "to" date to be the "from" date.
            mToDateDialogPreference.setMinimumDateInMilliseconds(mFromDateDialogPreference.getDateInMilliseconds());
        }
    }


    // Updates the value of the "from" and "to" dates for predefined date ranges, with the present time.
    private void updatePredefinedDateRanges(){
        Calendar calendar = Calendar.getInstance();
        long todayDateOnMilliseconds = calendar.getTimeInMillis();
        long millisecondsOnOneDay = TimeUnit.DAYS.toMillis(1);

        if (mDateRangeListPreference != null) {

            String dateRangeListPreferenceValue = mDateRangeListPreference.getValue();

            if (dateRangeListPreferenceValue.equals(getString(R.string.search_preference_date_range_last_24_hours_entry_value))) {
                mFromDateDialogPreference.setDateInMilliseconds(todayDateOnMilliseconds - millisecondsOnOneDay);
                mToDateDialogPreference.setDateInMilliseconds(todayDateOnMilliseconds);

            } else if (dateRangeListPreferenceValue.equals(getString(R.string.search_preference_date_range_last_7_days_entry_value))) {
                mFromDateDialogPreference.setDateInMilliseconds(todayDateOnMilliseconds - millisecondsOnOneDay * 7);
                mToDateDialogPreference.setDateInMilliseconds(todayDateOnMilliseconds);

            } else if (dateRangeListPreferenceValue.equals(getString(R.string.search_preference_date_range_last_30_days_entry_value))) {
                mFromDateDialogPreference.setDateInMilliseconds(todayDateOnMilliseconds - millisecondsOnOneDay * 30);
                mToDateDialogPreference.setDateInMilliseconds(todayDateOnMilliseconds);

            } else if (dateRangeListPreferenceValue.equals(getString(R.string.search_preference_date_range_last_365_days_entry_value))) {
                mFromDateDialogPreference.setDateInMilliseconds(todayDateOnMilliseconds - millisecondsOnOneDay * 365);
                mToDateDialogPreference.setDateInMilliseconds(todayDateOnMilliseconds);

            }
        }
    }


    private void setupMinimumMagnitudeSeekBarPreference(SeekBarPreference seekBarPreference) {

        seekBarPreference.setUpdatesContinuously(true);

        minimumMagnitudeSeekBarPreference.setSummary(String.valueOf(minimumMagnitudeSeekBarPreference.getValue()));

        seekBarPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                return (Integer) newValue <= maximumMagnitudeSeekBarPreference.getValue();
            }
        });
    }


    private void setupMaximumMagnitudeSeekBarPreference(SeekBarPreference seekBarPreference) {

        seekBarPreference.setUpdatesContinuously(true);

        maximumMagnitudeSeekBarPreference.setSummary(String.valueOf(maximumMagnitudeSeekBarPreference.getValue()));

        seekBarPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                return (Integer) newValue >= minimumMagnitudeSeekBarPreference.getValue();
            }
        });
    }

}
