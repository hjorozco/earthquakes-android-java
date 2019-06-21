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
import com.google.android.material.snackbar.Snackbar;
import com.weebly.hectorjorozco.earthquakes.R;
import com.weebly.hectorjorozco.earthquakes.adapters.EarthquakesListAdapter;
import com.weebly.hectorjorozco.earthquakes.utils.Utils;
import com.weebly.hectorjorozco.earthquakes.viewmodels.MainActivityViewModel;


public class MainActivity extends AppCompatActivity {

    public static final int MAX_NUMBER_OF_EARTHQUAKES_LIMIT = 20000;

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

            // If the list of earthquakes was empty before the search
            if (earthquakes == null) {
                // If the search has finished display a message with an icon
                if (!Utils.sSearchingForEarthquakes) {
                    setMessageVisible(true);
                    setMessage(Utils.sLoadEarthquakesResultCode);
                    enableRefresh();
                } else {
                    // If the search has not finished show the refreshing icon
                    mSwipeRefreshLayout.setRefreshing(true);
                }
            } else {
                if (earthquakes.size() == 0) {
                    // If the search has finished display a message with an icon
                    if (!Utils.sSearchingForEarthquakes) {
                        setMessageVisible(true);
                        if (Utils.sLoadEarthquakesResultCode == Utils.NO_INTERNET_CONNECTION ||
                                Utils.sLoadEarthquakesResultCode == Utils.SEARCH_CANCELLED ||
                                Utils.sLoadEarthquakesResultCode == Utils.SEARCH_RESULT_NULL) {
                            setMessage(Utils.sLoadEarthquakesResultCode);
                        } else {
                            setMessage(Utils.NO_EARTHQUAKES_FOUND);
                        }
                    }
                } else {
                    setMessageVisible(false);
                    mEarthquakesListAdapter.setEarthquakesListData(earthquakes);
                    // If the search has finished and no previous snack has been shown
                    if (!Utils.sSearchingForEarthquakes && Utils.sLoadEarthquakesResultCode!=Utils.NO_ACTION){
                        Snackbar.make(findViewById(android.R.id.content),
                                getSnackBarText(Utils.sLoadEarthquakesResultCode),
                                Snackbar.LENGTH_LONG).show();
                    }
                    Utils.sLoadEarthquakesResultCode=Utils.NO_ACTION;
                }

                // This is used when this observer on changed method is called after a screen rotation.
                checkForSearchingStatusToEnableRefresh();
            }

            mSwipeRefreshLayout.setEnabled(true);

            // Hide the progress bar. It is shown only when the app starts running.
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
    private void setMessage(byte type) {
        int imageID = 0;
        int textID = 0;
        switch (type) {
            case Utils.SEARCHING:
                imageID = R.drawable.ic_message_searching_earthquakes;
                textID = R.string.searching_earthquakes_text;
                break;
            case Utils.NO_INTERNET_CONNECTION:
                imageID = R.drawable.ic_message_no_internet;
                textID = R.string.no_internet_connection_text;
                break;
            case Utils.SEARCH_CANCELLED:
                imageID = R.drawable.ic_message_searching_cancelled;
                textID = R.string.search_cancelled_text;
                break;
            case Utils.NO_EARTHQUAKES_FOUND:
                imageID = R.drawable.ic_message_no_earthquakes;
                textID = R.string.no_earthquakes_found_text;
                break;
            case Utils.SEARCH_RESULT_NULL:
                imageID = R.drawable.ic_message_no_server_response;
                textID = R.string.no_server_response_text;
                break;
        }
        mMessageTextView.setCompoundDrawablesWithIntrinsicBounds(0, imageID, 0, 0);
        mMessageTextView.setText(getString(textID));
    }


    private String getSnackBarText(byte type) {
        String snackBarText = null;
        switch (type) {
            case Utils.NO_INTERNET_CONNECTION:
                snackBarText = getString(R.string.no_internet_connection_text);
                break;
            case Utils.SEARCH_CANCELLED:
                snackBarText = getString(R.string.search_cancelled_text);
                break;
            case Utils.SEARCH_RESULT_NULL:
                snackBarText = getString(R.string.no_server_response_text);
                break;
            case Utils.SEARCH_RESULT_NON_NULL:
                snackBarText = getString(R.string.earthquakes_list_updated_text);
                break;
        }
        return snackBarText;
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
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        mMenu = menu;
        // If the app is not searching for earthquakes show the refresh menu item.
        if (!Utils.sSearchingForEarthquakes) {
            setupRefreshMenuItem(true);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_activity_main_action_refresh:
                if (!Utils.sSearchingForEarthquakes) {
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
        // Show stop menu item
        setupRefreshMenuItem(false);
        mSwipeRefreshLayout.setRefreshing(true);
        Utils.sSearchingForEarthquakes = true;
        setMessage(Utils.SEARCHING);
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

    private void checkForSearchingStatusToEnableRefresh() {
        if (!Utils.sSearchingForEarthquakes) {
            enableRefresh();
        } else {
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }

}
