package com.weebly.hectorjorozco.earthquakes.models;

public class EarthquakesQueryParameters {

    private String mStartTime;
    private String mEndTime;
    private String mLimit;
    private String mMinMagnitude;
    private String mMaxMagnitude;
    private String mOrderBy;

    public EarthquakesQueryParameters(String startTime, String endTime, String limit,
                                      String minMagnitude, String maxMagnitude, String orderBy) {
        mStartTime = startTime;
        mEndTime = endTime;
        mLimit = limit;
        mMinMagnitude = minMagnitude;
        mMaxMagnitude = maxMagnitude;
        mOrderBy = orderBy;
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

}
