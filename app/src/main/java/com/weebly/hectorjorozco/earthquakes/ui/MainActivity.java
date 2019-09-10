package com.weebly.hectorjorozco.earthquakes.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.app.SharedElementCallback;
import androidx.core.util.Pair;
import androidx.core.view.MenuCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.stetho.Stetho;
import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.google.android.material.snackbar.Snackbar;
import com.weebly.hectorjorozco.earthquakes.R;
import com.weebly.hectorjorozco.earthquakes.adapters.EarthquakesListAdapter;
import com.weebly.hectorjorozco.earthquakes.models.Earthquake;
import com.weebly.hectorjorozco.earthquakes.ui.RecyclerViewFastScroller.RecyclerViewFastScrollerViewProvider;
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

    private EarthquakesListAdapter mEarthquakesListAdapter;
    private RecyclerView mRecyclerView;
    private MainActivityViewModel mMainActivityViewModel;
    private TextView mMessageTextView;
    private ProgressBar mProgressBar;
    private Menu mMenu;
    private int mNumberOfEarthquakesOnList;
    private FastScroller mRecyclerViewFastScroller;
    private int mEarthquakeRecyclerViewPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Sets style back to normal after splash image
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);

        // Workaround for orientation change issue
        if (savedInstanceState != null) {
            mEarthquakeRecyclerViewPosition = savedInstanceState.getInt("key", 0);
        }

        setContentView(R.layout.activity_main);

        // Initialize Stetho.
        Stetho.initializeWithDefaults(this);

        mEarthquakesListAdapter = new EarthquakesListAdapter(this, this);
        mMessageTextView = findViewById(R.id.activity_main_message_text_view);
        mProgressBar = findViewById(R.id.activity_main_progress_bar);

        setMessage(QueryUtils.SEARCHING);

        setupRecyclerView();

        setupViewModel();

        setupTransitions();
    }


    private void setupRecyclerView() {

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recycler_view_divider));

        mRecyclerView = findViewById(R.id.activity_main_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mEarthquakesListAdapter);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
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
                    }
                    QueryUtils.sListWillBeLoadedAfterEmpty = true;
                } else {

                    // If one or more earthquakes were found
                    mNumberOfEarthquakesOnList = earthquakes.size();
                    setMessageVisible(false);

                    // If there were new earthquakes displayed
                    if (QueryUtils.sLoadEarthquakesResultCode == QueryUtils.SEARCH_RESULT_NON_NULL) {
                        setEarthquakesListForMapsActivity(earthquakes);
                        setEarthquakesListInformationValues(earthquakes.get(0),
                                earthquakes.get(earthquakes.size() - 1));
                    }

                    mEarthquakesListAdapter.setEarthquakesListData(earthquakes);
                    mEarthquakesListAdapter.setLocation(QueryUtils.sEarthquakesListInformationValues.getLocation());

                    // If the search has finished and no previous snack has been shown
                    if (!QueryUtils.sSearchingForEarthquakes && QueryUtils.sLoadEarthquakesResultCode != QueryUtils.NO_ACTION
                            && !QueryUtils.sListWillBeLoadedAfterEmpty) {
                        Snackbar.make(findViewById(android.R.id.content),
                                getSnackBarText(QueryUtils.sLoadEarthquakesResultCode),
                                Snackbar.LENGTH_LONG).show();
                        if (mMenu != null) {
                            showListInformationAndEarthquakesMapMenuItems(true);
                        }
                    }

                    // Flag used when activity is recreated to indicate that no action is tacking place
                    QueryUtils.sLoadEarthquakesResultCode = QueryUtils.NO_ACTION;

                    QueryUtils.sListWillBeLoadedAfterEmpty = false;

                    if (mMenu != null && QueryUtils.sOneOrMoreEarthquakesFoundByRetrofitQuery) {
                        showListInformationAndEarthquakesMapMenuItems(true);
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
                imageID = R.drawable.ic_message_searching_earthquakes;
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
            mMessageTextView.setCompoundDrawablesWithIntrinsicBounds(0, imageID, 0, 0);
            mMessageTextView.setText(getString(textID));
        }
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
            mMessageTextView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            if (mNumberOfEarthquakesOnList > UPPER_LIMIT_TO_NOT_SHOW_FAST_SCROLLING) {
                mRecyclerViewFastScroller.setVisibility(View.VISIBLE);
            } else {
                mRecyclerViewFastScroller.setVisibility(View.INVISIBLE);
            }
            mMessageTextView.setVisibility(View.GONE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        mMenu = menu;

        if (QueryUtils.sOneOrMoreEarthquakesFoundByRetrofitQuery) {
            showListInformationAndEarthquakesMapMenuItems(true);
        } else {
            showListInformationAndEarthquakesMapMenuItems(false);
        }

        if (!QueryUtils.sSearchingForEarthquakes) {
            setupRefreshMenuItem(true);
        } else {
            showListInformationAndEarthquakesMapMenuItems(false);
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
                showSearchPreferences();
                break;
            case R.id.menu_activity_main_action_earthquakes_map:
                showEarthquakesMap();
                break;
            case R.id.menu_activity_main_action_list_information:
                showEarthquakesListInformation();
                break;
            case R.id.menu_activity_main_action_glossary:
                startActivity(new Intent(this, GlossaryActivity.class));
                break;
            case R.id.menu_activity_main_action_help:
                break;
            case R.id.menu_activity_main_action_about:
        }

        return super.onOptionsItemSelected(item);
    }


    private void selectRefreshOrStopAction() {
        if (!QueryUtils.sSearchingForEarthquakes) {
            doRefreshActions();
        } else {
            mMainActivityViewModel.cancelRetrofitRequest();
        }
    }

    private void showSearchPreferences() {
        Intent intent = new Intent(this, SearchPreferencesActivity.class);
        startActivity(intent);
    }


    private void showEarthquakesMap() {
        Intent earthquakesMapIntent = new Intent(this, EarthquakesMapActivity.class);
        startActivity(earthquakesMapIntent);
    }


    private void showEarthquakesListInformation() {
        MessageDialogFragment messageDialogFragment =
                MessageDialogFragment.newInstance(
                        QueryUtils.createCurrentListAlertDialogMessage(this, QueryUtils.sEarthquakesListInformationValues),
                        getString(R.string.menu_activity_main_action_list_information_title));

        messageDialogFragment.show(getSupportFragmentManager(),
                getString(R.string.activity_main_earthquakes_list_information_dialog_fragment_tag));
    }


    private void doRefreshActions() {
        // Show stop menu item
        setupRefreshMenuItem(false);
        mProgressBar.setVisibility(View.VISIBLE);
        showListInformationAndEarthquakesMapMenuItems(false);
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


    private void showListInformationAndEarthquakesMapMenuItems(boolean show) {
        mMenu.findItem(R.id.menu_activity_main_action_list_information).setVisible(show);
        mMenu.findItem(R.id.menu_activity_main_action_earthquakes_map).setVisible(show);
    }

    /**
     * Implementation of EarthquakesListAdapter.EarthquakesListClickListener
     * When the title of the list is clicked.
     */
    @Override
    public void onTitleClick() {
        showEarthquakesListInformation();
    }

    /**
     * Implementation of EarthquakesListAdapter.EarthquakesListClickListener
     * When an earthquake on the list is clicked show a new activity whith details of it.
     * Implement a transition if Android version is 21 or grater.
     */
    @Override
    public void onEarthquakeClick(Earthquake earthquake, int earthquakeRecyclerViewPosition,
                                  TextView magnitudeTextView, TextView locationOffsetTextView,
                                  TextView locationPrimaryTextView, TextView dateTextView) {

        mEarthquakeRecyclerViewPosition = earthquakeRecyclerViewPosition;


        Intent intent = new Intent(this, EarthquakeDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(EarthquakeDetailsActivity.EXTRA_EARTHQUAKE, earthquake);
        intent.putExtra(EarthquakeDetailsActivity.EXTRA_BUNDLE_KEY, bundle);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Pair<View, String> pair1 = Pair.create(magnitudeTextView, magnitudeTextView.getTransitionName());
            Pair<View, String> pair2 = Pair.create(locationOffsetTextView, locationOffsetTextView.getTransitionName());
            Pair<View, String> pair3 = Pair.create(locationPrimaryTextView, locationPrimaryTextView.getTransitionName());
            Pair<View, String> pair4 = Pair.create(dateTextView, dateTextView.getTransitionName());
            ActivityOptionsCompat activityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this, pair1, pair2, pair3, pair4);
            startActivity(intent, activityOptionsCompat.toBundle());
        } else {
            startActivity(intent);
        }
    }


    /**
     * Map the shared element names to the RecyclerView ViewHolder Views. (works only for visible RecyclerView elements).
     * Used to restore exit transitions on rotation.
     */
    private void setupTransitions() {
        setExitSharedElementCallback(
                new SharedElementCallback() {
                    @Override
                    public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {

                        RecyclerView.ViewHolder selectedViewHolder = mRecyclerView
                                .findViewHolderForAdapterPosition(mEarthquakeRecyclerViewPosition);

                        if (selectedViewHolder == null) return;

                        sharedElements.put(names.get(0), selectedViewHolder.itemView.
                                findViewById(R.id.earthquake_list_item_magnitude_text_view));
                        sharedElements.put(names.get(1), selectedViewHolder.itemView.
                                findViewById(R.id.earthquake_list_item_location_offset_text_view));
                        sharedElements.put(names.get(2), selectedViewHolder.itemView.
                                findViewById(R.id.earthquake_list_item_location_primary_text_view));
                        sharedElements
                                .put(names.get(3), selectedViewHolder.itemView.findViewById(R.id.earthquake_list_item_date_text_view));

                    }
                });
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("key", mEarthquakeRecyclerViewPosition);
    }
}
