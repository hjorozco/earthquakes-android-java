package com.weebly.hectorjorozco.earthquakes.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;

import androidx.fragment.app.DialogFragment;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;

import com.weebly.hectorjorozco.earthquakes.R;
import com.weebly.hectorjorozco.earthquakes.ui.datepreference.DateDialogPreference;
import com.weebly.hectorjorozco.earthquakes.ui.datepreference.DatePreferenceDialogFragmentCompat;
import com.weebly.hectorjorozco.earthquakes.utils.WordsUtils;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.weebly.hectorjorozco.earthquakes.ui.MainActivity.MAX_NUMBER_OF_EARTHQUAKES_LIMIT;


public class SearchPreferencesFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {


    private static final int MAX_NUMBER_OF_EARTHQUAKES_EDIT_TEXT_LENGTH_FILTER = 5;
    private static final int LOCATION_EDIT_TEXT_LENGTH_FILTER = 50;

    private ListPreference mDateRangeListPreference;
    private DateDialogPreference mFromDateDialogPreference;
    private DateDialogPreference mToDateDialogPreference;
    private SeekBarPreference mMinimumMagnitudeSeekBarPreference;
    private SeekBarPreference mMaximumMagnitudeSeekBarPreference;

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

        setupMaxNumberOfEarthquakesEditTextPreference(findPreference(getString(R.string.search_preference_max_number_of_earthquakes_key)));

        setupLocationEditTextPreference(findPreference(getString(R.string.search_preference_location_key)));

        mDateRangeListPreference = findPreference(getString(R.string.search_preference_date_range_key));
        mFromDateDialogPreference = findPreference(getString(R.string.search_preference_start_date_key));
        mToDateDialogPreference = findPreference(getString(R.string.search_preference_end_date_key));

        mMinimumMagnitudeSeekBarPreference = findPreference(getString(R.string.search_preference_minimum_magnitude_key));
        mMaximumMagnitudeSeekBarPreference = findPreference(getString(R.string.search_preference_maximum_magnitude_key));

