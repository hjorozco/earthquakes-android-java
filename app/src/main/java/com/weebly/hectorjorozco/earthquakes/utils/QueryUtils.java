package com.weebly.hectorjorozco.earthquakes.utils;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;

import com.weebly.hectorjorozco.earthquakes.models.Earthquake;
import com.weebly.hectorjorozco.earthquakes.models.EarthquakesSearchParameters;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private static String mNumberOfEarthquakesDisplayed;

    private static List<Earthquake> mEarthquakeList;

    /**
     * Query the USGS dataset and return an {@link ArrayList<Earthquake>} object with a list of
     * Earthquakes information.
     */
    public static List<Earthquake> fetchEarthquakeData(Context context, String requestUrl,
                                                       String location, String limit) {

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response
        List<Earthquake> earthquakes = extractFeaturesFromJson(context, jsonResponse, location, limit);

        // Assigns the value of the List<Earthquake>, extracted from the JSON Response, to a private
        // static variable that will be used by the MAPS.
        mEarthquakeList = earthquakes;

        // Return the {@link Event}
        return earthquakes;
    }

    /**
     * Returns new URL object from the given string URL.
     *
     * @param stringUrl String that represents a URL query to the USGS.
     * @return URL object if succeeds, if not returns null.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL object and return a String as the response.
     *
     * @param url URL object with the query to the USGS.
     * @return a String with the JSON response to the query. If there is no response, it returns an
     * empty String.
     * @throws IOException Thrown by a connection problem.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(120000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }


    /**
     * Convert the {@link InputStream} into a String which contains the whole JSON response from
     * the server.
     *
     * @param inputStream InputStream object with the response data from the USGS server.
     * @return a String which contains the whole JSON response from the USGS server.
     * @throws IOException Thrown by a connection problem.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return an {@link ArrayList} object by parsing out information from the input earthquakeJSON
     * string.
     *
     * @param earthquakeJSON A String which contains the whole JSON response from the USGS server.
     * @return A list of Earthquake objects
     */

    private static List<Earthquake> extractFeaturesFromJson(Context context, String earthquakeJSON,
                                                            String locationFilter, String limit) {

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
        locationFilter = locationFilter.toLowerCase(locale);
        locationFilter = LanguageUtils.removeSpanishAccents(locationFilter);
        locationFilter = " " + locationFilter + " ";

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(earthquakeJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding earthquakes to
        List<Earthquake> earthquakes = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Parse the response given by the JSON response string
            JSONObject jsonObject = new JSONObject(earthquakeJSON);

            //Get the instance of JSONArray with key name "features" within the previous JSONObject
            JSONArray featuresJsonArray = jsonObject.getJSONArray("features");

            //Iterate the JSONArray object until the end of the array or until the number of
            // added earthquakes is the same as the limit set by the user.
            int j = 0;
            JSONObject featureJsonObject, propertiesJsonObject, geometryJsonObject;
            JSONArray coordinatesJsonArray;

            for (int i = 0; (i < featuresJsonArray.length()) && (j < limitNumber); i++) {

                // Get JSONObject in position i of JSONArray
                featureJsonObject = featuresJsonArray.getJSONObject(i);

                // Get JSON Object with key value "properties" within the previous JSON object
                propertiesJsonObject = featureJsonObject.getJSONObject("properties");

                // Get JSON Object with key value "geometry" within the previous JSON object
                geometryJsonObject = featureJsonObject.getJSONObject("geometry");

                // Get JSONArray with the coordinates of the earthquake.
                coordinatesJsonArray = geometryJsonObject.getJSONArray("coordinates");

                // Get the String location from the current Earthquake object, and splits it in two
                // Strings.
                splitString = LanguageUtils.splitLocation(context, propertiesJsonObject.getString("place"), locality);

                // Changes all the letters of the primary location String (that will be used to filter
                // the results) to lower case, removes any Spanish accents, adds a space at the
                // beginning and end, and replaces the commas with a blank space.
                locationPrimary = splitString[1].toLowerCase(locale);
                locationPrimary = LanguageUtils.removeSpanishAccents(locationPrimary);
                locationPrimary = " " + locationPrimary + " ";
                locationPrimary = locationPrimary.replace(',', ' ');
                locationPrimary = locationPrimary.replace('-', ' ');

                // If the location filter is empty, then add the earthquake data to the List
                if (locationFilter.equals("  ")) {
                    // Get the values for the magnitude, place, time and URL of the previous JSON object,
                    // creates a new Earthquake object with the these values and adds it to the List
                    earthquakes.add(new Earthquake(
                            propertiesJsonObject.getDouble("mag"),
                            splitString[0],
                            splitString[1],
                            propertiesJsonObject.getLong("time"),
                            propertiesJsonObject.getString("url"),
                            coordinatesJsonArray.getDouble(1),
                            coordinatesJsonArray.getDouble(0)));
                    // Increments the counter of the number of earthquakes added to the List of
                    // Earthquakes
                    j++;
                } else {
                    // If the primary location contains the location filter AND it is not an special
                    // case, then adds the earthquake data to the List of Earthquake objects
                    if ((locationPrimary.contains(locationFilter)) &&
                            (!LanguageUtils.locationSearchSpecialCase(locationPrimary, locationFilter))) {
                        // Get the values for the magnitude, place, time and URL of the previous JSON object,
                        // creates a new Earthquake object with the these values and adds it to the List
                        earthquakes.add(new Earthquake(
                                propertiesJsonObject.getDouble("mag"),
                                splitString[0],
                                splitString[1],
                                propertiesJsonObject.getLong("time"),
                                propertiesJsonObject.getString("url"),
                                coordinatesJsonArray.getDouble(1),
                                coordinatesJsonArray.getDouble(0)));
                        // Increments the counter of the number of earthquakes added to the List of
                        // Earthquakes
                        j++;
                    }
                }
            }

            mNumberOfEarthquakesDisplayed = String.valueOf(j);

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return earthquakes;
    }


    // Returns the number of Earthquakes that will be displayed on the list.
    public static String getNumberOfEarthquakesDisplayed() {
        return mNumberOfEarthquakesDisplayed;
    }

    // Returns the List<Earthquake> from the USGS query.
    public static List<Earthquake> getEarthquakeList() {
        return mEarthquakeList;
    }


    /**
     *
     * @return
     */
    public static EarthquakesSearchParameters getEarthquakesSearchParameters(){
        return null;
    }

}
