package com.weebly.hectorjorozco.earthquakes.viewmodels;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.weebly.hectorjorozco.earthquakes.models.Earthquake;
import com.weebly.hectorjorozco.earthquakes.models.EarthquakesSearchParameters2;
import com.weebly.hectorjorozco.earthquakes.models.retrofit.Earthquakes;
import com.weebly.hectorjorozco.earthquakes.retrofit.RetrofitCallback;
import com.weebly.hectorjorozco.earthquakes.retrofit.RetrofitImplementation;
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

        Context context = getApplication();

        // If there is an internet connection load earthquakes from USGS. If not return null.
        if (QueryUtils.internetConnection(context)) {

            Log.d("TESTING", "Fetching earthquakes...");

            RetrofitImplementation retrofitImplementation = RetrofitImplementation.getRetrofitImplementationInstance();

            retrofitImplementation.getListOfEarthquakes(new RetrofitCallback<Earthquakes>() {
                @Override
                public void onResponse(Earthquakes retrofitResult) {
                    if (retrofitResult != null) {
                        for (int i = 0; i < retrofitResult.getFeatures().size(); i++) {
                            Log.d("RETROFIT", i + " " + retrofitResult.getFeatures().get(i).getProperties().getTitle());
                        }

                        Log.d("TESTING", "EARTHQUAKES FETCHED!");
                        QueryUtils.searchingForEarthquakes = false;
                        QueryUtils.earthquakesFetched = true;

                        // Assigns the value of the List<Earthquake>, extracted from the Retrofit Response, to a private
                        // static variable that will be used by the MAPS.
                        earthquakes.postValue(QueryUtils.getEarthquakesListFromRetrofitResult(context, retrofitResult));

                    } else {
                        Log.d("TESTING", "No EARTHQUAKES FETCHED!");
                        QueryUtils.searchingForEarthquakes = false;
                        QueryUtils.earthquakesFetched = true;
                        earthquakes.postValue(null);
                    }
                }

                @Override
                public void onCancel() {

                }
            }, context);

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
