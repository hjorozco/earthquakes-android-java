package com.weebly.hectorjorozco.earthquakes.viewmodels;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.weebly.hectorjorozco.earthquakes.models.Earthquake;
import com.weebly.hectorjorozco.earthquakes.models.EarthquakesQueryParameters;
import com.weebly.hectorjorozco.earthquakes.models.retrofit.Earthquakes;
import com.weebly.hectorjorozco.earthquakes.retrofit.RetrofitCallback;
import com.weebly.hectorjorozco.earthquakes.retrofit.RetrofitImplementation;
import com.weebly.hectorjorozco.earthquakes.utils.QueryUtils;

import java.util.List;

import retrofit2.Call;


public class MainActivityViewModel extends AndroidViewModel {

    private MutableLiveData<List<Earthquake>> mEarthquakes;

    private Call<Earthquakes> mRetrofitServiceCall;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
    }


    public LiveData<List<Earthquake>> getEarthquakes(QueryUtils.LocationUpdateListener locationUpdateListener) {
        if (mEarthquakes == null) {
            mEarthquakes = new MutableLiveData<>();
            loadEarthquakes(locationUpdateListener);
        }
        return mEarthquakes;
    }


    public void loadEarthquakes(QueryUtils.LocationUpdateListener locationUpdateListener) {

        Context context = getApplication();

        // If there is an internet connection load earthquakes from USGS.
        if (QueryUtils.internetConnection(context)) {

            RetrofitImplementation retrofitImplementation =
                    RetrofitImplementation.getRetrofitImplementationInstance();

            EarthquakesQueryParameters earthquakesQueryParameters =
                    QueryUtils.getEarthquakesQueryParameters(context, locationUpdateListener);

            mRetrofitServiceCall =
                    retrofitImplementation.getListOfEarthquakes(new RetrofitCallback<Earthquakes>() {

                        @Override
                        public void onResponse(Earthquakes retrofitResult) {

                            if (retrofitResult != null) {

                                List<Earthquake> earthquakeList =
                                        QueryUtils.getEarthquakesListFromRetrofitResult(context, retrofitResult);

                                setLoadEarthquakesResult(earthquakeList, QueryUtils.SEARCH_RESULT_NON_NULL);

                            } else {
                                setLoadEarthquakesResult(mEarthquakes.getValue(), QueryUtils.SEARCH_RESULT_NULL);
                                Log.d("TESTING", "Retrofit result null");
                            }
                        }

                        @Override
                        public void onCancel() {
                            setLoadEarthquakesResult(mEarthquakes.getValue(), QueryUtils.SEARCH_CANCELLED);
                        }

                    }, earthquakesQueryParameters);

        } else {
            setLoadEarthquakesResult(mEarthquakes.getValue(), QueryUtils.NO_INTERNET_CONNECTION);
        }
    }

    private void setLoadEarthquakesResult(List<Earthquake> earthquakesList, byte loadEarthquakesResultCode) {
        QueryUtils.sSearchingForEarthquakes = false;
        QueryUtils.sLoadEarthquakesResultCode = loadEarthquakesResultCode;
        mEarthquakes.postValue(earthquakesList);
    }

    public void cancelRetrofitRequest() {
        mRetrofitServiceCall.cancel();
    }

}
