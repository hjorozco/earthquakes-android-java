package com.weebly.hectorjorozco.earthquakes.ui;

import android.os.Bundle;

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


    private LinearLayout mLayoutFab1, mLayoutFab2, mLayoutFab3;
    private View mFabBackgroundLayout;
    private FloatingActionButton mMainFab;
    private boolean mIsFabOpen = false;
    private GoogleMap mGoogleMap;
    private int mMapType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earthquakes_map);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activity_earthquakes_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Sets up the Google Map FABs
        mLayoutFab1 = findViewById(R.id.activity_earthquakes_map_fab_1_linear_layout);
        mLayoutFab2 = findViewById(R.id.activity_earthquakes_map_fab_2_linear_layout);
        mLayoutFab3 = findViewById(R.id.activity_earthquakes_map_fab_3_linear_layout);
        mMainFab = findViewById(R.id.activity_earthquakes_map_main_fab);
        mFabBackgroundLayout = findViewById(R.id.activity_earthquakes_map_fab_background);

        mMainFab.setOnClickListener(view -> {
            if (!mIsFabOpen) {
                mIsFabOpen = true;
                MapsUtils.showFabMenu(mLayoutFab1, mLayoutFab2, mLayoutFab3,
                        mFabBackgroundLayout, mMainFab, this);
            } else {
                mIsFabOpen = false;
                MapsUtils.hideFabMenu(mLayoutFab1, mLayoutFab2, mLayoutFab3,
                        mFabBackgroundLayout, mMainFab, this);
            }
        });

        findViewById(R.id.activity_earthquakes_map_fab_1).setOnClickListener(v -> {
            mMapType = GoogleMap.MAP_TYPE_NORMAL;
            if (mGoogleMap!=null && mGoogleMap.getMapType() != mMapType) {
                mGoogleMap.setMapType(mMapType);
            }
        });

        findViewById(R.id.activity_earthquakes_map_fab_2).setOnClickListener(v -> {
            mMapType = GoogleMap.MAP_TYPE_HYBRID;
            if (mGoogleMap!=null && mGoogleMap.getMapType() != mMapType) {
                mGoogleMap.setMapType(mMapType);
            }
        });

        findViewById(R.id.activity_earthquakes_map_fab_3).setOnClickListener(v -> {
            mMapType = GoogleMap.MAP_TYPE_TERRAIN;
            if (mGoogleMap!=null && mGoogleMap.getMapType() != mMapType) {
                mGoogleMap.setMapType(mMapType);
            }
        });

        mFabBackgroundLayout.setOnClickListener(view -> {
            mIsFabOpen = false;
            MapsUtils.hideFabMenu(mLayoutFab1,
                    mLayoutFab2, mLayoutFab3, mFabBackgroundLayout, mMainFab, this);
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
    public void onMapReady(GoogleMap map) {

        mGoogleMap = map;

        List<Earthquake> earthquakes = QueryUtils.getEarthquakesList();
        Earthquake earthquake;
        LatLng earthquakePosition, firstEarthquakePosition;
        DecimalFormat formatter = new DecimalFormat("0.0");

        for (int i = 0; i < earthquakes.size(); i++) {

            earthquake = earthquakes.get(i);

            earthquakePosition = new LatLng(earthquake.getLatitude(), earthquake.getLongitude());

            String magnitudeToDisplay = formatter.format(earthquake.getMagnitude());
            magnitudeToDisplay = magnitudeToDisplay.replace(',', '.');
            Double roundedMagnitude = Double.valueOf(magnitudeToDisplay);

            MapsUtils.MarkerAttributes markerAttributes = MapsUtils.getMarkerAttributes(roundedMagnitude);

            map.addMarker(new MarkerOptions()
                    .position(earthquakePosition)
                    .title(MapsUtils.constructEarthquakeTitleForMarker(earthquake, magnitudeToDisplay))
                    .snippet(MapsUtils.constructEarthquakeSnippetForMarker(earthquake.getTimeInMilliseconds()))
                    .icon(BitmapDescriptorFactory.fromResource(markerAttributes.getMarkerImageResourceId()))
                    .anchor(0.5f, 0.5f)
                    .alpha(markerAttributes.getAlphaValue())
                    .zIndex(markerAttributes.getZIndex()));
        }

        firstEarthquakePosition = new LatLng(earthquakes.get(0).getLatitude(), earthquakes.get(0).getLongitude());
        map.animateCamera(CameraUpdateFactory.newLatLng(firstEarthquakePosition));

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}


