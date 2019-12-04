package com.weebly.hectorjorozco.earthquakes.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Comparator;
import java.util.Objects;

/**
 * {@link Earthquake} Represents the data from an Earthquake.
 * It contains the magnitude, the location, and the date of the Earthquake.
 * <p>
 */

@Entity(
        tableName = "favorite_earthquakes",
        indices = {@Index(value = {"id"}, unique = true)}
)
public class Earthquake implements Parcelable {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "id")
    private final String mId;
    @ColumnInfo(name = "magnitude")
    private final double mMagnitude;
    @ColumnInfo(name = "location_offset")
    private final String mLocationOffset;
    @ColumnInfo(name = "location_primary")
    private final String mLocationPrimary;
    @ColumnInfo(name = "time_in_milliseconds")
    private final long mTimeInMilliseconds;
    @ColumnInfo(name = "url")
    private final String mUrl;
    @ColumnInfo(name = "latitude")
    private final double mLatitude;
    @ColumnInfo(name = "longitude")
    private final double mLongitude;
    @ColumnInfo(name = "depth")
    private final double mDepth;
    @ColumnInfo(name = "felt")
    private final int mFelt;
    @ColumnInfo(name = "cdi")
    private final double mCdi;
    @ColumnInfo(name = "mmi")
    private final double mMmi;
    @ColumnInfo(name = "alert")
    private final String mAlert;
    @ColumnInfo(name = "tsunami")
    private final int mTsunami;
    @ColumnInfo(name = "distance")
    private float mDistance;


    public Earthquake(@NonNull String id, double magnitude, String locationOffset, String locationPrimary,
                      long timeInMilliseconds, String url, double latitude, double longitude, double depth,
                      int felt, double cdi, double mmi, String alert, int tsunami, float distance) {
        mId = id;
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
        mDistance = distance;
    }

    public String getId() {
        return mId;
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

    public float getDistance(){return mDistance;}


    public void setDistance(float distance){
        mDistance = distance;
    }


    // Comparators used to sort favorite earthquakes

    public static final Comparator<Earthquake> ascendingDateComparator = (earthquake1, earthquake2)
            -> Long.compare(earthquake1.getTimeInMilliseconds(), earthquake2.getTimeInMilliseconds());

    public static final Comparator<Earthquake> descendingDateComparator = (earthquake1, earthquake2)
            -> -Long.compare(earthquake1.getTimeInMilliseconds(), earthquake2.getTimeInMilliseconds());

    public static final Comparator<Earthquake> ascendingMagnitudeComparator = (earthquake1, earthquake2)
            -> Double.compare(earthquake1.getMagnitude(), earthquake2.getMagnitude());

    public static final Comparator<Earthquake> descendingMagnitudeComparator = (earthquake1, earthquake2)
            -> -Double.compare(earthquake1.getMagnitude(), earthquake2.getMagnitude());

    public static final Comparator<Earthquake> ascendingDistanceComparator = (earthquake1, earthquake2)
            -> Float.compare(earthquake1.getDistance(), earthquake2.getDistance());

    public static final Comparator<Earthquake> descendingDistanceComparator = (earthquake1, earthquake2)
            -> -Float.compare(earthquake1.getDistance(), earthquake2.getDistance());


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
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
        dest.writeFloat(mDistance);
    }

    protected Earthquake(Parcel in) {
        mId = Objects.requireNonNull(in.readString());
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
        mDistance = in.readFloat();
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
