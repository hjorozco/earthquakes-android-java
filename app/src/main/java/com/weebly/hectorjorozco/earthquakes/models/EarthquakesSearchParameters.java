package com.weebly.hectorjorozco.earthquakes.models;

public class EarthquakesSearchParameters {

    private String mUrl;
    private String mLocation;
    private String mMaxNumber;

    public EarthquakesSearchParameters(String url, String location, String maxNumber) {
        mUrl = url;
        mLocation = location;
        mMaxNumber = maxNumber;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getLocation() {
        return mLocation;
    }

    public String getMaxNumber() {
        return mMaxNumber;
    }


}
