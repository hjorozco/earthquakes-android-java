package com.weebly.hectorjorozco.earthquakes.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weebly.hectorjorozco.earthquakes.R;
import com.weebly.hectorjorozco.earthquakes.models.Earthquake;
import com.weebly.hectorjorozco.earthquakes.utils.QueryUtils;
import com.weebly.hectorjorozco.earthquakes.utils.SortFavoritesUtils;

import java.util.List;

public class FavoritesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TITTLE_TYPE = 0;
    private static final int FAVORITE_TYPE = 1;

    private Context mContext;
    private List<Earthquake> mFavorites;
    private FavoritesListClickListener mFavoritesListClickListener;

    // Used to restore favorite swipe to delete background on rotation
    private int mFavoriteWithDeleteBackgroundPosition = -1;
    private boolean mFavoriteWithDeleteBackgroundRightSwiped = false;


    // Interface implemented in FavoritesActivity.java to handle clicks
    public interface FavoritesListClickListener {
        void onTitleClick();

        void onFavoriteClick(Earthquake favorite, int favoriteRecyclerViewPosition,
                             TextView magnitudeTextView, TextView locationOffsetTextView,
                             TextView locationPrimaryTextView, TextView dateTextView);
    }

    // The adapter constructor
    public FavoritesListAdapter(Context context, FavoritesListClickListener favoritesListClickListener) {
        mContext = context;
        mFavoritesListClickListener = favoritesListClickListener;
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

            String pluralEnding, sortedBy;
            String title = "";

            if (mFavorites.size() == 1) {
                pluralEnding = "";
            } else {
                pluralEnding = mContext.getString(R.string.letter_s_lowercase);
            }

            if (mFavorites.size() == 1) {
                titleViewHolder.titleTextView.setVisibility(View.GONE);
                titleViewHolder.titleTextView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            } else {
                titleViewHolder.titleTextView.setVisibility(View.VISIBLE);
                titleViewHolder.titleTextView.setLayoutParams(new RecyclerView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                sortedBy = SortFavoritesUtils.getSortByValueString(mContext);
                title = mContext.getString(R.string.activity_favorites_list_title, mFavorites.size(),
                        pluralEnding, sortedBy);
            }

            titleViewHolder.titleTextView.setText(title);

        } else if (holder instanceof FavoriteViewHolder) {

            FavoriteViewHolder favoriteViewHolder = (FavoriteViewHolder) holder;
            Earthquake currentFavorite = mFavorites.get(position - 1);

            QueryUtils.setupEarthquakeInformationOnViews(mContext, currentFavorite,
                    favoriteViewHolder.magnitudeTextView,
                    favoriteViewHolder.locationOffsetTextView,
                    favoriteViewHolder.locationPrimaryTextView,
                    favoriteViewHolder.dateTextView,
                    favoriteViewHolder.timeTextView);

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


    public void setFavoriteWithDeleteBackgroundData(int position, boolean rightSwiped) {
        mFavoriteWithDeleteBackgroundPosition = position;
        mFavoriteWithDeleteBackgroundRightSwiped = rightSwiped;
    }


    public class FavoriteViewHolder extends RecyclerView.ViewHolder {

        LinearLayout earthquakeLinearLayout;
        TextView magnitudeTextView;
        TextView locationOffsetTextView;
        TextView locationPrimaryTextView;
        TextView dateTextView;
        TextView timeTextView;

        public final RelativeLayout viewLeftSwipeBackground;
        public final RelativeLayout viewRightSwipeBackground;
        public final FrameLayout viewForeground;

        FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            earthquakeLinearLayout = itemView.findViewById(R.id.earthquake_list_item_main_layout);
            magnitudeTextView = itemView.findViewById(R.id.earthquake_list_item_magnitude_text_view);
            locationOffsetTextView = itemView.findViewById(R.id.earthquake_list_item_location_offset_text_view);
            locationPrimaryTextView = itemView.findViewById(R.id.earthquake_list_item_location_primary_text_view);
            dateTextView = itemView.findViewById(R.id.earthquake_list_item_date_text_view);
            timeTextView = itemView.findViewById(R.id.earthquake_list_item_time_text_view);
            viewLeftSwipeBackground = itemView.findViewById(R.id.favorite_list_item_left_swipe_background);
            viewRightSwipeBackground = itemView.findViewById(R.id.favorite_list_item_right_swipe_background);
            viewForeground = itemView.findViewById(R.id.favorite_list_item_view_foreground);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                magnitudeTextView.setTransitionName(
                        mContext.getString(R.string.activity_earthquake_details_magnitude_text_view_transition));
                locationOffsetTextView.setTransitionName(
                        mContext.getString(R.string.activity_earthquake_details_location_offset_text_view_transition));
                locationPrimaryTextView.setTransitionName(
                        mContext.getString(R.string.activity_earthquake_details_location_primary_text_view_transition));
                dateTextView.setTransitionName(
                        mContext.getString(R.string.activity_earthquake_details_date_text_view_transition));
            }

            earthquakeLinearLayout.setOnClickListener((View v) ->
            {
                int favoriteRecyclerViewPosition = getAdapterPosition();
                if (favoriteRecyclerViewPosition > 0) {
                    mFavoritesListClickListener.onFavoriteClick(mFavorites.get(favoriteRecyclerViewPosition - 1),
                            favoriteRecyclerViewPosition, magnitudeTextView, locationOffsetTextView,
                            locationPrimaryTextView, dateTextView);
                }
            });
        }
    }


    class TitleViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;

        TitleViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_list_item_text_view);
            titleTextView.setOnClickListener(v -> mFavoritesListClickListener.onTitleClick());
        }
    }

}
