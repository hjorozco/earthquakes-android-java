package com.weebly.hectorjorozco.earthquakes.ui.sortbypreference;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import androidx.preference.DialogPreference;
import androidx.preference.PreferenceDialogFragmentCompat;

import com.weebly.hectorjorozco.earthquakes.R;
import com.weebly.hectorjorozco.earthquakes.ui.MainActivity;


// Class that handles the DialogPreference dialog fragment shown to the user

public class SortByPreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat {

    private RadioGroup mDateMagnitudeRadioGroup;
    private RadioGroup mAscendingDescendingRadioGroup;


    /**
     * Creates a new instance of this fragment.
     *
     * @param sortByDialogPreferenceKey The key of the DialogPreference that will use this fragment
     * @return A new instance of this fragment.
     */
    public static SortByPreferenceDialogFragmentCompat newInstance(
            String sortByDialogPreferenceKey) {
        final SortByPreferenceDialogFragmentCompat
                sortByPreferenceDialogFragmentCompat = new SortByPreferenceDialogFragmentCompat();
        final Bundle bundle = new Bundle(1);
        bundle.putString(ARG_KEY, sortByDialogPreferenceKey);
        sortByPreferenceDialogFragmentCompat.setArguments(bundle);
        return sortByPreferenceDialogFragmentCompat;
    }


    // Binds views in the content view of the dialog to data.
    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        mDateMagnitudeRadioGroup =
                view.findViewById(R.id.dialog_sort_by_date_magnitude_radio_group);
        mAscendingDescendingRadioGroup =
                view.findViewById(R.id.dialog_sort_by_ascending_descending_radio_group);

        // Get the "Sort by" entry value from the preference that opened the dialog
        int sortByEntryValue = -1;
        DialogPreference preference = getPreference();
        if (preference instanceof SortByDialogPreference) {
            sortByEntryValue =
                    ((SortByDialogPreference) preference).getSortByEntryValue();
        }

        // Set radio groups to the checked states corresponding to the sort by entry value
        if (sortByEntryValue != -1) {
            switch (sortByEntryValue) {
                case MainActivity.SORT_BY_ASCENDING_DATE:
                    mDateMagnitudeRadioGroup.check(R.id.dialog_sort_by_date_radio_button);
                    mAscendingDescendingRadioGroup.check(R.id.dialog_sort_by_ascending_radio_button);
                    break;
                case MainActivity.SORT_BY_DESCENDING_DATE:
                    mDateMagnitudeRadioGroup.check(R.id.dialog_sort_by_date_radio_button);
                    mAscendingDescendingRadioGroup.check(R.id.dialog_sort_by_descending_radio_button);
                    break;
                case MainActivity.SORT_BY_ASCENDING_MAGNITUDE:
                    mDateMagnitudeRadioGroup.check(R.id.dialog_sort_by_magnitude_radio_button);
                    mAscendingDescendingRadioGroup.check(R.id.dialog_sort_by_ascending_radio_button);
                    break;
                case MainActivity.SORT_BY_DESCENDING_MAGNITUDE:
                    mDateMagnitudeRadioGroup.check(R.id.dialog_sort_by_magnitude_radio_button);
                    mAscendingDescendingRadioGroup.check(R.id.dialog_sort_by_descending_radio_button);
                    break;
            }
        }
    }


    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {

            // Generate the value to save
            int dateMagnitudeValue, ascendingDescendingValue, sortByEntryValue;

            if (mDateMagnitudeRadioGroup.getCheckedRadioButtonId() ==
                    R.id.dialog_sort_by_date_radio_button) {
                dateMagnitudeValue = 0;
            } else {
                dateMagnitudeValue = 2;
            }

            if (mAscendingDescendingRadioGroup.getCheckedRadioButtonId() ==
                    R.id.dialog_sort_by_ascending_radio_button) {
                ascendingDescendingValue = 0;
            } else {
                ascendingDescendingValue = 1;
            }

            sortByEntryValue = dateMagnitudeValue + ascendingDescendingValue;

            // Get the related Preference and save the value
            DialogPreference preference = getPreference();
            if (preference instanceof SortByDialogPreference) {
                SortByDialogPreference sortByDialogPreference =
                        ((SortByDialogPreference) preference);
                // This allows the client to ignore the user value.
                if (sortByDialogPreference.callChangeListener(
                        sortByEntryValue)) {
                    // Save the value and update the summary
                    sortByDialogPreference.setSortByEntryValue(sortByEntryValue);
                }
            }
        }
    }
}
