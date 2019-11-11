package com.weebly.hectorjorozco.earthquakes.ui;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.app.SharedElementCallback;
import androidx.core.util.Pair;
import androidx.core.view.MenuCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
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
import com.google.android.material.snackbar.Snackbar;
import com.weebly.hectorjorozco.earthquakes.R;
import com.weebly.hectorjorozco.earthquakes.adapters.EarthquakesListAdapter;
import com.weebly.hectorjorozco.earthquakes.models.Earthquake;
import com.weebly.hectorjorozco.earthquakes.ui.recyclerviewfastscroller.RecyclerViewFastScrollerViewProvider;
import com.weebly.hectorjorozco.earthquakes.ui.dialogfragments.MessageDialogFragment;
import com.weebly.hectorjorozco.earthquakes.utils.WordsUtils;
import com.weebly.hectorjorozco.earthquakes.utils.QueryUtils;
import com.weebly.hectorjorozco.earthquakes.viewmodels.MainActivityViewModel;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements EarthquakesListAdapter.EarthquakesListClickListener {

    public static final int MAX_NUMBER_OF_EARTHQUAKES_LIMIT = 20000;
    public static final int UPPER_LIMIT_TO_NOT_SHOW_FAST_SCROLLING = 50;
    public static final int MAX_NUMBER_OF_EARTHQUAKES_FOR_MAP = 1000;
    private static final int SECONDS_UNTIL_SHOWING_LONG_SEARCH_MESSAGE = 30;
    public static final int LONG_TIME_SNACKBAR = 5;

    private static final String EARTHQUAKE_RECYCLER_VIEW_POSITION_KEY = "EARTHQUAKE_RECYCLER_VIEW_POSITION_KEY";

    public static final int SORT_BY_ASCENDING_DATE = 0;
    public static final int SORT_BY_DESCENDING_DATE = 1;
    public static final int SORT_BY_ASCENDING_MAGNITUDE = 2;
    public static final int SORT_BY_DESCENDING_MAGNITUDE = 3;

    private EarthquakesListAdapter mEarthquakesListAdapter;
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

    // Used to show a snack bar after a long search time.
    private Handler mHandler;
    private Runnable mRunnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (QueryUtils.isLocationPermissionGranted(this)
                && QueryUtils.getMaxDistanceSearchPreference(this) != 0) {
            QueryUtils.updateLastKnowLocation(this);
        }

        // After a rotation
        if (savedInstanceState != null) {
            mEarthquakeRecyclerViewPosition = savedInstanceState.getInt(EARTHQUAKE_RECYCLER_VIEW_POSITION_KEY, 0);
            mHandler = QueryUtils.sHandler;
            mRunnable = QueryUtils.sRunnable;
        } else {
            setupLongSearchMessageHandler();
        }

        setContentView(R.layout.activity_main);

        // Initialize Stetho.
        Stetho.initializeWithDefaults(this);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        mEarthquakesListAdapter = new EarthquakesListAdapter(this, this);
        mMessageImageView = findViewById(R.id.activity_main_message_image_view);
        mMessageTextView = findViewById(R.id.activity_main_message_text_view);
        mProgressBar = findViewById(R.id.activity_main_progress_bar);

        mMediaPlayer = new MediaPlayer();

        setMessage(QueryUtils.SEARCHING);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            QueryUtils.setupProgressBarForPreLollipop(mProgressBar, this);
        }

        setupRecyclerView();

        setupViewModel();

        saveSharedElementsTransitions();
    }


    private void setupLongSearchMessageHandler() {
        mHandler = new Handler();
        mRunnable = () -> Snackbar.make(findViewById(android.R.id.content),
                MainActivity.this.getString(R.string.activity_main_long_search_message),
                LONG_TIME_SNACKBAR * 1000).show();

        mHandler.postDelayed(mRunnable, SECONDS_UNTIL_SHOWING_LONG_SEARCH_MESSAGE * 1000);
        QueryUtils.sHandler = mHandler;
        QueryUtils.sRunnable = mRunnable;
    }


    private void setupRecyclerView() {

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recycler_view_divider_light));

        mRecyclerView = findViewById(R.id.activity_main_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mEarthquakesListAdapter);
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
        mMainActivityViewModel.getEarthquakes().observe(this, earthquakes -> {

            mNumberOfEarthquakesOnList = 0;

            // If the list of earthquakes was empty before the search
            if (earthquakes == null) {
                // If the search has finished display a message with an icon
                if (!QueryUtils.sSearchingForEarthquakes) {
                    setMessageVisible(true);
                    setMessage(QueryUtils.sLoadEarthquakesResultCode);
                    enableRefresh();
                    QueryUtils.sOneOrMoreEarthquakesFoundByRetrofitQuery = false;
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
                        QueryUtils.sOneOrMoreEarthquakesFoundByRetrofitQuery = false;
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
                        setEarthquakesListForMapsActivity(earthquakes);
                        setEarthquakesListInformationValues(earthquakes.get(0),
                                earthquakes.get(earthquakes.size() - 1));
                    }

                    mEarthquakesListAdapter.setLocation(QueryUtils.sEarthquakesListInformationValues.getLocation());
                    mEarthquakesListAdapter.setMaxDistance(QueryUtils.sEarthquakesListInformationValues.getMaxDistance());
                    mEarthquakesListAdapter.setEarthquakesListData(earthquakes);

                    // If the search has finished and no previous snack has been shown
                    if (!QueryUtils.sSearchingForEarthquakes && QueryUtils.sLoadEarthquakesResultCode != QueryUtils.NO_ACTION
                            && !QueryUtils.sListWillBeLoadedAfterEmpty) {
                        Snackbar.make(findViewById(android.R.id.content),
                                getSnackBarText(QueryUtils.sLoadEarthquakesResultCode),
                                Snackbar.LENGTH_LONG).show();
                        if (mMenu != null) {
                            enableListInformationAndEarthquakesMapMenuItems(true);
                        }
                    }

                    // Flag used when activity is recreated to indicate that no action is tacking place
                    QueryUtils.sLoadEarthquakesResultCode = QueryUtils.NO_ACTION;

                    QueryUtils.sListWillBeLoadedAfterEmpty = false;

                    if (mMenu != null && QueryUtils.sOneOrMoreEarthquakesFoundByRetrofitQuery) {
                        enableListInformationAndEarthquakesMapMenuItems(true);
                    }
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
            mMessageImageView.setImageDrawable(getResources().getDrawable(imageID));
            if (type == QueryUtils.SEARCHING) {
                mMessageImageView.setAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
                mMessageTextView.setVisibility(View.GONE);
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
            mRecyclerView.setVisibility(View.GONE);
            mMessageImageView.setVisibility(View.VISIBLE);
            mMessageTextView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            if (mNumberOfEarthquakesOnList > UPPER_LIMIT_TO_NOT_SHOW_FAST_SCROLLING) {
                mRecyclerViewFastScroller.setVisibility(View.VISIBLE);
            } else {
                mRecyclerViewFastScroller.setVisibility(View.INVISIBLE);
            }
            mMessageImageView.setVisibility(View.GONE);
            mMessageTextView.setVisibility(View.GONE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        mMenu = menu;

        if (QueryUtils.sOneOrMoreEarthquakesFoundByRetrofitQuery) {
            enableListInformationAndEarthquakesMapMenuItems(true);
        } else {
            enableListInformationAndEarthquakesMapMenuItems(false);
        }

        if (!QueryUtils.sSearchingForEarthquakes) {
            setupRefreshMenuItem(true);
        } else {
            enableListInformationAndEarthquakesMapMenuItems(false);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_activity_main_action_refresh:
                selectRefreshOrStopAction();
                break;
            case R.id.menu_activity_main_action_search_preferences:
                startActivity(new Intent(this, SearchPreferencesActivity.class));
                overridePendingTransition(R.anim.slide_up, R.anim.no_animation);
                break;
            case R.id.menu_activity_main_action_favorites:
                startActivity(new Intent(this, FavoritesActivity.class));
                overridePendingTransition(R.anim.slide_up, R.anim.no_animation);
                break;
            case R.id.menu_activity_main_action_earthquakes_map:
                startActivity(new Intent(this, EarthquakesMapActivity.class));
                overridePendingTransition(R.anim.slide_up, R.anim.no_animation);
                break;
            case R.id.menu_activity_main_action_glossary:
                startActivity(new Intent(this, GlossaryActivity.class));
                overridePendingTransition(R.anim.slide_up, R.anim.no_animation);
                break;
            case R.id.menu_activity_main_action_help:
            case R.id.menu_activity_main_action_about:
                Snackbar.make(findViewById(android.R.id.content),
                        getString(R.string.activity_main_under_construction_text),
                        Snackbar.LENGTH_LONG).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void selectRefreshOrStopAction() {
        if (!QueryUtils.sSearchingForEarthquakes) {
            setupLongSearchMessageHandler();
            doRefreshActions();
        } else {
            mMainActivityViewModel.cancelRetrofitRequest();
            removeRunnableFromHandler();
        }
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
        enableListInformationAndEarthquakesMapMenuItems(false);
        QueryUtils.sSearchingForEarthquakes = true;
        setMessage(QueryUtils.SEARCHING);
        mMainActivityViewModel.loadEarthquakes();
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


    private void enableListInformationAndEarthquakesMapMenuItems(boolean enable) {
        mMenu.findItem(R.id.menu_activity_main_action_earthquakes_map).setEnabled(enable);
    }

    /**
     * Implementation of EarthquakesListAdapter.EarthquakesListClickListener
     * When the title of the list is clicked.
     */
    @Override
    public void onTitleClick() {
        MessageDialogFragment messageDialogFragment =
                MessageDialogFragment.newInstance(
                        QueryUtils.createCurrentListAlertDialogMessage(this, QueryUtils.sEarthquakesListInformationValues),
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
        bundle.putBoolean(EarthquakeDetailsActivity.EXTRA_IS_FAVORITES_ACTIVITY_CALLING, false);
        intent.putExtra(EarthquakeDetailsActivity.EXTRA_BUNDLE_KEY, bundle);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Pair<View, String> pair1 = Pair.create(magnitudeCircleView, magnitudeCircleView.getTransitionName());
            Pair<View, String> pair2 = Pair.create(magnitudeTextView, magnitudeTextView.getTransitionName());
            Pair<View, String> pair3 = Pair.create(locationOffsetTextView, locationOffsetTextView.getTransitionName());
            Pair<View, String> pair4 = Pair.create(locationPrimaryTextView, locationPrimaryTextView.getTransitionName());
            Pair<View, String> pair5 = Pair.create(dateTextView, dateTextView.getTransitionName());
            Pair<View, String> pair6 = Pair.create(timeTextView, timeTextView.getTransitionName());

            ActivityOptionsCompat activityOptionsCompat;
            if (earthquake.getDistance() == QueryUtils.DISTANCE_NULL_VALUE) {
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

                        if (QueryUtils.getShowDistanceSearchPreference(MainActivity.this)) {
                            sharedElements.put(getString(R.string.activity_earthquake_details_distance_text_view_transition),
                                    selectedViewHolder.itemView.
                                    findViewById(R.id.earthquake_list_item_distance_text_view));
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

        if (QueryUtils.getSoundSearchPreference(this) && QueryUtils.sIsPlayingSound) {
            playEarthquakeSound();
        }

        if (QueryUtils.isLocationPermissionGranted(this)
                && QueryUtils.getMaxDistanceSearchPreference(this) != 0) {
            QueryUtils.updateLastKnowLocation(this);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            QueryUtils.sListWillBeLoadedAfterEmpty = true;
            QueryUtils.sSearchingForEarthquakes = true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
