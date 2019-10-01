package com.weebly.hectorjorozco.earthquakes.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.weebly.hectorjorozco.earthquakes.models.Earthquake;

import java.util.List;


// This data access object (dao) is used by the Room persistence library to access the Earthquakes
// data on the applications database.

@Dao
public interface EarthquakeDao {

    @Query("SELECT * FROM favorite_earthquakes" )
    LiveData<List<Earthquake>> loadFavoriteEarthquakes();

    @Query("SELECT * FROM favorite_earthquakes WHERE id = :id")
    Earthquake findFavoriteEarthquakeWithId(String id);

    @Query("DELETE FROM favorite_earthquakes")
    void deleteAllFavorites();

    @Insert
    void insertFavoriteEarthquake(Earthquake earthquake);

    @Delete
    void deleteFavoriteEarthquake(Earthquake earthquake);

}
