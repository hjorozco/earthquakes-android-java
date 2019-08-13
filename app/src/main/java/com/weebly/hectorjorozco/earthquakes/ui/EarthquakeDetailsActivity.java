package com.weebly.hectorjorozco.earthquakes.ui;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
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
import com.weebly.hectorjorozco.earthquakes.utils.QueryUtils;

import java.text.DecimalFormat;

import static com.weebly.hectorjorozco.earthquakes.ui.EarthquakesMapActivity.constructEarthquakeSnippet;
import static com.weebly.hectorjorozco.earthquakes.ui.EarthquakesMapActivity.constructEarthquakeTitle;
import static com.weebly.hectorjorozco.earthquakes.ui.EarthquakesMapActivity.getMarkerAttributes;


public class EarthquakeDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String EXTRA_EARTHQUAKE = "EXTRA_EARTHQUAKE";

    private Earthquake mEarthquake;
    private int mTextColor;

    private WebView mUsgsMapWebView;
    private FrameLayout mGoogleMapLinearLayout;
    private CustomScrollView mCustomScrollView;
    private TextView mUsgsMapNoInternetTextView;
    private FloatingActionButton mMainFab;
    private LinearLayout mLayoutFab1, mLayoutFab2, mLayoutFab3;
    private View fabBackgroundLayout;
    private boolean mUsgsMapLoaded = false;
    private boolean mGoogleMapLoaded = false;
    private boolean mGoogleMapRadioButtonClicked = false;
    private boolean mOnBackPressed = false;
    private boolean mRotation = false;
    private GoogleMap mGoogleMap;
    boolean isFabOpen = false;


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
        boolean usgsMapType = sharedPreferences.getBoolean(getString(
                R.string.earthquake_details_map_type_shared_preference_key), true);

        RadioGroup mapTypeRadioGroup = findViewById(R.id.activity_earthquake_details_map_type_radio_group);
        if (usgsMapType) {
            mapTypeRadioGroup.check(R.id.activity_earthquake_details_usgs_map_type_radio_button);
            showUsgsMap();
        } else {
            mapTypeRadioGroup.check(R.id.activity_earthquake_details_google_map_type_radio_button);
            showGoogleMap();
        }

        mUsgsMapNoInternetTextView.setOnClickListener(v -> showUsgsMap());

        mapTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            boolean usgsMapTypeValueToSave;
            if (checkedId == R.id.activity_earthquake_details_usgs_map_type_radio_button) {
                showUsgsMap();
                usgsMapTypeValueToSave = true;
            } else {
                showGoogleMap();
                usgsMapTypeValueToSave = false;
                mGoogleMapRadioButtonClicked = true;
            }
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(getString(R.string.earthquake_details_map_type_shared_preference_key),
                    usgsMapTypeValueToSave);
            editor.apply();
        });

        // Sets up the Google Map FABs
        mLayoutFab1 = findViewById(R.id.activity_earthquake_details_google_map_fab_1_linear_layout);
        mLayoutFab2 = findViewById(R.id.activity_earthquake_details_google_map_fab_2_linear_layout);
        mLayoutFab3 = findViewById(R.id.activity_earthquake_details_google_map_fab_3_linear_layout);
        mMainFab = findViewById(R.id.fab);
        FloatingActionButton fab1 = findViewById(R.id.activity_earthquake_details_google_map_fab_1);
        FloatingActionButton fab2 = findViewById(R.id.activity_earthquake_details_google_map_fab_2);
        FloatingActionButton fab3 = findViewById(R.id.activity_earthquake_details_google_map_fab_3);
        fabBackgroundLayout = findViewById(R.id.activity_earthquake_details_google_map_fab_background);

        mMainFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFabOpen) {
                    showFabMenu();
                } else {
                    hideFabMenu();
                }
            }
        });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGoogleMap.getMapType() != GoogleMap.MAP_TYPE_NORMAL) {
                    mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGoogleMap.getMapType() != GoogleMap.MAP_TYPE_SATELLITE) {
                    mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                }
            }
        });

        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGoogleMap.getMapType() != GoogleMap.MAP_TYPE_TERRAIN) {
                    mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                }
            }
        });

        fabBackgroundLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideFabMenu();
            }
        });
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void showUsgsMap() {
        mGoogleMapLinearLayout.setVisibility(View.GONE);
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


    // TODO Save map type and restore on rotation
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;

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

        if (mGoogleMapRadioButtonClicked) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(earthquakePosition, 6.7f));
            mGoogleMapRadioButtonClicked = false;
        } else if (!mRotation) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(earthquakePosition, 6.7f));
        }

    }


    private void showFabMenu() {
        isFabOpen = true;
        mLayoutFab1.setVisibility(View.VISIBLE);
        mLayoutFab2.setVisibility(View.VISIBLE);
        mLayoutFab3.setVisibility(View.VISIBLE);
        fabBackgroundLayout.setVisibility(View.VISIBLE);
        mMainFab.animate().rotationBy(180);

        mLayoutFab1.animate().translationY(getResources().getDimension(R.dimen.standard_52));
        mLayoutFab2.animate().translationY(getResources().getDimension(R.dimen.standard_96));
        mLayoutFab3.animate().translationY(getResources().getDimension(R.dimen.standard_140))
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        mMainFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_close_brown_24dp));
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                    }
                });
    }


    private void hideFabMenu() {
        isFabOpen = false;
        fabBackgroundLayout.setVisibility(View.GONE);
        mMainFab.animate().rotation(0);
        mMainFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_layers_brown_24dp));
        mLayoutFab1.animate().translationY(0);
        mLayoutFab2.animate().translationY(0);
        mLayoutFab3.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!isFabOpen) {
                    mLayoutFab1.setVisibility(View.GONE);
                    mLayoutFab2.setVisibility(View.GONE);
                    mLayoutFab3.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
    }

}

