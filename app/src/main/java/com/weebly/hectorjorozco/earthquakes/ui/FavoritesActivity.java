package com.weebly.hectorjorozco.earthquakes.ui;

import android.content.Intent;
import android.graphics.Canvas;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.app.SharedElementCallback;
import androidx.core.util.Pair;
import androidx.core.view.MenuCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.weebly.hectorjorozco.earthquakes.R;
import com.weebly.hectorjorozco.earthquakes.adapters.FavoritesListAdapter;
import com.weebly.hectorjorozco.earthquakes.database.AppDatabase;
import com.weebly.hectorjorozco.earthquakes.executors.AppExecutors;
import com.weebly.hectorjorozco.earthquakes.models.Earthquake;
import com.weebly.hectorjorozco.earthquakes.models.SparseBooleanArrayParcelable;
import com.weebly.hectorjorozco.earthquakes.ui.dialogfragments.ConfirmationDialogFragment;
import com.weebly.hectorjorozco.earthquakes.ui.dialogfragments.SortFavoritesDialogFragment;
import com.weebly.hectorjorozco.earthquakes.ui.recyclerviewfastscroller.RecyclerViewFastScrollerViewProvider;
import com.weebly.hectorjorozco.earthquakes.utils.QueryUtils;
import com.weebly.hectorjorozco.earthquakes.utils.SortFavoritesUtils;
import com.weebly.hectorjorozco.earthquakes.utils.UiUtils;
import com.weebly.hectorjorozco.earthquakes.viewmodels.FavoritesActivityViewModel;

import java.util.List;
import java.util.Map;

import static com.weebly.hectorjorozco.earthquakes.ui.MainActivity.UPPER_LIMIT_TO_NOT_SHOW_FAST_SCROLLING;


