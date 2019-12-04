package com.weebly.hectorjorozco.earthquakes.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.weebly.hectorjorozco.earthquakes.R;
import com.weebly.hectorjorozco.earthquakes.models.Earthquake;
import com.weebly.hectorjorozco.earthquakes.ui.MainActivity;

import java.util.Collections;
import java.util.List;

public class SortFavoritesUtils {

    /**
     * Gets the Students order value from Shared Preferences
     *
     * @return An integer that represents the Students order on the RecyclerView of activity_movies.xml
     * 0 = Order by First Name, 1 = Order by Last Name, 2 = Order by Test result
     */
    public static int getSortByValueFromSharedPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.app_shared_preferences_name), 0);
        return sharedPreferences.getInt(context.getString(
                R.string.activity_favorites_sort_by_shared_preference_key), MainActivity.SORT_BY_DESCENDING_DATE);
    }


    /**
     * Sets the student order value on Shared Preferences
     *
     * @param context      The context used to get resources
     * @param sortCriteria The favorites sort by value that will be saved
     */
    public static boolean setFavoritesSortCriteriaOnSharedPreferences(Context context, int sortCriteria) {
        // If the spinner selected value is different from the previous one save the value
        if (sortCriteria != getSortByValueFromSharedPreferences(context)) {

            SharedPreferences sharedPreferences = context.getSharedPreferences(
                    context.getString(R.string.app_shared_preferences_name), 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(context.getString(R.string.activity_favorites_sort_by_shared_preference_key), sortCriteria);
            editor.apply();

            return true;
        }

        return false;
    }


    /**
     * Orders a list of favorite earthquakes based on the sort criteria value saved on SharedPreferences.
     *
     * @param context   Used to access SharedPreferences
     * @param favorites The list of students to be ordered
     * @return an ordered list of students
     */
    public static List<Earthquake> SortFavorites(Context context, List<Earthquake> favorites, boolean isDistanceShown) {

        switch (getSortByValueFromSharedPreferences(context)) {
            case MainActivity.SORT_BY_ASCENDING_DATE:
                Collections.sort(favorites, Earthquake.ascendingDateComparator);
                break;
            case MainActivity.SORT_BY_DESCENDING_DATE:
                Collections.sort(favorites, Earthquake.descendingDateComparator);
                break;
            case MainActivity.SORT_BY_ASCENDING_MAGNITUDE:
                Collections.sort(favorites, Earthquake.ascendingDateComparator);
                Collections.sort(favorites, Earthquake.ascendingMagnitudeComparator);
                break;
            case MainActivity.SORT_BY_DESCENDING_MAGNITUDE:
                Collections.sort(favorites, Earthquake.descendingDateComparator);
                Collections.sort(favorites, Earthquake.descendingMagnitudeComparator);
                break;
            case MainActivity.SORT_BY_ASCENDING_DISTANCE:
                if (isDistanceShown){
                    List <Earthquake> favoritesWithDistance = QueryUtils.addDistanceToAllEarthquakes(favorites);
                    Collections.sort(favoritesWithDistance, Earthquake.ascendingDateComparator);
                    Collections.sort(favoritesWithDistance, Earthquake.ascendingDistanceComparator);
                    favorites = favoritesWithDistance;
                } else {
                    Collections.sort(favorites, Earthquake.ascendingDateComparator);
                    setFavoritesSortCriteriaOnSharedPreferences(context, MainActivity.SORT_BY_ASCENDING_DATE);
                }
                break;
            case MainActivity.SORT_BY_DESCENDING_DISTANCE:
                if (isDistanceShown){
                    List <Earthquake> favoritesWithDistance = QueryUtils.addDistanceToAllEarthquakes(favorites);
                    Collections.sort(favoritesWithDistance, Earthquake.descendingDateComparator);
                    Collections.sort(favoritesWithDistance, Earthquake.descendingDistanceComparator);
                    favorites = favoritesWithDistance;
                } else {
                    Collections.sort(favorites, Earthquake.descendingDateComparator);
                    setFavoritesSortCriteriaOnSharedPreferences(context, MainActivity.SORT_BY_DESCENDING_DATE);
                }
                break;
            default:
                break;
        }

        return favorites;
    }


    public static String getSortByValueString(Context context) {
        String sortByValueString = "";
        switch (getSortByValueFromSharedPreferences(context)) {
            case MainActivity.SORT_BY_ASCENDING_DATE:
                sortByValueString = context.getString(R.string.activity_favorites_sorted_by_ascending_date_text);
                break;
            case MainActivity.SORT_BY_DESCENDING_DATE:
                sortByValueString = context.getString(R.string.activity_favorites_sorted_by_descending_date_text);
                break;
            case MainActivity.SORT_BY_ASCENDING_MAGNITUDE:
                sortByValueString = context.getString(R.string.activity_favorites_sorted_by_ascending_magnitude_text);
                break;
            case MainActivity.SORT_BY_DESCENDING_MAGNITUDE:
                sortByValueString = context.getString(R.string.activity_favorites_sorted_by_descending_magnitude_text);
                break;
            case MainActivity.SORT_BY_ASCENDING_DISTANCE:
                sortByValueString = context.getString(R.string.activity_favorites_sorted_by_ascending_distance_text);
                break;
            case MainActivity.SORT_BY_DESCENDING_DISTANCE:
                sortByValueString = context.getString(R.string.activity_favorites_sorted_by_descending_distance_text);
                break;
        }
        return sortByValueString;
    }

}

