package com.weebly.hectorjorozco.earthquakes.viewmodels;

// ViewModel that will observe MainActivity for changes on the favorites earthquakes database

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.weebly.hectorjorozco.earthquakes.database.AppDatabase;
import com.weebly.hectorjorozco.earthquakes.models.Earthquake;

import java.util.List;

public class FavoritesActivityViewModel extends AndroidViewModel {

    // Cash the list of Earthquake objects wrapped in a LiveData object
    private final LiveData<List<Earthquake>> favoriteEarthquakes;

    public FavoritesActivityViewModel(Application application) {
        super(application);
        // Gets all favoriteEarthquakes from the Database
        AppDatabase appDatabase = AppDatabase.getInstance(this.getApplication());
        favoriteEarthquakes = appDatabase.earthquakeDao().loadFavoriteEarthquakes();
    }

    public LiveData<List<Earthquake>> getFavoriteEarthquakes() {
        return favoriteEarthquakes;
    }
}