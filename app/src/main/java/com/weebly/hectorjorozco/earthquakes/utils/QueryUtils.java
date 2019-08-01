package com.weebly.hectorjorozco.earthquakes.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Html;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.weebly.hectorjorozco.earthquakes.R;
import com.weebly.hectorjorozco.earthquakes.models.Earthquake;
import com.weebly.hectorjorozco.earthquakes.models.EarthquakesListInformationValues;
import com.weebly.hectorjorozco.earthquakes.models.EarthquakesQueryParameters;
import com.weebly.hectorjorozco.earthquakes.models.retrofit.Earthquakes;
import com.weebly.hectorjorozco.earthquakes.models.retrofit.Feature;
import com.weebly.hectorjorozco.earthquakes.models.retrofit.Geometry;
import com.weebly.hectorjorozco.earthquakes.models.retrofit.Properties;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

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

    // Used by the map activity
    public static List<Earthquake> sEarthquakesList;

    // Used to display the earthquakes list information
    public static EarthquakesListInformationValues
            sEarthquakesListInformationValuesWhenSearchStarted = null;
    public static EarthquakesListInformationValues sEarthquakesListInformationValues = null;

    // Other global variables
    public static boolean sSearchingForEarthquakes = true;
    public static byte sLoadEarthquakesResultCode = NO_ACTION;
    public static boolean sListWillBeLoadedAfterEmpty = true;
    public static boolean sOneOrMoreEarthquakesFoundByRetrofitQuery = false;


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

        //Iterate the list of features until the end of the list or until the number of
        // added earthquakes is the same as the limit set by the user.
        int earthquakesAddedToListCounter = 0;

        for (int i = 0; (i < featuresList.size()) && (earthquakesAddedToListCounter < limitNumber); i++) {

            Feature feature = featuresList.get(i);
            Properties properties = feature.getProperties();
            Geometry geometry = feature.getGeometry();
            List<Double> coordinates = geometry.getCoordinates();

            // Get the place string from the earthquake feature properties, and splits it in two
            // Strings.
            splitString = WordsUtils.splitLocation(context, properties.getPlace(), locality);

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
                        properties.getMag(),
                        splitString[0],
                        splitString[1],
                        properties.getTime(),
                        properties.getUrl(),
                        coordinates.get(1),
                        coordinates.get(0)));

                earthquakesAddedToListCounter++;

            } else {
                // If the primary location contains the location filter AND it is not an special
                // case, then adds the earthquake data to the List of Earthquake objects
                if ((locationPrimary.contains(location)) &&
                        (!WordsUtils.locationSearchSpecialCase(locationPrimary, location))) {
                    // Get the values for the magnitude, place, time and URL of the previous JSON object,
                    // creates a new Earthquake object with the these values and adds it to the List
                    earthquakeList.add(new Earthquake(
                            properties.getMag(),
                            splitString[0],
                            splitString[1],
                            properties.getTime(),
                            properties.getUrl(),
                            coordinates.get(1),
                            coordinates.get(0)));
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
    public static EarthquakesQueryParameters getEarthquakesQueryParameters(Context context) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();

        // Gets a String with the value of the "Sort by" setting.
        String orderBy = sharedPreferences.getString(
                context.getString(R.string.search_preference_sort_by_key),
                context.getString(R.string.search_preference_sort_by_default_value));


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

        // Depending on the "date range" value stored on preferences, sets the value of the "starttime"
        // and "endtime" options of the JSON query sent to the USGS. Calculates the start date time offset to UTC.
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

        String startDateForListInfo = dateForListInfoFormatter().format(startDateInMilliseconds);
        // Creates the startDate string that will be passed as a parameter to the USGS JSON query.
        startDateJSONQuery = dateForQueryFormatter().format(startDateInMilliseconds)
                + "T" + startDateTime + startDateTimeOffset + ":00";
        Log.d("TESTING", startDateJSONQuery);
        String endDateForListInfo = dateForListInfoFormatter().format(endDateInMilliseconds);
        // Creates the endDate string that will be passed as a parameter to the USGS JSON query.
        endDateJSONQuery = dateForQueryFormatter().format(endDateInMilliseconds)
                + "T" + endDateTime + endDateTimeOffset + ":00";
        Log.d("TESTING", endDateJSONQuery);
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
                orderBy, mLocation, datePeriod, startDateForListInfo, endDateForListInfo,
                minMagnitude, maxMagnitude, mLimit);

        return new EarthquakesQueryParameters(startDateJSONQuery, endDateJSONQuery, queryLimit,
                minMagnitude, maxMagnitude, orderBy);
    }