public class FavoritesActivity extends AppCompatActivity implements
        FavoritesListAdapter.FavoritesListAdapterListener,
        ConfirmationDialogFragment.ConfirmationDialogFragmentListener,
        SortFavoritesDialogFragment.SortFavoritesDialogFragmentListener {

    private static final String EARTHQUAKE_RECYCLER_VIEW_POSITION_KEY = "EARTHQUAKE_RECYCLER_VIEW_POSITION_KEY";

    // Used to restore multiple favorites selected on action mode after rotation
    private static final String SAVED_INSTANCE_STATE_SELECTED_ITEMS_KEY = "saved_instance_state_selected_items_key";

    // Used for swipe to delete restore after rotation
    private static final String SAVED_INSTANCE_STATE_FAVORITE_WITH_DELETE_BACKGROUND_POSITION_KEY =
            "saved_instance_state_favorite_with_delete_background_position_key";
    private static final String SAVED_INSTANCE_STATE_RIGHT_SWIPED_KEY =
            "saved_instance_state_right_swiped_key";

    private FavoritesListAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private TextView mMessageTextView;
    private ProgressBar mProgressBar;
    private int mNumberOfFavoritesOnList;
    private FastScroller mRecyclerViewFastScroller;
    private int mEarthquakeRecyclerViewPosition;
    private Menu mMenu;
    private DividerItemDecoration mDividerItemDecoration;
    private BottomNavigationView mBottomNavigationView;

    // Used for action mode
    private ActionModeCallback actionModeCallback;
    private ActionMode mActionMode;

    // Used to save the swiped to delete student on rotation
    private boolean mIsAskingToDeleteFavorite;
    private int mFavoriteWithDeleteBackgroundPosition;
    private boolean mRightSwipe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_favorites);

        // Initialize Stetho.
        Stetho.initializeWithDefaults(this);

        mAdapter = new FavoritesListAdapter(this, this);
        mMessageTextView = findViewById(R.id.activity_favorites_message_text_view);
        mProgressBar = findViewById(R.id.activity_favorites_progress_bar);

        actionModeCallback = new ActionModeCallback();

        mIsAskingToDeleteFavorite = false;
        mRightSwipe = false;

        setupBottomNavigationView();

        setupRecyclerView();

        setupViewModel();

        saveSharedElementsTransitions();

        if (savedInstanceState != null) {
            // To restore animation on orientation change
            mEarthquakeRecyclerViewPosition = savedInstanceState.getInt(EARTHQUAKE_RECYCLER_VIEW_POSITION_KEY, 0);

            // To restore swipe to delete background on rotation
            if (savedInstanceState.containsKey(SAVED_INSTANCE_STATE_RIGHT_SWIPED_KEY) &&
                    savedInstanceState.containsKey(SAVED_INSTANCE_STATE_FAVORITE_WITH_DELETE_BACKGROUND_POSITION_KEY)) {
                mFavoriteWithDeleteBackgroundPosition =
                        savedInstanceState.getInt(SAVED_INSTANCE_STATE_FAVORITE_WITH_DELETE_BACKGROUND_POSITION_KEY);
                mRightSwipe = savedInstanceState.getBoolean(SAVED_INSTANCE_STATE_RIGHT_SWIPED_KEY);
                mAdapter.setFavoriteWithDeleteBackgroundData(mFavoriteWithDeleteBackgroundPosition, mRightSwipe);
                mAdapter.notifyItemChanged(mFavoriteWithDeleteBackgroundPosition);
                mIsAskingToDeleteFavorite = true;
            }

            // To restore action mode on rotation
            if (savedInstanceState.containsKey(SAVED_INSTANCE_STATE_SELECTED_ITEMS_KEY)) {
                SparseBooleanArrayParcelable selectedFavorites =
                        savedInstanceState.getParcelable(SAVED_INSTANCE_STATE_SELECTED_ITEMS_KEY);
                if (selectedFavorites != null && selectedFavorites.size() > 0) {
                    mActionMode = startSupportActionMode(actionModeCallback);
                    if (mActionMode != null) {
                        String selectedWordEnding;
                        int selectedFavoritesCount = selectedFavorites.size();
                        if (selectedFavoritesCount == 1) {
                            selectedWordEnding = "";
                        } else {
                            selectedWordEnding = getString(R.string.spanish_only_plural_ending_letter_s);
                        }
                        mActionMode.setTitle(getString(R.string.activity_favorites_action_mode_toolbar_title,
                                selectedFavoritesCount, selectedWordEnding));
                    }
                    mAdapter.setSelectedFavorites(selectedFavorites);
                    mDividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recycler_view_divider_dark));
                }
            }
        }
    }


    @SuppressWarnings("SameReturnValue")
    private void setupBottomNavigationView() {
        mBottomNavigationView = findViewById(R.id.activity_favorites_bottom_navigation_view);
        mBottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.menu_activity_main_bottom_navigation_view_action_list:
                    onBackPressed();
                    break;
                case R.id.menu_activity_main_bottom_navigation_view_action_map:
                    onBackPressed();
                    startActivity(new Intent(FavoritesActivity.this, EarthquakesMapActivity.class));
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    break;
                case R.id.menu_activity_main_bottom_navigation_view_action_favorites:
                    break;
                case R.id.menu_activity_main_bottom_navigation_view_action_news:
                    break;
            }
            return true;
        });
    }


    private void setupRecyclerView() {

        mDividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        mDividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recycler_view_divider_light));

        mRecyclerView = findViewById(R.id.activity_favorites_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(mDividerItemDecoration);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setNestedScrollingEnabled(true);

        // Sets up the fast scroller
        mRecyclerViewFastScroller = findViewById(R.id.activity_favorites_recycler_view_fast_scroller);
        RecyclerViewFastScrollerViewProvider viewProvider = new RecyclerViewFastScrollerViewProvider();
        mRecyclerViewFastScroller.setRecyclerView(mRecyclerView);
        mRecyclerViewFastScroller.setViewProvider(viewProvider);

        // Add a touch helper to the RecyclerView to recognize when a user swipes to delete a student.
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull
                    RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            // When the row is being swiped set the default UI to be the foreground view.
            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                if (viewHolder != null) {
                    getDefaultUIUtil().onSelected(((FavoritesListAdapter.FavoriteViewHolder) viewHolder).viewForeground);
                }
            }

            // Sets the foreground view to be moving when swiped
            @Override
            public void onChildDrawOver(@NonNull Canvas canvas, @NonNull RecyclerView recyclerView,
                                        RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                        int actionState, boolean isCurrentlyActive) {
                getDefaultUIUtil().onDrawOver(canvas, recyclerView, ((FavoritesListAdapter.FavoriteViewHolder) viewHolder).viewForeground,
                        dX, dY, actionState, isCurrentlyActive);
            }

            // Draws the corresponding delete background based on the swipe direction
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {
                getDefaultUIUtil().onDraw(c, recyclerView, ((FavoritesListAdapter.FavoriteViewHolder) viewHolder).viewForeground,
                        dX, dY, actionState, isCurrentlyActive);

                // Right swipe
                if (dX > 0) {
                    mRightSwipe = true;
                    ((FavoritesListAdapter.FavoriteViewHolder) viewHolder).viewRightSwipeBackground.setVisibility(View.VISIBLE);
                    ((FavoritesListAdapter.FavoriteViewHolder) viewHolder).viewLeftSwipeBackground.setVisibility(View.INVISIBLE);
                } else {
                    // Left swipe
                    mRightSwipe = false;
                    ((FavoritesListAdapter.FavoriteViewHolder) viewHolder).viewRightSwipeBackground.setVisibility(View.INVISIBLE);
                    ((FavoritesListAdapter.FavoriteViewHolder) viewHolder).viewLeftSwipeBackground.setVisibility(View.VISIBLE);
                }

            }

            // If the student is not deleted sets the view again to foreground
            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                getDefaultUIUtil().clearView(((FavoritesListAdapter.FavoriteViewHolder) viewHolder).viewForeground);
            }

            // Disables swipe when Action Mode is enabled
            @Override
            public boolean isItemViewSwipeEnabled() {
                return true; // (// mActionMode == null);
            }

            // When the swipe gesture finishes ask for a delete confirmation
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                showDeleteOneFavoriteConfirmationDialogFragment(viewHolder.getAdapterPosition());
            }

        }).attachToRecyclerView(mRecyclerView);

    }


    private void setupViewModel() {

        FavoritesActivityViewModel mFavoritesActivityViewModel = new ViewModelProvider(this).get(FavoritesActivityViewModel.class);
        mFavoritesActivityViewModel.getFavoriteEarthquakes().observe(this, favorites -> {

            mNumberOfFavoritesOnList = 0;

            // If the list of favorite earthquakes was empty before loading from the db
            if (favorites == null || favorites.size() == 0) {
                setMessageVisible(true);
                mMessageTextView.setText(R.string.activity_favorites_no_favorites_message);
            } else {
                // If one or more earthquakes were found
                mNumberOfFavoritesOnList = favorites.size();
                setMessageVisible(false);
                mAdapter.setFavoritesListData(SortFavoritesUtils.SortFavorites(this, favorites));
            }

            if (mMenu != null) {
                setupMenuItems();
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
            if (mNumberOfFavoritesOnList > UPPER_LIMIT_TO_NOT_SHOW_FAST_SCROLLING) {
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

        setupMenuItems();

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


    private void setupMenuItems() {
        MenuItem deleteMenuItem = mMenu.findItem(R.id.menu_activity_favorites_action_delete);
        MenuItem sortMenuItem = mMenu.findItem(R.id.menu_activity_favorites_action_sort);
        int sortByCriteria = SortFavoritesUtils.getSortByValueFromSharedPreferences(this);

        if (mNumberOfFavoritesOnList == 0) {
            deleteMenuItem.setEnabled(false);
            deleteMenuItem.setIcon(R.drawable.ic_delete_grey_24dp);
        } else {
            deleteMenuItem.setEnabled(true);
            deleteMenuItem.setIcon(R.drawable.ic_delete_white_24dp);
        }

        if (sortByCriteria == 0 || sortByCriteria == 2) {
            sortMenuItem.setIcon(R.drawable.ic_sort_ascending_white_24dp);
        } else {
            sortMenuItem.setIcon(R.drawable.ic_sort_descending_white_24dp);
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


    private void showDeleteOneFavoriteConfirmationDialogFragment(int position) {

        mIsAskingToDeleteFavorite = true;
        mFavoriteWithDeleteBackgroundPosition = position;

        ConfirmationDialogFragment confirmationDialogFragment =
                ConfirmationDialogFragment.newInstance(
                        Html.fromHtml(getString(R.string.activity_favorites_delete_one_confirmation_dialog_fragment_text)),
                        getString(R.string.activity_favorites_delete_text),
                        position,
                        ConfirmationDialogFragment.FAVORITES_ACTIVITY_DELETE_ONE_FAVORITE);

        confirmationDialogFragment.show(getSupportFragmentManager(),
                getString(R.string.activity_favorites_delete_one_confirmation_dialog_fragment_tag));

    }


    private void showDeleteSomeFavoritesConfirmationDialogFragment() {

        ConfirmationDialogFragment confirmationDialogFragment =
                ConfirmationDialogFragment.newInstance(
                        Html.fromHtml(getString(
                                R.string.activity_favorites_delete_some_confirmation_dialog_fragment_text,
                                mAdapter.getSelectedFavoritesPositions().size(),
                                getPluralEnding(mAdapter.getSelectedFavoritesCount(), false))),
                        getString(R.string.activity_favorites_delete_text),
                        0, ConfirmationDialogFragment.FAVORITES_ACTIVITY_DELETE_SOME_FAVORITES);

        confirmationDialogFragment.show(getSupportFragmentManager(),
                getString(R.string.activity_favorites_delete_some_confirmation_dialog_fragment_tag));
    }


    // Helper method that show a DialogFragment that lets the user confirm if he wants to delete
    // all favorites
    private void showDeleteAllFavoritesConfirmationDialogFragment() {
        ConfirmationDialogFragment confirmationDialogFragment =
                ConfirmationDialogFragment.newInstance(
                        Html.fromHtml(getString(R.string.activity_favorites_delete_all_confirmation_dialog_fragment_text)),
                        getString(R.string.activity_favorites_delete_all_text),
                        0,
                        ConfirmationDialogFragment.FAVORITES_ACTIVITY_DELETE_ALL_FAVORITES);

        confirmationDialogFragment.show(getSupportFragmentManager(),
                getString(R.string.activity_favorites_delete_all_confirmation_dialog_fragment_tag));
    }


    private String getPluralEnding(int numberOfItems, boolean spanishOnly) {
        String pluralEnding;
        if (numberOfItems == 1) {
            pluralEnding = "";
        } else {
            if (spanishOnly) {
                pluralEnding = getString(R.string.spanish_only_plural_ending_letter_s);
            } else {
                pluralEnding = getString(R.string.letter_s_lowercase);
            }

        }
        return pluralEnding;
    }


    /**
     * Implementation of FavoritesListAdapter.FavoritesListAdapterListener
     * When a favorite on the list is clicked show a new activity with details of it.
     * Implement a transition if Android version is 21 or grater.
     */
    @Override
    public void onFavoriteClick(Earthquake favorite, int favoriteRecyclerViewPosition,
                                View magnitudeCircleView,
                                TextView magnitudeTextView, TextView locationOffsetTextView,
                                TextView locationPrimaryTextView, TextView distanceTextView,
                                TextView dateTextView, TextView timeTextView) {

        // If there are favorites selected (action mode is enabled) set up action mode
        if (mAdapter.getSelectedFavoritesCount() > 0) {
            setupItemForActionMode(favoriteRecyclerViewPosition);
        } else {
            // If not, show EarthquakeDetails activity.
            mEarthquakeRecyclerViewPosition = favoriteRecyclerViewPosition;
            Intent intent = new Intent(this, EarthquakeDetailsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable(EarthquakeDetailsActivity.EXTRA_EARTHQUAKE, favorite);
            bundle.putByte(EarthquakeDetailsActivity.EXTRA_CALLER, EarthquakeDetailsActivity.FAVORITES_ACTIVITY_CALLER);
            intent.putExtra(EarthquakeDetailsActivity.EXTRA_BUNDLE_KEY, bundle);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                Pair<View, String> pair1 = Pair.create(magnitudeCircleView, magnitudeCircleView.getTransitionName());
                Pair<View, String> pair2 = Pair.create(magnitudeTextView, magnitudeTextView.getTransitionName());
                Pair<View, String> pair3 = Pair.create(locationOffsetTextView, locationOffsetTextView.getTransitionName());
                Pair<View, String> pair4 = Pair.create(locationPrimaryTextView, locationPrimaryTextView.getTransitionName());
                Pair<View, String> pair5 = Pair.create(dateTextView, dateTextView.getTransitionName());
                Pair<View, String> pair6 = Pair.create(timeTextView, timeTextView.getTransitionName());

                ActivityOptionsCompat activityOptionsCompat;
                if (favorite.getDistance() == QueryUtils.DISTANCE_NULL_VALUE) {
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
    }


    // Implementation of FavoritesListAdapter.FavoritesListAdapterListener
    @Override
    public void onFavoriteLongClick(int favoriteRecyclerViewPosition) {
        setupItemForActionMode(favoriteRecyclerViewPosition);
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
                mIsAskingToDeleteFavorite = false;
                mAdapter.setFavoriteWithDeleteBackgroundData(-1, false);
                if (answerYes) {

                    // Delete favorite from the favorites table
                    Earthquake favoriteToDelete = mAdapter.getFavorite(itemToDeletePosition - 1);
                    AppExecutors.getInstance().diskIO().execute(() ->
                            appDatabase.earthquakeDao().deleteFavoriteEarthquake(favoriteToDelete));

                    // Delete the student from the adapter
                    mAdapter.removeFavorite(itemToDeletePosition - 1);

                    showSnackBarMessage(getString(R.string.activity_favorites_one_favorite_deleted_message));

                } else {
                    mAdapter.notifyItemChanged(itemToDeletePosition);
                }
                break;
            case ConfirmationDialogFragment.FAVORITES_ACTIVITY_DELETE_SOME_FAVORITES:
                if (answerYes) {
                    List<Integer> selectedItemPositions = mAdapter.getSelectedFavoritesPositions();
                    int numberOfDeletedFavorites = mAdapter.getSelectedFavoritesCount();
                    for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
                        final Earthquake favoriteToDelete = mAdapter.getFavorite(selectedItemPositions.get(i) - 1);

                        // Delete the student from the adapter
                        mAdapter.removeFavorite(selectedItemPositions.get(i) - 1);

                        // Delete student from the students table
                        AppExecutors.getInstance().diskIO().execute(() ->
                                appDatabase.earthquakeDao().deleteFavoriteEarthquake(favoriteToDelete));
                    }
                    mActionMode.finish();
                    showSnackBarMessage(getString(R.string.activity_favorites_some_favorites_deleted_message,
                            selectedItemPositions.size(),
                            getPluralEnding(numberOfDeletedFavorites, false),
                            getPluralEnding(numberOfDeletedFavorites, true)));
                }
                break;
            case ConfirmationDialogFragment.FAVORITES_ACTIVITY_DELETE_ALL_FAVORITES:
                if (answerYes) {
                    // Delete all tables from the database
                    AppExecutors.getInstance().diskIO().execute(() ->
                            appDatabase.earthquakeDao().deleteAllFavorites());

                    showSnackBarMessage(getString(R.string.activity_favorites_all_favorites_deleted_message));
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

        String sortedByMessage = "";
        int sortByMenuItemIcon = 0;

        switch (sortCriteriaSelected) {
            case MainActivity.SORT_BY_ASCENDING_DATE:
                sortedByMessage = getString(R.string.activity_favorites_sorted_by_ascending_date_text);
                sortByMenuItemIcon = R.drawable.ic_sort_ascending_white_24dp;
                break;
            case MainActivity.SORT_BY_DESCENDING_DATE:
                sortedByMessage = getString(R.string.activity_favorites_sorted_by_descending_date_text);
                sortByMenuItemIcon = R.drawable.ic_sort_descending_white_24dp;
                break;
            case MainActivity.SORT_BY_ASCENDING_MAGNITUDE:
                sortedByMessage = getString(R.string.activity_favorites_sorted_by_ascending_magnitude_text);
                sortByMenuItemIcon = R.drawable.ic_sort_ascending_white_24dp;
                break;
            case MainActivity.SORT_BY_DESCENDING_MAGNITUDE:
                sortedByMessage = getString(R.string.activity_favorites_sorted_by_descending_magnitude_text);
                sortByMenuItemIcon = R.drawable.ic_sort_descending_white_24dp;
                break;
        }

        // If the sorting criteria is different from the previous one
        if (SortFavoritesUtils.setFavoritesSortCriteriaOnSharedPreferences(this, sortCriteriaSelected)) {
            List<Earthquake> favorites = mAdapter.getFavoritesListData();
            if (favorites != null) {
                mAdapter.setFavoritesListData(SortFavoritesUtils.SortFavorites(this, favorites));

            }
            mMenu.findItem(R.id.menu_activity_favorites_action_sort).setIcon(sortByMenuItemIcon);
            mMessageTextView.setText(R.string.activity_favorites_no_favorites_message);
            // If some earthquakes were sorted show a snackbar message
            if (mNumberOfFavoritesOnList > 1) {
                showSnackBarMessage(
                        getString(R.string.activity_favorites_sorted_by_snack_text, sortedByMessage));
            }
        }
    }


    private void showSnackBarMessage(String message) {
        Snackbar.make(findViewById(R.id.activity_favorites_coordinator_layout), message, Snackbar.LENGTH_LONG).show();
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

                        Location location = QueryUtils.getLastKnowLocationFromSharedPreferences(FavoritesActivity.this);
                        if (QueryUtils.getShowDistanceSearchPreference(FavoritesActivity.this) &&
                                location.getLatitude() != QueryUtils.LAST_KNOW_LOCATION_LAT_LONG_NULL_VALUE &&
                                location.getLongitude() != QueryUtils.LAST_KNOW_LOCATION_LAT_LONG_NULL_VALUE) {
                            sharedElements.put(getString(R.string.activity_earthquake_details_distance_text_view_transition),
                                    selectedViewHolder.itemView.
                                            findViewById(R.id.earthquake_list_item_distance_text_view));
                        }

                        float viewHolderBottomYPosition = selectedViewHolder.itemView.getY() +
                                selectedViewHolder.itemView.getHeight() - UiUtils.getPxFromDp(FavoritesActivity.this, 8);
                        float bottomNavigationViewTopYPosition = mBottomNavigationView.getY();

                        if (viewHolderBottomYPosition > bottomNavigationViewTopYPosition) {
                            mBottomNavigationView.setVisibility(View.INVISIBLE);
                        }

                    }
                });
    }


    /**
     * Enables ActionMode if not enabled, toggles the selected value of the student selected and
     * sets the title of the action mode menu to be the number of favorites selected
     *
     * @param position Position of the favorite clicked or long clicked.
     */
    private void setupItemForActionMode(int position) {

        if (mActionMode == null) {
            mActionMode = startSupportActionMode(actionModeCallback);
            if (mAdapter.getSelectedFavoritesCount() == 0) {
                mDividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recycler_view_divider_dark));
            }
        }

        mAdapter.toggleFavoriteSelectionState(position);

        int selectedFavoritesCount = mAdapter.getSelectedFavoritesCount();

        // If the last selected favorite was deselected finish action mode
        if (selectedFavoritesCount == 0) {
            mActionMode.finish();
            mDividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recycler_view_divider_light));
        } else {
            // If not, update action mode title with the number of favorites selected
            mActionMode.setTitle(getString(R.string.activity_favorites_action_mode_toolbar_title,
                    selectedFavoritesCount, getPluralEnding(selectedFavoritesCount, true)));
            mActionMode.invalidate();
        }
    }


    /**
     * Class that implement ActionMode callbacks to change the menu when favorites are selected by a
     * long click and handle the deletion of those selected favorites.
     */
    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            actionMode.getMenuInflater().inflate(R.menu.menu_action_mode, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            if (menuItem.getItemId() == R.id.menu_action_mode_action_delete) {
                showDeleteSomeFavoritesConfirmationDialogFragment();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            mAdapter.clearSelectedFavorites();
            mActionMode = null;
            mDividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recycler_view_divider_light));
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EARTHQUAKE_RECYCLER_VIEW_POSITION_KEY, mEarthquakeRecyclerViewPosition);

        // If the a dialog fragment asking for confirmation to delete a favorite is displayed
        if (mIsAskingToDeleteFavorite) {
            outState.putBoolean(SAVED_INSTANCE_STATE_RIGHT_SWIPED_KEY, mRightSwipe);
            outState.putInt(SAVED_INSTANCE_STATE_FAVORITE_WITH_DELETE_BACKGROUND_POSITION_KEY, mFavoriteWithDeleteBackgroundPosition);
        }

        // If there are students selected
        if (mAdapter.getSelectedFavoritesCount() > 0) {
            outState.putParcelable(SAVED_INSTANCE_STATE_SELECTED_ITEMS_KEY, mAdapter.getSelectedFavorites());
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        mBottomNavigationView.setVisibility(View.VISIBLE);
        mBottomNavigationView.setSelectedItemId(R.id.menu_activity_main_bottom_navigation_view_action_favorites);
    }
}
