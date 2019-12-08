package com.weebly.hectorjorozco.earthquakes.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.preference.PreferenceManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.weebly.hectorjorozco.earthquakes.R;
import com.weebly.hectorjorozco.earthquakes.models.Earthquake;
import com.weebly.hectorjorozco.earthquakes.models.EarthquakesListInformationValues;
import com.weebly.hectorjorozco.earthquakes.models.EarthquakesQueryParameters;
import com.weebly.hectorjorozco.earthquakes.models.retrofit.Earthquakes;
import com.weebly.hectorjorozco.earthquakes.models.retrofit.Feature;
import com.weebly.hectorjorozco.earthquakes.models.retrofit.Geometry;
import com.weebly.hectorjorozco.earthquakes.models.retrofit.Properties;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static com.weebly.hectorjorozco.earthquakes.ui.MainActivity.APP_LOCATION_PERMISSION;
import static com.weebly.hectorjorozco.earthquakes.ui.MainActivity.MAX_NUMBER_OF_EARTHQUAKES_FOR_MAP;
import static com.weebly.hectorjorozco.earthquakes.ui.MainActivity.MAX_NUMBER_OF_EARTHQUAKES_LIMIT;


/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public class QueryUtils {

    private static String mLimit;
    private static String mLocation;

    private static final int MILLISECONDS_IN_ONE_HOUR = 3600000;

    public static final byte NO_ACTION = 0;
    public static final byte SEARCHING = 1;
    public static final byte NO_EARTHQUAKES_FOUND = 2;
    public static final byte NO_INTERNET_CONNECTION = 3;
    public static final byte SEARCH_RESULT_NON_NULL = 4;
    public static final byte SEARCH_RESULT_NULL = 5;
    public static final byte SEARCH_CANCELLED = 6;

    public static final double LAT_LONG_NULL_VALUE = 1000;
    public static final double DEPTH_NULL_VALUE = -1;
    public static final float DISTANCE_NULL_VALUE = -1;

    public static final double LAST_KNOW_LOCATION_LAT_LONG_NULL_VALUE = 1000;

    // Used by the map activity
    public static List<Earthquake> sEarthquakesList;
    public static boolean sMoreThanMaximumNumberOfEarthquakesForMap;

    // Used to display the earthquakes list information
    public static EarthquakesListInformationValues
            sEarthquakesListInformationValuesWhenSearchStarted = null;
    public static EarthquakesListInformationValues sEarthquakesListInformationValues = null;

    // Other global variables
    public static boolean sSearchingForEarthquakes = true;
    public static byte sLoadEarthquakesResultCode = NO_ACTION;
    public static boolean sListWillBeLoadedAfterEmpty = true;
    public static boolean sIsPlayingSound = false;

    public static Handler sHandler;
    public static Runnable sRunnable;

    public static double sLastKnownLocationLatitude;
    public static double sLastKnownLocationLongitude;

    public static String sEarthquakesListSortedByDistanceText = "";

    public static final EarthquakesLoadedObservable sEarthquakesLoadedObservable = new EarthquakesLoadedObservable();


    public interface LocationUpdateListener {
        void onLocationUpdate(boolean locationUpdated);
    }


    public static List<Earthquake> getEarthquakesListFromRetrofitResult(Context context,
                                                                        Earthquakes retrofitResult) {

        String location = mLocation;
        String limit = mLimit;

        // Number used to limit the results shown from the JSON query to the USGS.
        int limitNumber = Integer.valueOf(limit);

        String[] splitString;

        // Contains the primary location of the earthquake
        String locationPrimary;

        // Gets the locale of the system
        Locale locale = Resources.getSystem().getConfiguration().locale;

        // Two letter code of the language of the device, for example: English (en) Spanish (es)
        String locality = locale.toString().substring(0, 2);

        location = WordsUtils.removeSpanishAccents(location);
        location = WordsUtils.setUsaSearchName(location);
        location = " " + location + " ";

        List<Earthquake> earthquakeList = new ArrayList<>();

        // Get the list of "feature" objects from the retrofit result. Each feature is an earthquake.
        List<Feature> featuresList = retrofitResult.getFeatures();

        Log.d("TESTING", "From QueryUtils " + retrofitResult.getMetadata().getUrl());

        //Iterate the list of features until the end of the list or until the number of
        // added earthquakes is the same as the limit set by the user.
        int earthquakesAddedToListCounter = 0;

        for (int i = 0; (i < featuresList.size()) && (earthquakesAddedToListCounter < limitNumber); i++) {

            Feature feature = featuresList.get(i);
            Properties properties = feature.getProperties();
            Geometry geometry = feature.getGeometry();
            String id = feature.getId();
            List<Double> coordinates = geometry.getCoordinates();

            // Checks for null values on place
            String place;
            if (properties.getPlace() == null) {
                place = "";
            } else {
                place = properties.getPlace();
            }

            // Checks for null values on latitude
            double latitude;
            if (coordinates.get(1) == null) {
                latitude = LAT_LONG_NULL_VALUE;
            } else {
                latitude = coordinates.get(1);
            }

            // Checks for null values on longitude
            double longitude;
            if (coordinates.get(0) == null) {
                longitude = LAT_LONG_NULL_VALUE;
            } else {
                longitude = coordinates.get(0);
            }

            // Checks for null values of longitude

            // Checks for null values on depth
            double depth;
            if (coordinates.get(2) == null) {
                depth = DEPTH_NULL_VALUE;
            } else {
                depth = coordinates.get(2);
            }

            // Get the place string from the earthquake feature properties, and splits it in two
            // Strings.
            splitString = WordsUtils.splitLocation(context, place, locality);

            // Changes all the letters of the primary location String (that will be used to filter
            // the results) to lower case, removes any Spanish accents, adds a space at the
            // beginning and end, and replaces the commas with a blank space.
            locationPrimary = splitString[1].toLowerCase(locale);
            locationPrimary = WordsUtils.removeSpanishAccents(locationPrimary);
            locationPrimary = " " + locationPrimary + " ";
            locationPrimary = locationPrimary.replace(',', ' ');
            locationPrimary = locationPrimary.replace('-', ' ');

            // If the location filter is empty, then add the earthquake data to the List
            if (location.equals("  ")) {

                earthquakeList.add(new Earthquake(
                        id,
                        properties.getMag(),
                        splitString[0],
                        splitString[1],
                        properties.getTime(),
                        properties.getUrl(),
                        latitude,
                        longitude,
                        depth,
                        properties.getFelt(),
                        properties.getCdi(),
                        properties.getMmi(),
                        properties.getAlert(),
                        properties.getTsunami(),
                        DISTANCE_NULL_VALUE));

                earthquakesAddedToListCounter++;

            } else {
                // If the primary location contains the location filter AND it is not an special
                // case, then adds the earthquake data to the List of Earthquake objects
                if ((locationPrimary.contains(location)) &&
                        (!WordsUtils.locationSearchSpecialCase(locationPrimary, location))) {
                    // Get the values for the magnitude, place, time and URL of the previous JSON object,
                    // creates a new Earthquake object with the these values and adds it to the List
                    earthquakeList.add(new Earthquake(
                            id,
                            properties.getMag(),
                            splitString[0],
                            splitString[1],
                            properties.getTime(),
                            properties.getUrl(),
                            latitude,
                            longitude,
                            depth,
                            properties.getFelt(),
                            properties.getCdi(),
                            properties.getMmi(),
                            properties.getAlert(),
                            properties.getTsunami(),
                            DISTANCE_NULL_VALUE));
                    // Increments the counter of the number of earthquakes added to the List of
                    // Earthquakes
                    earthquakesAddedToListCounter++;
                }
            }
        }

        // Return the list of earthquakes
        return earthquakeList;
    }


    // Returns the List<Earthquake> from the USGS query.
    public static List<Earthquake> getEarthquakesList() {
        return sEarthquakesList;
    }


    /**
     * Builds the parameters for an Earthquakes search based on the preference values saved on
     * Shared Preferences.
     *
     * @return An object that contains the parameters needed to do an Earthquakes search.
     */
    public static EarthquakesQueryParameters getEarthquakesQueryParameters(Context context, LocationUpdateListener locationUpdateListener) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();

        // Gets a String with the value of the "Sort by" setting.
        String[] sortByEntryValues =
                context.getResources().getStringArray(R.array.search_preference_sort_by_entry_values);
        int sortByEntryValue = sharedPreferences.getInt(
                context.getString(R.string.search_preference_sort_by_key),
                context.getResources().getInteger(R.integer.search_preference_sort_by_default_value));
        String sortBy = sortByEntryValues[sortByEntryValue];


        // Gets an integer with the value of the "minimum magnitude" setting and converts it to a String
        String minMagnitude = String.valueOf(sharedPreferences.getInt(
                context.getString(R.string.search_preference_minimum_magnitude_key),
                context.getResources().getInteger(R.integer.search_preference_minimum_magnitude_default_value)));


        // Gets a integer with the value of the "maximum magnitude" setting and converts it to a String
        String maxMagnitude = String.valueOf(sharedPreferences.getInt(
                context.getString(R.string.search_preference_maximum_magnitude_key),
                context.getResources().getInteger(R.integer.search_preference_maximum_magnitude_default_value)));


        // Get the actual time of the day
        long currentTimeInMilliseconds = System.currentTimeMillis();
        String currentTime = new SimpleDateFormat
                ("HH:mm:ss", Locale.getDefault()).format(currentTimeInMilliseconds);

        String startDateTime = currentTime;
        String endDateTime = currentTime;

        // Gets the time zone of the device
        TimeZone timeZone = TimeZone.getDefault();

        // Number of hours that needs to be added to convert the current time from UTC to the
        // users time zone.
        int endDateTimeOffset = timeZone.getOffset(currentTimeInMilliseconds) / MILLISECONDS_IN_ONE_HOUR;
        int startDateTimeOffset;

        long millisecondsInOneDay = TimeUnit.DAYS.toMillis(1);

        // Depending on the "date range" value stored on preferences, sets the value of the "start time"
        // and "end ime" options of the JSON query sent to the USGS. Calculates the start date time offset to UTC.
        String datePeriod = sharedPreferences.getString(
                context.getString(R.string.search_preference_date_range_key),
                context.getString(R.string.search_preference_date_range_default_value));
        String startDateJSONQuery, endDateJSONQuery;
        long startDateInMilliseconds, endDateInMilliseconds;

        switch (datePeriod) {
            case "day":
                startDateInMilliseconds = currentTimeInMilliseconds - millisecondsInOneDay;
                startDateTimeOffset =
                        timeZone.getOffset(startDateInMilliseconds) / MILLISECONDS_IN_ONE_HOUR;
                endDateInMilliseconds = currentTimeInMilliseconds;
                break;
            case "week":
                startDateInMilliseconds = currentTimeInMilliseconds - (millisecondsInOneDay * 7);
                startDateTimeOffset =
                        timeZone.getOffset(startDateInMilliseconds) / MILLISECONDS_IN_ONE_HOUR;
                endDateInMilliseconds = currentTimeInMilliseconds;
                break;
            case "month":
                startDateInMilliseconds = currentTimeInMilliseconds - (millisecondsInOneDay * 30);
                startDateTimeOffset =
                        timeZone.getOffset(startDateInMilliseconds) / MILLISECONDS_IN_ONE_HOUR;
                endDateInMilliseconds = currentTimeInMilliseconds;
                break;
            case "year":
                startDateInMilliseconds = currentTimeInMilliseconds - (millisecondsInOneDay * 365);
                startDateTimeOffset =
                        timeZone.getOffset(startDateInMilliseconds) / MILLISECONDS_IN_ONE_HOUR;
                endDateInMilliseconds = currentTimeInMilliseconds;
                break;
            default:

                startDateInMilliseconds = sharedPreferences.getLong(
                        context.getString(R.string.search_preference_start_date_key),
                        context.getResources().getInteger(R.integer.search_preference_start_date_default_value));

                endDateInMilliseconds = sharedPreferences.getLong(
                        context.getString(R.string.search_preference_end_date_key),
                        context.getResources().getInteger(R.integer.search_preference_end_date_default_value));

                startDateTime = new SimpleDateFormat
                        ("HH:mm:ss", Locale.getDefault()).format(startDateInMilliseconds);

                endDateTime = new SimpleDateFormat
                        ("HH:mm:ss", Locale.getDefault()).format(endDateInMilliseconds);

                startDateTimeOffset = timeZone.getOffset(startDateInMilliseconds) / MILLISECONDS_IN_ONE_HOUR;
                endDateTimeOffset = timeZone.getOffset(endDateInMilliseconds) / MILLISECONDS_IN_ONE_HOUR;

                break;
        }

        String startDateForListInfo = WordsUtils.displayedDateFormatter().format(startDateInMilliseconds);
        // Creates the startDate string that will be passed as a parameter to the USGS JSON query.
        startDateJSONQuery = dateForQueryFormatter().format(startDateInMilliseconds)
                + "T" + startDateTime + startDateTimeOffset + ":00";
        String endDateForListInfo = WordsUtils.displayedDateFormatter().format(endDateInMilliseconds);
        // Creates the endDate string that will be passed as a parameter to the USGS JSON query.
        endDateJSONQuery = dateForQueryFormatter().format(endDateInMilliseconds)
                + "T" + endDateTime + endDateTimeOffset + ":00";

        String latitude = "";
        String longitude = "";
        String maxDistance = "";
        int maxDistanceValue = getMaxDistanceSearchPreference(context);

        if (isLocationPermissionGranted(context) &&
                (maxDistanceValue != 0 || getShowDistanceSearchPreference(context))) {
            updateLastKnowLocation(context, locationUpdateListener);
        }

        if (isLocationPermissionGranted(context) && maxDistanceValue != 0) {
            maxDistance = String.valueOf(maxDistanceValue);
            Location location = getLastKnowLocationFromSharedPreferences(context);
            latitude = String.valueOf(location.getLatitude());
            longitude = String.valueOf(location.getLongitude());
        }


        mLocation = sharedPreferences.getString(
                context.getString(R.string.search_preference_location_key),
                context.getString(R.string.search_preference_location_default_value)).trim().
                replaceAll(" +", " ").
                toLowerCase(Resources.getSystem().getConfiguration().locale);

        // Gets a String with the value of the "Max earthquakes to display" setting.
        mLimit = sharedPreferences.getString(
                context.getString(R.string.search_preference_max_number_of_earthquakes_key),
                context.getString(R.string.search_preference_max_number_of_earthquakes_default_value));

        // If the limit set by the user is empty, set the limit for the search to 20,000
        if (mLimit.isEmpty()) {
            mLimit = String.valueOf(MAX_NUMBER_OF_EARTHQUAKES_LIMIT);

        } else if (Integer.valueOf(mLimit) > MAX_NUMBER_OF_EARTHQUAKES_LIMIT) {
            // If the limit set by the user is 20,000 or more, set the limit for the search to 20,000
            // and update the limit preference value to 20,000
            mLimit = String.valueOf(MAX_NUMBER_OF_EARTHQUAKES_LIMIT);
            preferencesEditor = preferencesEditor.putString(
                    context.getString(R.string.search_preference_max_number_of_earthquakes_key), mLimit);
            preferencesEditor.apply();
        }


        // If the location is empty, set the limit of the query to the limit set by the user on preferences.
        String queryLimit;
        if (mLocation.isEmpty()) {
            queryLimit = mLimit;
        }
        // If there is a location on preferences, set the max number of earthquakes for the query
        // to 20,000 earthquakes because the location will to be searched on all the possible earthquakes.
        // Then then the user's limit will be applied on those results.
        else {
            queryLimit = String.valueOf(MAX_NUMBER_OF_EARTHQUAKES_LIMIT);
        }

        // Stores the values needed to display the Earthquakes list information message
        sEarthquakesListInformationValuesWhenSearchStarted = new EarthquakesListInformationValues(
                sortBy, mLocation, datePeriod, startDateForListInfo, endDateForListInfo,
                minMagnitude, maxMagnitude, mLimit, maxDistance);

        return new EarthquakesQueryParameters(startDateJSONQuery, endDateJSONQuery, queryLimit,
                minMagnitude, maxMagnitude, sortBy, latitude, longitude, maxDistance);
    }


    public static int getMaxDistanceSearchPreference(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(
                context.getString(R.string.search_preference_maximum_distance_key),
                context.getResources().getInteger(R.integer.search_preferences_maximum_distance_default_value));

    }


    /**
     * Formatter for the date used in the USGS query parameters
     *
     * @return The formatter.
     */
    private static SimpleDateFormat dateForQueryFormatter() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    }


    /**
     * Checks if there is an Internet connection available for the app to use.
     *
     * @return true if there is a connection, false otherwise.
     */
    public static boolean internetConnection(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        } else {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return (networkInfo != null && networkInfo.isConnected());
        }
    }


    // Creates a message about the list of earthquakes .
    public static CharSequence createEarthquakesInformationMessageDialogMessage(Context context, EarthquakesListInformationValues values,
                                                                                boolean forCurrentList) {

        String numberOfEarthquakes, earthquakesWord, location, distance, sortedBySuffix, sortedBy, dateRange,
                endDate, startDate, minMagnitude, maxMagnitude, maxNumberOfEarthquakes,
                firstAndLastEarthquakesInfoMessage, orderBy, message;

        orderBy = values.getOrderBy();

        numberOfEarthquakes = String.format(Locale.getDefault(), "%,d",
                Integer.valueOf(values.getNumberOfEarthquakesDisplayed()));


        firstAndLastEarthquakesInfoMessage = "";
        // If there is only one earthquake on the list, set the strings values to singular.
        if (numberOfEarthquakes.equals("1")) {
            earthquakesWord = context.getString(R.string.earthquakes_text_singular);
            sortedBySuffix = "";
        } else {
            // If there is more than one earthquake on the list, set the strings values to plural
            earthquakesWord = context.getString(R.string.earthquakes_text_plural);
            sortedBySuffix = context.getString(R.string.earthquakes_list_title_found_and_sorted_words_suffix);

            // If the message is for the list
            if (forCurrentList) {
                String firstAndLastEarthquakeOrder, firstEarthquakeValue, lastEarthquakeValue;

                if (QueryUtils.sEarthquakesListSortedByDistanceText.isEmpty()) {
                    if (orderBy.equals(context.getString(R.string.search_preference_sort_by_ascending_magnitude_entry_value)) ||
                            orderBy.equals(context.getString(R.string.search_preference_sort_by_descending_magnitude_entry_value))) {
                        firstAndLastEarthquakeOrder = context.getString(R.string.magnitude_text);
                        firstEarthquakeValue = values.getFirstEarthquakeMag();
                        lastEarthquakeValue = values.getLastEarthquakeMag();

                    } else {
                        firstAndLastEarthquakeOrder = context.getString(R.string.date_text);
                        firstEarthquakeValue = values.getFirstEarthquakeDate();
                        lastEarthquakeValue = values.getLastEarthquakeDate();
                    }
                } else {
                    firstAndLastEarthquakeOrder = context.getString(R.string.distance_text);
                    firstEarthquakeValue = values.getFirstEarthquakeDistance();
                    lastEarthquakeValue = values.getLastEarthquakeDistance();

                }
                firstAndLastEarthquakesInfoMessage = String.format(context.getString(R.string.current_list_alert_dialog_message_bottom_section),
                        firstAndLastEarthquakeOrder, firstEarthquakeValue, lastEarthquakeValue);
            } else {

                // If the message is for the map
                if (QueryUtils.sMoreThanMaximumNumberOfEarthquakesForMap) {
                    numberOfEarthquakes = String.format(Locale.getDefault(), "%,d", MAX_NUMBER_OF_EARTHQUAKES_FOR_MAP);
                    firstAndLastEarthquakesInfoMessage = context.getString(R.string.activity_earthquakes_map_info_alert_dialog_message_bottom_section,
                            numberOfEarthquakes);
                }
            }
        }

        location = values.getLocation();
        if (location.isEmpty()) {
            location = context.getString(R.string.the_whole_world_text);
        } else {
            if (WordsUtils.isUnitedStatesAbbreviation(location) ||
                    WordsUtils.isUnitedStatesName(location)) {
                location = context.getString(R.string.earthquakes_list_title_us_name);
            } else {
                location = WordsUtils.formatLocationText(location);
            }
        }

        distance = "";
        if (!values.getMaxDistance().isEmpty() &&
                QueryUtils.sLastKnownLocationLatitude != QueryUtils.LAST_KNOW_LOCATION_LAT_LONG_NULL_VALUE &&
                QueryUtils.sLastKnownLocationLongitude != QueryUtils.LAST_KNOW_LOCATION_LAT_LONG_NULL_VALUE) {
            distance = " " + context.getString(R.string.earthquakes_list_title_max_distance_from_you_section,
                    String.format(Locale.getDefault(), "%,d", Integer.valueOf(values.getMaxDistance())));
        }

        switch (values.getDatePeriod()) {
            case "day":
                dateRange = context.getString(R.string.twenty_four_hours_text);
                break;
            case "week":
                dateRange = context.getString(R.string.seven_days_text);
                break;
            case "month":
                dateRange = context.getString(R.string.thirty_days_text);
                break;
            case "year":
                dateRange = context.getString(R.string.three_hundred_sixty_five_days_text);
                break;
            default:
                dateRange = context.getString(R.string.custom_text);
                dateRange = WordsUtils.changeFirstLetterToLowercase(dateRange);
                break;
        }

        endDate = values.getEndDate();
        startDate = values.getStartDate();
        minMagnitude = values.getMinMagnitude();
        maxMagnitude = values.getMaxMagnitude();

        maxNumberOfEarthquakes = String.format(Locale.getDefault(), "%,d", Integer.valueOf(values.getLimit()));

        if (forCurrentList) {

            sortedBy = "";
            if (orderBy.equals(context.getString(R.string.search_preference_sort_by_ascending_date_entry_value))) {
                sortedBy = context.getString(R.string.search_preference_sort_by_ascending_date_entry);
            } else if (orderBy.equals(context.getString(R.string.search_preference_sort_by_descending_date_entry_value))) {
                sortedBy = context.getString(R.string.search_preference_sort_by_descending_date_entry);
            } else if (orderBy.equals(context.getString(R.string.search_preference_sort_by_ascending_magnitude_entry_value))) {
                sortedBy = context.getString(R.string.search_preference_sort_by_ascending_magnitude_entry);
            } else if (orderBy.equals(context.getString(R.string.search_preference_sort_by_descending_magnitude_entry_value))) {
                sortedBy = context.getString(R.string.search_preference_sort_by_descending_magnitude_entry);
            }
            sortedBy = WordsUtils.changeFirstLetterToLowercase(sortedBy);

            String reSortedBy = "";

            if (!QueryUtils.sEarthquakesListSortedByDistanceText.isEmpty()) {
                reSortedBy = " " + QueryUtils.sEarthquakesListSortedByDistanceText + ",";
                reSortedBy = reSortedBy.toLowerCase();
            }

            message = context.getString(R.string.current_list_alert_dialog_message_top_section, numberOfEarthquakes,
                    earthquakesWord, location, distance, sortedBySuffix, sortedBy, reSortedBy, maxNumberOfEarthquakes, dateRange,
                    startDate, endDate, minMagnitude,
                    maxMagnitude, firstAndLastEarthquakesInfoMessage);
        } else {
            message = context.getString(R.string.activity_earthquakes_map_info_alert_dialog_message_top_section, numberOfEarthquakes,
                    earthquakesWord, location, distance, maxNumberOfEarthquakes, dateRange, startDate, endDate, minMagnitude,
                    maxMagnitude, firstAndLastEarthquakesInfoMessage);
        }

        return Html.fromHtml(message);
    }


    // Helper method that rounds a double to only one decimal place and converts it to a String
    public static String getMagnitudeText(double magnitude) {
        int scale = (int) Math.pow(10, 1);
        double roundedMagnitude = (double) Math.round(magnitude * scale) / scale;
        String magnitudeText = new DecimalFormat("0.0").format(roundedMagnitude);
        magnitudeText = magnitudeText.replace(',', '.');
        return magnitudeText;
    }


    /**
     * Helper method used to determine the color used to display the earthquake information
     *
     * @param magnitude The magnitude of the earthquake.
     * @return The colors used to display the earthquake information
     */
    private static EarthquakeColors getEarthquakeColors(Context context, double magnitude) {
        EarthquakeColors earthquakeColors;
        int magnitudeFloor = (int) Math.floor(magnitude);
        switch (magnitudeFloor) {
            case 0:
                earthquakeColors = new EarthquakeColors
                        (context.getResources().getColor(R.color.magnitude0),
                                context.getResources().getColor(R.color.background0));
                break;
            case 1:
                earthquakeColors = new EarthquakeColors
                        (context.getResources().getColor(R.color.magnitude1),
                                context.getResources().getColor(R.color.background1));
                break;
            case 2:
                earthquakeColors = new EarthquakeColors
                        (context.getResources().getColor(R.color.magnitude2),
                                context.getResources().getColor(R.color.background2));
                break;
            case 3:
                earthquakeColors = new EarthquakeColors
                        (context.getResources().getColor(R.color.magnitude3),
                                context.getResources().getColor(R.color.background3));
                break;
            case 4:
                earthquakeColors = new EarthquakeColors
                        (context.getResources().getColor(R.color.magnitude4),
                                context.getResources().getColor(R.color.background4));
                break;
            case 5:
                earthquakeColors = new EarthquakeColors
                        (context.getResources().getColor(R.color.magnitude5),
                                context.getResources().getColor(R.color.background5));
                break;
            case 6:
                earthquakeColors = new EarthquakeColors
                        (context.getResources().getColor(R.color.magnitude6),
                                context.getResources().getColor(R.color.background6));
                break;
            case 7:
                earthquakeColors = new EarthquakeColors
                        (context.getResources().getColor(R.color.magnitude7),
                                context.getResources().getColor(R.color.background7));
                break;
            case 8:
                earthquakeColors = new EarthquakeColors
                        (context.getResources().getColor(R.color.magnitude8),
                                context.getResources().getColor(R.color.background8));
                break;
            default:
                earthquakeColors = new EarthquakeColors
                        (context.getResources().getColor(R.color.magnitude9plus),
                                context.getResources().getColor(R.color.background9plus));
                break;
        }
        return earthquakeColors;
    }


    /**
     * Class that models the colors used to display an earthquake information in the RecyclerView
     */
    static class EarthquakeColors {
        // The color used in the magnitude circle border, its text and the information texts
        final int mMagnitudeColor;
        // The background color of the magnitude circle
        final int mMagnitudeBackgroundColor;

        private EarthquakeColors(int magnitudeColor, int magnitudeBackgroundColor) {
            mMagnitudeColor = magnitudeColor;
            mMagnitudeBackgroundColor = magnitudeBackgroundColor;
        }

        int getMagnitudeColor() {
            return mMagnitudeColor;
        }

        int getMagnitudeBackgroundColor() {
            return mMagnitudeBackgroundColor;
        }

    }


    public static void setupEarthquakeInformationOnViews(
            Context context, Earthquake earthquake, View magnitudeCircleView,
            TextView magnitudeTextView, TextView locationOffsetTextView,
            TextView locationPrimaryTextView, TextView distanceTextView,
            TextView dateTextView, TextView timeTextView) {

        // Set magnitude text
        String magnitudeToDisplay = QueryUtils.getMagnitudeText(earthquake.getMagnitude());
        magnitudeTextView.setText(magnitudeToDisplay);

        // Set colors for magnitude circle and text
        double roundedMagnitude = Double.parseDouble(magnitudeToDisplay);
        GradientDrawable magnitudeCircle = (GradientDrawable) magnitudeCircleView.getBackground();
        EarthquakeColors earthquakeColors = QueryUtils.getEarthquakeColors(context, roundedMagnitude);
        int magnitudeColor = earthquakeColors.getMagnitudeColor();
        int magnitudeBackgroundColor = earthquakeColors.getMagnitudeBackgroundColor();
        magnitudeCircle.setColor(magnitudeBackgroundColor);
        magnitudeCircle.setStroke(context.getResources().getDimensionPixelSize(R.dimen.magnitude_circle_stroke_width),
                magnitudeColor);
        magnitudeTextView.setTextColor(magnitudeColor);

        // Location primary can be empty
        String locationPrimary = earthquake.getLocationPrimary();
        if (locationPrimary.isEmpty()) {
            locationPrimary = context.getString(R.string.activity_main_no_earthquake_location_text);
        }

        locationOffsetTextView.setText(earthquake.getLocationOffset());
        locationPrimaryTextView.setText(locationPrimary);
        locationOffsetTextView.setTextColor(magnitudeColor);
        locationPrimaryTextView.setTextColor(magnitudeColor);

        // If the distance needs to be displayed
        if (distanceTextView != null) {

            float distance = earthquake.getDistance();

            // If the earthquake does not have a distance saved calculate it and save on the earthquake
            if (distance == DISTANCE_NULL_VALUE) {
                float[] result = new float[1];
                Location.distanceBetween(sLastKnownLocationLatitude, sLastKnownLocationLongitude,
                        earthquake.getLatitude(), earthquake.getLongitude(), result);
                distance = result[0];

                earthquake.setDistance(distance);
            }

            distanceTextView.setText(context.getString(R.string.activity_main_distance_from_you_text, formatDistance(distance)));
            distanceTextView.setTextColor(magnitudeColor);
        }

        Date dateObject = new Date(earthquake.getTimeInMilliseconds());
        dateTextView.setText(WordsUtils.formatDate(dateObject));
        timeTextView.setText(WordsUtils.formatTime(dateObject));
        dateTextView.setTextColor(magnitudeColor);
        timeTextView.setTextColor(magnitudeColor);
    }


    public static String formatDistance(float distance) {
        return String.format(Locale.getDefault(), "%,d",  Math.round(distance/1000));
    }


    public static void openWebPageInGoogleChrome(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage("com.android.chrome");
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            // Chrome browser presumably not installed so allow user to choose instead
            intent.setPackage(null);
            context.startActivity(intent);
        }
    }


    public static boolean getSoundSearchPreference(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).
                getBoolean(context.getString(R.string.search_preference_sound_key),
                        context.getResources().getBoolean(R.bool.search_preference_sound_default_value));

    }


    public static boolean getShowDistanceSearchPreference(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).
                getBoolean(context.getString(R.string.search_preference_show_distance_key),
                        context.getResources().getBoolean(R.bool.search_preference_show_distance_default_value));
    }


    public static Location getLastKnowLocationFromSharedPreferences(Context context) {
        SharedPreferences mSharedPreferences = context.getSharedPreferences(
                context.getString(R.string.app_shared_preferences_name), 0);
        String latitude = mSharedPreferences.getString(context.getString(
                R.string.last_known_device_location_latitude_shared_preference_key), "");
        String longitude = mSharedPreferences.getString(context.getString(
                R.string.last_known_device_location_longitude_shared_preference_key), "");
        Location location = new Location("");

        double latitudeNumber, longitudeNumber;
        if (latitude.isEmpty()) {
            latitudeNumber = LAST_KNOW_LOCATION_LAT_LONG_NULL_VALUE;
        } else {
            latitudeNumber = Double.valueOf(latitude);
        }
        if (longitude.isEmpty()) {
            longitudeNumber = LAST_KNOW_LOCATION_LAT_LONG_NULL_VALUE;
        } else {
            longitudeNumber = Double.valueOf(longitude);
        }

        location.setLatitude(latitudeNumber);
        location.setLongitude(longitudeNumber);

        return location;
    }


    public static void setupProgressBarForPreLollipop(ProgressBar progressBar, Context context) {
        // Fixes pre-Lollipop progressBar indeterminateDrawable tinting
        Drawable wrapDrawable = DrawableCompat.wrap(progressBar.getIndeterminateDrawable());
        DrawableCompat.setTint(wrapDrawable, context.getResources().getColor(R.color.colorAccent));
        progressBar.setIndeterminateDrawable(DrawableCompat.unwrap(wrapDrawable));
    }


    public static boolean isLocationPermissionGranted(Context context) {
        return ContextCompat.checkSelfPermission(context, APP_LOCATION_PERMISSION)
                == PackageManager.PERMISSION_GRANTED;
    }


    public static void updateLastKnowLocation(Context context, LocationUpdateListener
            locationUpdateListener) {
        FusedLocationProviderClient fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(context);
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        SharedPreferences sharedPreferences = context.getSharedPreferences(
                                context.getString(R.string.app_shared_preferences_name), 0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(context.getString(R.string.last_known_device_location_latitude_shared_preference_key),
                                String.valueOf(location.getLatitude()));
                        editor.putString(context.getString(R.string.last_known_device_location_longitude_shared_preference_key),
                                String.valueOf(location.getLongitude()));
                        editor.apply();

                        locationUpdateListener.onLocationUpdate(true);

                    } else {
                        locationUpdateListener.onLocationUpdate(false);
                    }
                });
    }


    public static List<Earthquake> addDistanceToAllEarthquakes(List<Earthquake> earthquakes) {
        for (int i = 0; i < earthquakes.size(); i++) {
            Earthquake earthquake = earthquakes.get(i);
            if (earthquake.getDistance() == DISTANCE_NULL_VALUE) {
                float[] result = new float[1];
                Location.distanceBetween(sLastKnownLocationLatitude, sLastKnownLocationLongitude,
                        earthquake.getLatitude(), earthquake.getLongitude(), result);
                earthquake.setDistance(result[0]);
            }
        }
        return earthquakes;
    }


    // Returns true if the "show distance from you" search preference is enabled and there is a last
    // known location available.
    public static boolean isDistanceShown(Context context) {
        return (QueryUtils.getShowDistanceSearchPreference(context) &&
                QueryUtils.sLastKnownLocationLatitude != QueryUtils.LAST_KNOW_LOCATION_LAT_LONG_NULL_VALUE &&
                QueryUtils.sLastKnownLocationLongitude != QueryUtils.LAST_KNOW_LOCATION_LAT_LONG_NULL_VALUE);
    }

}
