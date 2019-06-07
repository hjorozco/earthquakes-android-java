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
import com.weebly.hectorjorozco.earthquakes.utils.LanguageUtils;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

public class EarthquakesListAdapter extends RecyclerView.Adapter<EarthquakesListAdapter.EarthquakeViewHolder> {

    private Context mContext;
    private List<Earthquake> mEarthquakes;


    // The adapter constructor
    public EarthquakesListAdapter(Context context) {
        mContext = context;
    }


    // Called when ViewHolders are created to fill the RecyclerView.
    @NonNull
    @Override
    public EarthquakeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.earthquake_list_item, parent, false);
        return new EarthquakeViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull EarthquakeViewHolder holder, int position) {

        // Get the {@link Earthquake} object located at this position in the list
        Earthquake currentEarthquake = mEarthquakes.get(position);

        // ********************  DISPLAY MAGNITUDE OF THE EARTHQUAKE ********************

        // Set magnitude text
        DecimalFormat formatter = new DecimalFormat("0.0");
        String magnitudeToDisplay = formatter.format(currentEarthquake.getMagnitude());
        magnitudeToDisplay = magnitudeToDisplay.replace(',','.');
        holder.magnitudeTextView.setText(magnitudeToDisplay);

        // Set colors for magnitude circle and text
        Double roundedMagnitude = Double.valueOf(magnitudeToDisplay);
        GradientDrawable magnitudeCircle = (GradientDrawable) holder.magnitudeTextView.getBackground();
        EarthquakeInfoColors earthquakeInfoColors = getEarthquakeInfoColors(roundedMagnitude);
        int magnitudeColor = earthquakeInfoColors.getMagnitudeColor();
        int magnitudeBackgroundColor = earthquakeInfoColors.getMagnitudeBackgroundColor();
        magnitudeCircle.setColor(magnitudeBackgroundColor);
        magnitudeCircle.setStroke(10, magnitudeColor);
        holder.magnitudeTextView.setTextColor(magnitudeColor);


        // ********************  DISPLAY LOCATION OF THE EARTHQUAKE ********************

        holder.locationOffsetTextView.setText(currentEarthquake.getLocationOffset());
        holder.locationPrimaryTextView.setText(currentEarthquake.getLocationPrimary());
        holder.locationOffsetTextView.setTextColor(magnitudeColor);
        holder.locationPrimaryTextView.setTextColor(magnitudeColor);


        // ********************  DISPLAY DATE OF THE EARTHQUAKE ********************

        // Create a new Date object from the time in milliseconds of the earthquake
        Date dateObject = new Date(currentEarthquake.getTimeInMilliseconds());

        // Get the formatted date from the dateObject created from the time in Milliseconds of the
        // current Earthquake object and set this text on the dateTextView.
        holder.dateTextView.setText(LanguageUtils.formatDate(dateObject));

        /// Get the formatted time from the dateObject created from the time in Milliseconds of the
        // current Earthquake object and set this text on the timeTextView.
        holder.timeTextView.setText(LanguageUtils.formatTime(dateObject));

        // Set the color of dateTextView and TimeTextView
        holder.dateTextView.setTextColor(magnitudeColor);
        holder.timeTextView.setTextColor(magnitudeColor);

    }


    @Override
    public int getItemCount() {
        if (mEarthquakes == null) {
            return 0;
        } else {
            return mEarthquakes.size();
        }
    }

    public void setEarthquakesListData(List<Earthquake> earthquakes) {
        mEarthquakes = earthquakes;
        notifyDataSetChanged();
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
            magnitudeTextView = itemView.findViewById(R.id.list_item_magnitude);
            locationOffsetTextView = itemView.findViewById(R.id.list_item_location_offset);
            locationPrimaryTextView = itemView.findViewById(R.id.list_item_location_primary);
            dateTextView = itemView.findViewById(R.id.list_item_date);
            timeTextView = itemView.findViewById(R.id.list_item_time);
        }

    }




    /** Helper method used to determine the color used to display the earthquake information
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

        private int getMagnitudeBackgroundColor () {return mMagnitudeBackgroundColor;}

    }

    // Helper method that returns the color number from the Color Resource ID.
    private int getColor(int colorResourceId){
        return ContextCompat.getColor(mContext,colorResourceId);
    }


}
