package com.weebly.hectorjorozco.earthquakes.models;

public class EarthquakesQueryParameters {

    private final String mStartTime;
    private final String mEndTime;
    private final String mLimit;
    private final String mMinMagnitude;
    private final String mMaxMagnitude;
    private final String mOrderBy;
    private final String mLatitude;
    private final String mLongitude;
    private final String mMaxDistance;

    public EarthquakesQueryParameters(String startTime, String endTime, String limit,
                                      String minMagnitude, String maxMagnitude, String orderBy,
                                      String latitude, String longitude, String maxDistance) {
        mStartTime = startTime;
        mEndTime = endTime;
        mLimit = limit;
        mMinMagnitude = minMagnitude;
        mMaxMagnitude = maxMagnitude;
        mOrderBy = orderBy;
        mLatitude = latitude;
        mLongitude = longitude;
        mMaxDistance = maxDistance;
    }

    public String getStartTime() {
        return mStartTime;
    }

    public String getEndTime() {
        return mEndTime;
    }

    public String getLimit() {
        return mLimit;
    }

    public String getMinMagnitude() {
        return mMinMagnitude;
    }

    public String getMaxMagnitude() {
        return mMaxMagnitude;
    }

    public String getOrderBy() {
        return mOrderBy;
    }

    public String getLatitude() {
        return mLatitude;
    }

    public String getLongitude() {
        return mLongitude;
    }

    public String getMaxDistance() {
        return mMaxDistance;
    }

}