        setupDatePreferences();
        setupMinimumMagnitudeSeekBarPreference(mMinimumMagnitudeSeekBarPreference);
        setupMaximumMagnitudeSeekBarPreference(mMaximumMagnitudeSeekBarPreference);

    }


    // Called when a shared preference is changed, added, or removed.
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals(getString(R.string.search_preference_date_range_key))) {
            mDateRangeChanged = true;
            updatePredefinedDateRanges();

        } else if (key.equals(getString(R.string.search_preference_start_date_key))) {
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
            mMinimumMagnitudeSeekBarPreference.setSummary(String.valueOf(mMinimumMagnitudeSeekBarPreference.getValue()));

        } else if (key.equals(getString(R.string.search_preference_maximum_magnitude_key))) {
            mMaximumMagnitudeSeekBarPreference.setSummary(String.valueOf(mMaximumMagnitudeSeekBarPreference.getValue()));
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

            // Sets the input type to only whole numbers, filters the lengthemoves all leading zeros if any
            // and shows "20000" when the value is more than 20000
            editTextPreference.setOnBindEditTextListener(
                    editText -> {
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(
                                MAX_NUMBER_OF_EARTHQUAKES_EDIT_TEXT_LENGTH_FILTER)});

                        String maxNumberOfEarthquakesString = editText.getText().toString();
                        if (!maxNumberOfEarthquakesString.isEmpty()) {
                            int maxNumberOfEarthquakes = Integer.valueOf(maxNumberOfEarthquakesString);
                            if (maxNumberOfEarthquakes > MAX_NUMBER_OF_EARTHQUAKES_LIMIT) {
                                editText.setText(String.valueOf(MAX_NUMBER_OF_EARTHQUAKES_LIMIT));
                            } else {
                                editText.setText(String.valueOf(maxNumberOfEarthquakes));
                            }
                        }
                        editText.selectAll();
                    });

            editTextPreference.setSummaryProvider((Preference.SummaryProvider<EditTextPreference>) preference -> {
                if (preference.getText().isEmpty()) {
                    return getString(R.string.search_preference_max_number_of_earthquakes_not_set_text);
                } else {
                    int maxNumberOfEarthquakes = Integer.valueOf(preference.getText());
                    if (maxNumberOfEarthquakes == 0) {
                        return getString(R.string.search_preference_max_number_of_earthquakes_zero_text);
                    } else {
                        if (maxNumberOfEarthquakes > MAX_NUMBER_OF_EARTHQUAKES_LIMIT) {
                            return getString(R.string.search_preference_max_number_of_earthquakes_limit_passed_text);
                        } else {
                            return String.format(Locale.getDefault(), "%,d", maxNumberOfEarthquakes);
                        }
                    }
                }
            });
        }
    }


    private void setupLocationEditTextPreference(EditTextPreference editTextPreference) {

        if (editTextPreference != null) {

            editTextPreference.setOnBindEditTextListener(
                    editText -> {
                        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(
                                LOCATION_EDIT_TEXT_LENGTH_FILTER)});

                        editText.setText(WordsUtils.formatLocationText(editText.getText().toString()));
                        editText.selectAll();
                    });

            editTextPreference.setSummaryProvider((Preference.SummaryProvider<EditTextPreference>) preference -> {

                String editTextPreferenceText = WordsUtils.formatLocationText(preference.getText());
                if (android.text.TextUtils.isEmpty(editTextPreferenceText)) {
                    return getString(R.string.search_preference_location_not_set_value);
                }
                return editTextPreferenceText;
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
    private void updatePredefinedDateRanges() {
        Calendar calendar = Calendar.getInstance();
        long todayDateOnMilliseconds = calendar.getTimeInMillis();
        long millisecondsOnOneDay = TimeUnit.DAYS.toMillis(1);

        if (mDateRangeListPreference != null) {

            String dateRangeListPreferenceValue = mDateRangeListPreference.getValue();

            if (dateRangeListPreferenceValue.equals(getString(R.string.search_preference_date_range_last_24_hours_entry_value))) {
                mFromDateDialogPreference.setDateInMilliseconds(todayDateOnMilliseconds - millisecondsOnOneDay, true);
                mToDateDialogPreference.setDateInMilliseconds(todayDateOnMilliseconds, true);

            } else if (dateRangeListPreferenceValue.equals(getString(R.string.search_preference_date_range_last_7_days_entry_value))) {
                mFromDateDialogPreference.setDateInMilliseconds(todayDateOnMilliseconds - millisecondsOnOneDay * 7, true);
                mToDateDialogPreference.setDateInMilliseconds(todayDateOnMilliseconds, true);

            } else if (dateRangeListPreferenceValue.equals(getString(R.string.search_preference_date_range_last_30_days_entry_value))) {
                mFromDateDialogPreference.setDateInMilliseconds(todayDateOnMilliseconds - millisecondsOnOneDay * 30, true);
                mToDateDialogPreference.setDateInMilliseconds(todayDateOnMilliseconds, true);

            } else if (dateRangeListPreferenceValue.equals(getString(R.string.search_preference_date_range_last_365_days_entry_value))) {
                mFromDateDialogPreference.setDateInMilliseconds(todayDateOnMilliseconds - millisecondsOnOneDay * 365, true);
                mToDateDialogPreference.setDateInMilliseconds(todayDateOnMilliseconds, true);

            }
        }
    }


    private void setupMinimumMagnitudeSeekBarPreference(SeekBarPreference seekBarPreference) {

        seekBarPreference.setUpdatesContinuously(true);

        mMinimumMagnitudeSeekBarPreference.setSummary(String.valueOf(mMinimumMagnitudeSeekBarPreference.getValue()));

        // Saves the minimum magnitude value only if it is less than or equal to the maximum magnitude
        seekBarPreference.setOnPreferenceChangeListener((preference, newValue) ->
                (Integer) newValue <= mMaximumMagnitudeSeekBarPreference.getValue());
    }


    private void setupMaximumMagnitudeSeekBarPreference(SeekBarPreference seekBarPreference) {

        seekBarPreference.setUpdatesContinuously(true);

        mMaximumMagnitudeSeekBarPreference.setSummary(String.valueOf(mMaximumMagnitudeSeekBarPreference.getValue()));

        // Saves the maximum magnitude value only if it is more than or equal to the minimum magnitude
        seekBarPreference.setOnPreferenceChangeListener((preference, newValue) ->
                (Integer) newValue >= mMinimumMagnitudeSeekBarPreference.getValue());
    }

}
