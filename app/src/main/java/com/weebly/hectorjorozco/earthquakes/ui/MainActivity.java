package com.weebly.hectorjorozco.earthquakes.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.stetho.Stetho;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.weebly.hectorjorozco.earthquakes.R;
import com.weebly.hectorjorozco.earthquakes.adapters.EarthquakesListAdapter;
import com.weebly.hectorjorozco.earthquakes.utils.QueryUtils;
import com.weebly.hectorjorozco.earthquakes.viewmodels.MainActivityViewModel;


public class MainActivity extends AppCompatActivity {

    public static final int MAX_NUMBER_OF_EARTHQUAKES_LIMIT = 20000;

    private static final byte SEARCHING_MESSAGE_REFRESH = 0;
    private static final byte NO_INTERNET_MESSAGE = 1;
    private static final byte NO_EARTHQUAKES_MESSAGE = 2;

    private EarthquakesListAdapter mEarthquakesListAdapter;
    private RecyclerView mRecyclerView;
    private MainActivityViewModel mMainActivityViewModel;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RelativeLayout mMessageRelativeLayout;
    private TextView mMessageTextView;
    private ProgressBar mProgressBar;
    private Menu mMenu;
    private FloatingActionButton mFloatingActionButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Sets style back to normal after splash image
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Stetho.
        Stetho.initializeWithDefaults(this);

        mEarthquakesListAdapter = new EarthquakesListAdapter(this);
        mMessageRelativeLayout = findViewById(R.id.activity_main_message_relative_layout);
        mMessageTextView = findViewById(R.id.activity_main_message_text_view);
        mProgressBar = findViewById(R.id.activity_main_progress_bar);
        mFloatingActionButton = findViewById(R.id.activity_main_fab);

        setupSwipeRefreshLayout();

        setupRecyclerView();

        setupViewModel();

        mFloatingActionButton.setOnClickListener(v -> mRecyclerView.scrollToPosition(0));
    }

    private void setupRecyclerView() {

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recycler_view_divider));

        mRecyclerView = findViewById(R.id.activity_main_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mEarthquakesListAdapter);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setNestedScrollingEnabled(true);
    }


    private void setupViewModel() {
        mMainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        mMainActivityViewModel.getEarthquakes().observe(this, earthquakes -> {

            Log.d("TESTING", "OnChanged");

            if (earthquakes == null) {
                if (!QueryUtils.searchingForEarthquakes) {
                    setMessageVisible(true);
                    setMessage(NO_INTERNET_MESSAGE);
                    enableRefresh();
                    Log.d("TESTING", "No connection to USGS server");
                } else {
                    checkForEarthquakesFetchedToEnableRefresh();
                }
            } else {
                if (earthquakes.size() == 0) {
                    if (!QueryUtils.searchingForEarthquakes) {
                        setMessageVisible(true);
                        setMessage(NO_EARTHQUAKES_MESSAGE);
                        Log.d("TESTING", "No earthquakes found");
                    }
                } else {
                    setMessageVisible(false);
                    mEarthquakesListAdapter.setEarthquakesListData(earthquakes);
                    Log.d("TESTING", "Earthquake adapter updated with earthquakes list");
                }
                checkForEarthquakesFetchedToEnableRefresh();
            }

            mSwipeRefreshLayout.setEnabled(true);
            mProgressBar.setVisibility(View.INVISIBLE);
        });

    }


    private void setupSwipeRefreshLayout() {
        mSwipeRefreshLayout = findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setEnabled(false);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(this::doRefreshActions);
    }


    // Helper method that sets the message image and text
    private void setMessage(byte messageType) {
        int imageID = 0;
        int textID = 0;
        switch (messageType) {
            case SEARCHING_MESSAGE_REFRESH:
                imageID = R.drawable.ic_message_searching_earthquakes;
                textID = R.string.searching_earthquakes_text;
                break;
            case NO_INTERNET_MESSAGE:
                imageID = R.drawable.ic_message_no_internet;
                textID = R.string.no_internet_connection_text;
                break;
            case NO_EARTHQUAKES_MESSAGE:
                imageID = R.drawable.ic_message_no_earthquakes;
                textID = R.string.no_earthquakes_found_text;
                break;
        }
        mMessageTextView.setCompoundDrawablesWithIntrinsicBounds(0, imageID, 0, 0);
        mMessageTextView.setText(getString(textID));
    }


    // Helper method that shows the message and hides the RecyclerView and vice versa.
    private void setMessageVisible(boolean showMessage) {
        if (showMessage) {
            mFloatingActionButton.hide();
            mRecyclerView.setVisibility(View.GONE);
            mMessageRelativeLayout.setVisibility(View.VISIBLE);
        } else {
            mFloatingActionButton.show();
            mRecyclerView.setVisibility(View.VISIBLE);
            mMessageRelativeLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);

        mMenu = menu;

        // If the Earthquakes where fetched from the USGS server or the app is not searching for
        // earthquakes enable the refresh menu item.
        if (QueryUtils.earthquakesFetched || !QueryUtils.searchingForEarthquakes) {
            setupRefreshMenuItem(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_activity_main_action_refresh:
                if (!QueryUtils.searchingForEarthquakes) {
                    doRefreshActions();
                } else {
                    mMainActivityViewModel.cancelRetrofitRequest();
                }
                break;
            case R.id.menu_activity_main_action_search_preferences:
                showSearchPreferences();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void showSearchPreferences() {
        Intent intent = new Intent(this, SearchPreferencesActivity.class);
        startActivity(intent);
    }


    private void doRefreshActions() {
        // Disable refresh menu item
        setupRefreshMenuItem(false);
        // Show refreshing icon
        mSwipeRefreshLayout.setRefreshing(true);
        // Initialize global variables
        QueryUtils.earthquakesFetched = false;
        QueryUtils.searchingForEarthquakes = true;
        // Set message to be show when the earthquakes list is empty.
        setMessage(SEARCHING_MESSAGE_REFRESH);
        // Force the ViewModel to load the earthquakes from the USGS server.
        mMainActivityViewModel.loadEarthquakes();
    }


    // Stops refreshing animation and changes the refresh menu item icon and title to refresh
    private void enableRefresh() {
        if (mMenu != null) {
            setupRefreshMenuItem(true);
        }
        mSwipeRefreshLayout.setRefreshing(false);
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

    private void checkForEarthquakesFetchedToEnableRefresh() {
        if (QueryUtils.earthquakesFetched) {
            enableRefresh();
        } else {
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }

}
