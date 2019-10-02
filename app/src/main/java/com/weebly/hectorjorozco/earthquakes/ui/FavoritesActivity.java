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
import androidx.fragment.app.FragmentManager;
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
import com.google.android.material.snackbar.Snackbar;
import com.weebly.hectorjorozco.earthquakes.R;
import com.weebly.hectorjorozco.earthquakes.adapters.EarthquakesListAdapter;
import com.weebly.hectorjorozco.earthquakes.database.AppDatabase;
import com.weebly.hectorjorozco.earthquakes.executors.AppExecutors;
import com.weebly.hectorjorozco.earthquakes.models.Earthquake;
import com.weebly.hectorjorozco.earthquakes.ui.dialogfragments.ConfirmationDialogFragment;
import com.weebly.hectorjorozco.earthquakes.ui.dialogfragments.SortFavoritesDialogFragment;
import com.weebly.hectorjorozco.earthquakes.ui.recyclerviewfastscroller.RecyclerViewFastScrollerViewProvider;
import com.weebly.hectorjorozco.earthquakes.utils.SortFavoritesUtils;
import com.weebly.hectorjorozco.earthquakes.viewmodels.FavoritesActivityViewModel;

import java.util.List;
import java.util.Map;


public class FavoritesActivity extends AppCompatActivity implements
        EarthquakesListAdapter.EarthquakesListClickListener,
        ConfirmationDialogFragment.ConfirmationDialogFragmentListener,
        SortFavoritesDialogFragment.SortFavoritesDialogFragmentListener {

    public static final int UPPER_LIMIT_TO_NOT_SHOW_FAST_SCROLLING = 50;

    private static final String EARTHQUAKE_RECYCLER_VIEW_POSITION_KEY = "EARTHQUAKE_RECYCLER_VIEW_POSITION_KEY";

    private EarthquakesListAdapter mAdapter;
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

        mAdapter = new EarthquakesListAdapter(this, this,
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
        mRecyclerView.setAdapter(mAdapter);
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

            // If the list of favorite earthquakes was empty before loading from the db
            if (earthquakes == null || earthquakes.size() == 0) {
                setMessageVisible(true);
                mMessageTextView.setText(R.string.activity_favorites_no_favorites_message);
            } else {
                // If one or more earthquakes were found
                setMessageVisible(false);
                mNumberOfEarthquakesOnList = earthquakes.size();
                mAdapter.setEarthquakesListData(SortFavoritesUtils.SortFavorites(this, earthquakes));
            }

            if (mMenu != null) {
                setupDeleteMenuItem();
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

        setupDeleteMenuItem();

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_activity_favorites_action_sort:
                showSortFavoritesDialogFragment();
                break;
            case R.id.menu_activity_favorites_action_delete:
                showDeleteAllFavoritesConfirmationDialogFragment();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void setupDeleteMenuItem() {
        MenuItem deleteMenuItem = mMenu.findItem(R.id.menu_activity_favorites_action_delete);
        if (mNumberOfEarthquakesOnList == 0) {
            deleteMenuItem.setEnabled(false);
            deleteMenuItem.setIcon(R.drawable.ic_delete_grey_24dp);
        } else {
            deleteMenuItem.setEnabled(true);
            deleteMenuItem.setIcon(R.drawable.ic_delete_white_24dp);
        }
    }


    // Helper method that show a DialogFragment that lets the user select how to sort favorites
    private void showSortFavoritesDialogFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        SortFavoritesDialogFragment sortFavoritesDialogFragment =
                SortFavoritesDialogFragment.newInstance(
                        getString(R.string.activity_favorites_sort_dialog_fragment_title));

        sortFavoritesDialogFragment.show(fragmentManager,
                getString(R.string.activity_favorites_sort_dialog_fragment_tag));
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
     * @param answerYes            True if the user clicked the POSITIVE button on the AlertDialog, false otherwise
     * @param itemToDeletePosition The position of the item to delete if only one is being deleted.
     * @param caller               Indicates what activity / method called the ConfirmationDialogFragment
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
                    AppExecutors.getInstance().diskIO().execute(() ->
                            appDatabase.earthquakeDao().deleteAllFavorites());
                }
                break;
        }
    }


    /**
     * Implementation of SortFavoritesDialogFragment.SortFavoritesDialogFragmentListener interface
     *
     * @param sortCriteriaSelected 0=Ascending Date, 1=Descending Date, 2=Ascending Magnitude,
     *                             3=Descending Magnitude
     */
    @Override
    public void onSortCriteriaSelected(int sortCriteriaSelected) {

        int confirmationMessageId = 0;

        switch (sortCriteriaSelected) {
            case MainActivity.SORT_BY_ASCENDING_DATE:
                confirmationMessageId = R.string.activity_favorites_sorted_by_ascending_date_text;
                break;
            case MainActivity.SORT_BY_DESCENDING_DATE:
                confirmationMessageId = R.string.activity_favorites_sorted_by_descending_date_text;
                break;
            case MainActivity.SORT_BY_ASCENDING_MAGNITUDE:
                confirmationMessageId = R.string.activity_favorites_sorted_by_ascending_magnitude_text;
                break;
            case MainActivity.SORT_BY_DESCENDING_MAGNITUDE:
                confirmationMessageId = R.string.activity_favorites_sorted_by_descending_magnitude_text;
                break;
        }

        // If the sorting criteria is different from the previous one
        if (SortFavoritesUtils.setFavoritesSortCriteriaOnSharedPreferences(this, sortCriteriaSelected)) {
            List<Earthquake> favorites = mAdapter.getEarthquakesListData();
            if (favorites != null) {
                mAdapter.setEarthquakesListData(SortFavoritesUtils.SortFavorites(this, favorites));

            }
        }

        Snackbar.make(findViewById(android.R.id.content), confirmationMessageId, Snackbar.LENGTH_LONG).show();
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
