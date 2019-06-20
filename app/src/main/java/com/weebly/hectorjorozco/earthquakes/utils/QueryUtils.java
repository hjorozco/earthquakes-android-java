package com.weebly.hectorjorozco.earthquakes.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import androidx.preference.PreferenceManager;

import android.text.TextUtils;
import android.util.Log;

import com.weebly.hectorjorozco.earthquakes.R;
import com.weebly.hectorjorozco.earthquakes.models.Earthquake;
import com.weebly.hectorjorozco.earthquakes.models.EarthquakesQueryParameters;
import com.weebly.hectorjorozco.earthquakes.models.EarthquakesSearchParameters2;
import com.weebly.hectorjorozco.earthquakes.models.retrofit.Earthquakes;
import com.weebly.hectorjorozco.earthquakes.models.retrofit.Feature;
import com.weebly.hectorjorozco.earthquakes.models.retrofit.Geometry;
import com.weebly.hectorjorozco.earthquakes.models.retrofit.Properties;
import com.weebly.hectorjorozco.earthquakes.retrofit.RetrofitCallback;
import com.weebly.hectorjorozco.earthquakes.retrofit.RetrofitImplementation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
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

    private static String mOrderBy, mMinMagnitude, mMaxMagnitude, mStartDateTime, mEndDateTime,
            mDatePeriod, mStartDate, mEndDate, mLimit, mLocation;

    private static final int MILLISECONDS_IN_ONE_HOUR = 3600000;

    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query";

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    // Used on the information message
    private static String mNumberOfEarthquakesDisplayed;

    // Used by the map activity
    public static List<Earthquake> mEarthquakesList;


    // Global variables
    public static boolean earthquakesFetched = false;
    public static boolean searchingForEarthquakes = true;


    public static List<Earthquake> getEarthquakesListFromRetrofitResult(Context context, Earthquakes retrofitResult) {

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

        // Changes all of the location filter letters to lowercase. Then removes all Spanish
        // accents and adds a space at beginning and end of the it.
        location = location.toLowerCase(locale);
        location = LanguageUtils.removeSpanishAccents(location);
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
            splitString = LanguageUtils.splitLocation(context, properties.getPlace(), locality);

            // Changes all the letters of the primary location String (that will be used to filter
            // the results) to lower case, removes any Spanish accents, adds a space at the
            // beginning and end, and replaces the commas with a blank space.
            locationPrimary = splitString[1].toLowerCase(locale);
            locationPrimary = LanguageUtils.removeSpanishAccents(locationPrimary);
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
                        (!LanguageUtils.locationSearchSpecialCase(locationPrimary, location))) {
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

        mNumberOfEarthquakesDisplayed = String.valueOf(earthquakesAddedToListCounter);

        // Return the list of earthquakes
        return earthquakeList;
    }


    // Returns the number of Earthquakes that will be displayed on the list.
    public static String getNumberOfEarthquakesDisplayed() {
        return mNumberOfEarthquakesDisplayed;
    }

    // Returns the List<Earthquake> from the USGS query.
    public static List<Earthquake> getEarthquakeList() {
        return mEarthquakesList;
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
        mOrderBy = sharedPreferences.getString(
                context.getString(R.string.search_preference_sort_by_key),
                context.getString(R.string.search_preference_sort_by_default_value));


        // Gets an integer with the value of the "minimum magnitude" setting and converts it to a String
        mMinMagnitude = String.valueOf(sharedPreferences.getInt(
                context.getString(R.string.search_preference_minimum_magnitude_key),
                context.getResources().getInteger(R.integer.search_preference_minimum_magnitude_default_value)));


        // Gets a integer with the value of the "maximum magnitude" setting and converts it to a String
        mMaxMagnitude = String.valueOf(sharedPreferences.getInt(
                context.getString(R.string.search_preference_maximum_magnitude_key),
                context.getResources().getInteger(R.integer.search_preference_maximum_magnitude_default_value)));


        // Get the actual time of the day
        long currentTimeInMilliseconds = System.currentTimeMillis();
        String currentTime = new SimpleDateFormat
                ("HH:mm:ss", Locale.getDefault()).format(currentTimeInMilliseconds);
        String currentTime12Hours = new SimpleDateFormat
                ("h:mm a", Locale.getDefault()).format(currentTimeInMilliseconds);

        String startDateTime = currentTime;
        String endDateTime = currentTime;

        mStartDateTime = currentTime12Hours;
        mEndDateTime = currentTime12Hours;

        // Gets the time zone of the device
        TimeZone timeZone = TimeZone.getDefault();

        // Number of hours that needs to be added to convert the current time from UTC to the
        // users time zone.
        int endDateTimeOffset = timeZone.getOffset(currentTimeInMilliseconds) / MILLISECONDS_IN_ONE_HOUR;
        int startDateTimeOffset;

        long millisecondsInOneDay = TimeUnit.DAYS.toMillis(1);

        // Depending on the "date range" value stored on preferences, sets the value of the "starttime"
        // and "endtime" options of the JSON query sent to the USGS. Calculates the start date time offset to UTC.
        mDatePeriod = sharedPreferences.getString(
                context.getString(R.string.search_preference_date_range_key),
                context.getString(R.string.search_preference_date_range_default_value));
        String startDateJSONQuery, endDateJSONQuery;
        long startDateInMilliseconds, endDateInMilliseconds;

        switch (mDatePeriod) {
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

                startDateTime = "00:00:00";
                mStartDateTime = "0:00 AM";
                endDateTime = "23:59:59";
                mEndDateTime = "11:59 PM";

                startDateTimeOffset = timeZone.getOffset(startDateInMilliseconds) / MILLISECONDS_IN_ONE_HOUR;
                endDateTimeOffset = timeZone.getOffset(endDateInMilliseconds) / MILLISECONDS_IN_ONE_HOUR;

                break;
        }

        mStartDate = dateForDisplayFormatter().format(startDateInMilliseconds);
        // Creates the startDate string that will be passed as a parameter to the USGS JSON query.
        startDateJSONQuery = dateForQueryFormatter().format(startDateInMilliseconds)
                + "T" + startDateTime + startDateTimeOffset + ":00";
        mEndDate = dateForDisplayFormatter().format(endDateInMilliseconds);
        // Creates the endDate string that will be passed as a parameter to the USGS JSON query.
        endDateJSONQuery = dateForQueryFormatter().format(endDateInMilliseconds)
                + "T" + endDateTime + endDateTimeOffset + ":00";

        mLocation = sharedPreferences.getString(
                context.getString(R.string.search_preference_location_key),
                context.getString(R.string.search_preference_location_default_value)).trim();

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

        // Create the URI that will be used to query the USGS
        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("starttime", startDateJSONQuery);
        uriBuilder.appendQueryParameter("endtime", endDateJSONQuery);
        uriBuilder.appendQueryParameter("limit", queryLimit);
        uriBuilder.appendQueryParameter("minmagnitude", mMinMagnitude);
        uriBuilder.appendQueryParameter("maxmagnitude", mMaxMagnitude);
        uriBuilder.appendQueryParameter("orderby", mOrderBy);

        return new EarthquakesQueryParameters(startDateJSONQuery, endDateJSONQuery, queryLimit,
                mMinMagnitude, mMaxMagnitude, mOrderBy);
    }


    private static SimpleDateFormat dateForDisplayFormatter() {
        return new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
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

}
