package com.weebly.hectorjorozco.earthquakes.ui;

import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;

import com.weebly.hectorjorozco.earthquakes.R;

public class SearchPreferencesFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.search_preferences_hierarchy, rootKey);

        EditTextPreference minimumMagnitudeEditTextPreference = findPreference(getString(R.string.search_preferences_minimum_magnitude_key));
        setupEditTextPreference(minimumMagnitudeEditTextPreference);



    }

    private void setupEditTextPreference(EditTextPreference editTextPreference) {
        if (editTextPreference != null) {
            editTextPreference.setOnBindEditTextListener(
                    new EditTextPreference.OnBindEditTextListener() {
                        @Override
                        public void onBindEditText(@NonNull EditText editText) {
                            editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                            editText.selectAll();
                        }
                    });
        }

    }
}
