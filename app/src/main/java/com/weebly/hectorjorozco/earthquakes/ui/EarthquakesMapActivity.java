package com.weebly.hectorjorozco.earthquakes.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.weebly.hectorjorozco.earthquakes.R;
import com.weebly.hectorjorozco.earthquakes.models.Earthquake;
import com.weebly.hectorjorozco.earthquakes.utils.QueryUtils;
import com.weebly.hectorjorozco.earthquakes.utils.WordsUtils;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class EarthquakesMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earthquakes_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (QueryUtils.sMoreThanMaximumNumberOfEarthquakesForMap){

            Snackbar.make(findViewById(android.R.id.content),
                    getString(R.string.activity_earthquakes_map_max_number_exceeded_message,
                            String.format(Locale.getDefault(), "%,d",
                                    MainActivity.MAX_NUMBER_OF_EARTHQUAKES_FOR_MAP) ),
                    Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap map) {

        List<Earthquake> earthquakes = QueryUtils.getEarthquakesList();
        Earthquake earthquake;
        LatLng earthquakePosition;
        DecimalFormat formatter = new DecimalFormat("0.0");

        for (int i = 0; i < earthquakes.size(); i++) {

            earthquake = earthquakes.get(i);

            earthquakePosition = new LatLng(earthquake.getLatitude(), earthquake.getLongitude());

            String magnitudeToDisplay = formatter.format(earthquake.getMagnitude());
            magnitudeToDisplay = magnitudeToDisplay.replace(',', '.');
            Double roundedMagnitude = Double.valueOf(magnitudeToDisplay);

            MarkerAttributes markerAttributes = getMarkerAttributes(roundedMagnitude);

            map.addMarker(new MarkerOptions()
                    .position(earthquakePosition)
                    .title(constructEarthquakeTitle(earthquake, magnitudeToDisplay))
                    .snippet(constructEarthquakeSnippet(earthquake.getTimeInMilliseconds()))
                    .icon(BitmapDescriptorFactory.fromResource(markerAttributes.getMarkerImageResourceId()))
                    .anchor(0.5f, 0.5f)
                    .alpha(markerAttributes.getAlphaValue())
                    .zIndex(markerAttributes.getZIndex()));
        }
    }

    private String constructEarthquakeTitle(Earthquake earthquake, String magnitude) {
        return magnitude + " - " + earthquake.getLocationOffset() + " " + earthquake.getLocationPrimary();
    }

    private String constructEarthquakeSnippet(long earthquakeTimeInMilliseconds) {
        Date dateObject = new Date(earthquakeTimeInMilliseconds);
        return WordsUtils.formatDate(dateObject) + " " + WordsUtils.formatTime(dateObject);
    }


    /**
     * Returns the marker image based on the magnitude of the earthquake.
     *
     * @param magnitude The magnitude of the earthquake.
     * @return The marker image to be displayed on the map based on the magnitude of the earthquake.
     */
    private MarkerAttributes getMarkerAttributes(double magnitude) {
        MarkerAttributes markerAttributes;
        int magnitudeFloor = (int) Math.floor(magnitude);
        switch (magnitudeFloor) {
            case 0:
                markerAttributes = new MarkerAttributes(R.drawable.ic_mag_0, 0.0f, 0.8f);
                break;
            case 1:
                markerAttributes = new MarkerAttributes(R.drawable.ic_mag_1, 1.0f, 0.8f);
                break;
            case 2:
                markerAttributes = new MarkerAttributes(R.drawable.ic_mag_2, 2.0f, 0.8f);
                break;
            case 3:
                markerAttributes = new MarkerAttributes(R.drawable.ic_mag_3, 3.0f, 0.9f);
                break;
            case 4:
                markerAttributes = new MarkerAttributes(R.drawable.ic_mag_4, 4.0f, 0.9f);
                break;
            case 5:
                markerAttributes = new MarkerAttributes(R.drawable.ic_mag_5, 5.0f, 0.9f);
                break;
            case 6:
                markerAttributes = new MarkerAttributes(R.drawable.ic_mag_6, 6.0f, 1.0f);
                break;
            case 7:
                markerAttributes = new MarkerAttributes(R.drawable.ic_mag_7, 7.0f, 1.0f);
                break;
            case 8:
                markerAttributes = new MarkerAttributes(R.drawable.ic_mag_8, 8.0f, 1.0f);
                break;
            default:
                markerAttributes = new MarkerAttributes(R.drawable.ic_mag_9, 9.0f, 1.0f);
                break;
        }
        return markerAttributes;
    }


    /**
     * Class the contains attributes of a marker to be displayed on the earthquakes map.
     */
    private class MarkerAttributes {
        int mMarkerImageResourceId;
        float mZIndex;
        float mAlphaValue;

        private MarkerAttributes(int markerImageResourceId, float zIndex, float alphaValue) {
            mMarkerImageResourceId = markerImageResourceId;
            mZIndex = zIndex;
            mAlphaValue = alphaValue;
        }

        private int getMarkerImageResourceId() {
            return mMarkerImageResourceId;
        }

        private float getZIndex() {
            return mZIndex;
        }

        private float getAlphaValue() {
            return mAlphaValue;
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}


