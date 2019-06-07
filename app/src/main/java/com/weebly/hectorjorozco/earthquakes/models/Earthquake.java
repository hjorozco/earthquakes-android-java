package com.weebly.hectorjorozco.earthquakes.models;

/**
 * {@link Earthquake} Represents the data from an Earthquake.
 * It contains the magnitude, the location, and the date of the Earthquake.
 * <p>
 */

public class Earthquake {

    private double mMagnitude;
    private String mLocationOffset;
    private String mLocationPrimary;
    private long mTimeInMilliseconds;
    private String mUrl;
    private double mLatitude;
    private double mLongitude;


    public Earthquake(double magnitude, String locationOffset, String locationPrimary, long timeInMilliseconds, String url, double latitude, double longitude) {
        mMagnitude = magnitude;
        mLocationOffset = locationOffset;
        mLocationPrimary = locationPrimary;
        mTimeInMilliseconds = timeInMilliseconds;
        mUrl = url;
        mLatitude = latitude;
        mLongitude = longitude;
    }

    public double getMagnitude() {
        return mMagnitude;
    }

    public String getLocationOffset() {
        return mLocationOffset;
    }

    public String getLocationPrimary() {
        return mLocationPrimary;
    }

    public long getTimeInMilliseconds() {
        return mTimeInMilliseconds;
    }

    public String getUrl() {
        return mUrl;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

}
