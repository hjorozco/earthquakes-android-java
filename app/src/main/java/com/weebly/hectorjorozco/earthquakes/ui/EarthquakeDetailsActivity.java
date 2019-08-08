package com.weebly.hectorjorozco.earthquakes.ui;

import android.os.Build;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.weebly.hectorjorozco.earthquakes.R;
import com.weebly.hectorjorozco.earthquakes.models.Earthquake;
import com.weebly.hectorjorozco.earthquakes.utils.QueryUtils;

import java.text.DecimalFormat;

import static com.weebly.hectorjorozco.earthquakes.ui.EarthquakesMapActivity.constructEarthquakeSnippet;
import static com.weebly.hectorjorozco.earthquakes.ui.EarthquakesMapActivity.constructEarthquakeTitle;
import static com.weebly.hectorjorozco.earthquakes.ui.EarthquakesMapActivity.getMarkerAttributes;


public class EarthquakeDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String EXTRA_EARTHQUAKE = "EXTRA_EARTHQUAKE";

    private Earthquake mEarthquake;
    private int mTextColor;
    private boolean mRotation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earthquake_details);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(EXTRA_EARTHQUAKE)) {
            mEarthquake = bundle.getParcelable(EXTRA_EARTHQUAKE);
        }

        if (savedInstanceState != null) {
            mRotation = true;
        }

        TextView magnitudeTextView = findViewById(R.id.activity_earthquake_details_magnitude_text_view);
        TextView locationOffsetTextView = findViewById(R.id.activity_earthquake_details_location_offset_text_view);
        TextView locationPrimaryTextView = findViewById(R.id.activity_earthquake_details_location_primary_text_view);
        TextView dateAndTimeTextView = findViewById(R.id.activity_earthquake_details_date_and_time_text_view);

        mTextColor = QueryUtils.setupEarthquakeInformationOnViews(this, mEarthquake, magnitudeTextView,
                locationOffsetTextView, locationPrimaryTextView, dateAndTimeTextView, null);


        // If Android version is 21 or up set a transition for the elements in the top of the activity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setSharedElementEnterTransition(
                    TransitionInflater.from(this).inflateTransition(R.transition.move));

            Transition sharedElementEnterTransition = getWindow().getSharedElementEnterTransition();
            sharedElementEnterTransition.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {
                }

                @Override
                public void onTransitionEnd(Transition transition) {
                    setupEarthquakeDetails();
                }

                @Override
                public void onTransitionCancel(Transition transition) {
                }

                @Override
                public void onTransitionPause(Transition transition) {
                }

                @Override
                public void onTransitionResume(Transition transition) {
                }
            });
        } else {
            setupEarthquakeDetails();
        }

        // After a rotation set up the earthquake details again because "onTransitionEnd" will
        // not be called.
        if (mRotation) {
            setupEarthquakeDetails();
        }
    }


    private void setupEarthquakeDetails() {
        TextView coordinatesAndDepthTextView =
                findViewById(R.id.activity_earthquake_details_coordinates_and_depth_text_view);

        double latitude, longitude;
        String latitudeLetter, longitudeLetter;

        if (mEarthquake.getLatitude() < 0) {
            latitude = mEarthquake.getLatitude() * -1;
            latitudeLetter = getString(R.string.activity_earthquake_details_south_latitude_letter);
        } else {
            latitude = mEarthquake.getLatitude();
            latitudeLetter = getString(R.string.activity_earthquake_details_north_latitude_letter);
        }

        if (mEarthquake.getLongitude() < 0) {
            longitude = mEarthquake.getLongitude() * -1;
            longitudeLetter = getString(R.string.activity_earthquake_details_west_longitude_letter);
        } else {
            longitude = mEarthquake.getLongitude();
            longitudeLetter = getString(R.string.activity_earthquake_details_east_longitude_letter);
        }

        coordinatesAndDepthTextView.setText(getString(R.string.activity_earthquake_details_coordinates_and_depth_text,
                latitude, latitudeLetter, longitude, longitudeLetter, mEarthquake.getDepth()));
        coordinatesAndDepthTextView.setTextColor(mTextColor);

        SupportMapFragment earthquakeDetailsMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.activity_earthquake_details_map);
        if (earthquakeDetailsMapFragment != null) {
            earthquakeDetailsMapFragment.getMapAsync(this);
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
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        DecimalFormat formatter = new DecimalFormat("0.0");

        LatLng earthquakePosition = new LatLng(mEarthquake.getLatitude(), mEarthquake.getLongitude());

        String magnitudeToDisplay = formatter.format(mEarthquake.getMagnitude());
        magnitudeToDisplay = magnitudeToDisplay.replace(',', '.');
        Double roundedMagnitude = Double.valueOf(magnitudeToDisplay);

        EarthquakesMapActivity.MarkerAttributes markerAttributes = getMarkerAttributes(roundedMagnitude);

        googleMap.addMarker(new MarkerOptions()
                .position(earthquakePosition)
                .title(constructEarthquakeTitle(mEarthquake, magnitudeToDisplay))
                .snippet(constructEarthquakeSnippet(mEarthquake.getTimeInMilliseconds()))
                .icon(BitmapDescriptorFactory.fromResource(markerAttributes.getMarkerImageResourceId()))
                .anchor(0.5f, 0.5f)
                .alpha(markerAttributes.getAlphaValue())
                .zIndex(markerAttributes.getZIndex()));

        if (!mRotation) googleMap.animateCamera(CameraUpdateFactory.newLatLng(earthquakePosition));
    }
}
