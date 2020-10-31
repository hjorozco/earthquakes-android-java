package com.weebly.hectorjorozco.earthquakes.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.app.SharedElementCallback;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.util.Pair;
import androidx.core.view.MenuCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.stetho.Stetho;
import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.weebly.hectorjorozco.earthquakes.R;
import com.weebly.hectorjorozco.earthquakes.adapters.EarthquakesListAdapter;
import com.weebly.hectorjorozco.earthquakes.models.Earthquake;
import com.weebly.hectorjorozco.earthquakes.ui.dialogfragments.SortEarthquakesDialogFragment;
import com.weebly.hectorjorozco.earthquakes.ui.recyclerviewfastscroller.RecyclerViewFastScrollerViewProvider;
import com.weebly.hectorjorozco.earthquakes.ui.dialogfragments.MessageDialogFragment;
import com.weebly.hectorjorozco.earthquakes.utils.WordsUtils;
import com.weebly.hectorjorozco.earthquakes.utils.QueryUtils;
import com.weebly.hectorjorozco.earthquakes.viewmodels.MainActivityViewModel;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static android.view.View.GONE;


public class MainActivity extends AppCompatActivity implements EarthquakesListAdapter.EarthquakesListClickListener,
        QueryUtils.LocationUpdateListener, SortEarthquakesDialogFragment.SortEarthquakesDialogFragmentListener {

    public static final int MAX_NUMBER_OF_EARTHQUAKES_LIMIT = 20000;
    public static final int UPPER_LIMIT_TO_NOT_SHOW_FAST_SCROLLING = 50;
    public static final int MAX_NUMBER_OF_EARTHQUAKES_FOR_MAP = 1000;
    private static final int SECONDS_UNTIL_SHOWING_LONG_SEARCH_MESSAGE = 30;
    public static final int LONG_TIME_SNACKBAR = 5;

    public static final String APP_LOCATION_PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final String EARTHQUAKE_RECYCLER_VIEW_POSITION_KEY = "EARTHQUAKE_RECYCLER_VIEW_POSITION_KEY";

    public static final int SORT_BY_ASCENDING_DATE = 0;
    public static final int SORT_BY_DESCENDING_DATE = 1;
    public static final int SORT_BY_ASCENDING_MAGNITUDE = 2;
    public static final int SORT_BY_DESCENDING_MAGNITUDE = 3;
    public static final int SORT_BY_ASCENDING_DISTANCE = 4;
    public static final int SORT_BY_DESCENDING_DISTANCE = 5;

    private EarthquakesListAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private MainActivityViewModel mMainActivityViewModel;
    private TextView mMessageTextView;
    private ImageView mMessageImageView;
    private ProgressBar mProgressBar;
    private Menu mMenu;
    private int mNumberOfEarthquakesOnList;
    private FastScroller mRecyclerViewFastScroller;
    private int mEarthquakeRecyclerViewPosition;
    private MediaPlayer mMediaPlayer;
    private CoordinatorLayout mSnackbarLayout;
    private BottomNavigationView mBottomNavigationView;
    private Snackbar mSnackbar;
    private FloatingActionButton mFab;
    private List<Earthquake> mEarthquakes;
    private MenuItem mSortByDistanceMenuItem;

    // Used to show a snack bar after a long search time.
    private Handler mHandler;
    private Runnable mRunnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // After a rotation
        if (savedInstanceState != null) {
            mEarthquakeRecyclerViewPosition = savedInstanceState.getInt(EARTHQUAKE_RECYCLER_VIEW_POSITION_KEY, 0);
            mHandler = QueryUtils.sHandler;
            mRunnable = QueryUtils.sRunnable;
        } else {
            setupLongSearchMessageHandler();
        }

        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.activity_main_label));
        }

        // Initialize Stetho.
        Stetho.initializeWithDefaults(this);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        mAdapter = new EarthquakesListAdapter(this, this);
        mMessageImageView = findViewById(R.id.activity_main_message_image_view);
        mMessageTextView = findViewById(R.id.activity_main_message_text_view);
        mProgressBar = findViewById(R.id.activity_main_progress_bar);
        mSnackbarLayout = findViewById(R.id.activity_main_coordinator_layout);
        mFab = findViewById(R.id.activity_main_fab);

        mMediaPlayer = new MediaPlayer();

        setMessage(QueryUtils.SEARCHING);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            QueryUtils.setupProgressBarForPreLollipop(mProgressBar, this);
        }

        setupBottomNavigationView();

        setupRecyclerView();

        saveSharedElementsTransitions();
    }


    private void setupLongSearchMessageHandler() {
        mHandler = new Handler();
        mRunnable = () -> {
            Snackbar snackbar = Snackbar.make(mSnackbarLayout,
                    MainActivity.this.getString(R.string.activity_main_long_search_message),
                    LONG_TIME_SNACKBAR * 1000);
            snackbar.show();
        };

        mHandler.postDelayed(mRunnable, SECONDS_UNTIL_SHOWING_LONG_SEARCH_MESSAGE * 1000);
        QueryUtils.sHandler = mHandler;
        QueryUtils.sRunnable = mRunnable;
    }


    @SuppressWarnings("SameReturnValue")
    private void setupBottomNavigationView() {
        mBottomNavigationView = findViewById(R.id.activity_main_bottom_navigation_view);
        mBottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            int menuItemId = menuItem.getItemId();
            if (menuItemId == R.id.menu_activity_main_bottom_navigation_view_action_map) {
                startActivity(new Intent(this, EarthquakesMapActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } else if (menuItemId == R.id.menu_activity_main_bottom_navigation_view_action_favorites) {
                startActivity(new Intent(MainActivity.this, FavoritesActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
            return true;
        });
    }


    private void setupRecyclerView() {

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(Objects.requireNonNull(ResourcesCompat.getDrawable(
                getResources(), R.drawable.recycler_view_divider_light, null)));

        mRecyclerView = findViewById(R.id.activity_main_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setNestedScrollingEnabled(true);

        // Sets up the fast scroller
        mRecyclerViewFastScroller = findViewById(R.id.activity_main_recycler_view_fast_scroller);
        RecyclerViewFastScrollerViewProvider viewProvider = new RecyclerViewFastScrollerViewProvider();
        mRecyclerViewFastScroller.setRecyclerView(mRecyclerView);
        mRecyclerViewFastScroller.setViewProvider(viewProvider);

    }


    private void setupViewModel() {
        mMainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        mMainActivityViewModel.getEarthquakes(this).observe(this, earthquakes -> {

            mNumberOfEarthquakesOnList = 0;

            // If the list of earthquakes was empty before the search
            if (earthquakes == null) {
                // If the search has finished display a message with an icon
                if (!QueryUtils.sSearchingForEarthquakes) {
                    setMessageVisible(true);
                    setMessage(QueryUtils.sLoadEarthquakesResultCode);
                    enableRefresh();
                    removeRunnableFromHandler();
                }
                QueryUtils.sListWillBeLoadedAfterEmpty = true;
            } else {

                // If no earthquakes were found
                if (earthquakes.size() == 0) {
                    // If the search has finished display a message with an icon
                    if (!QueryUtils.sSearchingForEarthquakes) {
                        setMessageVisible(true);
                        if (QueryUtils.sLoadEarthquakesResultCode == QueryUtils.NO_INTERNET_CONNECTION ||
                                QueryUtils.sLoadEarthquakesResultCode == QueryUtils.SEARCH_CANCELLED ||
                                QueryUtils.sLoadEarthquakesResultCode == QueryUtils.SEARCH_RESULT_NULL) {
                            setMessage(QueryUtils.sLoadEarthquakesResultCode);
                        } else {
                            setMessage(QueryUtils.NO_EARTHQUAKES_FOUND);
                        }
                        removeRunnableFromHandler();
                    }
                    QueryUtils.sListWillBeLoadedAfterEmpty = true;
                } else {

                    // If one or more earthquakes were found
                    mNumberOfEarthquakesOnList = earthquakes.size();
                    setMessageVisible(false);

                    // If not searching for earthquakes stop the sound
                    if (!QueryUtils.sSearchingForEarthquakes) {
                        mMediaPlayer.stop();
                        QueryUtils.sIsPlayingSound = false;
                        removeRunnableFromHandler();
                    }

                    // If there were new earthquakes displayed
                    if (QueryUtils.sLoadEarthquakesResultCode == QueryUtils.SEARCH_RESULT_NON_NULL) {
                        // Remove sorted by distance message
                        QueryUtils.sEarthquakesListSortedByDistanceText = "";
                        setEarthquakesListForMapsActivity(earthquakes);
                        setEarthquakesListInformationValues(earthquakes.get(0),
                                earthquakes.get(earthquakes.size() - 1));
                    }

                    mAdapter.setLocation(QueryUtils.sEarthquakesListInformationValues.getLocation());
                    mAdapter.setMaxDistance(QueryUtils.sEarthquakesListInformationValues.getMaxDistance());
                    mAdapter.setEarthquakesListData(earthquakes);

                    // After the adapter is updated with one or more earthquakes
                    if (mSortByDistanceMenuItem != null) {
                        // If the distance is shown enable sort by distance menu item if not disable it
                        mSortByDistanceMenuItem.setEnabled(QueryUtils.isDistanceShown(this));
                    }

                    // If the search has finished and no previous snack has been shown
                    if (!QueryUtils.sSearchingForEarthquakes && QueryUtils.sLoadEarthquakesResultCode != QueryUtils.NO_ACTION
                            && !QueryUtils.sListWillBeLoadedAfterEmpty) {
                        mSnackbar = Snackbar.make(mSnackbarLayout,
                                getSnackBarText(QueryUtils.sLoadEarthquakesResultCode),
                                Snackbar.LENGTH_INDEFINITE);
                        mSnackbar.setAction(getString(R.string.ok_text), v -> mSnackbar.dismiss());
                        mSnackbar.show();
                    }

                    // Flag used when activity is recreated to indicate that no action is tacking place
                    QueryUtils.sLoadEarthquakesResultCode = QueryUtils.NO_ACTION;

                    QueryUtils.sListWillBeLoadedAfterEmpty = false;
                }

                // This is used when this observer on changed method is called after a screen rotation.
                checkForSearchingStatusToEnableRefresh();
            }

            if (QueryUtils.sSearchingForEarthquakes) {
                mProgressBar.setVisibility(View.VISIBLE);
            } else {
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });

    }


    private void setEarthquakesListForMapsActivity(List<Earthquake> earthquakes) {

        if (earthquakes.size() > MAX_NUMBER_OF_EARTHQUAKES_FOR_MAP) {
            QueryUtils.sMoreThanMaximumNumberOfEarthquakesForMap = true;
            QueryUtils.sEarthquakesList = earthquakes.subList(0, MAX_NUMBER_OF_EARTHQUAKES_FOR_MAP);

        } else {
            QueryUtils.sMoreThanMaximumNumberOfEarthquakesForMap = false;
            QueryUtils.sEarthquakesList = earthquakes;
        }
    }


    private void setEarthquakesListInformationValues(Earthquake firstEarthquake, Earthquake lastEarthquake) {
        DecimalFormat magnitudesFormatter = new DecimalFormat("0.0");

        SimpleDateFormat simpleDateFormatter;
        if (WordsUtils.getLocaleLanguage().equals("es")) {
            simpleDateFormatter = new SimpleDateFormat("d 'de' MMMM 'del' yyyy, hh:mm aaa", Locale.getDefault());
        } else {
            simpleDateFormatter = new SimpleDateFormat("MMMM d, yyyy hh:mm aaa", Locale.getDefault());
        }

        QueryUtils.sEarthquakesListInformationValues = QueryUtils.sEarthquakesListInformationValuesWhenSearchStarted;
        QueryUtils.sEarthquakesListInformationValues.
                setFirstEarthquakeMag(magnitudesFormatter.format(firstEarthquake.getMagnitude()));
        QueryUtils.sEarthquakesListInformationValues.
                setLastEarthquakeMag(magnitudesFormatter.format(lastEarthquake.getMagnitude()));
        QueryUtils.sEarthquakesListInformationValues.
                setFirstEarthquakeDate(simpleDateFormatter.format(firstEarthquake.getTimeInMilliseconds()));
        QueryUtils.sEarthquakesListInformationValues.
                setLastEarthquakeDate(simpleDateFormatter.format(lastEarthquake.getTimeInMilliseconds()));
        QueryUtils.sEarthquakesListInformationValues.
                setFirstEarthquakeDistance(firstEarthquake.getDistance());
        QueryUtils.sEarthquakesListInformationValues.
                setLastEarthquakeDistance(lastEarthquake.getDistance());
        QueryUtils.sEarthquakesListInformationValues.
                setNumberOfEarthquakesDisplayed(String.valueOf(mNumberOfEarthquakesOnList));
    }


    // Helper method that sets the message image and text
    private void setMessage(byte type) {
        int imageID = 0;
        int textID = 0;
        switch (type) {
            case QueryUtils.SEARCHING:
                imageID = R.drawable.ic_earthquakes;
                textID = R.string.searching_earthquakes_text;
                break;
            case QueryUtils.NO_INTERNET_CONNECTION:
                imageID = R.drawable.ic_message_no_internet;
                textID = R.string.no_internet_connection_text;
                break;
            case QueryUtils.SEARCH_CANCELLED:
                imageID = R.drawable.ic_message_searching_cancelled;
                textID = R.string.search_cancelled_text;
                break;
            case QueryUtils.NO_EARTHQUAKES_FOUND:
                imageID = R.drawable.ic_message_no_earthquakes;
                textID = R.string.no_earthquakes_found_text;
                break;
            case QueryUtils.SEARCH_RESULT_NULL:
                imageID = R.drawable.ic_message_no_server_response;
                textID = R.string.no_server_response_text;
                break;
        }
        if (type != QueryUtils.SEARCH_RESULT_NON_NULL) {
            mMessageImageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), imageID, null));
            if (type == QueryUtils.SEARCHING) {
                mMessageImageView.setAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
                mMessageTextView.setVisibility(GONE);
                if (QueryUtils.getSoundSearchPreference(this) && !QueryUtils.sIsPlayingSound) {
                    playEarthquakeSound();
                }
            } else {
                mMessageImageView.clearAnimation();
                mMediaPlayer.stop();
                QueryUtils.sIsPlayingSound = false;
            }
            mMessageTextView.setText(getString(textID));
        }
    }


    private void playEarthquakeSound() {
        mMediaPlayer = MediaPlayer.create(this, R.raw.earthquake_sound);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();
        QueryUtils.sIsPlayingSound = true;
    }


    private String getSnackBarText(byte type) {
        String snackBarText = null;
        switch (type) {
            case QueryUtils.NO_EARTHQUAKES_FOUND:
                snackBarText = getString(R.string.no_earthquakes_found_text);
                break;
            case QueryUtils.NO_INTERNET_CONNECTION:
                snackBarText = getString(R.string.no_internet_connection_text);
                break;
            case QueryUtils.SEARCH_CANCELLED:
                snackBarText = getString(R.string.search_cancelled_text);
                break;
            case QueryUtils.SEARCH_RESULT_NULL:
                snackBarText = getString(R.string.no_server_response_text);
                break;
            case QueryUtils.SEARCH_RESULT_NON_NULL:
                snackBarText = getString(R.string.earthquakes_list_updated_text);
                break;
        }
        return snackBarText;
    }


    // Helper method that shows the message and hides the RecyclerView and vice versa.
    private void setMessageVisible(boolean showMessage) {
        if (showMessage) {
            mRecyclerView.setVisibility(GONE);
            mMessageImageView.setVisibility(View.VISIBLE);
            mMessageTextView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            if (mNumberOfEarthquakesOnList > UPPER_LIMIT_TO_NOT_SHOW_FAST_SCROLLING) {
                mRecyclerViewFastScroller.setVisibility(View.VISIBLE);
            } else {
                mRecyclerViewFastScroller.setVisibility(View.INVISIBLE);
            }
            mMessageImageView.setVisibility(GONE);
            mMessageTextView.setVisibility(GONE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        mMenu = menu;
        mSortByDistanceMenuItem = mMenu.findItem(R.id.menu_activity_main_action_sort_by_distance);

        if (mAdapter.getEarthquakesListData() != null) {
            if (mAdapter.getEarthquakesListData().size() > 0) {
                mSortByDistanceMenuItem.setEnabled(QueryUtils.isDistanceShown(this));
            }
        }

        if (!QueryUtils.sSearchingForEarthquakes) {
            setupRefreshMenuItem(true);
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int menuItemId = item.getItemId();

        if (menuItemId == R.id.menu_activity_main_action_refresh) {
            selectRefreshOrStopAction();
        } else if (menuItemId == R.id.menu_activity_main_action_search_preferences) {
            startActivity(new Intent(this, SearchPreferencesActivity.class));
            overridePendingTransition(R.anim.slide_up, R.anim.no_animation);
        } else if (menuItemId == R.id.menu_activity_main_action_sort_by_distance) {
            showSortEarthquakesByDistanceDialogFragment();
        } else if (menuItemId == R.id.menu_activity_main_action_glossary) {
            startActivity(new Intent(this, GlossaryActivity.class));
            overridePendingTransition(R.anim.slide_up, R.anim.no_animation);
        } else if (menuItemId == R.id.menu_activity_main_action_help) {
            showHelpMessageDialogFragment();
        } else if (menuItemId == R.id.menu_activity_main_action_about) {
            showAboutMessageDialogFragment();
        }

        return super.onOptionsItemSelected(item);
    }


    // Helper method that show a DialogFragment with help for the application
    private void showHelpMessageDialogFragment() {
        MessageDialogFragment messageDialogFragment =
                MessageDialogFragment.newInstance(
                        Html.fromHtml(getString(R.string.activity_main_help_dialog_fragment_text)),
                        getString(R.string.menu_activity_main_action_help_title),
                        MessageDialogFragment.MESSAGE_DIALOG_FRAGMENT_CALLER_HELP_ABOUT_MESSAGE);

        messageDialogFragment.show(getSupportFragmentManager(),
                getString(R.string.activity_main_help_dialog_fragment_tag));
    }


    // Helper method that show a DialogFragment that shows info about the application
    private void showAboutMessageDialogFragment() {
        MessageDialogFragment messageDialogFragment =
                MessageDialogFragment.newInstance(
                        Html.fromHtml(getString(R.string.activity_main_about_dialog_fragment_text, getString(R.string.version_name))),
                        getString(R.string.menu_activity_main_action_about_title),
                        MessageDialogFragment.MESSAGE_DIALOG_FRAGMENT_CALLER_HELP_ABOUT_MESSAGE);

        messageDialogFragment.show(getSupportFragmentManager(),
                getString(R.string.activity_main_about_dialog_fragment_tag));
    }


    private void selectRefreshOrStopAction() {
        if (!QueryUtils.sSearchingForEarthquakes) {
            if (mSnackbar != null) {
                mSnackbar.dismiss();
            }
            setupLongSearchMessageHandler();
            doRefreshActions();
        } else {
            mMainActivityViewModel.cancelRetrofitRequest();
            removeRunnableFromHandler();
        }
    }


    private void enableSortByDistanceMenuItem(boolean enable) {
        mMenu.findItem(R.id.menu_activity_main_action_sort_by_distance).setEnabled(enable);
    }


    // Helper method that show a DialogFragment that lets the user select how to sort favorites
    private void showSortEarthquakesByDistanceDialogFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        SortEarthquakesDialogFragment sortEarthquakesDialogFragment =
                SortEarthquakesDialogFragment.newInstance(
                        getString(R.string.menu_activity_main_action_sort_by_distance_title));

        sortEarthquakesDialogFragment.show(fragmentManager,
                getString(R.string.activity_main_sort_by_distance_dialog_fragment_tag));
    }

    private void removeRunnableFromHandler() {
        // Removes the runnable that shows a snack bar when a long time searching has passed.
        if (mHandler != null && mRunnable != null) {
            mHandler.removeCallbacks(mRunnable);
        }
    }


    private void doRefreshActions() {
        // Show stop menu item
        setupRefreshMenuItem(false);
        mProgressBar.setVisibility(View.VISIBLE);
        QueryUtils.sSearchingForEarthquakes = true;
        setMessage(QueryUtils.SEARCHING);
        mMainActivityViewModel.loadEarthquakes(this);
    }


    // Stops refreshing animation and changes the refresh menu item icon and title to refresh
    private void enableRefresh() {
        if (mMenu != null) {
            setupRefreshMenuItem(true);
        }
        mProgressBar.setVisibility(View.INVISIBLE);
    }


    // Sets the icon and title of the "refresh" menu item.
    private void setupRefreshMenuItem(boolean refresh) {
        MenuItem menuItemRefresh = mMenu.findItem(R.id.menu_activity_main_action_refresh);
        if (refresh) {
            menuItemRefresh.setIcon(R.drawable.ic_refresh_white_24dp);
            menuItemRefresh.setTitle(R.string.menu_activity_main_action_refresh_title);
        } else {
            menuItemRefresh.setIcon(R.drawable.ic_stop_white_24dp);
            menuItemRefresh.setTitle(R.string.menu_activity_main_action_stop_title);
        }

    }

    private void checkForSearchingStatusToEnableRefresh() {
        if (!QueryUtils.sSearchingForEarthquakes) {
            enableRefresh();
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }


    /**
     * Implementation of EarthquakesListAdapter.EarthquakesListClickListener
     * When the title of the list is clicked.
     */
    @Override
    public void onTitleClick() {
        MessageDialogFragment messageDialogFragment =
                MessageDialogFragment.newInstance(
                        QueryUtils.createEarthquakesInformationMessageDialogMessage(this,
                                QueryUtils.sEarthquakesListInformationValues, true),
                        getString(R.string.menu_activity_main_action_list_information_title),
                        MessageDialogFragment.MESSAGE_DIALOG_FRAGMENT_CALLER_OTHER);

        messageDialogFragment.show(getSupportFragmentManager(),
                getString(R.string.activity_main_earthquakes_list_information_dialog_fragment_tag));
    }

    /**
     * Implementation of EarthquakesListAdapter.EarthquakesListClickListener
     * When an earthquake on the list is clicked show a new activity with details of it.
     * Implement a transition if Android version is 21 or grater.
     */
    @Override
    public void onEarthquakeClick(Earthquake earthquake, int earthquakeRecyclerViewPosition,
                                  View magnitudeCircleView, TextView magnitudeTextView,
                                  TextView locationOffsetTextView, TextView locationPrimaryTextView,
                                  TextView distanceTextView, TextView dateTextView, TextView timeTextView) {

        mEarthquakeRecyclerViewPosition = earthquakeRecyclerViewPosition;


        Intent intent = new Intent(this, EarthquakeDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(EarthquakeDetailsActivity.EXTRA_EARTHQUAKE, earthquake);
        bundle.putByte(EarthquakeDetailsActivity.EXTRA_CALLER, EarthquakeDetailsActivity.MAIN_ACTIVITY_CALLER);
        intent.putExtra(EarthquakeDetailsActivity.EXTRA_BUNDLE_KEY, bundle);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Pair<View, String> pair1 = Pair.create(magnitudeCircleView, magnitudeCircleView.getTransitionName());
            Pair<View, String> pair2 = Pair.create(magnitudeTextView, magnitudeTextView.getTransitionName());
            Pair<View, String> pair3 = Pair.create(locationOffsetTextView, locationOffsetTextView.getTransitionName());
            Pair<View, String> pair4 = Pair.create(locationPrimaryTextView, locationPrimaryTextView.getTransitionName());
            Pair<View, String> pair5 = Pair.create(dateTextView, dateTextView.getTransitionName());
            Pair<View, String> pair6 = Pair.create(timeTextView, timeTextView.getTransitionName());

            ActivityOptionsCompat activityOptionsCompat;
            if (!QueryUtils.isDistanceShown(this)) {
                //noinspection unchecked
                activityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(this, pair1, pair2, pair3, pair4, pair5, pair6);
            } else {
                Pair<View, String> pair7 = Pair.create(distanceTextView, distanceTextView.getTransitionName());
                //noinspection unchecked
                activityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(this, pair1, pair2, pair3, pair4, pair5, pair6, pair7);
            }

            startActivity(intent, activityOptionsCompat.toBundle());
        } else {
            startActivity(intent);
            overridePendingTransition(R.anim.slide_up, R.anim.no_animation);
        }

    }


    /**
     * Map the shared element names to the RecyclerView ViewHolder Views. (works only for visible RecyclerView elements).
     * Used to restore exit transitions on rotation.
     */
    private void saveSharedElementsTransitions() {

        setExitSharedElementCallback(
                new SharedElementCallback() {
                    @Override
                    public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {

                        RecyclerView.ViewHolder selectedViewHolder = mRecyclerView
                                .findViewHolderForAdapterPosition(mEarthquakeRecyclerViewPosition);

                        if (selectedViewHolder == null) return;

                        sharedElements.put(getString(R.string.activity_earthquake_details_magnitude_circle_view_transition),
                                selectedViewHolder.itemView.
                                        findViewById(R.id.earthquake_list_item_magnitude_circle_view));
                        sharedElements.put(getString(R.string.activity_earthquake_details_magnitude_text_view_transition),
                                selectedViewHolder.itemView.
                                        findViewById(R.id.earthquake_list_item_magnitude_text_view));
                        sharedElements.put(getString(R.string.activity_earthquake_details_location_offset_text_view_transition),
                                selectedViewHolder.itemView.
                                        findViewById(R.id.earthquake_list_item_location_offset_text_view));
                        sharedElements.put(getString(R.string.activity_earthquake_details_location_primary_text_view_transition),
                                selectedViewHolder.itemView.
                                        findViewById(R.id.earthquake_list_item_location_primary_text_view));
                        sharedElements.put(getString(R.string.activity_earthquake_details_date_text_view_transition),
                                selectedViewHolder.itemView.
                                        findViewById(R.id.earthquake_list_item_date_text_view));
                        sharedElements.put(getString(R.string.activity_earthquake_details_time_text_view_transition),
                                selectedViewHolder.itemView.
                                        findViewById(R.id.earthquake_list_item_time_text_view));

                        Location location = QueryUtils.getLastKnowLocationFromSharedPreferences(MainActivity.this);
                        if (QueryUtils.getShowDistanceSearchPreference(MainActivity.this) &&
                                location.getLatitude() != QueryUtils.LAST_KNOW_LOCATION_LAT_LONG_NULL_VALUE &&
                                location.getLongitude() != QueryUtils.LAST_KNOW_LOCATION_LAT_LONG_NULL_VALUE) {
                            sharedElements.put(getString(R.string.activity_earthquake_details_distance_text_view_transition),
                                    selectedViewHolder.itemView.
                                            findViewById(R.id.earthquake_list_item_distance_text_view));
                        }


                        float viewTopPosition = selectedViewHolder.itemView.getY();
                        float viewBottomPosition = viewTopPosition +
                                selectedViewHolder.itemView.getHeight();

                        float bottomNavigationViewTopPosition = mBottomNavigationView.getY();
                        float viewTextBottomPosition = viewBottomPosition -
                                getResources().getDimensionPixelSize(R.dimen.application_small_margin);

                        // If the ViewHolder text is covered by the BottomNavigationView, hide it on click.
                        if (viewTextBottomPosition > bottomNavigationViewTopPosition) {
                            mBottomNavigationView.setVisibility(View.INVISIBLE);
                        }

                        float fabTopPosition = mFab.getY() +
                                getResources().getDimensionPixelSize(R.dimen.activity_main_fab_padding_for_position);
                        float fabBottomPosition = fabTopPosition +
                                getResources().getDimensionPixelSize(R.dimen.activity_main_fab_size);

                        // If the ViewHolder text is covered by the FAB , hide it on click.
                        if (fabTopPosition > viewTopPosition && fabBottomPosition < viewBottomPosition) {
                            mFab.hide();
                        }

                        // If the ViewHolder text is covered by the Snackbar , hide it on click.
                        if (mSnackbar != null) {
                            if (mSnackbar.isShown()) {
                                float snackbarTopPosition = mSnackbar.getView().getY();
                                float snackbarBottomPosition = snackbarTopPosition + mSnackbar.getView().getHeight();
                                if (snackbarBottomPosition > viewTopPosition && snackbarTopPosition < viewBottomPosition) {
                                    mSnackbar.dismiss();
                                }
                            }
                        }

                    }
                });
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EARTHQUAKE_RECYCLER_VIEW_POSITION_KEY, mEarthquakeRecyclerViewPosition);
    }


    @Override
    protected void onPause() {
        super.onPause();
        mMediaPlayer.stop();
    }


    @Override
    protected void onResume() {
        super.onResume();

        setupViewModel();

        if (QueryUtils.getSoundSearchPreference(this) && QueryUtils.sIsPlayingSound) {
            playEarthquakeSound();
        }

        if (QueryUtils.isLocationPermissionGranted(this)
                && (QueryUtils.getMaxDistanceSearchPreference(this) != 0) ||
                QueryUtils.getShowDistanceSearchPreference(this)) {
            QueryUtils.updateLastKnowLocation(this, this);
        }

        mFab.hide();
        mBottomNavigationView.setVisibility(View.VISIBLE);
        mBottomNavigationView.setSelectedItemId(R.id.menu_activity_main_bottom_navigation_view_action_list);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            QueryUtils.sListWillBeLoadedAfterEmpty = true;
            QueryUtils.sSearchingForEarthquakes = true;
        }
        return super.onKeyDown(keyCode, event);
    }


    // Implementation of interface QueryUtils.LocationUpdateListener
    public void onLocationUpdate(boolean locationUpdated) {
        if (locationUpdated) {
            Log.d("TESTING", "LISTENER: Location updated");
            mFab.hide();
        } else {
            Log.d("TESTING", "LISTENER: Location not updated");
            mFab.show();
            mFab.setOnClickListener(v -> {
                String message;
                if (QueryUtils.getShowDistanceSearchPreference(MainActivity.this)) {
                    if (QueryUtils.getMaxDistanceSearchPreference(MainActivity.this) != 0) {
                        if (isThereALocationSaved(MainActivity.this)) {
                            message = getString(R.string.activity_main_distance_and_filter_not_accurate_message);
                        } else {
                            message = getString(R.string.activity_main_distance_and_filter_not_shown_message);
                        }
                    } else {
                        if (isThereALocationSaved(MainActivity.this)) {
                            message = getString(R.string.activity_main_distance_not_accurate_message);
                        } else {
                            message = getString(R.string.activity_main_distance_not_shown_message);
                        }
                    }
                } else {
                    if (isThereALocationSaved(MainActivity.this)) {
                        message = getString(R.string.activity_main_filter_not_accurate_message);
                    } else {
                        message = getString(R.string.activity_main_filter_not_shown_message);
                    }
                }

                MessageDialogFragment messageDialogFragment =
                        MessageDialogFragment.newInstance(
                                Html.fromHtml(message),
                                getString(R.string.activity_main_location_can_not_be_determined_message_title),
                                MessageDialogFragment.MESSAGE_DIALOG_FRAGMENT_CALLER_OTHER);

                messageDialogFragment.show(getSupportFragmentManager(),
                        getString(R.string.activity_main_location_can_not_be_determined_message_tag));
            });
        }
    }


    private boolean isThereALocationSaved(Context context) {
        Location location = QueryUtils.getLastKnowLocationFromSharedPreferences(context);
        return location.getLatitude() != QueryUtils.LAST_KNOW_LOCATION_LAT_LONG_NULL_VALUE &&
                location.getLongitude() != QueryUtils.LAST_KNOW_LOCATION_LAT_LONG_NULL_VALUE;
    }


    // Implementation of SortEarthquakesDialogFragment.SortEarthquakesDialogFragmentListener interface
    @Override
    public void onSortCriteriaSelected(boolean sortByAscendingDistance) {

        String sortedByMessage = "";

        List<Earthquake> earthquakes = mAdapter.getEarthquakesListData();
        if (earthquakes != null) {
            List<Earthquake> earthquakesWithDistance = QueryUtils.addDistanceToAllEarthquakes(earthquakes);
            if (sortByAscendingDistance) {
                sortedByMessage = getString(R.string.activity_favorites_sorted_by_ascending_distance_text);
                Collections.sort(earthquakesWithDistance, Earthquake.ascendingDateComparator);
                Collections.sort(earthquakesWithDistance, Earthquake.ascendingDistanceComparator);
            } else {
                sortedByMessage = getString(R.string.activity_favorites_sorted_by_descending_distance_text);
                Collections.sort(earthquakesWithDistance, Earthquake.descendingDateComparator);
                Collections.sort(earthquakesWithDistance, Earthquake.descendingDistanceComparator);
            }
            earthquakes = earthquakesWithDistance;
            QueryUtils.sEarthquakesListSortedByDistanceText =
                    getString(R.string.activity_main_list_title_sorted_by_distance_text, sortedByMessage);
            setEarthquakesListForMapsActivity(earthquakes);
            setEarthquakesListInformationValues(earthquakes.get(0),
                    earthquakes.get(earthquakes.size() - 1));
            mAdapter.setEarthquakesListData(earthquakes);
        }

        mSnackbar = Snackbar.make(mSnackbarLayout,
                getString(R.string.activity_main_sorted_by_distance_snack_text, sortedByMessage),
                Snackbar.LENGTH_LONG);
        mSnackbar.show();
    }
}
