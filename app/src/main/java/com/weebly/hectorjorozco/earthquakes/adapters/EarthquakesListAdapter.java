package com.weebly.hectorjorozco.earthquakes.adapters;

import android.content.Context;
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
import com.weebly.hectorjorozco.earthquakes.utils.WordsUtils;
import com.weebly.hectorjorozco.earthquakes.utils.QueryUtils;

import java.util.List;

public class EarthquakesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TITTLE_TYPE = 0;
    private static final int EARTHQUAKE_TYPE = 1;

    private Context mContext;
    private List<Earthquake> mEarthquakes;
    private String mLocation;
    private EarthquakesListClickListener mEarthquakesListClickListener;


    // Interface implemented in MainActivity.java to handle clicks
    public interface EarthquakesListClickListener {
        void onTitleClick();

        void onEarthquakeClick(Earthquake earthquake, int earthquakeRecyclerViewPosition,
                               TextView magnitudeTextView, TextView locationOffsetTextView,
                               TextView locationPrimaryTextView, TextView dateTextView);
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

            String pluralEnding, foundWordSuffix, orderBy, sortedBy;
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

            if (mEarthquakes.size() == 1) {
                title = mContext.getString(R.string.earthquakes_list_title_for_one_earthquake,
                        mEarthquakes.size(), pluralEnding, foundWordSuffix, mLocation);
            } else {
                title = mContext.getString(R.string.earthquakes_list_title_for_multiple_earthquakes,
                        mEarthquakes.size(), pluralEnding, foundWordSuffix, mLocation, sortedBy);
            }

            titleViewHolder.titleTextView.setText(title);

        } else if (holder instanceof EarthquakeViewHolder) {

            EarthquakeViewHolder earthquakeViewHolder = (EarthquakeViewHolder) holder;
            Earthquake currentEarthquake = mEarthquakes.get(position - 1);

            QueryUtils.setupEarthquakeInformationOnViews(mContext, currentEarthquake,
                    earthquakeViewHolder.magnitudeTextView,
                    earthquakeViewHolder.locationOffsetTextView,
                    earthquakeViewHolder.locationPrimaryTextView,
                    earthquakeViewHolder.dateTextView,
                    earthquakeViewHolder.timeTextView);

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
        notifyDataSetChanged();
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


    class EarthquakeViewHolder extends RecyclerView.ViewHolder {

        LinearLayout earthquakeLinearLayout;
        TextView magnitudeTextView;
        TextView locationOffsetTextView;
        TextView locationPrimaryTextView;
        TextView dateTextView;
        TextView timeTextView;

        EarthquakeViewHolder(@NonNull View itemView) {
            super(itemView);
            earthquakeLinearLayout = itemView.findViewById(R.id.earthquake_list_item_main_layout);
            magnitudeTextView = itemView.findViewById(R.id.earthquake_list_item_magnitude_text_view);
            locationOffsetTextView = itemView.findViewById(R.id.earthquake_list_item_location_offset_text_view);
            locationPrimaryTextView = itemView.findViewById(R.id.earthquake_list_item_location_primary_text_view);
            dateTextView = itemView.findViewById(R.id.earthquake_list_item_date_text_view);
            timeTextView = itemView.findViewById(R.id.earthquake_list_item_time_text_view);

            // For Android version 21 and up set transition names
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                magnitudeTextView.setTransitionName(
                        mContext.getString(R.string.activity_earthquake_details_magnitude_text_view_transition));
                locationOffsetTextView.setTransitionName(
                        mContext.getString(R.string.activity_earthquake_details_location_offset_text_view_transition));
                locationPrimaryTextView.setTransitionName(
                        mContext.getString(R.string.activity_earthquake_details_location_primary_text_view_transition));
                dateTextView.setTransitionName(
                        mContext.getString(R.string.activity_earthquake_details_date_text_view_transition));
            } else {
                // For Android 19 set list item background as a touch selector since
                // ?android:attr/selectableItemBackground does not work on 19.
                earthquakeLinearLayout.setBackground(mContext.getResources().
                        getDrawable(R.drawable.touch_selector));
            }

            earthquakeLinearLayout.setOnClickListener((View v) ->
            {
                int earthquakeRecyclerViewPosition = getAdapterPosition();
                if (earthquakeRecyclerViewPosition > 0) {
                    mEarthquakesListClickListener.onEarthquakeClick(mEarthquakes.get(earthquakeRecyclerViewPosition - 1),
                            earthquakeRecyclerViewPosition, magnitudeTextView, locationOffsetTextView,
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

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                titleTextView.setBackground(mContext.getResources().
                        getDrawable(R.drawable.touch_selector));
            }

            titleTextView.setOnClickListener(v -> mEarthquakesListClickListener.onTitleClick());
        }
    }

}
