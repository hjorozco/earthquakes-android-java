package com.weebly.hectorjorozco.earthquakes.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.weebly.hectorjorozco.earthquakes.models.Earthquake;
import com.weebly.hectorjorozco.earthquakes.utils.QueryUtils;

import java.util.List;
import java.util.concurrent.Executor;

public class MainActivityViewModel extends AndroidViewModel {
    private MutableLiveData<List<Earthquake>> earthquakes;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Earthquake>> getEarthquakes(){
        if (earthquakes==null) {
            earthquakes = new MutableLiveData<>();
            loadEarthquakes();
        }
        return earthquakes;
    }

    public void loadEarthquakes(){
        final String url = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2018-04-25T16%3A29%3A35-5%3A00&endtime=2019-04-25T16%3A29%3A35-5%3A00&limit=10000&minmagnitude=0&maxmagnitude=12&orderby=time";
        final String location = "Mexico";
        final String limit = "10000";

        Executor executor = new NetworkQueryExecutor();
        executor.execute(() -> {
            earthquakes.postValue(QueryUtils.fetchEarthquakeData(getApplication(), url , location, limit));
            Log.d("TESTING", "Earthquakes fetched from the internet");
        });
    }

    class NetworkQueryExecutor implements Executor {
        public void execute(@NonNull Runnable runnable) {
            new Thread(runnable).start();
        }
    }
}
