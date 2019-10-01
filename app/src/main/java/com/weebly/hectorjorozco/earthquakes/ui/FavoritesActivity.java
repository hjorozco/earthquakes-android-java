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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.stetho.Stetho;
import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.weebly.hectorjorozco.earthquakes.R;
import com.weebly.hectorjorozco.earthquakes.adapters.EarthquakesListAdapter;
import com.weebly.hectorjorozco.earthquakes.database.AppDatabase;
import com.weebly.hectorjorozco.earthquakes.executors.AppExecutors;
import com.weebly.hectorjorozco.earthquakes.models.Earthquake;
import com.weebly.hectorjorozco.earthquakes.ui.dialogfragments.ConfirmationDialogFragment;
import com.weebly.hectorjorozco.earthquakes.ui.recyclerviewfastscroller.RecyclerViewFastScrollerViewProvider;
import com.weebly.hectorjorozco.earthquakes.viewmodels.FavoritesActivityViewModel;

import java.util.List;
import java.util.Map;


public class FavoritesActivity extends AppCompatActivity implements
        EarthquakesListAdapter.EarthquakesListClickListener, ConfirmationDialogFragment.ConfirmationDialogFragmentListener {

    public static final int UPPER_LIMIT_TO_NOT_SHOW_FAST_SCROLLING = 50;

    private static final String EARTHQUAKE_RECYCLER_VIEW_POSITION_KEY = "EARTHQUAKE_RECYCLER_VIEW_POSITION_KEY";

    private EarthquakesListAdapter mEarthquakesListAdapter;
    private RecyclerView mRecyclerView;
    private TextView mMessageTextView;
    private ProgressBar mProgressBar;
    private int mNumberOfEarthquakesOnList;
    private FastScroller mRecyclerViewFastScroller;
    private int mEarthquakeRecyclerViewPosition;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_favorites);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // To restore animation on orientation change
        if (savedInstanceState != null) {
            mEarthquakeRecyclerViewPosition = savedInstanceState.getInt(EARTHQUAKE_RECYCLER_VIEW_POSITION_KEY, 0);
        }

        // Initialize Stetho.
        Stetho.initializeWithDefaults(this);

        mEarthquakesListAdapter = new EarthquakesListAdapter(this, this,
                true);
        mMessageTextView = findViewById(R.id.activity_favorites_message_text_view);
        mProgressBar = findViewById(R.id.activity_favorites_progress_bar);

        setupRecyclerView();

        setupViewModel();

        setupTransitions();
    }


    private void setupRecyclerView() {

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recycler_view_divider));

        mRecyclerView = findViewById(R.id.activity_favorites_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mEarthquakesListAdapter);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setNestedScrollingEnabled(true);

        // Sets up the fast scroller
        mRecyclerViewFastScroller = findViewById(R.id.activity_favorites_recycler_view_fast_scroller);
        RecyclerViewFastScrollerViewProvider viewProvider = new RecyclerViewFastScrollerViewProvider();
        mRecyclerViewFastScroller.setRecyclerView(mRecyclerView);
        mRecyclerViewFastScroller.setViewProvider(viewProvider);

    }


    private void setupViewModel() {

        FavoritesActivityViewModel mFavoritesActivityViewModel = new ViewModelProvider(this).get(FavoritesActivityViewModel.class);
        mFavoritesActivityViewModel.getFavoriteEarthquakes().observe(this, earthquakes -> {

            mNumberOfEarthquakesOnList = 0;
            boolean deleteMenuItemStatus;

            // If the list of favorite earthquakes was empty before loading from the db
            if (earthquakes == null || earthquakes.size() == 0) {
                setMessageVisible(true);
                mMessageTextView.setText(R.string.activity_favorites_no_favorites_message);
                deleteMenuItemStatus = false;
            } else {
                // If one or more earthquakes were found
                setMessageVisible(false);
                mNumberOfEarthquakesOnList = earthquakes.size();
                mEarthquakesListAdapter.setEarthquakesListData(earthquakes);
                deleteMenuItemStatus = true;
            }

            if (mMenu != null) {
                mMenu.findItem(R.id.menu_activity_favorites_action_delete).setEnabled(deleteMenuItemStatus);
            }

        });

    }


    // Helper method that shows the message and hides the RecyclerView and vice versa.
    private void setMessageVisible(boolean showMessage) {
        if (showMessage) {
            mRecyclerView.setVisibility(View.GONE);
            mMessageTextView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mMessageTextView.setVisibility(View.GONE);
            if (mNumberOfEarthquakesOnList > UPPER_LIMIT_TO_NOT_SHOW_FAST_SCROLLING) {
                mRecyclerViewFastScroller.setVisibility(View.VISIBLE);
            } else {
                mRecyclerViewFastScroller.setVisibility(View.INVISIBLE);
            }
        }
        mProgressBar.setVisibility(View.GONE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_favorites, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);

        mMenu = menu;

        if (mNumberOfEarthquakesOnList==0){
            mMenu.findItem(R.id.menu_activity_favorites_action_delete).setEnabled(false);
        } else {
            mMenu.findItem(R.id.menu_activity_favorites_action_delete).setEnabled(true);
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_activity_favorites_action_sort:
                // selectRefreshOrStopAction();
                break;
            case R.id.menu_activity_favorites_action_delete:
                showDeleteAllFavoritesConfirmationDialogFragment();
                break;
            case R.id.menu_activity_favorites_action_help:
                // showFavorites();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    // Helper method that show a DialogFragment that lets the user confirm if he wants to delete
    // all favorites
    private void showDeleteAllFavoritesConfirmationDialogFragment() {
        ConfirmationDialogFragment confirmationDialogFragment =
                ConfirmationDialogFragment.newInstance(
                        Html.fromHtml(getString(R.string.activity_favorites_delete_all_confirmation_dialog_fragment_text)),
                        getString(R.string.activity_favorites_delete_all_confirmation_dialog_fragment_title),
                        0,
                        ConfirmationDialogFragment.FAVORITES_ACTIVITY_DELETE_ALL_FAVORITES);

        confirmationDialogFragment.show(getSupportFragmentManager(),
                getString(R.string.activity_favorites_delete_all_confirmation_dialog_fragment_tag));
    }


    /**
     * Implementation of EarthquakesListAdapter.EarthquakesListClickListener
     * When the title of the list is clicked.
     */
    @Override
    public void onTitleClick() {
        // Nothing to do on favorites activity.
    }

    /**
     * Implementation of EarthquakesListAdapter.EarthquakesListClickListener
     * When an earthquake on the list is clicked show a new activity with details of it.
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
     * Implementation of ConfirmationDialogFragment.ConfirmationDialogFragmentListener interface
     *
     * @param answerYes
     * @param itemToDeletePosition
     * @param caller
     */
    @Override
    public void onConfirmation(boolean answerYes, int itemToDeletePosition, byte caller) {

        AppDatabase appDatabase = AppDatabase.getInstance(this);

        switch (caller) {
            case ConfirmationDialogFragment.FAVORITES_ACTIVITY_DELETE_ONE_FAVORITE:
                break;
            case ConfirmationDialogFragment.FAVORITES_ACTIVITY_DELETE_SOME_FAVORITES:
                break;
            case ConfirmationDialogFragment.FAVORITES_ACTIVITY_DELETE_ALL_FAVORITES:
                if (answerYes) {
                    // Delete all tables from the database
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            appDatabase.earthquakeDao().deleteAllFavorites();
                        }
                    });
                }
                break;
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
        outState.putInt(EARTHQUAKE_RECYCLER_VIEW_POSITION_KEY, mEarthquakeRecyclerViewPosition);
    }

}
