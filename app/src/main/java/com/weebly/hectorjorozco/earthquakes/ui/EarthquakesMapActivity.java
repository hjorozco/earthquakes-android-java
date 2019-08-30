package com.weebly.hectorjorozco.earthquakes.ui;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.weebly.hectorjorozco.earthquakes.R;
import com.weebly.hectorjorozco.earthquakes.models.Earthquake;
import com.weebly.hectorjorozco.earthquakes.utils.MapsUtils;
import com.weebly.hectorjorozco.earthquakes.utils.QueryUtils;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;


public class EarthquakesMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String IS_FAB_MENU_OPEN_VALUE_KEY = "IS_FAB_MENU_OPEN_VALUE_KEY";

    private GoogleMap mGoogleMap;
    private SharedPreferences mSharedPreferences;
    private int mGoogleMapType;
    private boolean mIsFabMenuOpen = false;
    private boolean mRotation = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earthquakes_map);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState != null) {
            mRotation = true;
            mIsFabMenuOpen = savedInstanceState.getBoolean(IS_FAB_MENU_OPEN_VALUE_KEY);
        }

        // Gets the values saved on Shared Preferences to set them on the map
        mSharedPreferences = getSharedPreferences(
                getString(R.string.app_shared_preferences_name), 0);
        mGoogleMapType = mSharedPreferences.getInt(getString(
                R.string.google_map_type_shared_preference_key), GoogleMap.MAP_TYPE_NORMAL);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activity_earthquakes_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Sets up the Google Map FABs
        LinearLayout layoutFabLayer1 = findViewById(R.id.activity_earthquakes_map_fab_layer_1_linear_layout);
        LinearLayout layoutFabLayer2 = findViewById(R.id.activity_earthquakes_map_fab_layer_2_linear_layout);
        LinearLayout layoutFabLayer3 = findViewById(R.id.activity_earthquakes_map_fab_layer_3_linear_layout);
        FloatingActionButton layersFab = findViewById(R.id.activity_earthquakes_map_layers_fab);
        View fabBackgroundLayout = findViewById(R.id.activity_earthquakes_map_fab_background);

        if (mIsFabMenuOpen) {
            MapsUtils.showFabMenu(layoutFabLayer1, layoutFabLayer2, layoutFabLayer3,
                    fabBackgroundLayout, layersFab, this);
        }

        layersFab.setOnClickListener(view -> {
            if (!mIsFabMenuOpen) {
                mIsFabMenuOpen = true;
                MapsUtils.showFabMenu(layoutFabLayer1, layoutFabLayer2, layoutFabLayer3,
                        fabBackgroundLayout, layersFab, this);
            } else {
                mIsFabMenuOpen = false;
                MapsUtils.hideFabMenu(layoutFabLayer1, layoutFabLayer2, layoutFabLayer3,
                        fabBackgroundLayout, layersFab, this);
            }
        });

        findViewById(R.id.activity_earthquakes_map_fab_1).setOnClickListener(v -> {
            mGoogleMapType = GoogleMap.MAP_TYPE_NORMAL;
            if (mGoogleMap != null && mGoogleMap.getMapType() != mGoogleMapType) {
                mGoogleMap.setMapType(mGoogleMapType);
            }
        });

        findViewById(R.id.activity_earthquakes_map_fab_2).setOnClickListener(v -> {
            mGoogleMapType = GoogleMap.MAP_TYPE_HYBRID;
            if (mGoogleMap != null && mGoogleMap.getMapType() != mGoogleMapType) {
                mGoogleMap.setMapType(mGoogleMapType);
            }
        });

        findViewById(R.id.activity_earthquakes_map_fab_3).setOnClickListener(v -> {
            mGoogleMapType = GoogleMap.MAP_TYPE_TERRAIN;
            if (mGoogleMap != null && mGoogleMap.getMapType() != mGoogleMapType) {
                mGoogleMap.setMapType(mGoogleMapType);
            }
        });

        fabBackgroundLayout.setOnClickListener(view -> {
            mIsFabMenuOpen = false;
            MapsUtils.hideFabMenu(layoutFabLayer1,
                    layoutFabLayer2, layoutFabLayer3, fabBackgroundLayout, layersFab, this);
        });


        // Show a message if the number of earthquakes in the list is greater than 1000 (the map
        // will only show a maximum of 1000 earthquakes.
        if (QueryUtils.sMoreThanMaximumNumberOfEarthquakesForMap) {
            Snackbar.make(findViewById(android.R.id.content),
                    getString(R.string.activity_earthquakes_map_max_number_exceeded_message,
                            String.format(Locale.getDefault(), "%,d",
                                    MainActivity.MAX_NUMBER_OF_EARTHQUAKES_FOR_MAP)),
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
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;

        List<Earthquake> earthquakes = QueryUtils.getEarthquakesList();
        Earthquake earthquake;
        LatLng earthquakePosition, firstEarthquakePosition;
        DecimalFormat formatter = new DecimalFormat("0.0");

        for (int i = 0; i < earthquakes.size(); i++) {

            earthquake = earthquakes.get(i);

            earthquakePosition = new LatLng(earthquake.getLatitude(), earthquake.getLongitude());

            String magnitudeToDisplay = formatter.format(earthquake.getMagnitude());
            magnitudeToDisplay = magnitudeToDisplay.replace(',', '.');
            double roundedMagnitude = Double.parseDouble(magnitudeToDisplay);

            MapsUtils.MarkerAttributes markerAttributes = MapsUtils.getMarkerAttributes(roundedMagnitude);

            googleMap.addMarker(new MarkerOptions()
                    .position(earthquakePosition)
                    .title(MapsUtils.constructEarthquakeTitleForMarker(earthquake, magnitudeToDisplay, this))
                    .snippet(MapsUtils.constructEarthquakeSnippetForMarker(earthquake.getTimeInMilliseconds()))
                    .icon(BitmapDescriptorFactory.fromResource(markerAttributes.getMarkerImageResourceId()))
                    .anchor(0.5f, 0.5f)
                    .alpha(markerAttributes.getAlphaValue())
                    .zIndex(markerAttributes.getZIndex()));
        }

        googleMap.setMapType(mGoogleMapType);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        // Animate the camera only when the activity is created, not after a rotation
        if (!mRotation) {
            firstEarthquakePosition = new LatLng(earthquakes.get(0).getLatitude(), earthquakes.get(0).getLongitude());
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(firstEarthquakePosition));
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_FAB_MENU_OPEN_VALUE_KEY, mIsFabMenuOpen);
    }


    /**
     * When the activity is destroyed save the values of the map preferences so
     * they can be restored when the activity is started again.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(getString(R.string.google_map_type_shared_preference_key),
                mGoogleMapType);
        editor.apply();
    }

}


