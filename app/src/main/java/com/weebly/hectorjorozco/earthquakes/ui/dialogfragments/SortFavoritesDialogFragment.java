package com.weebly.hectorjorozco.earthquakes.ui.dialogfragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.CompoundButtonCompat;
import androidx.fragment.app.DialogFragment;

import com.weebly.hectorjorozco.earthquakes.R;
import com.weebly.hectorjorozco.earthquakes.ui.MainActivity;
import com.weebly.hectorjorozco.earthquakes.utils.QueryUtils;
import com.weebly.hectorjorozco.earthquakes.utils.SortFavoritesUtils;
import com.weebly.hectorjorozco.earthquakes.utils.UiUtils;

import java.util.Objects;


public class SortFavoritesDialogFragment extends DialogFragment {

    private static final String DIALOG_FRAGMENT_TITLE_ARGUMENT_KEY = "title";

    public interface SortFavoritesDialogFragmentListener {
        void onSortCriteriaSelected(int sortCriteriaSelected, boolean isDistanceShown);
    }


    public SortFavoritesDialogFragment() {
        // Empty constructor is required for DialogFragment
    }

    public static SortFavoritesDialogFragment newInstance(String title) {
        SortFavoritesDialogFragment sortStudentsDialogFragment =
                new SortFavoritesDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DIALOG_FRAGMENT_TITLE_ARGUMENT_KEY, title);
        sortStudentsDialogFragment.setArguments(bundle);
        return sortStudentsDialogFragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        int colorPrimaryDark = getResources().getColor(R.color.colorPrimaryDark);
        String colorPrimaryDarkString = Integer.toHexString(colorPrimaryDark & 0x00ffffff);

