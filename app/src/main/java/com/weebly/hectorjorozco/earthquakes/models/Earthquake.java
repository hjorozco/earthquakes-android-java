package com.weebly.hectorjorozco.earthquakes.models;

public class Earthquake {

    private String mText;

    public Earthquake(String text){
        mText = text;
    }

    public String getText() {
        return mText;
    }
}
