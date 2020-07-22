package com.weebly.hectorjorozco.earthquakes.utils;

import android.animation.Animator;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.weebly.hectorjorozco.earthquakes.R;
import com.weebly.hectorjorozco.earthquakes.models.Earthquake;

import java.util.Date;

public class MapsUtils {

    public static String constructEarthquakeTitleForMarker(Earthquake earthquake, String magnitude, Context context) {

        String locationPrimary = earthquake.getLocationPrimary();
        if (locationPrimary.isEmpty()){
            locationPrimary= context.getString(R.string.activity_main_no_earthquake_location_text);
            locationPrimary = locationPrimary.toLowerCase();
        }

        String locationOffset = earthquake.getLocationOffset();

        // If Distance Units are miles
        if (QueryUtils.isDistanceUnitSearchPreferenceValueMiles(context)) {
            if (!locationOffset.equals(context.getString(R.string.activity_main_location_text))) {
                locationOffset = UiUtils.changeLocationOffsetFromMilesToKilometers(locationOffset, context);
            }
        }
        return magnitude + " - " + locationOffset + " " + locationPrimary;
    }

    public static String constructEarthquakeSnippetForMarker(long earthquakeTimeInMilliseconds, String distanceText) {
        Date dateObject = new Date(earthquakeTimeInMilliseconds);
        return WordsUtils.formatDate(dateObject) + " " + WordsUtils.formatTime(dateObject) + distanceText;
    }

    /**
     * Returns the marker image based on the magnitude of the earthquake.
     *
     * @param magnitude The magnitude of the earthquake.
     * @return The marker image to be displayed on the map based on the magnitude of the earthquake.
     */
    public static MarkerAttributes getMarkerAttributes(double magnitude) {
        MarkerAttributes markerAttributes;
        int magnitudeFloor = (int) Math.floor(magnitude);
        switch (magnitudeFloor) {
            case 0:
                markerAttributes = new MarkerAttributes(R.drawable.ic_mag_0, 0.0f, 0.8f);
                break;
            case 1:
                markerAttributes = new MarkerAttributes(R.drawable.ic_mag_1, 1.0f, 0.8f);
                break;
            case 2:
                markerAttributes = new MarkerAttributes(R.drawable.ic_mag_2, 2.0f, 0.8f);
                break;
            case 3:
                markerAttributes = new MarkerAttributes(R.drawable.ic_mag_3, 3.0f, 0.8f);
                break;
            case 4:
                markerAttributes = new MarkerAttributes(R.drawable.ic_mag_4, 4.0f, 0.9f);
                break;
            case 5:
                markerAttributes = new MarkerAttributes(R.drawable.ic_mag_5, 5.0f, 0.9f);
                break;
            case 6:
                markerAttributes = new MarkerAttributes(R.drawable.ic_mag_6, 6.0f, 0.9f);
                break;
            case 7:
                markerAttributes = new MarkerAttributes(R.drawable.ic_mag_7, 7.0f, 1.0f);
                break;
            case 8:
                markerAttributes = new MarkerAttributes(R.drawable.ic_mag_8, 8.0f, 1.0f);
                break;
            default:
                markerAttributes = new MarkerAttributes(R.drawable.ic_mag_9, 9.0f, 1.0f);
                break;
        }
        return markerAttributes;
    }


    /**
     * Class the contains attributes of a marker to be displayed on the earthquakes map.
     */
    public static class MarkerAttributes {
        private final int mMarkerImageResourceId;
        private final float mZIndex;
        private final float mAlphaValue;

        private MarkerAttributes(int markerImageResourceId, float zIndex, float alphaValue) {
            mMarkerImageResourceId = markerImageResourceId;
            mZIndex = zIndex;
            mAlphaValue = alphaValue;
        }

        public int getMarkerImageResourceId() {
            return mMarkerImageResourceId;
        }

        public float getZIndex() {
            return mZIndex;
        }

        public float getAlphaValue() {
            return mAlphaValue;
        }

    }

    public static void showFabMenu(LinearLayout mLayoutFab1, LinearLayout mLayoutFab2, LinearLayout mLayoutFab3,
                             View mFabBackgroundLayout, FloatingActionButton mMainFab, Context context) {
        mLayoutFab1.setVisibility(View.VISIBLE);
        mLayoutFab2.setVisibility(View.VISIBLE);
        mLayoutFab3.setVisibility(View.VISIBLE);
        mFabBackgroundLayout.setVisibility(View.VISIBLE);
        mMainFab.animate().rotationBy(180);
        mLayoutFab1.animate().translationY(context.getResources().getDimension(R.dimen.standard_52));
        mLayoutFab2.animate().translationY(context.getResources().getDimension(R.dimen.standard_96));
        mLayoutFab3.animate().translationY(context.getResources().getDimension(R.dimen.standard_140))
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        mMainFab.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_close_brown_24dp));
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                    }
                });
    }


    public static void hideFabMenu(LinearLayout mLayoutFab1, LinearLayout mLayoutFab2, LinearLayout mLayoutFab3,
                             View mFabBackgroundLayout, FloatingActionButton mMainFab, Context context) {
        mFabBackgroundLayout.setVisibility(View.GONE);
        mMainFab.animate().rotation(0);
        mMainFab.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_layers_brown_24dp));
        mLayoutFab1.animate().translationY(0);
        mLayoutFab2.animate().translationY(0);
        mLayoutFab3.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                    mLayoutFab1.setVisibility(View.GONE);
                    mLayoutFab2.setVisibility(View.GONE);
                    mLayoutFab3.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
    }

}
