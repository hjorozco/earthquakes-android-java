package com.weebly.hectorjorozco.earthquakes.ui;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.weebly.hectorjorozco.earthquakes.R;
import com.weebly.hectorjorozco.earthquakes.models.Earthquake;
import com.weebly.hectorjorozco.earthquakes.utils.MapsUtils;
import com.weebly.hectorjorozco.earthquakes.utils.QueryUtils;

import java.text.DecimalFormat;


public class EarthquakeDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String EXTRA_EARTHQUAKE = "EXTRA_EARTHQUAKE";
    private static final String MAP_TYPE_KEY = "MAP_TYPE_KEY";

    private Earthquake mEarthquake;
    private int mTextColor;

    private WebView mUsgsMapWebView;
    private FrameLayout mGoogleMapLinearLayout;
    private CustomScrollView mCustomScrollView;
    private TextView mUsgsMapNoInternetTextView;
    private FloatingActionButton mMainFab;
    private RadioGroup mMapTypeRadioGroup;
    private LinearLayout mLayoutFab1, mLayoutFab2, mLayoutFab3;
    private View mFabBackgroundLayout;
    private GoogleMap mGoogleMap;
    private boolean mUsgsMapLoaded = false;
    private boolean mGoogleMapLoaded = false;
    private boolean mGoogleMapRadioButtonClicked = false;
    private boolean mOnBackPressed = false;
    private boolean mRotation = false;
    private boolean mIsFabOpen = false;
    private int mMapType;




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
            mMapType = savedInstanceState.getInt(MAP_TYPE_KEY);
        } else {
            mMapType = GoogleMap.MAP_TYPE_NORMAL;
        }


        // Sets up the views that will be animated on entry and exit for Android versions 21 or up
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
                    if (!mOnBackPressed) {
                        setupEarthquakeDetails();
                    }
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


    /**
     * Sets up all the views that are not animated on this Activity.
     */
    private void setupEarthquakeDetails() {

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        // Set up the TextView that shows the coordinates and depth of the earthquake
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

        // Sets up the maps views
        mUsgsMapWebView = findViewById(R.id.activity_earthquake_details_usgs_map_web_view);
        mGoogleMapLinearLayout = findViewById(R.id.activity_earthquake_details_google_map_frame_layout);
        mCustomScrollView = findViewById(R.id.activity_earthquake_details_custom_scroll_view);
        mUsgsMapNoInternetTextView = findViewById(R.id.activity_earthquake_details_usgs_map_no_internet_text_view);

        SharedPreferences sharedPreferences = getSharedPreferences(
                getString(R.string.app_shared_preferences_name), 0);

        mMapTypeRadioGroup = findViewById(R.id.activity_earthquake_details_map_type_radio_group);
        if (sharedPreferences.getBoolean(getString(
                R.string.earthquake_details_map_type_shared_preference_key), true)) {
            mMapTypeRadioGroup.check(R.id.activity_earthquake_details_google_map_type_radio_button);
            showGoogleMap();
        } else {
            mMapTypeRadioGroup.check(R.id.activity_earthquake_details_usgs_map_type_radio_button);
            showUsgsMap();
        }

        mUsgsMapNoInternetTextView.setOnClickListener(v -> showUsgsMap());

        mMapTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            boolean googleMapType;
            if (checkedId == R.id.activity_earthquake_details_google_map_type_radio_button) {
                showGoogleMap();
                googleMapType = true;
                mGoogleMapRadioButtonClicked = true;
            } else {
                showUsgsMap();
                googleMapType = false;
            }
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(getString(R.string.earthquake_details_map_type_shared_preference_key),
                    googleMapType);
            editor.apply();
        });

        // Sets up the Google Map FABs
        mLayoutFab1 = findViewById(R.id.activity_earthquake_details_google_map_fab_1_linear_layout);
        mLayoutFab2 = findViewById(R.id.activity_earthquake_details_google_map_fab_2_linear_layout);
        mLayoutFab3 = findViewById(R.id.activity_earthquake_details_google_map_fab_3_linear_layout);
        mMainFab = findViewById(R.id.activity_earthquake_details_main_fab);
        mFabBackgroundLayout = findViewById(R.id.activity_earthquake_details_google_map_fab_background);

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

        findViewById(R.id.activity_earthquake_details_google_map_fab_1).setOnClickListener(v -> {
            mMapType = GoogleMap.MAP_TYPE_NORMAL;
            if (mGoogleMap!=null && mGoogleMap.getMapType() != mMapType) {
                mGoogleMap.setMapType(mMapType);
            }
        });

        findViewById(R.id.activity_earthquake_details_google_map_fab_2).setOnClickListener(v -> {
            mMapType = GoogleMap.MAP_TYPE_HYBRID;
            if (mGoogleMap!=null && mGoogleMap.getMapType() != mMapType) {
                mGoogleMap.setMapType(mMapType);
            }
        });

        findViewById(R.id.activity_earthquake_details_google_map_fab_3).setOnClickListener(v -> {
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
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void showUsgsMap() {
        mGoogleMapLinearLayout.setVisibility(View.GONE);
        mMapTypeRadioGroup.setVisibility(View.VISIBLE);
        if (!QueryUtils.internetConnection(this)) {
            mUsgsMapWebView.setVisibility(View.GONE);
            mUsgsMapNoInternetTextView.setVisibility(View.VISIBLE);
        } else {
            mUsgsMapNoInternetTextView.setVisibility(View.GONE);
            mUsgsMapWebView.setVisibility(View.VISIBLE);
            if (!mUsgsMapLoaded) {
                mUsgsMapWebView.getSettings().setJavaScriptEnabled(true);
                mUsgsMapWebView.getSettings().setDomStorageEnabled(true);
                mUsgsMapWebView.setWebChromeClient(new WebChromeClient());
                mUsgsMapWebView.loadUrl(mEarthquake.getUrl() + "/map");
                mCustomScrollView.addInterceptScrollView(mUsgsMapWebView);
                mUsgsMapLoaded = true;
            }
        }
    }


    private void showGoogleMap() {
        mUsgsMapWebView.setVisibility(View.GONE);
        mUsgsMapNoInternetTextView.setVisibility(View.GONE);
        mGoogleMapLinearLayout.setVisibility(View.VISIBLE);
        if (!mGoogleMapLoaded) {
            SupportMapFragment earthquakeDetailsMapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.activity_earthquake_details_map);
            if (earthquakeDetailsMapFragment != null) {
                earthquakeDetailsMapFragment.getMapAsync(this);
                // Add the map fragment view to a list of views that intercept touch events on
                // CustomScrollView for the user to be able to interact with the map inside the ScrollView
                mCustomScrollView.addInterceptScrollView(earthquakeDetailsMapFragment.getView());
            }
            mGoogleMapLoaded = true;
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;

        mMapTypeRadioGroup.setVisibility(View.VISIBLE);

        DecimalFormat formatter = new DecimalFormat("0.0");

        LatLng earthquakePosition = new LatLng(mEarthquake.getLatitude(), mEarthquake.getLongitude());

        String magnitudeToDisplay = formatter.format(mEarthquake.getMagnitude());
        magnitudeToDisplay = magnitudeToDisplay.replace(',', '.');
        Double roundedMagnitude = Double.valueOf(magnitudeToDisplay);

        MapsUtils.MarkerAttributes markerAttributes = MapsUtils.getMarkerAttributes(roundedMagnitude);

        googleMap.addMarker(new MarkerOptions()
                .position(earthquakePosition)
                .title(MapsUtils.constructEarthquakeTitleForMarker(mEarthquake, magnitudeToDisplay))
                .snippet(MapsUtils.constructEarthquakeSnippetForMarker(mEarthquake.getTimeInMilliseconds()))
                .icon(BitmapDescriptorFactory.fromResource(markerAttributes.getMarkerImageResourceId()))
                .anchor(0.5f, 0.5f)
                .alpha(markerAttributes.getAlphaValue())
                .zIndex(markerAttributes.getZIndex()));

        googleMap.setMapType(mMapType);

        if (mGoogleMapRadioButtonClicked) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(earthquakePosition, 6.7f));
            mGoogleMapRadioButtonClicked = false;
        } else if (!mRotation) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(earthquakePosition, 6.7f));
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Set this flag to true to prevent reloading USGS web view onTransitionEnd callback
            mOnBackPressed = true;
            // Fade out view
            mUsgsMapWebView.animate().alpha(0.0f);
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(MAP_TYPE_KEY, mMapType);
    }
}

