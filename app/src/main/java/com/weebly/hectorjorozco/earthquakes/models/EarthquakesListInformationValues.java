package com.weebly.hectorjorozco.earthquakes.models;

public class EarthquakesListInformationValues {

    private String mOrderBy;
    private String mLocation;
    private String mDatePeriod;
    private String mStartDate;
    private String mEndDate;
    private String mStartDateTime;
    private String mEndDateTime;
    private String mMinMagnitude;
    private String mMaxMagnitude;
    private String mLimit;

    private String mFirstEarthquakeMag;
    private String mLastEarthquakeMag;
    private String mFirstEarthquakeDate;
    private String mLastEarthquakeDate;
    private String mNumberOfEarthquakesDisplayed;


    public EarthquakesListInformationValues(String orderBy, String location, String datePeriod,
                                            String startDate, String endDate, String startDateTime,
                                            String endDateTime, String minMagnitude, String maxMagnitude,
                                            String limit) {
        mOrderBy = orderBy;
        mLocation = location;
        mDatePeriod = datePeriod;
        mStartDate = startDate;
        mEndDate = endDate;
        mStartDateTime = startDateTime;
        mEndDateTime = endDateTime;
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

    public String getStartDateTime() {
        return mStartDateTime;
    }

    public String getEndDateTime() {
        return mEndDateTime;
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
