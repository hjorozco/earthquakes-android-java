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
import com.weebly.hectorjorozco.earthquakes.utils.UiUtils;

import java.util.Objects;


public class SortEarthquakesDialogFragment extends DialogFragment {

    private static final String DIALOG_FRAGMENT_TITLE_ARGUMENT_KEY = "title";

    public interface SortEarthquakesDialogFragmentListener {
        void onSortCriteriaSelected(boolean sortByDistanceAscending);
    }


    public SortEarthquakesDialogFragment() {
        // Empty constructor is required for DialogFragment
    }

    public static SortEarthquakesDialogFragment newInstance(String title) {
        SortEarthquakesDialogFragment sortStudentsDialogFragment =
                new SortEarthquakesDialogFragment();
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
                LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment_activity_main_sort_by_distance, null);
        builder.setView(view);

        RadioGroup sortByDistanceRadioGroup =
                view.findViewById(R.id.dialog_sort_by_distance_radio_group);

        setupRadioButton(view.findViewById(R.id.dialog_sort_by_ascending_distance_radio_button));
        setupRadioButton(view.findViewById(R.id.dialog_sort_by_descending_distance_radio_button));

        sortByDistanceRadioGroup.check(R.id.dialog_sort_by_ascending_distance_radio_button);

        builder.setPositiveButton(getString(R.string.ok_text), (dialog, which) -> {
            SortEarthquakesDialogFragmentListener listener = (SortEarthquakesDialogFragmentListener) getActivity();
            Objects.requireNonNull(listener).onSortCriteriaSelected(
                    sortByDistanceRadioGroup.getCheckedRadioButtonId() ==
                            R.id.dialog_sort_by_ascending_distance_radio_button);
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