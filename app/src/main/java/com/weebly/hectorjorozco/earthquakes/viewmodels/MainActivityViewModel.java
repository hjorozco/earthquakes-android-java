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
import com.weebly.hectorjorozco.earthquakes.utils.Utils;

import java.util.List;

import retrofit2.Call;


public class MainActivityViewModel extends AndroidViewModel {

    private MutableLiveData<List<Earthquake>> mEarthquakes;

    private Call<Earthquakes> mRetrofitServiceCall;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
    }


    public LiveData<List<Earthquake>> getEarthquakes() {
        if (mEarthquakes == null) {
            mEarthquakes = new MutableLiveData<>();
            loadEarthquakes();
        }
        return mEarthquakes;
    }


    public void loadEarthquakes() {

        Context context = getApplication();

        // If there is an internet connection load earthquakes from USGS.
        if (Utils.internetConnection(context)) {

            Log.d("TESTING", "Fetching earthquakes...");

            RetrofitImplementation retrofitImplementation =
                    RetrofitImplementation.getRetrofitImplementationInstance();

            EarthquakesQueryParameters earthquakesQueryParameters =
                    Utils.getEarthquakesQueryParameters(context);

            mRetrofitServiceCall =
                    retrofitImplementation.getListOfEarthquakes(new RetrofitCallback<Earthquakes>() {

                        @Override
                        public void onResponse(Earthquakes retrofitResult) {

                            if (retrofitResult != null) {

                                Log.d("TESTING", "List of Earthquakes fetched successfully");
                                List<Earthquake> earthquakeList =
                                        Utils.getEarthquakesListFromRetrofitResult(context, retrofitResult);

                                // Assigns the value of the List<Earthquake>, extracted from the Retrofit
                                // Response, to a variable that will be used by the MAPS activity.
                                Utils.sEarthquakesList = earthquakeList;

                                setLoadEarthquakesResult(earthquakeList, Utils.SEARCH_RESULT_NON_NULL);

                                if (earthquakeList.size() > 0) {
                                    Utils.sOneOrMoreEarthquakesFoundByRetrofitQuery = true;
                                }

                            } else {
                                Log.d("TESTING", "Retrofit result was NULL");
                                setLoadEarthquakesResult(mEarthquakes.getValue(), Utils.SEARCH_RESULT_NULL);
                            }
                        }

                        @Override
                        public void onCancel() {
                            Log.d("TESTING", "Cancelled query");
                            setLoadEarthquakesResult(mEarthquakes.getValue(), Utils.SEARCH_CANCELLED);
                        }

                    }, earthquakesQueryParameters);

        } else {
            Log.d("TESTING", "No internet connection");
            setLoadEarthquakesResult(mEarthquakes.getValue(), Utils.NO_INTERNET_CONNECTION);
        }
    }

    private void setLoadEarthquakesResult(List<Earthquake> earthquakesList, byte loadEarthquakesResultCode) {
        Utils.sSearchingForEarthquakes = false;
        Utils.sLoadEarthquakesResultCode = loadEarthquakesResultCode;
        mEarthquakes.postValue(earthquakesList);
    }

    public void cancelRetrofitRequest() {
        mRetrofitServiceCall.cancel();
    }

}
