package com.weebly.hectorjorozco.earthquakes.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;
import androidx.lifecycle.ViewModelProviders;
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


public class MainActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Sets style back to normal after splash image
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Stetho.
        Stetho.initializeWithDefaults(this);

        mEarthquakesListAdapter = new EarthquakesListAdapter(this);
        mMessageTextView = findViewById(R.id.activity_main_message_text_view);
        mProgressBar = findViewById(R.id.activity_main_progress_bar);

        setMessage(QueryUtils.SEARCHING);

        setupRecyclerView();

        setupViewModel();
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
        mMainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
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


    // TODO Modifity to give only 1000 earthquakes
    private void setEarthquakesListForMapsActivity(List<Earthquake> earthquakes){

        if (earthquakes.size()> MAX_NUMBER_OF_EARTHQUAKES_FOR_MAP){
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
        }

        return super.onOptionsItemSelected(item);
    }


    private void selectRefreshOrStopAction(){
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


    private void showEarthquakesMap(){
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

}
