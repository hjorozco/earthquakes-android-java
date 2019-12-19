package com.weebly.hectorjorozco.earthquakes.adapters;

import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weebly.hectorjorozco.earthquakes.R;
import com.weebly.hectorjorozco.earthquakes.models.Earthquake;
import com.weebly.hectorjorozco.earthquakes.utils.UiUtils;
import com.weebly.hectorjorozco.earthquakes.utils.WordsUtils;
import com.weebly.hectorjorozco.earthquakes.utils.QueryUtils;

import java.util.List;
import java.util.Locale;


public class EarthquakesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TITTLE_TYPE = 0;
    private static final int EARTHQUAKE_TYPE = 1;

    private final Context mContext;
    private List<Earthquake> mEarthquakes;
    private String mLocation;
    private final EarthquakesListClickListener mEarthquakesListClickListener;
    private String mMaxDistance;
    private boolean mIsDistanceShown;


    // Interface implemented in MainActivity.java to handle clicks
    public interface EarthquakesListClickListener {
        void onTitleClick();

        void onEarthquakeClick(Earthquake earthquake, int earthquakeRecyclerViewPosition,
                               View magnitudeCircleView,
                               TextView magnitudeTextView, TextView locationOffsetTextView,
                               TextView locationPrimaryTextView, TextView distanceTextView,
                               TextView dateTextView, TextView timeTextView);
    }

    // The adapter constructor
    public EarthquakesListAdapter(Context context, EarthquakesListClickListener earthquakesListClickListener) {
        mContext = context;
        mEarthquakesListClickListener = earthquakesListClickListener;
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
            viewHolder = new EarthquakeViewHolder(LayoutInflater.from(mContext).inflate(
                    R.layout.earthquake_list_item, parent, false));
        }
        return viewHolder;

    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof TitleViewHolder) {

            TitleViewHolder titleViewHolder = (TitleViewHolder) holder;

            String pluralEnding, foundWordSuffix, orderBy, sortedBy, distance;
            String title;

            if (mEarthquakes.size() == 1) {
                pluralEnding = "";
                foundWordSuffix = "";
            } else {
                pluralEnding = mContext.getString(R.string.letter_s_lowercase);
                foundWordSuffix = mContext.getString(R.string.earthquakes_list_title_found_and_sorted_words_suffix);
            }

            orderBy = QueryUtils.sEarthquakesListInformationValues.getOrderBy();
            sortedBy = "";
            if (orderBy.equals(mContext.getString(R.string.search_preference_sort_by_ascending_date_entry_value))) {
                sortedBy = mContext.getString(R.string.search_preference_sort_by_ascending_date_entry);
            } else if (orderBy.equals(mContext.getString(R.string.search_preference_sort_by_descending_date_entry_value))) {
                sortedBy = mContext.getString(R.string.search_preference_sort_by_descending_date_entry);
            } else if (orderBy.equals(mContext.getString(R.string.search_preference_sort_by_ascending_magnitude_entry_value))) {
                sortedBy = mContext.getString(R.string.search_preference_sort_by_ascending_magnitude_entry);
            } else if (orderBy.equals(mContext.getString(R.string.search_preference_sort_by_descending_magnitude_entry_value))) {
                sortedBy = mContext.getString(R.string.search_preference_sort_by_descending_magnitude_entry);
            }

            distance = "";
            if (!mMaxDistance.isEmpty() &&
                    QueryUtils.sLastKnownLocationLatitude != QueryUtils.LAST_KNOW_LOCATION_LAT_LONG_NULL_VALUE &&
                    QueryUtils.sLastKnownLocationLongitude != QueryUtils.LAST_KNOW_LOCATION_LAT_LONG_NULL_VALUE) {

                String distanceUnits = mContext.getString(R.string.kilometers_text);
                int maxDistance = Integer.valueOf(mMaxDistance);
                if (QueryUtils.isDistanceUnitSearchPreferenceValueMiles(mContext)){
                    distanceUnits = mContext.getString(R.string.miles_text);
                    maxDistance = Math.round(UiUtils.getMilesFromKilometers(maxDistance));
                }

                distance = " " + mContext.getString(R.string.earthquakes_list_title_max_distance_from_you_section,
                        String.format(Locale.getDefault(), "%,d", maxDistance), distanceUnits);
            }

            if (mEarthquakes.size() == 1) {
                title = mContext.getString(R.string.earthquakes_list_title_for_one_earthquake,
                        String.format(Locale.getDefault(), "%,d", mEarthquakes.size()), pluralEnding, foundWordSuffix, mLocation, distance);
            } else {
                title = mContext.getString(R.string.earthquakes_list_title_for_multiple_earthquakes,
                        String.format(Locale.getDefault(), "%,d", mEarthquakes.size()), pluralEnding, foundWordSuffix, mLocation, distance, sortedBy);
                if (!QueryUtils.sEarthquakesListSortedByDistanceText.isEmpty()) {
                    title = title + " " + QueryUtils.sEarthquakesListSortedByDistanceText + ".";
                }
            }

            titleViewHolder.titleTextView.setText(title);

        } else if (holder instanceof EarthquakeViewHolder) {

            EarthquakeViewHolder earthquakeViewHolder = (EarthquakeViewHolder) holder;
            TextView distanceTextView;

            // If "Show distance from you" search preference is not checked, or there is not a location
            // saved, hide the distance TextView
            if (!mIsDistanceShown ||
                    QueryUtils.sLastKnownLocationLatitude == QueryUtils.LAST_KNOW_LOCATION_LAT_LONG_NULL_VALUE ||
                    QueryUtils.sLastKnownLocationLongitude == QueryUtils.LAST_KNOW_LOCATION_LAT_LONG_NULL_VALUE) {
                earthquakeViewHolder.distanceTextView.setVisibility(View.GONE);
                distanceTextView = null;
            } else {
                earthquakeViewHolder.distanceTextView.setVisibility(View.VISIBLE);
                distanceTextView = earthquakeViewHolder.distanceTextView;
            }

            Earthquake currentEarthquake = mEarthquakes.get(position - 1);

            QueryUtils.setupEarthquakeInformationOnViews(mContext, currentEarthquake,
                    earthquakeViewHolder.magnitudeCircleView,
                    earthquakeViewHolder.magnitudeTextView,
                    earthquakeViewHolder.locationOffsetTextView,
                    earthquakeViewHolder.locationPrimaryTextView,
                    distanceTextView,
                    earthquakeViewHolder.dateTextView,
                    earthquakeViewHolder.timeTextView);

            // For Android version 21 and up set transition names
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                earthquakeViewHolder.magnitudeCircleView.setTransitionName(
                        mContext.getString(R.string.activity_earthquake_details_magnitude_circle_view_transition));
                earthquakeViewHolder.magnitudeTextView.setTransitionName(
                        mContext.getString(R.string.activity_earthquake_details_magnitude_text_view_transition));
                earthquakeViewHolder.locationOffsetTextView.setTransitionName(
                        mContext.getString(R.string.activity_earthquake_details_location_offset_text_view_transition));
                earthquakeViewHolder.locationPrimaryTextView.setTransitionName(
                        mContext.getString(R.string.activity_earthquake_details_location_primary_text_view_transition));
                earthquakeViewHolder.distanceTextView.setTransitionName(
                        mContext.getString(R.string.activity_earthquake_details_distance_text_view_transition));
                earthquakeViewHolder.dateTextView.setTransitionName(
                        mContext.getString(R.string.activity_earthquake_details_date_text_view_transition));
                earthquakeViewHolder.timeTextView.setTransitionName(
                        mContext.getString(R.string.activity_earthquake_details_time_text_view_transition));
            } else {
                // For Android 19 set list item background as a touch selector since
                // ?android:attr/selectableItemBackground does not work on 19.
                earthquakeViewHolder.earthquakeMainLayout.setBackground(mContext.getResources().
                        getDrawable(R.drawable.touch_selector));
            }

        }

    }


    @Override
    public int getItemCount() {
        if (mEarthquakes == null) {
            return 1;
        } else {
            return mEarthquakes.size() + 1;
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TITTLE_TYPE;
        else
            return EARTHQUAKE_TYPE;
    }


    public void setEarthquakesListData(List<Earthquake> earthquakes) {
        mEarthquakes = earthquakes;
        mIsDistanceShown = QueryUtils.getShowDistanceSearchPreference(mContext);
        Location location = QueryUtils.getLastKnowLocationFromSharedPreferences(mContext);
        QueryUtils.sLastKnownLocationLatitude = location.getLatitude();
        QueryUtils.sLastKnownLocationLongitude = location.getLongitude();
        notifyDataSetChanged();
    }

    public List<Earthquake> getEarthquakesListData() {
        return mEarthquakes;
    }


    public void setLocation(String location) {
        if (location.isEmpty()) {
            mLocation = mContext.getString(R.string.the_whole_world_text);
        } else {
            if (WordsUtils.isUnitedStatesAbbreviation(location) ||
                    WordsUtils.isUnitedStatesName(location)) {
                mLocation = mContext.getString(R.string.earthquakes_list_title_us_name);
            } else {
                mLocation = WordsUtils.formatLocationText(location);
            }
        }
    }


    public void setMaxDistance(String maxDistance) {
        mMaxDistance = maxDistance;
    }


    class EarthquakeViewHolder extends RecyclerView.ViewHolder {

        final LinearLayout earthquakeMainLayout;
        final View magnitudeCircleView;
        final TextView magnitudeTextView;
        final TextView locationOffsetTextView;
        final TextView locationPrimaryTextView;
        final TextView distanceTextView;
        final TextView dateTextView;
        final TextView timeTextView;

        EarthquakeViewHolder(@NonNull View itemView) {
            super(itemView);
            earthquakeMainLayout = itemView.findViewById(R.id.earthquake_list_item_main_layout);
            magnitudeCircleView = itemView.findViewById(R.id.earthquake_list_item_magnitude_circle_view);
            magnitudeTextView = itemView.findViewById(R.id.earthquake_list_item_magnitude_text_view);
            locationOffsetTextView = itemView.findViewById(R.id.earthquake_list_item_location_offset_text_view);
            locationPrimaryTextView = itemView.findViewById(R.id.earthquake_list_item_location_primary_text_view);
            distanceTextView = itemView.findViewById(R.id.earthquake_list_item_distance_text_view);
            dateTextView = itemView.findViewById(R.id.earthquake_list_item_date_text_view);
            timeTextView = itemView.findViewById(R.id.earthquake_list_item_time_text_view);

            earthquakeMainLayout.setOnClickListener((View v) ->
            {
                int earthquakeRecyclerViewPosition = getAdapterPosition();
                if (earthquakeRecyclerViewPosition > 0) {
                    mEarthquakesListClickListener.onEarthquakeClick(mEarthquakes.get(earthquakeRecyclerViewPosition - 1),
                            earthquakeRecyclerViewPosition, magnitudeCircleView, magnitudeTextView, locationOffsetTextView,
                            locationPrimaryTextView, distanceTextView, dateTextView, timeTextView);
                }
            });
        }
    }


    class TitleViewHolder extends RecyclerView.ViewHolder {

        final TextView titleTextView;

        TitleViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_list_item_text_view);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                titleTextView.setBackground(mContext.getResources().
                        getDrawable(R.drawable.touch_selector));
            }

            titleTextView.setOnClickListener(v -> mEarthquakesListClickListener.onTitleClick());
        }
    }

}
