package com.weebly.hectorjorozco.earthquakes.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ShareCompat;
import androidx.core.view.MenuCompat;

import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.weebly.hectorjorozco.earthquakes.R;
import com.weebly.hectorjorozco.earthquakes.database.AppDatabase;
import com.weebly.hectorjorozco.earthquakes.executors.AppExecutors;
import com.weebly.hectorjorozco.earthquakes.models.Earthquake;
import com.weebly.hectorjorozco.earthquakes.ui.dialogfragments.MessageDialogFragment;
import com.weebly.hectorjorozco.earthquakes.utils.MapsUtils;
import com.weebly.hectorjorozco.earthquakes.utils.QueryUtils;
import com.weebly.hectorjorozco.earthquakes.utils.WebViewUtils;
import com.weebly.hectorjorozco.earthquakes.utils.WordsUtils;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static android.view.View.GONE;


public class EarthquakeDetailsActivity extends AppCompatActivity implements OnMapReadyCallback,
        MessageDialogFragment.MessageDialogFragmentListener {

    public static final String EXTRA_EARTHQUAKE = "EXTRA_EARTHQUAKE_KEY";
    public static final String EXTRA_IS_FAVORITES_ACTIVITY_CALLING = "EXTRA_IS_FAVORITES_ACTIVITY_CALLING";
    public static final String EXTRA_BUNDLE_KEY = "EXTRA_BUNDLE_KEY";
    private static final String IS_FAB_MENU_OPEN_VALUE_KEY = "IS_FAB_MENU_OPEN_VALUE_KEY";
    private static final String IS_FAVORITE_VALUE_KEY = "IS_FAVORITE_VALUE_KEY";

    private Earthquake mEarthquake;

    private WebView mUsgsMapWebView;
    private FrameLayout mGoogleMapFrameLayout, mUsgsMapFrameLayout;
    private TextView mUsgsMapNoInternetTextView;
    private RadioGroup mMapTypeRadioGroup;
    private GoogleMap mGoogleMap;
    private SharedPreferences mSharedPreferences;
    private LatLng mEarthquakePosition;
    private MenuItem mFavoritesMenuItem;
    private AppDatabase mAppDatabase;
    private int mGoogleMapType;
    private boolean mIsGoogleMap;
    private boolean mUsgsMapLoaded = false;
    private boolean mGoogleMapLoaded = false;
    private boolean mGoogleMapRadioButtonClicked = false;
    private boolean mOnBackPressed = false;
    private boolean mRotation = false;
    private boolean mIsFabMenuOpen = false;
    private boolean mIsFavorite = false;
    private boolean mIsFavoritesActivityCalling;
    private boolean mIsReportedIntensityShown;
    private boolean mIsEstimatedIntensityShown;
    private boolean mIsAlertTextShown;
    private boolean mIsCoordinatesTextShown;
    private boolean mIsDepthTextShown;
    private String mReportedIntensityRomanNumeral;
    private String mEstimatedIntensityRomanNumeral;
    private String mAlertText;
    private String mCoordinatesText;
    private String mDepthText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earthquake_details);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Bundle bundle = getIntent().getBundleExtra(EXTRA_BUNDLE_KEY);
        if (bundle != null) {
            if (bundle.containsKey(EXTRA_EARTHQUAKE)) {
                mEarthquake = bundle.getParcelable(EXTRA_EARTHQUAKE);
            }
            if (bundle.containsKey(EXTRA_IS_FAVORITES_ACTIVITY_CALLING)) {
                mIsFavoritesActivityCalling = bundle.getBoolean(EXTRA_IS_FAVORITES_ACTIVITY_CALLING);
            }
        }

        mAppDatabase = AppDatabase.getInstance(this);

        if (savedInstanceState != null) {
            mRotation = true;
            mIsFabMenuOpen = savedInstanceState.getBoolean(IS_FAB_MENU_OPEN_VALUE_KEY);
            mIsFavorite = savedInstanceState.getBoolean(IS_FAVORITE_VALUE_KEY);
        } else {
            mIsFavorite = checkIfEarthquakeIsFavorite();
        }

        // Gets the values saved on Shared Preferences to set them on the map
        mSharedPreferences = getSharedPreferences(
                getString(R.string.app_shared_preferences_name), 0);
        mGoogleMapType = mSharedPreferences.getInt(getString(
                R.string.activity_earthquake_details_google_map_type_shared_preference_key), GoogleMap.MAP_TYPE_NORMAL);
        mIsGoogleMap = mSharedPreferences.getBoolean(getString(
                R.string.activity_earthquake_details_map_type_shared_preference_key), true);


        // Sets up the views that will be animated on entry and exit for Android versions 21 or up
        TextView magnitudeTextView = findViewById(R.id.activity_earthquake_details_magnitude_text_view);
        TextView locationOffsetTextView = findViewById(R.id.activity_earthquake_details_location_offset_text_view);
        TextView locationPrimaryTextView = findViewById(R.id.activity_earthquake_details_location_primary_text_view);
        TextView dateAndTimeTextView = findViewById(R.id.activity_earthquake_details_date_and_time_text_view);
        LinearLayout magnitudeTextViewLinearLayout =
                findViewById(R.id.activity_earthquake_details_magnitude_text_view_linear_layout);

        magnitudeTextViewLinearLayout.setOnClickListener(v -> showMagnitudeMessage());

        QueryUtils.setupEarthquakeInformationOnViews(
                this, mEarthquake, magnitudeTextView, locationOffsetTextView,
                locationPrimaryTextView, dateAndTimeTextView, null, null);

        // If Android version is 21 or up set a transition for the elements in the top of the activity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            magnitudeTextView.setTransitionName(
                    getString(R.string.activity_earthquake_details_magnitude_text_view_transition));
            locationOffsetTextView.setTransitionName(
                    getString(R.string.activity_earthquake_details_location_offset_text_view_transition));
            locationPrimaryTextView.setTransitionName(
                    getString(R.string.activity_earthquake_details_location_primary_text_view_transition));
            dateAndTimeTextView.setTransitionName(
                    getString(R.string.activity_earthquake_details_date_text_view_transition));

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
            // If Android 19
            magnitudeTextViewLinearLayout.setBackground(getResources().
                    getDrawable(R.drawable.touch_selector));
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

        // Used to assign vector drawables to the Google Map layers fab in API 19
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        setupDetailsOnTopOfMap();

        setupMapsViews();

        setupGoogleMapFabs();

    }


    @SuppressLint("ClickableViewAccessibility")
    private void setupDetailsOnTopOfMap() {

        findViewById(R.id.activity_earthquake_details_on_top_of_map_linear_layout).setVisibility(View.VISIBLE);

        mMapTypeRadioGroup = findViewById(R.id.activity_earthquake_details_map_type_radio_group);
        mMapTypeRadioGroup.setVisibility(View.VISIBLE);

        // Intensity views
        TextView intensityLabelTextView =
                findViewById(R.id.activity_earthquake_details_intensity_label_text_view);
        LinearLayout intensityValuesLinearLayout =
                findViewById(R.id.activity_earthquake_details_intensity_values_linear_layout);
        FlexboxLayout reportedIntensityFlexboxLayout =
                findViewById(R.id.activity_earthquake_details_reported_intensity_flex_box_layout);
        TextView reportedValueTextView =
                findViewById(R.id.activity_earthquake_details_reported_value_text_view);
        FlexboxLayout estimatedIntensityFlexboxLayout =
                findViewById(R.id.activity_earthquake_details_estimated_intensity_flex_box_layout);
        TextView estimatedValueTextView =
                findViewById(R.id.activity_earthquake_details_estimated_value_text_view);

        int estimatedIntensity, reportedIntensity;
        String[] romanNumerals;

        estimatedIntensity = (int) Math.round(mEarthquake.getMmi());
        reportedIntensity = (int) Math.round(mEarthquake.getCdi());

        if (estimatedIntensity < 1 && reportedIntensity < 1) {
            intensityLabelTextView.setVisibility(GONE);
            intensityValuesLinearLayout.setVisibility(GONE);
        } else {
            romanNumerals =
                    getResources().getStringArray(R.array.activity_earthquake_details_roman_numerals);
            if (estimatedIntensity < 1) {
                estimatedIntensityFlexboxLayout.setVisibility(GONE);
                mIsEstimatedIntensityShown = false;
            } else {
                mEstimatedIntensityRomanNumeral = romanNumerals[estimatedIntensity - 1];
                String estimatedType = getString(R.string.activity_earthquake_details_estimated_intensity_type);
                estimatedValueTextView.setText(mEstimatedIntensityRomanNumeral);
                estimatedValueTextView.setTextColor(getIntensityColor(estimatedIntensity));
                estimatedIntensityFlexboxLayout.
                        setOnClickListener(v -> showIntensityMessage(estimatedIntensity, estimatedType,
                                mEstimatedIntensityRomanNumeral));
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    estimatedIntensityFlexboxLayout.setBackground(getResources().
                            getDrawable(R.drawable.touch_selector));
                }
                mIsEstimatedIntensityShown = true;
            }
            if (reportedIntensity < 1) {
                reportedIntensityFlexboxLayout.setVisibility(GONE);
                mIsReportedIntensityShown = false;
            } else {
                mReportedIntensityRomanNumeral = romanNumerals[reportedIntensity - 1];
                String reportedType = getString(R.string.activity_earthquake_details_reported_intensity_type);
                reportedValueTextView.setText(mReportedIntensityRomanNumeral);
                reportedValueTextView.setTextColor(getIntensityColor(reportedIntensity));
                reportedIntensityFlexboxLayout.
                        setOnClickListener(v -> showIntensityMessage(reportedIntensity, reportedType,
                                mReportedIntensityRomanNumeral));
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    reportedIntensityFlexboxLayout.setBackground(getResources().
                            getDrawable(R.drawable.touch_selector));
                }
                mIsReportedIntensityShown = true;
            }
        }

        // Alert Views
        FlexboxLayout alertFlexboxLayout = findViewById(R.id.activity_earthquake_details_alert_flex_box_layout);
        TextView alertValueTextView =
                findViewById(R.id.activity_earthquake_details_alert_value_text_view);

        String alertText;

        alertText = mEarthquake.getAlert();
        if (alertText == null) {
            alertFlexboxLayout.setVisibility(GONE);
            mIsAlertTextShown = false;
        } else {
            int alertValueTextColor = 0;
            mAlertText = "";
            int alertType = 0;
            switch (alertText) {
                case "green":
                    mAlertText = getString(R.string.activity_earthquake_details_green_text);
                    alertValueTextColor = getResources().getColor(R.color.colorAlertGreen);
                    alertType = 0;
                    break;
                case "yellow":
                    mAlertText = getString(R.string.activity_earthquake_details_yellow_text);
                    alertValueTextColor = getResources().getColor(R.color.colorAlertYellow);
                    alertType = 1;
                    break;
                case "orange":
                    mAlertText = getString(R.string.activity_earthquake_details_orange_text);
                    alertValueTextColor = getResources().getColor(R.color.colorAlertOrange);
                    alertType = 2;
                    break;
                case "red":
                    mAlertText = getString(R.string.activity_earthquake_details_red_text);
                    alertValueTextColor = getResources().getColor(R.color.colorAlertRed);
                    alertType = 3;
                    break;
            }
            alertValueTextView.setText(mAlertText);
            alertValueTextView.setTextColor(alertValueTextColor);

            int finalAlertType = alertType;
            String finalAlertValueText = mAlertText.toLowerCase();
            alertFlexboxLayout.setOnClickListener(v -> showAlertMessage(finalAlertType, finalAlertValueText));

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                alertFlexboxLayout.setBackground(getResources().
                        getDrawable(R.drawable.touch_selector));
            }

            mIsAlertTextShown = true;
        }

        // Tsunami views
        TextView tsunamiTextView =
                findViewById(R.id.activity_earthquake_details_tsunami_text_view);

        if (mEarthquake.getTsunami() == 0) {
            tsunamiTextView.setVisibility(GONE);
        } else {
            tsunamiTextView.setVisibility(View.VISIBLE);
            tsunamiTextView.setOnClickListener(v -> showPossibleTsunamiMessage());
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                tsunamiTextView.setBackground(getResources().
                        getDrawable(R.drawable.touch_selector));
            }
        }

        // Felt views
        TextView feltReportsValueTextView =
                findViewById(R.id.activity_earthquake_details_felt_reports_value_text_view);

        feltReportsValueTextView.setText(String.valueOf(mEarthquake.getFelt()));

        int colorPrimary = getResources().getColor(R.color.colorPrimary);
        int colorLight = getResources().getColor(R.color.medium_light_brown);

        int[][] states = new int[][]{
                new int[]{android.R.attr.state_enabled}, // enabled
                new int[]{-android.R.attr.state_enabled}, // disabled
                new int[]{-android.R.attr.state_checked}, // unchecked
                new int[]{android.R.attr.state_pressed}  // pressed
        };
        int[] buttonBackGroundColors = new int[]{
                colorLight,
                colorLight,
                colorLight,
                colorLight,
        };
        int[] buttonColors = new int[]{
                colorPrimary,
                colorPrimary,
                colorPrimary,
                colorPrimary,
        };
        ColorStateList buttonBackGroundColorStateList = new ColorStateList(states, buttonBackGroundColors);
        ColorStateList buttonColorStateList = new ColorStateList(states, buttonColors);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            MaterialButton feltReportsButton = findViewById(R.id.activity_earthquake_details_felt_reports_button);
            feltReportsButton.setRippleColor(buttonBackGroundColorStateList);
            feltReportsButton.setStrokeColor(buttonColorStateList);
            feltReportsButton.setOnClickListener(v -> showReportEarthquakeActivity());
        } else {
            AppCompatButton feltReportsButton = findViewById(R.id.activity_earthquake_details_felt_reports_button);
            feltReportsButton.setOnClickListener(v -> showReportEarthquakeActivity());
        }

        // Epicenter location views
        TextView coordinatesValueTextView =
                findViewById(R.id.activity_earthquake_details_coordinates_value_text_view);

        TextView depthValueTextView =
                findViewById(R.id.activity_earthquake_details_depth_value_text_view);

        String latitudeLetter, longitudeLetter;

        double latitude, longitude, depth;

        latitude = mEarthquake.getLatitude();
        longitude = mEarthquake.getLongitude();
        depth = mEarthquake.getDepth();

        if (latitude == QueryUtils.LAT_LONG_NULL_VALUE && longitude == QueryUtils.LAT_LONG_NULL_VALUE) {
            coordinatesValueTextView.setVisibility(GONE);
            mIsCoordinatesTextShown = false;
        } else {
            coordinatesValueTextView.setVisibility(View.VISIBLE);
            if (latitude < 0) {
                latitude = -latitude;
                latitudeLetter = getString(R.string.activity_earthquake_details_south_latitude_letter);
            } else {
                latitudeLetter = getString(R.string.activity_earthquake_details_north_latitude_letter);
            }
            if (longitude < 0) {
                longitude = -longitude;
                longitudeLetter = getString(R.string.activity_earthquake_details_west_longitude_letter);
            } else {
                longitudeLetter = getString(R.string.activity_earthquake_details_east_longitude_letter);
            }
            mCoordinatesText = getString(
                    R.string.activity_earthquake_details_coordinates_text, latitude, latitudeLetter,
                    longitude, longitudeLetter);
            coordinatesValueTextView.setText(mCoordinatesText);
            mIsCoordinatesTextShown = true;
        }

        if (depth == QueryUtils.DEPTH_NULL_VALUE) {
            depthValueTextView.setVisibility(GONE);
            mIsDepthTextShown = false;
        } else {
            depthValueTextView.setVisibility(View.VISIBLE);
            mDepthText = getString(R.string.activity_earthquake_details_depth_text, depth);
            depthValueTextView.setText(mDepthText);
            mIsDepthTextShown = true;
        }
    }


    private int getIntensityColor(int intensity) {
        int intensityColor = 0;
        switch (intensity) {
            case 1:
                intensityColor = getResources().getColor(R.color.colorIntensity1);
                break;
            case 2:
                intensityColor = getResources().getColor(R.color.colorIntensity2);
                break;
            case 3:
                intensityColor = getResources().getColor(R.color.colorIntensity3);
                break;
            case 4:
                intensityColor = getResources().getColor(R.color.colorIntensity4);
                break;
            case 5:
                intensityColor = getResources().getColor(R.color.colorIntensity5);
                break;
            case 6:
                intensityColor = getResources().getColor(R.color.colorIntensity6);
                break;
            case 7:
                intensityColor = getResources().getColor(R.color.colorIntensity7);
                break;
            case 8:
                intensityColor = getResources().getColor(R.color.colorIntensity8);
                break;
            case 9:
                intensityColor = getResources().getColor(R.color.colorIntensity9);
                break;
            case 10:
                intensityColor = getResources().getColor(R.color.colorIntensity10);
                break;
            case 11:
                intensityColor = getResources().getColor(R.color.colorIntensity11);
                break;
            case 12:
                intensityColor = getResources().getColor(R.color.colorIntensity12);
                break;
        }
        return intensityColor;
    }


    private void setupMapsViews() {
        mUsgsMapWebView = findViewById(R.id.activity_earthquake_details_usgs_map_web_view);
        mUsgsMapFrameLayout = findViewById(R.id.activity_earthquake_details_usgs_map_frame_layout);
        mGoogleMapFrameLayout = findViewById(R.id.activity_earthquake_details_google_map_frame_layout);
        mUsgsMapNoInternetTextView = findViewById(R.id.activity_earthquake_details_usgs_map_no_internet_text_view);

        if (mIsGoogleMap) {
            mMapTypeRadioGroup.check(R.id.activity_earthquake_details_google_map_type_radio_button);
            showGoogleMap();
        } else {
            mMapTypeRadioGroup.check(R.id.activity_earthquake_details_usgs_map_type_radio_button);
            showUsgsMap();
        }

        mUsgsMapNoInternetTextView.setOnClickListener(v -> showUsgsMap());


        mMapTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            if (checkedId == R.id.activity_earthquake_details_google_map_type_radio_button) {
                showGoogleMap();
                mIsGoogleMap = true;
                mGoogleMapRadioButtonClicked = true;
            } else {
                showUsgsMap();
                mIsGoogleMap = false;
            }
            editor.putBoolean(getString(R.string.activity_earthquake_details_map_type_shared_preference_key),
                    mIsGoogleMap);
            editor.apply();
        });
    }


    private void setupGoogleMapFabs() {
        LinearLayout layoutFabLayer1 =
                findViewById(R.id.activity_earthquake_details_google_map_fab_layer_1_linear_layout);
        LinearLayout layoutFabLayer2 =
                findViewById(R.id.activity_earthquake_details_google_map_fab_layer_2_linear_layout);
        LinearLayout layoutFabLayer3 =
                findViewById(R.id.activity_earthquake_details_google_map_fab_layer_3_linear_layout);
        FloatingActionButton layersFab =
                findViewById(R.id.activity_earthquake_details_google_map_layers_fab);
        FloatingActionButton earthquakeLocationFab =
                findViewById(R.id.activity_earthquake_details_google_map_earthquake_location_fab);

        View fabsBackgroundLayout = findViewById(R.id.activity_earthquake_details_google_map_fabs_background);

        if (mIsFabMenuOpen) {
            MapsUtils.showFabMenu(layoutFabLayer1, layoutFabLayer2, layoutFabLayer3,
                    fabsBackgroundLayout, layersFab, this);
        }

        layersFab.setOnClickListener(view -> {
            if (!mIsFabMenuOpen) {
                mIsFabMenuOpen = true;
                MapsUtils.showFabMenu(layoutFabLayer1, layoutFabLayer2, layoutFabLayer3,
                        fabsBackgroundLayout, layersFab, this);
            } else {
                mIsFabMenuOpen = false;
                MapsUtils.hideFabMenu(layoutFabLayer1, layoutFabLayer2, layoutFabLayer3,
                        fabsBackgroundLayout, layersFab, this);
            }
        });

        earthquakeLocationFab.setOnClickListener(v ->
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(mEarthquakePosition)));

        findViewById(R.id.activity_earthquake_details_google_map_fab_layer_1).setOnClickListener(v -> {
            mGoogleMapType = GoogleMap.MAP_TYPE_NORMAL;
            if (mGoogleMap != null && mGoogleMap.getMapType() != mGoogleMapType) {
                mGoogleMap.setMapType(mGoogleMapType);
                saveGoogleMapTypeOnSharedPreferences(mGoogleMapType);
            }
        });

        findViewById(R.id.activity_earthquake_details_google_map_fab_layer_2).setOnClickListener(v -> {
            mGoogleMapType = GoogleMap.MAP_TYPE_HYBRID;
            if (mGoogleMap != null && mGoogleMap.getMapType() != mGoogleMapType) {
                mGoogleMap.setMapType(mGoogleMapType);
                saveGoogleMapTypeOnSharedPreferences(mGoogleMapType);
            }
        });

        findViewById(R.id.activity_earthquake_details_google_map_fab_layer_3).setOnClickListener(v -> {
            mGoogleMapType = GoogleMap.MAP_TYPE_TERRAIN;
            if (mGoogleMap != null && mGoogleMap.getMapType() != mGoogleMapType) {
                mGoogleMap.setMapType(mGoogleMapType);
                saveGoogleMapTypeOnSharedPreferences(mGoogleMapType);
            }
        });

        fabsBackgroundLayout.setOnClickListener(view -> {
            mIsFabMenuOpen = false;
            MapsUtils.hideFabMenu(layoutFabLayer1,
                    layoutFabLayer2, layoutFabLayer3, fabsBackgroundLayout, layersFab, this);
        });
    }


    private void saveGoogleMapTypeOnSharedPreferences(int googleMapType) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(getString(R.string.activity_earthquake_details_google_map_type_shared_preference_key),
                googleMapType);
        editor.apply();
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void showUsgsMap() {
        mUsgsMapFrameLayout.setVisibility(View.VISIBLE);
        mGoogleMapFrameLayout.setVisibility(GONE);

        ProgressBar progressBar = findViewById(R.id.activity_earthquake_details_usgs_map_progress_bar);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            QueryUtils.setupProgressBarForPreLollipop(progressBar, this);
        }

        if (QueryUtils.internetConnection(this)) {
            progressBar.setVisibility(View.VISIBLE);
            mUsgsMapNoInternetTextView.setVisibility(GONE);
            mUsgsMapWebView.setVisibility(GONE);
            if (!mUsgsMapLoaded) {
                mUsgsMapWebView.getSettings().setJavaScriptEnabled(true);
                mUsgsMapWebView.getSettings().setDomStorageEnabled(true);
                mUsgsMapWebView.setWebViewClient(WebViewUtils.setupWebViewClient(
                        getString(R.string.activity_earthquake_details_usgs_map_loading_error_message),
                        mUsgsMapNoInternetTextView, mUsgsMapWebView, progressBar, true));
                mUsgsMapWebView.loadUrl(mEarthquake.getUrl() + "/map");
                mUsgsMapLoaded = true;
            } else {
                mUsgsMapWebView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(GONE);
            }
        } else {
            mUsgsMapNoInternetTextView.setVisibility(View.VISIBLE);
            mUsgsMapNoInternetTextView.setText(getString(R.string.activity_earthquake_details_usgs_map_no_internet_message));
            mUsgsMapWebView.setVisibility(GONE);
            progressBar.setVisibility(GONE);

        }
    }


    private void showGoogleMap() {
        mUsgsMapWebView.setVisibility(GONE);
        mUsgsMapNoInternetTextView.setVisibility(GONE);
        mUsgsMapFrameLayout.setVisibility(GONE);
        mGoogleMapFrameLayout.setVisibility(View.VISIBLE);
        if (!mGoogleMapLoaded) {
            SupportMapFragment earthquakeDetailsMapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.activity_earthquake_details_google_map_fragment);
            if (earthquakeDetailsMapFragment != null) {
                earthquakeDetailsMapFragment.getMapAsync(this);
            }
            mGoogleMapLoaded = true;
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;

        DecimalFormat formatter = new DecimalFormat("0.0");

        mEarthquakePosition = new LatLng(mEarthquake.getLatitude(), mEarthquake.getLongitude());

        String magnitudeToDisplay = formatter.format(mEarthquake.getMagnitude());
        magnitudeToDisplay = magnitudeToDisplay.replace(',', '.');
        double roundedMagnitude = Double.parseDouble(magnitudeToDisplay);

        MapsUtils.MarkerAttributes markerAttributes = MapsUtils.getMarkerAttributes(roundedMagnitude);

        googleMap.addMarker(new MarkerOptions()
                .position(mEarthquakePosition)
                .title(MapsUtils.constructEarthquakeTitleForMarker(mEarthquake, magnitudeToDisplay, this))
                .snippet(MapsUtils.constructEarthquakeSnippetForMarker(mEarthquake.getTimeInMilliseconds()))
                .icon(BitmapDescriptorFactory.fromResource(markerAttributes.getMarkerImageResourceId()))
                .anchor(0.5f, 0.5f)
                .alpha(markerAttributes.getAlphaValue())
                .zIndex(markerAttributes.getZIndex()));

        googleMap.setMapType(mGoogleMapType);

        googleMap.getUiSettings().setZoomControlsEnabled(true);

        if (mGoogleMapRadioButtonClicked) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mEarthquakePosition, 6.7f));
            mGoogleMapRadioButtonClicked = false;
        } else if (!mRotation) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mEarthquakePosition, 6.7f));
        }
    }


    private void showMagnitudeMessage() {

        String magnitudeText = QueryUtils.getMagnitudeText(mEarthquake.getMagnitude());
        int magnitude = (int) Double.parseDouble(magnitudeText);

        String[] magnitudeMessages = getResources().getStringArray(
                R.array.activity_earthquake_details_magnitude_information_array);
        MessageDialogFragment messageDialogFragment =
                MessageDialogFragment.newInstance(
                        Html.fromHtml(magnitudeMessages[magnitude]),
                        getString(R.string.activity_earthquake_details_magnitude_information_dialog_fragment_title, magnitudeText),
                        MessageDialogFragment.MESSAGE_DIALOG_FRAGMENT_CALLER_OTHER);

        messageDialogFragment.show(getSupportFragmentManager(),
                getString(R.string.activity_earthquake_details_magnitude_information_dialog_fragment_tag));
    }


    private void showIntensityMessage(int intensity, String intensityType, String romanNumeral) {
        String[] intensityMessages = getResources().
                getStringArray(R.array.activity_earthquake_details_intensity_information_array);

        MessageDialogFragment messageDialogFragment =
                MessageDialogFragment.newInstance(
                        Html.fromHtml(intensityMessages[intensity - 1]),
                        getString(R.string.activity_earthquake_details_intensity_information_dialog_fragment_title,
                                intensityType, romanNumeral),
                        MessageDialogFragment.MESSAGE_DIALOG_FRAGMENT_CALLER_OTHER);

        messageDialogFragment.show(getSupportFragmentManager(),
                getString(R.string.activity_earthquake_details_intensity_information_dialog_fragment_tag));
    }


    private void showAlertMessage(int alertType, String alertTypeText) {
        String[] alertMessages = getResources().
                getStringArray(R.array.activity_earthquake_details_alert_information_array);

        MessageDialogFragment messageDialogFragment =
                MessageDialogFragment.newInstance(
                        Html.fromHtml(alertMessages[alertType]),
                        getString(R.string.activity_earthquake_details_alert_information_dialog_fragment_title,
                                alertTypeText), MessageDialogFragment.MESSAGE_DIALOG_FRAGMENT_CALLER_OTHER);

        messageDialogFragment.show(getSupportFragmentManager(),
                getString(R.string.activity_earthquake_details_alert_information_dialog_fragment_tag));
    }


    private void showPossibleTsunamiMessage() {
        MessageDialogFragment messageDialogFragment =
                MessageDialogFragment.newInstance(
                        Html.fromHtml(getString(R.string.activity_earthquake_details_possible_tsunami_dialog_fragment_message)),
                        getString(R.string.activity_earthquake_details_possible_tsumami_dialog_fragment_title),
                        MessageDialogFragment.MESSAGE_DIALOG_FRAGMENT_CALLER_OTHER);

        messageDialogFragment.show(getSupportFragmentManager(),
                getString(R.string.activity_earthquake_details_possible_tsunami_dialog_fragment_tag));
    }


    private void showEarthquakeDetailsHelpMessage() {
        MessageDialogFragment messageDialogFragment =
                MessageDialogFragment.newInstance(
                        Html.fromHtml(getString(
                                R.string.activity_earthquake_details_help_dialog_fragment_message)),
                        getString(R.string.activity_earthquake_details_help_dialog_fragment_title),
                        MessageDialogFragment.MESSAGE_DIALOG_FRAGMENT_CALLER_OTHER);

        messageDialogFragment.show(getSupportFragmentManager(),
                getString(R.string.activity_earthquake_details_help_dialog_fragment_tag));
    }


    private void showReportEarthquakeInSpanishHelpMessage() {
        MessageDialogFragment messageDialogFragment =
                MessageDialogFragment.newInstance(
                        Html.fromHtml(getString(
                                R.string.activity_earthquake_details_report_spanish_help_dialog_fragment_message)),
                        getString(R.string.activity_earthquake_details_report_spanish_help_dialog_fragment_title),
                        MessageDialogFragment.MESSAGE_DIALOG_FRAGMENT_CALLER_REPORT_BUTTON);

        messageDialogFragment.show(getSupportFragmentManager(),
                getString(R.string.activity_earthquake_details_report_spanish_help_dialog_fragment_tag));
    }


    private boolean checkIfEarthquakeIsFavorite() {

        Future<Boolean> isEarthquakeFavoriteFuture = AppExecutors.getInstance().diskIO().submit(() -> {
            if (mAppDatabase != null) {
                return mAppDatabase.earthquakeDao().findFavoriteEarthquakeWithId(mEarthquake.getId()) != null;
            } else {
                return false;
            }
        });

        boolean isEarthquakeFavorite = false;
        try {
            isEarthquakeFavorite = isEarthquakeFavoriteFuture.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        if (mFavoritesMenuItem != null) {
            setupFavoritesMenuItem(mIsFavorite);
        }

        return isEarthquakeFavorite;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_earthquake_details, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);

        mFavoritesMenuItem = menu.getItem(0);

        setupFavoritesMenuItem(mIsFavorite);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                dismissEarthquakeDetailsActivity();
                break;
            case R.id.menu_activity_earthquake_details_action_favorites:
                deleteOrInsertOnFavoritesTableDb();
                break;
            case R.id.menu_activity_earthquake_details_action_share:
                shareEarthquakeDetails();
                break;
            case R.id.menu_activity_earthquake_details_action_help:
                showEarthquakeDetailsHelpMessage();
                break;
            case R.id.menu_activity_earthquake_details_action_web_page:
                showEarthquakeWebSiteActivity();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void setupFavoritesMenuItem(boolean isFavorite) {
        if (isFavorite) {
            mFavoritesMenuItem.setIcon(R.drawable.ic_star_yellow_24dp);
            mFavoritesMenuItem.setTitle(R.string.menu_activity_earthquake_details_action_remove_from_favorites_title);
        } else {
            mFavoritesMenuItem.setIcon(R.drawable.ic_star_border_white_24dp);
            mFavoritesMenuItem.setTitle(R.string.menu_activity_earthquake_details_action_add_to_favorites_title);
        }
    }


    private void deleteOrInsertOnFavoritesTableDb() {
        setupFavoritesMenuItem(!mIsFavorite);
        if (mIsFavorite) {
            // Remove from "favorite_earthquakes" db table
            AppExecutors.getInstance().diskIO().execute(() ->
                    mAppDatabase.earthquakeDao().deleteFavoriteEarthquake(mEarthquake));
            showSnackBar(getString(R.string.activity_earthquake_details_earthquake_removed_from_favorites_snack_bar_text));
        } else {
            // Insert to "favorite_earthquakes" db table
            AppExecutors.getInstance().diskIO().execute(() ->
                    mAppDatabase.earthquakeDao().insertFavoriteEarthquake(mEarthquake));
            showSnackBar(getString(R.string.activity_earthquake_details_earthquake_added_to_favorites_snack_bar_text));
        }
        mIsFavorite = !mIsFavorite;
    }


    private void showSnackBar(String text) {
        Snackbar.make(findViewById(android.R.id.content), text, Snackbar.LENGTH_LONG).show();
    }


    private void shareEarthquakeDetails() {

        // Location offset can be empty
        String locationOffset = mEarthquake.getLocationOffset();
        String spaceForLocation = " ";
        if (locationOffset.equals(getString(R.string.near_the))) {
            locationOffset = "";
            spaceForLocation = "";
        }

        // Location primary can be empty
        String locationPrimary = mEarthquake.getLocationPrimary();
        if (locationPrimary.isEmpty()) {
            locationPrimary = getString(R.string.activity_main_no_earthquake_location_text);
        }

        String text = getString(R.string.activity_earthquake_details_share_option_magnitude_text) + ' '
                + QueryUtils.getMagnitudeText(mEarthquake.getMagnitude()) + '\n'
                + getString(R.string.activity_earthquake_details_share_option_location_text) + ' '
                + locationOffset + spaceForLocation + locationPrimary + '\n'
                + getString(R.string.activity_earthquake_details_share_option_date_time_text) + ' '
                + WordsUtils.displayedDateFormatter().format(new Date(mEarthquake.getTimeInMilliseconds())) + "\n\n";

        boolean isNextSectionPopulated = false;

        if (mIsReportedIntensityShown) {
            text = text + getString(R.string.activity_earthquake_details_share_option_reported_intensity_text)
                    + " " + mReportedIntensityRomanNumeral;
            isNextSectionPopulated = true;
        }

        String nextLine;

        if (mIsEstimatedIntensityShown) {
            if (mIsReportedIntensityShown) {
                nextLine = "\n";
            } else {
                nextLine = "";
            }
            text = text + nextLine + getString(R.string.activity_earthquake_details_share_option_estimated_intensity_text)
                    + " " + mEstimatedIntensityRomanNumeral;
            isNextSectionPopulated = true;
        }

        if (mIsAlertTextShown) {
            if (mIsReportedIntensityShown || mIsEstimatedIntensityShown) {
                nextLine = "\n";
            } else {
                nextLine = "";
            }
            text = text + nextLine + getString(R.string.activity_earthquake_details_alert_text) + ": " + mAlertText;
            isNextSectionPopulated = true;
        }

        if (mEarthquake.getTsunami() != 0) {
            if (mIsReportedIntensityShown || mIsEstimatedIntensityShown || mIsAlertTextShown) {
                nextLine = "\n";
            } else {
                nextLine = "";
            }
            text = text + nextLine + getString(R.string.activity_earthquake_details_tsunami_text);
            isNextSectionPopulated = true;
        }

        if (isNextSectionPopulated) {
            text = text + "\n\n";
        }

        text = text + getString(R.string.activity_earthquake_details_felt_reports_text) + ": "
                + mEarthquake.getFelt() + '\n'
                + getString(R.string.activity_earthquake_details_share_option_go_to_website_to_report_text) + '\n'
                + mEarthquake.getUrl() + "/tellus" + "\n\n";


        String coordinatesText;
        if (mIsCoordinatesTextShown) {
            coordinatesText = mCoordinatesText + "  ";
        } else {
            coordinatesText = "";
        }

        String depthText;
        if (mIsDepthTextShown) {
            depthText = mDepthText;
        } else {
            depthText = "";
        }

        text = text + getString(R.string.activity_earthquake_details_epicenter_text) + ": "
                + coordinatesText + depthText + '\n' +
                getString(R.string.activity_earthquake_details_share_option_google_map_text) + '\n' +
                getString(R.string.activity_earthquake_details_share_option_google_map_link_text_1) +
                mEarthquake.getLatitude() + ',' + mEarthquake.getLongitude() +
                getString(R.string.activity_earthquake_details_share_option_google_map_link_text_2) + '\n' +
                getString(R.string.activity_earthquake_details_share_option_usgs_map_text) + '\n'
                + mEarthquake.getUrl() + "/map" + "\n\n" +
                getString(R.string.activity_earthquake_details_share_option_about_app_link_text) + '\n'
                + getString(R.string.activity_earthquake_details_share_option_app_link_text) + "\n\n";

        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(text)
                .setSubject(getString(R.string.activity_earthquake_details_label) + ".")
                .getIntent();

        startActivity(shareIntent);
    }


    private void showEarthquakeWebSiteActivity() {
        if (WordsUtils.getLocaleLanguage().equals("es")) {
            QueryUtils.openWebPageInGoogleChrome(this, mEarthquake.getUrl());
        } else {
            Intent intent = new Intent(this, EarthquakeWebPageActivity.class);
            intent.putExtra(EarthquakeWebPageActivity.EARTHQUAKE_URL_EXTRA_KEY, mEarthquake.getUrl());
            startActivity(intent);
            overridePendingTransition(R.anim.slide_up, R.anim.no_animation);
        }
    }


    private void showReportEarthquakeActivity() {
        String reportUrl = mEarthquake.getUrl() + "/tellus";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Intent intent = new Intent(this, ReportEarthquakeActivity.class);
            intent.putExtra(ReportEarthquakeActivity.REPORT_EARTHQUAKE_URL_EXTRA_KEY, reportUrl);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_up, R.anim.no_animation);
        } else {
            // For Android 19 in Spanish show a message indicating how to change language to Spanish.
            if (WordsUtils.getLocaleLanguage().equals("es")){
                showReportEarthquakeInSpanishHelpMessage();
            } else {
                // On Android 19 report web site does not work correctly on ReportEarthquakeActivity WebView
                // so open the report web site in Google Chrome.
                QueryUtils.openWebPageInGoogleChrome(this, reportUrl);
            }
        }
    }

    // Implementation of the MessageDialogFragment.MessageDialogFragmentListener interface
    @Override
    public void onAccept() {
        // On Android 19 report web site does not work correctly on ReportEarthquakeActivity WebView
        // so open the report web site in Google Chrome.
        QueryUtils.openWebPageInGoogleChrome(this, mEarthquake.getUrl() + "/tellus");
    }

    private void dismissEarthquakeDetailsActivity() {
        if (mUsgsMapWebView != null && mUsgsMapWebView.canGoBack()) {
            mUsgsMapWebView.goBack();
        } else {
            // Set this flag to true to prevent reloading USGS web view onTransitionEnd callback
            mOnBackPressed = true;
            // Fade out view
            if (mUsgsMapWebView != null) {
                mUsgsMapWebView.animate().alpha(0.0f);
            }
            onBackPressed();
        }
    }


    @Override
    public void onBackPressed() {
        if (mIsFavoritesActivityCalling && !mIsFavorite) {
            finish();
            overridePendingTransition(R.anim.no_animation, R.anim.slide_down);
        } else {
            super.onBackPressed();
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                overridePendingTransition(R.anim.no_animation, R.anim.slide_down);
            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            dismissEarthquakeDetailsActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_FAB_MENU_OPEN_VALUE_KEY, mIsFabMenuOpen);
        outState.putBoolean(IS_FAVORITE_VALUE_KEY, mIsFavorite);
    }

}

