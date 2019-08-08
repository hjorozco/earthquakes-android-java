package com.weebly.hectorjorozco.earthquakes.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * {@link Earthquake} Represents the data from an Earthquake.
 * It contains the magnitude, the location, and the date of the Earthquake.
 * <p>
 */

public class Earthquake implements Parcelable {

    private double mMagnitude;
    private String mLocationOffset;
    private String mLocationPrimary;
    private long mTimeInMilliseconds;
    private String mUrl;
    private double mLatitude;
    private double mLongitude;
    private double mDepth;


    public Earthquake(double magnitude, String locationOffset, String locationPrimary,
                      long timeInMilliseconds, String url, double latitude, double longitude, double depth) {
        mMagnitude = magnitude;
        mLocationOffset = locationOffset;
        mLocationPrimary = locationPrimary;
        mTimeInMilliseconds = timeInMilliseconds;
        mUrl = url;
        mLatitude = latitude;
        mLongitude = longitude;
        mDepth = depth;
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

    public double getDepth(){return mDepth;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.mMagnitude);
        dest.writeString(this.mLocationOffset);
        dest.writeString(this.mLocationPrimary);
        dest.writeLong(this.mTimeInMilliseconds);
        dest.writeString(this.mUrl);
        dest.writeDouble(this.mLatitude);
        dest.writeDouble(this.mLongitude);
        dest.writeDouble(this.mDepth);
    }

    protected Earthquake(Parcel in) {
        this.mMagnitude = in.readDouble();
        this.mLocationOffset = in.readString();
        this.mLocationPrimary = in.readString();
        this.mTimeInMilliseconds = in.readLong();
        this.mUrl = in.readString();
        this.mLatitude = in.readDouble();
        this.mLongitude = in.readDouble();
        this.mDepth = in.readDouble();
    }

    public static final Parcelable.Creator<Earthquake> CREATOR = new Parcelable.Creator<Earthquake>() {
        @Override
        public Earthquake createFromParcel(Parcel source) {
            return new Earthquake(source);
        }

        @Override
        public Earthquake[] newArray(int size) {
            return new Earthquake[size];
        }
    };
}
