package com.weebly.hectorjorozco.earthquakes.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.weebly.hectorjorozco.earthquakes.R;
import com.weebly.hectorjorozco.earthquakes.models.Earthquake;
import com.weebly.hectorjorozco.earthquakes.utils.WordsUtils;
import com.weebly.hectorjorozco.earthquakes.utils.QueryUtils;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

public class EarthquakesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TITTLE_TYPE = 0;
    private static final int EARTHQUAKE_TYPE = 1;

    private Context mContext;
    private List<Earthquake> mEarthquakes;
    private String mLocation;

    // The adapter constructor
    public EarthquakesListAdapter(Context context) {
        mContext = context;
    }


    // Called when ViewHolders are created to fill the RecyclerView.
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        if (viewType == TITTLE_TYPE) {
            viewHolder = new TitleViewHolder(LayoutInflater.from(mContext).inflate(R.layout.title_list_item, parent, false));
        } else {
            viewHolder = new EarthquakeViewHolder(LayoutInflater.from(mContext).inflate(R.layout.earthquake_list_item, parent, false));
        }
        return viewHolder;

    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof TitleViewHolder) {

            TitleViewHolder titleViewHolder = (TitleViewHolder) holder;

            String earthquakesWordSuffix, foundAndOrderedWordSuffix, sortedBy;
            if (mEarthquakes.size() == 1) {
                earthquakesWordSuffix = "";
                foundAndOrderedWordSuffix = "";
            } else {
                earthquakesWordSuffix = mContext.getString(R.string.earthquakes_list_title_earthquakes_word_suffix);
                foundAndOrderedWordSuffix = mContext.getString(R.string.earthquakes_list_title_found_and_sorted_words_suffix);
            }

            if (QueryUtils.sEarthquakesListInformationValues.getOrderBy()
                    .equals(mContext.getString(R.string.search_preference_sort_by_magnitude_entry_value))) {
                sortedBy = mContext.getString(R.string.earthquakes_list_title_sorted_by_magnitude_text);
            } else {
                sortedBy = mContext.getString(R.string.earthquakes_list_title_sorted_by_date_text);
            }

            titleViewHolder.titleTextView.setText(mContext.getString(R.string.earthquakes_list_title,
                    mEarthquakes.size(), earthquakesWordSuffix, foundAndOrderedWordSuffix, mLocation,
                    foundAndOrderedWordSuffix, sortedBy));

        } else if (holder instanceof EarthquakeViewHolder) {

            EarthquakeViewHolder earthquakeViewHolder = (EarthquakeViewHolder) holder;

            // Get the {@link Earthquake} object located at this position in the list
            Earthquake currentEarthquake = mEarthquakes.get(position - 1);

            // ********************  DISPLAY MAGNITUDE OF THE EARTHQUAKE ********************

            // Set magnitude text
            DecimalFormat formatter = new DecimalFormat("0.0");
            String magnitudeToDisplay = formatter.format(roundToOneDecimal(currentEarthquake.getMagnitude()));
            magnitudeToDisplay = magnitudeToDisplay.replace(',', '.');
            earthquakeViewHolder.magnitudeTextView.setText(magnitudeToDisplay);

            // Set colors for magnitude circle and text
            Double roundedMagnitude = Double.valueOf(magnitudeToDisplay);
            GradientDrawable magnitudeCircle = (GradientDrawable) earthquakeViewHolder.magnitudeTextView.getBackground();
            EarthquakeInfoColors earthquakeInfoColors = getEarthquakeInfoColors(roundedMagnitude);
            int magnitudeColor = earthquakeInfoColors.getMagnitudeColor();
            int magnitudeBackgroundColor = earthquakeInfoColors.getMagnitudeBackgroundColor();
            magnitudeCircle.setColor(magnitudeBackgroundColor);
            magnitudeCircle.setStroke(mContext.getResources().getDimensionPixelSize(R.dimen.magnitude_circle_stroke_width),
                    magnitudeColor);
            earthquakeViewHolder.magnitudeTextView.setTextColor(magnitudeColor);


            // ********************  DISPLAY LOCATION OF THE EARTHQUAKE ********************

            earthquakeViewHolder.locationOffsetTextView.setText(currentEarthquake.getLocationOffset());
            earthquakeViewHolder.locationPrimaryTextView.setText(currentEarthquake.getLocationPrimary());
            earthquakeViewHolder.locationOffsetTextView.setTextColor(magnitudeColor);
            earthquakeViewHolder.locationPrimaryTextView.setTextColor(magnitudeColor);


            // ********************  DISPLAY DATE OF THE EARTHQUAKE ********************

            // Create a new Date object from the time in milliseconds of the earthquake
            Date dateObject = new Date(currentEarthquake.getTimeInMilliseconds());

            // Get the formatted date from the dateObject created from the time in Milliseconds of the
            // current Earthquake object and set this text on the dateTextView.
            earthquakeViewHolder.dateTextView.setText(WordsUtils.formatDate(dateObject));

            /// Get the formatted time from the dateObject created from the time in Milliseconds of the
            // current Earthquake object and set this text on the timeTextView.
            earthquakeViewHolder.timeTextView.setText(WordsUtils.formatTime(dateObject));

            // Set the color of dateTextView and TimeTextView
            earthquakeViewHolder.dateTextView.setTextColor(magnitudeColor);
            earthquakeViewHolder.timeTextView.setTextColor(magnitudeColor);

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


    // Inner class for creating ViewHolders
    class EarthquakeViewHolder extends RecyclerView.ViewHolder {

        TextView magnitudeTextView;
        TextView locationOffsetTextView;
        TextView locationPrimaryTextView;
        TextView dateTextView;
        TextView timeTextView;

        EarthquakeViewHolder(@NonNull View itemView) {
            super(itemView);
            magnitudeTextView = itemView.findViewById(R.id.earthquake_list_item_magnitude_text_view);
            locationOffsetTextView = itemView.findViewById(R.id.earthquake_list_item_location_offset_text_view);
            locationPrimaryTextView = itemView.findViewById(R.id.earthquake_list_item_location_primary_text_view);
            dateTextView = itemView.findViewById(R.id.earthquake_list_item_date_text_view);
            timeTextView = itemView.findViewById(R.id.earthquake_list_item_time_text_view);
        }
    }


    class TitleViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;

        TitleViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_list_item_text_view);
        }
    }


    /**
     * Helper method used to determine the color used to display the earthquake information
     *
     * @param magnitude The magnitude of the earthquake.
     * @return The colors used to display the earthquake information
     */
    private EarthquakeInfoColors getEarthquakeInfoColors(double magnitude) {
        EarthquakeInfoColors earthquakeInfoColors;
        int magnitudeFloor = (int) Math.floor(magnitude);
        switch (magnitudeFloor) {
            case 0:
                earthquakeInfoColors = new EarthquakeInfoColors
                        (getColor(R.color.magnitude0), getColor(R.color.background0));
                break;
            case 1:
                earthquakeInfoColors = new EarthquakeInfoColors
                        (getColor(R.color.magnitude1), getColor(R.color.background1));
                break;
            case 2:
                earthquakeInfoColors = new EarthquakeInfoColors
                        (getColor(R.color.magnitude2), getColor(R.color.background2));
                break;
            case 3:
                earthquakeInfoColors = new EarthquakeInfoColors
                        (getColor(R.color.magnitude3), getColor(R.color.background3));
                break;
            case 4:
                earthquakeInfoColors = new EarthquakeInfoColors
                        (getColor(R.color.magnitude4), getColor(R.color.background4));
                break;
            case 5:
                earthquakeInfoColors = new EarthquakeInfoColors
                        (getColor(R.color.magnitude5), getColor(R.color.background5));
                break;
            case 6:
                earthquakeInfoColors = new EarthquakeInfoColors
                        (getColor(R.color.magnitude6), getColor(R.color.background6));
                break;
            case 7:
                earthquakeInfoColors = new EarthquakeInfoColors
                        (getColor(R.color.magnitude7), getColor(R.color.background7));
                break;
            case 8:
                earthquakeInfoColors = new EarthquakeInfoColors
                        (getColor(R.color.magnitude8), getColor(R.color.background8));
                break;
            default:
                earthquakeInfoColors = new EarthquakeInfoColors
                        (getColor(R.color.magnitude9plus), getColor(R.color.background9plus));
                break;
        }
        return earthquakeInfoColors;
    }


    /**
     * Class that models the colors used to display an earthquake information in the RecyclerView
     */
    private class EarthquakeInfoColors {
        // The color used in the magnitude circle border, its text and the information texts
        int mMagnitudeColor;
        // The background color of the magnitude circle
        int mMagnitudeBackgroundColor;

        private EarthquakeInfoColors(int magnitudeColor, int magnitudeBackgroundColor) {
            mMagnitudeColor = magnitudeColor;
            mMagnitudeBackgroundColor = magnitudeBackgroundColor;
        }

        private int getMagnitudeColor() {
            return mMagnitudeColor;
        }

        private int getMagnitudeBackgroundColor() {
            return mMagnitudeBackgroundColor;
        }

    }

    // Helper method that returns the color number from the Color Resource ID.
    private int getColor(int colorResourceId) {
        return ContextCompat.getColor(mContext, colorResourceId);
    }


    // Helper method that rounds a double to only one decimal place
    private static double roundToOneDecimal(double value) {
        int scale = (int) Math.pow(10, 1);
        return (double) Math.round(value * scale) / scale;
    }

}
