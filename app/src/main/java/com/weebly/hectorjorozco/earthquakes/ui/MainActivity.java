package com.weebly.hectorjorozco.earthquakes.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.stetho.Stetho;
import com.weebly.hectorjorozco.earthquakes.R;
import com.weebly.hectorjorozco.earthquakes.adapters.EarthquakesListAdapter;
import com.weebly.hectorjorozco.earthquakes.viewmodels.MainActivityViewModel;

public class MainActivity extends AppCompatActivity {

    public static final int MAX_NUMBER_OF_EARTHQUAKES_LIMIT = 20000;

    private EarthquakesListAdapter mEarthquakesListAdapter;
    private RecyclerView mRecyclerView;
    private MainActivityViewModel mMainActivityViewModel;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Sets style back to normal after splash image
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Stetho.
        Stetho.initializeWithDefaults(this);

        mEarthquakesListAdapter = new EarthquakesListAdapter(this);

        setupRecyclerView();

        setupViewModel();

        setupSwipeRefreshLayout();
    }


    private void setupRecyclerView() {

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recycler_view_divider));

        mRecyclerView = findViewById(R.id.activity_main_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mEarthquakesListAdapter);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setNestedScrollingEnabled(false);
    }


    private void setupViewModel(){
        mMainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        mMainActivityViewModel.getEarthquakes().observe(this, earthquakes -> {
            mEarthquakesListAdapter.setEarthquakesListData(earthquakes);
            mSwipeRefreshLayout.setRefreshing(false);
            Log.d("TESTING", "ViewModel OnChanged (set adapter with earthquakes list)");
        });
    }

    private void setupSwipeRefreshLayout(){
        mSwipeRefreshLayout = findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setRefreshing(true);
        mSwipeRefreshLayout.setOnRefreshListener(() -> mMainActivityViewModel.loadEarthquakes());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_activity_main_action_refresh:
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

}
