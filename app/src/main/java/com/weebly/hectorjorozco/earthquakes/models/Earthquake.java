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
    private int mFelt;
    private double mCdi;
    private double mMmi;
    private String mAlert;
    private int mTsunami;

    public Earthquake(double magnitude, String locationOffset, String locationPrimary,
                      long timeInMilliseconds, String url, double latitude, double longitude, double depth,
                      int felt, double cdi, double mmi, String alert, int tsunami) {
        mMagnitude = magnitude;
        mLocationOffset = locationOffset;
        mLocationPrimary = locationPrimary;
        mTimeInMilliseconds = timeInMilliseconds;
        mUrl = url;
        mLatitude = latitude;
        mLongitude = longitude;
        mDepth = depth;
        mFelt = felt;
        mCdi = cdi;
        mMmi = mmi;
        mAlert = alert;
        mTsunami = tsunami;
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

    public double getDepth() {
        return mDepth;
    }

    public int getFelt() {
        return mFelt;
    }

    public double getCdi() {
        return mCdi;
    }

    public double getMmi() {
        return mMmi;
    }

    public String getAlert() {
        return mAlert;
    }

    public int getTsunami() {
        return mTsunami;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(mMagnitude);
        dest.writeString(mLocationOffset);
        dest.writeString(mLocationPrimary);
        dest.writeLong(mTimeInMilliseconds);
        dest.writeString(mUrl);
        dest.writeDouble(mLatitude);
        dest.writeDouble(mLongitude);
        dest.writeDouble(mDepth);
        dest.writeInt(mFelt);
        dest.writeDouble(mCdi);
        dest.writeDouble(mMmi);
        dest.writeString(mAlert);
        dest.writeInt(mTsunami);
    }

    protected Earthquake(Parcel in) {
        mMagnitude = in.readDouble();
        mLocationOffset = in.readString();
        mLocationPrimary = in.readString();
        mTimeInMilliseconds = in.readLong();
        mUrl = in.readString();
        mLatitude = in.readDouble();
        mLongitude = in.readDouble();
        mDepth = in.readDouble();
        mFelt = in.readInt();
        mCdi = in.readDouble();
        mMmi = in.readDouble();
        mAlert = in.readString();
        mTsunami = in.readInt();
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
