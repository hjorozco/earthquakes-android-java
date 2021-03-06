package com.weebly.hectorjorozco.earthquakes.adapters;

import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.weebly.hectorjorozco.earthquakes.R;
import com.weebly.hectorjorozco.earthquakes.models.Earthquake;
import com.weebly.hectorjorozco.earthquakes.models.SparseBooleanArrayParcelable;
import com.weebly.hectorjorozco.earthquakes.utils.QueryUtils;
import com.weebly.hectorjorozco.earthquakes.utils.SortFavoritesUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.Context.VIBRATOR_SERVICE;

public class FavoritesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TITTLE_TYPE = 0;
    private static final int FAVORITE_TYPE = 1;

    private static final int LONG_PRESS_VIBRATION_TIME_IN_MILLISECONDS = 10;

    private final Context mContext;
    private List<Earthquake> mFavorites;
    private final FavoritesListAdapterListener mFavoritesListAdapterListener;

    // Array that saves the selected state of students (true if selected, false otherwise)
    private SparseBooleanArrayParcelable mSelectedFavorites;

    // Used to restore favorite swipe to delete background on rotation
    private int mFavoriteWithDeleteBackgroundPosition = -1;
    private boolean mFavoriteWithDeleteBackgroundRightSwiped = false;


    // Interface implemented in FavoritesActivity.java to handle clicks and long clicks
    public interface FavoritesListAdapterListener {

        void onFavoriteClick(Earthquake favorite, int favoriteRecyclerViewPosition,
                             View magnitudeCircleView, TextView magnitudeTextView,
                             TextView locationOffsetTextView, TextView locationPrimaryTextView,
                             TextView distanceTextView, TextView dateTextView, TextView timeTextView);

        void onFavoriteLongClick(int favoriteRecyclerViewPosition);
    }

    // The adapter constructor
    public FavoritesListAdapter(Context context, FavoritesListAdapterListener favoritesListAdapterListener) {
        mContext = context;
        mFavoritesListAdapterListener = favoritesListAdapterListener;
        mSelectedFavorites = new SparseBooleanArrayParcelable();
    }


    // Called when ViewHolders are created to fill the RecyclerView.
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        if (viewType == TITTLE_TYPE) {
            viewHolder = new TitleViewHolder(LayoutInflater.from(mContext).inflate(
                    R.layout.title_list_item, parent, false));
        } else {
            viewHolder = new FavoriteViewHolder(LayoutInflater.from(mContext).inflate(
                    R.layout.favorite_list_item, parent, false));
        }
        return viewHolder;

    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof TitleViewHolder) {

            TitleViewHolder titleViewHolder = (TitleViewHolder) holder;

            String pluralEnding, sortedBy, sortedByTitleSection, title;

            if (mFavorites.size() == 1) {
                pluralEnding = "";
            } else {
                pluralEnding = mContext.getString(R.string.letter_s_lowercase);
            }



            if (mFavorites.size()==1){
                sortedByTitleSection = "";
            } else {
                sortedBy = SortFavoritesUtils.getSortByValueString(mContext);
                sortedByTitleSection = " " + mContext.getString(R.string.activity_favorites_list_title_sorted_by_section, sortedBy);
            }

            title = mContext.getString(R.string.activity_favorites_list_title,
                    String.format(Locale.getDefault(), "%,d", mFavorites.size()),
                    pluralEnding, sortedByTitleSection);


            titleViewHolder.titleTextView.setText(title);

        } else if (holder instanceof FavoriteViewHolder) {

            FavoriteViewHolder favoriteViewHolder = (FavoriteViewHolder) holder;
            TextView distanceTextView;

            Earthquake currentFavorite = mFavorites.get(position - 1);

            // If "Show distance from you" search preference is not checked, or there is not a location
            // saved, hide the distance TextView
            if (!QueryUtils.getShowDistanceSearchPreference(mContext) ||
                    QueryUtils.sLastKnownLocationLatitude == QueryUtils.LAST_KNOW_LOCATION_LAT_LONG_NULL_VALUE ||
                    QueryUtils.sLastKnownLocationLongitude == QueryUtils.LAST_KNOW_LOCATION_LAT_LONG_NULL_VALUE) {
                favoriteViewHolder.distanceTextView.setVisibility(View.GONE);
                distanceTextView = null;
            } else {
                favoriteViewHolder.distanceTextView.setVisibility(View.VISIBLE);
                distanceTextView = favoriteViewHolder.distanceTextView;
            }

            QueryUtils.setupEarthquakeInformationOnViews(mContext, currentFavorite,
                    favoriteViewHolder.magnitudeCircleView,
                    favoriteViewHolder.magnitudeTextView,
                    favoriteViewHolder.locationOffsetTextView,
                    favoriteViewHolder.locationPrimaryTextView,
                    distanceTextView,
                    favoriteViewHolder.dateTextView,
                    favoriteViewHolder.timeTextView);


            // For Android version 21 and up set transition names
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                favoriteViewHolder.magnitudeCircleView.setTransitionName(
                        mContext.getString(R.string.activity_earthquake_details_magnitude_circle_view_transition));
                favoriteViewHolder.magnitudeTextView.setTransitionName(
                        mContext.getString(R.string.activity_earthquake_details_magnitude_text_view_transition));
                favoriteViewHolder.locationOffsetTextView.setTransitionName(
                        mContext.getString(R.string.activity_earthquake_details_location_offset_text_view_transition));
                favoriteViewHolder.locationPrimaryTextView.setTransitionName(
                        mContext.getString(R.string.activity_earthquake_details_location_primary_text_view_transition));
                favoriteViewHolder.distanceTextView.setTransitionName(
                        mContext.getString(R.string.activity_earthquake_details_distance_text_view_transition));
                favoriteViewHolder.dateTextView.setTransitionName(
                        mContext.getString(R.string.activity_earthquake_details_date_text_view_transition));
                favoriteViewHolder.timeTextView.setTransitionName(
                        mContext.getString(R.string.activity_earthquake_details_time_text_view_transition));
            } else {
                // For Android 19 set list item background as a touch selector since
                // ?android:attr/selectableItemBackground does not work on 19.
                favoriteViewHolder.earthquakeLinearLayout.setBackground(ResourcesCompat.getDrawable
                        (mContext.getResources(), R.drawable.touch_selector, null));
            }

            // If the student has been selected by a long click
            if (mSelectedFavorites.get(position, false)) {
                favoriteViewHolder.viewForeground.setBackgroundColor(mContext.getResources().getColor(R.color.colorRowActivated));
                favoriteViewHolder.selectedImageView.setVisibility(View.VISIBLE);
            } else {
                favoriteViewHolder.viewForeground.setBackgroundColor(mContext.getResources().getColor(R.color.colorAppBackground));
                favoriteViewHolder.selectedImageView.setVisibility(View.GONE);
            }

            // If the favorite was swiped to delete, the app is asking for confirmation and the device's config changed then:
            if (mFavoriteWithDeleteBackgroundPosition == position) {
                favoriteViewHolder.viewForeground.setVisibility(View.INVISIBLE);
                if (mFavoriteWithDeleteBackgroundRightSwiped) {
                    favoriteViewHolder.viewRightSwipeBackground.setVisibility(View.VISIBLE);
                    favoriteViewHolder.viewLeftSwipeBackground.setVisibility(View.INVISIBLE);
                } else {
                    favoriteViewHolder.viewRightSwipeBackground.setVisibility(View.INVISIBLE);
                    favoriteViewHolder.viewLeftSwipeBackground.setVisibility(View.VISIBLE);
                }
            } else {
                favoriteViewHolder.viewForeground.setVisibility(View.VISIBLE);
            }

        }

    }


    @Override
    public int getItemCount() {
        if (mFavorites == null) {
            return 1;
        } else {
            return mFavorites.size() + 1;
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TITTLE_TYPE;
        else
            return FAVORITE_TYPE;
    }


    public void setFavoritesListData(List<Earthquake> favorites) {
        mFavorites = favorites;
        Location location = QueryUtils.getLastKnowLocationFromSharedPreferences(mContext);
        QueryUtils.sLastKnownLocationLatitude = location.getLatitude();
        QueryUtils.sLastKnownLocationLongitude = location.getLongitude();
        notifyDataSetChanged();
    }


    // Returns the list of favorites.
    public List<Earthquake> getFavoritesListData() {
        return mFavorites;
    }


    public Earthquake getFavorite(int position) {
        return mFavorites.get(position);
    }


    public void removeFavorite(int position) {
        mFavorites.remove(position);
        notifyItemRemoved(position);
    }


    // Used for swipe to delete one favorite
    public void setFavoriteWithDeleteBackgroundData(int position, boolean rightSwiped) {
        mFavoriteWithDeleteBackgroundPosition = position;
        mFavoriteWithDeleteBackgroundRightSwiped = rightSwiped;
    }


    // The following are xix methods used for long click favorite selection for deletion in Action Mode
    public void toggleFavoriteSelectionState(int position) {
        if (mSelectedFavorites.get(position, false)) {
            mSelectedFavorites.delete(position);
        } else {
            mSelectedFavorites.put(position, true);
        }
        notifyItemChanged(position);
    }

    public void clearSelectedFavorites() {
        mSelectedFavorites.clear();
        notifyDataSetChanged();
    }

    public int getSelectedFavoritesCount() {
        return mSelectedFavorites.size();
    }

    public List<Integer> getSelectedFavoritesPositions() {
        List<Integer> selectedFavorites = new ArrayList<>(mSelectedFavorites.size());
        for (int i = 0; i < mSelectedFavorites.size(); i++) {
            selectedFavorites.add(mSelectedFavorites.keyAt(i));
        }
        return selectedFavorites;
    }

    public void setSelectedFavorites(SparseBooleanArrayParcelable selectedFavorites) {
        mSelectedFavorites = selectedFavorites;
    }

    public SparseBooleanArrayParcelable getSelectedFavorites() {
        return mSelectedFavorites;
    }

    // End of six methods used for long click favorite selection for deletion in Action Mode.


    public class FavoriteViewHolder extends RecyclerView.ViewHolder {

        final LinearLayout earthquakeLinearLayout;
        final View magnitudeCircleView;
        final TextView magnitudeTextView;
        final TextView locationOffsetTextView;
        final TextView locationPrimaryTextView;
        final TextView distanceTextView;
        final TextView dateTextView;
        final TextView timeTextView;
        final ImageView selectedImageView;

        public final RelativeLayout viewLeftSwipeBackground;
        public final RelativeLayout viewRightSwipeBackground;
        public final FrameLayout viewForeground;

        FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            earthquakeLinearLayout = itemView.findViewById(R.id.earthquake_list_item_main_layout);
            magnitudeCircleView = itemView.findViewById(R.id.earthquake_list_item_magnitude_circle_view);
            magnitudeTextView = itemView.findViewById(R.id.earthquake_list_item_magnitude_text_view);
            locationOffsetTextView = itemView.findViewById(R.id.earthquake_list_item_location_offset_text_view);
            locationPrimaryTextView = itemView.findViewById(R.id.earthquake_list_item_location_primary_text_view);
            distanceTextView = itemView.findViewById(R.id.earthquake_list_item_distance_text_view);
            dateTextView = itemView.findViewById(R.id.earthquake_list_item_date_text_view);
            timeTextView = itemView.findViewById(R.id.earthquake_list_item_time_text_view);
            selectedImageView = itemView.findViewById(R.id.favorite_list_item_selected_image_view);
            viewLeftSwipeBackground = itemView.findViewById(R.id.favorite_list_item_left_swipe_background);
            viewRightSwipeBackground = itemView.findViewById(R.id.favorite_list_item_right_swipe_background);
            viewForeground = itemView.findViewById(R.id.favorite_list_item_view_foreground);

            earthquakeLinearLayout.setOnClickListener((View v) ->
            {
                int favoriteRecyclerViewPosition = getAdapterPosition();

                if (favoriteRecyclerViewPosition > 0) {
                    mFavoritesListAdapterListener.onFavoriteClick(mFavorites.get(favoriteRecyclerViewPosition - 1),
                            favoriteRecyclerViewPosition, magnitudeCircleView, magnitudeTextView, locationOffsetTextView,
                            locationPrimaryTextView, distanceTextView, dateTextView, timeTextView);
                }
            });

            earthquakeLinearLayout.setOnLongClickListener(v -> {
                mFavoritesListAdapterListener.onFavoriteLongClick(getAdapterPosition());
                vibrate();
                return true;
            });
        }
    }


    class TitleViewHolder extends RecyclerView.ViewHolder {

        final TextView titleTextView;

        TitleViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_list_item_text_view);
            titleTextView.setBackgroundColor(mContext.getResources().getColor(R.color.colorAppBackground));
        }
    }


    private void vibrate() {
        Vibrator vibrator = (Vibrator) mContext.getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(LONG_PRESS_VIBRATION_TIME_IN_MILLISECONDS);
        }
    }

}
