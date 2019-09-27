package com.weebly.hectorjorozco.earthquakes.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.weebly.hectorjorozco.earthquakes.models.Earthquake;

// Class that defines a method to create a RoomDatabase that will contain one table: "favorites"

@Database(entities = {Earthquake.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "earthquakesdb";
    private static AppDatabase sDatabaseInstance;

    public static AppDatabase getInstance(Context context) {
        if (sDatabaseInstance == null) {
            synchronized (LOCK) {
                // Create a new database instance
                sDatabaseInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, AppDatabase.DATABASE_NAME)
                        .build();
            }
        }

        return sDatabaseInstance;
    }

    // Returns a Data Access Object used to access the "students" table.
    public abstract EarthquakeDao earthquakeDao();

}
