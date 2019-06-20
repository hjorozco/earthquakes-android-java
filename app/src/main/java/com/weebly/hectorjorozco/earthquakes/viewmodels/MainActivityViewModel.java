package com.weebly.hectorjorozco.earthquakes.viewmodels;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.weebly.hectorjorozco.earthquakes.models.Earthquake;
import com.weebly.hectorjorozco.earthquakes.models.retrofit.Earthquakes;
import com.weebly.hectorjorozco.earthquakes.retrofit.RetrofitCallback;
import com.weebly.hectorjorozco.earthquakes.retrofit.RetrofitImplementation;
import com.weebly.hectorjorozco.earthquakes.utils.QueryUtils;

import java.util.List;

import retrofit2.Call;

public class MainActivityViewModel extends AndroidViewModel {
    private MutableLiveData<List<Earthquake>> earthquakes;

    private Call<Earthquakes> mRetrofitServiceCall;

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

            mRetrofitServiceCall = retrofitImplementation.getListOfEarthquakes(new RetrofitCallback<Earthquakes>() {
                @Override
                public void onResponse(Earthquakes retrofitResult) {
                    if (retrofitResult != null) {

                        Log.d("TESTING", "EARTHQUAKES FETCHED!");
                        QueryUtils.searchingForEarthquakes = false;
                        QueryUtils.earthquakesFetched = true;

                        List<Earthquake> earthquakeList = QueryUtils.getEarthquakesListFromRetrofitResult(context, retrofitResult);

                        // Assigns the value of the List<Earthquake>, extracted from the Retrofit Response, to a private
                        // static variable that will be used by the MAPS.
                        QueryUtils.mEarthquakesList = earthquakeList;

                        earthquakes.postValue(earthquakeList);

                    } else {
                        Log.d("TESTING", "No EARTHQUAKES FETCHED!");
                        QueryUtils.searchingForEarthquakes = false;
                        QueryUtils.earthquakesFetched = true;
                        earthquakes.postValue(null);
                    }
                }

                @Override
                public void onCancel() {
                    Log.d("TESTING", "Query CANCELLED!");
                    QueryUtils.searchingForEarthquakes = false;
                    QueryUtils.earthquakesFetched = true;
                    earthquakes.postValue(earthquakes.getValue());
                }

            }, context);

        } else {
            QueryUtils.searchingForEarthquakes = false;
            earthquakes.postValue(null);
        }
    }

    public void cancelRetrofitRequest(){
        mRetrofitServiceCall.cancel();
    }

}
