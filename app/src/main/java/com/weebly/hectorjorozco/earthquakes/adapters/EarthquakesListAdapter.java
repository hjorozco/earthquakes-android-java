package com.weebly.hectorjorozco.earthquakes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weebly.hectorjorozco.earthquakes.R;
import com.weebly.hectorjorozco.earthquakes.models.Earthquake;

import java.util.ArrayList;
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
        holder.textView.setText(mEarthquakes.get(position).getText());
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

        TextView textView;

        EarthquakeViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.earthquake_list_item_text_view);
        }

    }
}
