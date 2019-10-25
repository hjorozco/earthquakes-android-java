package com.weebly.hectorjorozco.earthquakes.models;

public class EarthquakesListInformationValues {

    private final String mOrderBy;
    private final String mLocation;
    private final String mDatePeriod;
    private final String mStartDate;
    private final String mEndDate;
    private final String mMinMagnitude;
    private final String mMaxMagnitude;
    private final String mLimit;

    private String mFirstEarthquakeMag;
    private String mLastEarthquakeMag;
    private String mFirstEarthquakeDate;
    private String mLastEarthquakeDate;
    private String mNumberOfEarthquakesDisplayed;


    public EarthquakesListInformationValues(String orderBy, String location, String datePeriod,
                                            String startDate, String endDate, String minMagnitude, String maxMagnitude,
                                            String limit) {
        mOrderBy = orderBy;
        mLocation = location;
        mDatePeriod = datePeriod;
        mStartDate = startDate;
        mEndDate = endDate;
        mMinMagnitude = minMagnitude;
        mMaxMagnitude = maxMagnitude;
        mLimit = limit;
    }

    public String getOrderBy() {
        return mOrderBy;
    }

    public String getLocation() {
        return mLocation;
    }

    public String getDatePeriod() {
        return mDatePeriod;
    }

    public String getStartDate() {
        return mStartDate;
    }

    public String getEndDate() {
        return mEndDate;
    }

    public String getMinMagnitude() {
        return mMinMagnitude;
    }

    public String getMaxMagnitude() {
        return mMaxMagnitude;
    }

    public String getLimit() {
        return mLimit;
    }

    public String getFirstEarthquakeMag() {
        return mFirstEarthquakeMag;
    }

    public String getLastEarthquakeMag() {
        return mLastEarthquakeMag;
    }

    public String getFirstEarthquakeDate() {
        return mFirstEarthquakeDate;
    }

    public String getLastEarthquakeDate() {
        return mLastEarthquakeDate;
    }

    public String getNumberOfEarthquakesDisplayed(){return mNumberOfEarthquakesDisplayed;}


    public void setFirstEarthquakeMag(String firstEarthquakeMag) {
        mFirstEarthquakeMag = firstEarthquakeMag;
    }

    public void setLastEarthquakeMag(String lastEarthquakeMag) {
        mLastEarthquakeMag = lastEarthquakeMag;
    }

    public void setFirstEarthquakeDate(String firstEarthquakeDate) {
        mFirstEarthquakeDate = firstEarthquakeDate;
    }

    public void setLastEarthquakeDate(String lastEarthquakeDate) {
        mLastEarthquakeDate = lastEarthquakeDate;
    }

    public void setNumberOfEarthquakesDisplayed(String numberOfEarthquakesDisplayed) {
        mNumberOfEarthquakesDisplayed = numberOfEarthquakesDisplayed;
    }
}
