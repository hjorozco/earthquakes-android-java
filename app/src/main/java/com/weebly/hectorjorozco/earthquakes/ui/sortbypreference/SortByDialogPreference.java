package com.weebly.hectorjorozco.earthquakes.ui.sortbypreference;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.preference.DialogPreference;

import com.weebly.hectorjorozco.earthquakes.R;

// Class that handles the value of the DialogPreference that is saved on SharedPreferences

@SuppressWarnings("WeakerAccess")
public class SortByDialogPreference extends DialogPreference {


    private int mSortByEntryValue;

    public SortByDialogPreference(Context context) {
        this(context, null);
    }

    public SortByDialogPreference(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.dialogPreferenceStyle);
    }

    public SortByDialogPreference(Context context, AttributeSet attrs,
                                  int defStyleAttr) {
        this(context, attrs, defStyleAttr, defStyleAttr);
    }

    public SortByDialogPreference(Context context, AttributeSet attrs,
                                  int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    public int getSortByEntryValue() {
        return mSortByEntryValue;
    }


    // Save the "Sort by" int value to Shared Preferences and update the preference summary and icon
    public void setSortByEntryValue(int sortByEntryValue) {
        mSortByEntryValue = sortByEntryValue;
        persistInt(sortByEntryValue);
        String[] sortBySummaries =
                getContext().getResources().getStringArray(R.array.search_preference_sort_by_summaries);
        setSummary(sortBySummaries[sortByEntryValue]);

        if (sortByEntryValue % 2 == 0){
            setIcon(R.drawable.ic_sort_ascending_brown_24dp);
        } else {
            setIcon(R.drawable.ic_sort_descending_brown_24dp);
        }


    }


    // Reads the preference default value from xml attribute. Fallback value is set to 0.
    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, 0);
    }


    // Set the initial value of the preference when the preference screen is shown
    @Override
    protected void onSetInitialValue(Object defaultValue) {

        // The first time the preference is created defaultValue is not null
        int defaultValueInt;
        if (defaultValue!=null){
            defaultValueInt = (int) defaultValue;
        } else {
            defaultValueInt = mSortByEntryValue;
        }

        setSortByEntryValue(getPersistedInt(defaultValueInt));
    }


    // Set the layout resource for the DialogPreference
    @Override
    public int getDialogLayoutResource() {
        return R.layout.dialog_preference_sort_by;
    }

}