//    private static SimpleDateFormat dateForDisplayFormatter() {
//        return new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
//    }


    /**
     * Produces the date formatter used for showing the date in the summary.
     *
     * @return the SimpleDateFormat used for summary dates
     */
    private static SimpleDateFormat dateForListInfoFormatter() {

        SimpleDateFormat simpleDateFormat;
        if (WordsUtils.getLocaleLanguage().equals("es")) {
            simpleDateFormat = new SimpleDateFormat("d 'de' MMMM 'del' yyyy, hh:mm aaa", Locale.getDefault());
        } else {
            simpleDateFormat = new SimpleDateFormat("MMMM d, yyyy hh:mm aaa", Locale.getDefault());
        }

        return simpleDateFormat;
    }

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
    public static CharSequence createCurrentListAlertDialogMessage(Context context, EarthquakesListInformationValues values) {

        String numberOfEarthquakes, earthquakesWord, location, sortedBySuffix, sortedBy, dateRange, firstEarthquakeEndDate, firstEarthquakeStartDate, firstEarthquakeMinMagnitude,
                firstEarthquakeMaxMagnitude, maxNumberOfEarthquakes, firstAndLastEarthquakesInfoMessage, orderBy;

        orderBy = values.getOrderBy();

        numberOfEarthquakes = values.getNumberOfEarthquakesDisplayed();

        // If there is only one earthquake on the list, set the strings values to singular.
        if (numberOfEarthquakes.equals("1")) {
            earthquakesWord = context.getString(R.string.earthquakes_text_singular);
            sortedBySuffix = "";
            firstAndLastEarthquakesInfoMessage = "";
        } else {
            // If there is more than one earthquake on the list, set the strings values to plural
            earthquakesWord = context.getString(R.string.earthquakes_text_plural);
            sortedBySuffix = context.getString(R.string.earthquakes_list_title_found_and_sorted_words_suffix);
            if (orderBy.equals(context.getString(R.string.search_preference_sort_by_magnitude_entry_value))) {
                firstAndLastEarthquakesInfoMessage = String.format(context.getString(R.string.current_list_alert_dialog_message_2),
                        context.getString(R.string.magnitude_text), values.getFirstEarthquakeMag(), values.getLastEarthquakeMag());

            } else {
                firstAndLastEarthquakesInfoMessage = String.format(context.getString(R.string.current_list_alert_dialog_message_2),
                        context.getString(R.string.date_text), values.getFirstEarthquakeDate(), values.getLastEarthquakeDate());
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

        firstEarthquakeEndDate = values.getEndDate();
        firstEarthquakeStartDate = values.getStartDate();
        firstEarthquakeMinMagnitude = values.getMinMagnitude();
        firstEarthquakeMaxMagnitude = values.getMaxMagnitude();

        if (orderBy.equals(context.getString(R.string.search_preference_sort_by_magnitude_entry_value))) {
            sortedBy = context.getString(R.string.search_preference_sort_by_magnitude_entry);
        } else {
            sortedBy = context.getString(R.string.search_preference_sort_by_date_entry);
        }
        sortedBy = WordsUtils.changeFirstLetterToLowercase(sortedBy);

        maxNumberOfEarthquakes = String.format(Locale.getDefault(), "%,d", Integer.valueOf(values.getLimit()));

        String text = String.format
                (context.getString(R.string.current_list_alert_dialog_message_1), numberOfEarthquakes,
                        earthquakesWord, location, sortedBySuffix, sortedBy, maxNumberOfEarthquakes, dateRange,
                        firstEarthquakeStartDate, firstEarthquakeEndDate, firstEarthquakeMinMagnitude,
                        firstEarthquakeMaxMagnitude, firstAndLastEarthquakesInfoMessage);

        return Html.fromHtml(text);
    }


    // public static boolean

}