        Bundle bundle = getArguments();
        String title = Objects.requireNonNull(bundle).getString(DIALOG_FRAGMENT_TITLE_ARGUMENT_KEY);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity(),
                R.style.ThemeDialogCustomPrimaryColor);

        builder.setTitle(Html.fromHtml(getString(R.string.html_text_with_color,
                colorPrimaryDarkString, title)));

        @SuppressLint("InflateParams") View view =
                LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment_activity_favorites_sort_by, null);
        builder.setView(view);

        RadioGroup dateMagnitudeDistanceRadioGroup =
                view.findViewById(R.id.dialog_sort_by_date_magnitude_radio_group);
        RadioGroup ascendingDescendingRadioGroup =
                view.findViewById(R.id.dialog_sort_by_ascending_descending_radio_group);

        setupRadioButton(view.findViewById(R.id.dialog_sort_by_date_radio_button));
        setupRadioButton(view.findViewById(R.id.dialog_sort_by_magnitude_radio_button));
        setupRadioButton(view.findViewById(R.id.dialog_sort_by_ascending_radio_button));
        setupRadioButton(view.findViewById(R.id.dialog_sort_by_descending_radio_button));

        // Shows or hides the distance from you RadioButton depending on the search preferences and location values
        boolean isDistanceShown = QueryUtils.getShowDistanceSearchPreference(getContext()) &&
                QueryUtils.sLastKnownLocationLatitude != QueryUtils.LAST_KNOW_LOCATION_LAT_LONG_NULL_VALUE &&
                QueryUtils.sLastKnownLocationLongitude != QueryUtils.LAST_KNOW_LOCATION_LAT_LONG_NULL_VALUE;

        RadioButton distanceRadioButton = view.findViewById(R.id.dialog_sort_by_distance_radio_button);
        if (isDistanceShown) {
            setupRadioButton(distanceRadioButton);
            distanceRadioButton.setVisibility(View.VISIBLE);
        } else {
            distanceRadioButton.setVisibility(View.GONE);
        }

        view.findViewById(R.id.dialog_sort_by_divider).setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));

        switch (SortFavoritesUtils.getSortByValueFromSharedPreferences(requireActivity())) {
            case MainActivity.SORT_BY_ASCENDING_DATE:
                dateMagnitudeDistanceRadioGroup.check(R.id.dialog_sort_by_date_radio_button);
                ascendingDescendingRadioGroup.check(R.id.dialog_sort_by_ascending_radio_button);
                break;
            case MainActivity.SORT_BY_DESCENDING_DATE:
                dateMagnitudeDistanceRadioGroup.check(R.id.dialog_sort_by_date_radio_button);
                ascendingDescendingRadioGroup.check(R.id.dialog_sort_by_descending_radio_button);
                break;
            case MainActivity.SORT_BY_ASCENDING_MAGNITUDE:
                dateMagnitudeDistanceRadioGroup.check(R.id.dialog_sort_by_magnitude_radio_button);
                ascendingDescendingRadioGroup.check(R.id.dialog_sort_by_ascending_radio_button);
                break;
            case MainActivity.SORT_BY_DESCENDING_MAGNITUDE:
                dateMagnitudeDistanceRadioGroup.check(R.id.dialog_sort_by_magnitude_radio_button);
                ascendingDescendingRadioGroup.check(R.id.dialog_sort_by_descending_radio_button);
                break;
            case MainActivity.SORT_BY_ASCENDING_DISTANCE:
                if (isDistanceShown) {
                    dateMagnitudeDistanceRadioGroup.check(R.id.dialog_sort_by_distance_radio_button);
                } else {
                    dateMagnitudeDistanceRadioGroup.check(R.id.dialog_sort_by_date_radio_button);
                }
                ascendingDescendingRadioGroup.check(R.id.dialog_sort_by_ascending_radio_button);
                break;
            case MainActivity.SORT_BY_DESCENDING_DISTANCE:
                if (isDistanceShown) {
                    dateMagnitudeDistanceRadioGroup.check(R.id.dialog_sort_by_distance_radio_button);
                } else {
                    dateMagnitudeDistanceRadioGroup.check(R.id.dialog_sort_by_date_radio_button);
                }
                ascendingDescendingRadioGroup.check(R.id.dialog_sort_by_descending_radio_button);
                break;
        }


        builder.setPositiveButton(getString(R.string.ok_text), (dialog, which) -> {

            int dateMagnitudeDistanceValue, ascendingDescendingValue, sortByEntryValue;
            int checkedRadioButtonId = dateMagnitudeDistanceRadioGroup.getCheckedRadioButtonId();

            if (checkedRadioButtonId == R.id.dialog_sort_by_date_radio_button) {
                dateMagnitudeDistanceValue = 0;
            } else if (checkedRadioButtonId == R.id.dialog_sort_by_magnitude_radio_button) {
                dateMagnitudeDistanceValue = 2;
            } else if (checkedRadioButtonId == R.id.dialog_sort_by_distance_radio_button) {
                dateMagnitudeDistanceValue = 4;
            } else {
                dateMagnitudeDistanceValue = -1;
            }

            if (ascendingDescendingRadioGroup.getCheckedRadioButtonId() ==
                    R.id.dialog_sort_by_ascending_radio_button) {
                ascendingDescendingValue = 0;
            } else {
                ascendingDescendingValue = 1;
            }

            sortByEntryValue = dateMagnitudeDistanceValue + ascendingDescendingValue;

            SortFavoritesDialogFragmentListener listener = (SortFavoritesDialogFragmentListener) getActivity();
            Objects.requireNonNull(listener).onSortCriteriaSelected(sortByEntryValue, isDistanceShown);
            dismiss();
        });

        builder.setNegativeButton(getString(R.string.cancel_text), (dialog, which) -> dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();

        // Sets the color of the positive and negative buttons.
        int colorAccent = getResources().getColor(R.color.colorAccent);
        Button button = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        button.setTextColor(colorAccent);
        UiUtils.setupAlertDialogButtonBackground(getContext(), button);
        button = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        button.setTextColor(colorAccent);
        UiUtils.setupAlertDialogButtonBackground(getContext(), button);

        return alertDialog;
    }


    private void setupRadioButton(RadioButton radioButton) {

        int colorAccent = getResources().getColor(R.color.colorAccent);
        int colorPrimary = getResources().getColor(R.color.colorPrimary);
        int[][] states = {{android.R.attr.state_checked}, {}};
        int[] colors = {colorAccent, colorPrimary};

        radioButton.setTextColor(getResources().getColor(R.color.colorPrimary));
        CompoundButtonCompat.setButtonTintList(radioButton, new ColorStateList(states, colors));
    }

}