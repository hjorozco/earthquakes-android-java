package com.weebly.hectorjorozco.earthquakes.utils;

import java.util.Observable;

public class EarthquakesLoadedObservable extends Observable {

    public void notifyEarthquakesMapActivity(){
        setChanged();
        notifyObservers();
    }
}
