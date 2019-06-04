package com.weebly.hectorjorozco.earthquakes.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;

import com.weebly.hectorjorozco.earthquakes.R;
import com.weebly.hectorjorozco.earthquakes.ui.datepreference.DateDialogPreference;
import com.weebly.hectorjorozco.earthquakes.ui.datepreference.DatePreferenceDialogFragmentCompat;


public class SearchPreferencesFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {


    private static final int MAX_NUMBER_OF_EARTHQUAKES_EDIT_TEXT_LENGTH_FILTER = 10;
    private static final int LOCATION_EDIT_TEXT_LENGTH_FILTER = 50;

    private SeekBarPreference minimumMagnitudeSeekBarPreference;
    private SeekBarPreference maximumMagnitudeSeekBarPreference;


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

        minimumMagnitudeSeekBarPreference = findPreference(getString(R.string.search_preference_minimum_magnitude_key));
        maximumMagnitudeSeekBarPreference = findPreference(getString(R.string.search_preference_maximum_magnitude_key));

        setupMinimumMagnitudeSeekBarPreference(minimumMagnitudeSeekBarPreference);
        setupMaximumMagnitudeSeekBarPreference(maximumMagnitudeSeekBarPreference);

    }


    // Called when a shared preference is changed, added, or removed.
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals(getString(R.string.search_preference_minimum_magnitude_key))) {
            minimumMagnitudeSeekBarPreference.setSummary(String.valueOf(minimumMagnitudeSeekBarPreference.getValue()));
        }

        if (key.equals(getString(R.string.search_preference_maximum_magnitude_key))) {
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
        }
        else {
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
