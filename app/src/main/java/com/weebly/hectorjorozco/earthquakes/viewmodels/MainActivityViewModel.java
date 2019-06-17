package com.weebly.hectorjorozco.earthquakes.viewmodels;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.weebly.hectorjorozco.earthquakes.models.Earthquake;
import com.weebly.hectorjorozco.earthquakes.models.EarthquakesSearchParameters;
import com.weebly.hectorjorozco.earthquakes.utils.QueryUtils;

import java.util.List;
import java.util.concurrent.Executor;

public class MainActivityViewModel extends AndroidViewModel {
    private MutableLiveData<List<Earthquake>> earthquakes;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Earthquake>> getEarthquakes() {
        if (earthquakes == null) {
            earthquakes = new MutableLiveData<>();
            loadEarthquakes();
        }
        return earthquakes;
    }

    public void loadEarthquakes() {

        // If there is an internet connection load earthquakes from USGS. If not return null.
        if (QueryUtils.internetConnection(getApplication())) {

            Context context = getApplication();
            EarthquakesSearchParameters earthquakesSearchParameters = QueryUtils.getEarthquakesSearchParameters(context);

            Executor networkQueryExecutor = new NetworkQueryExecutor();
            networkQueryExecutor.execute(() -> {
                Log.d("TESTING", "Fetching earthquakes...");
                earthquakes.postValue(QueryUtils.fetchEarthquakeData(context,
                        earthquakesSearchParameters.getUrl(),
                        earthquakesSearchParameters.getLocation(),
                        earthquakesSearchParameters.getMaxNumber()));
                Log.d("TESTING", "EARTHQUAKES FETCHED!");
                QueryUtils.searchingForEarthquakes = false;
                QueryUtils.earthquakesFetched = true;
            });
        } else {
            QueryUtils.searchingForEarthquakes = false;
            earthquakes.postValue(null);
        }
    }

    class NetworkQueryExecutor implements Executor {
        public void execute(@NonNull Runnable runnable) {
            new Thread(runnable).start();
        }
    }
}
