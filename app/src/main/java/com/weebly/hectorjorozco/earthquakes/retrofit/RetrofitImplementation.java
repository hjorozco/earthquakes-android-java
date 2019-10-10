package com.weebly.hectorjorozco.earthquakes.retrofit;

import android.util.Log;

import androidx.annotation.NonNull;

import com.weebly.hectorjorozco.earthquakes.models.EarthquakesQueryParameters;
import com.weebly.hectorjorozco.earthquakes.models.retrofit.Earthquakes;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;


/**
 * Retrofit 2 Implementation that gets earthquakes information from a JSON file on the USGS API. It does it
 * asynchronously and parses it into a List of Earthquake objects by using the JacksonConverterFactory.
 * It makes use of two interfaces: RetrofitCallback and RetrofitService
 */
public final class RetrofitImplementation implements Serializable {

    private static final String USGS_API_URL = "https://earthquake.usgs.gov/fdsnws/event/1/";

    private static volatile RetrofitImplementation retrofitImplementation = new RetrofitImplementation();
    private RetrofitService retrofitService;

    private RetrofitImplementation() {
        // Avoid the reflection api.
        if (retrofitImplementation != null) {
            throw new RuntimeException("Only one instance of this class can be used. " +
                    "Use getRetrofitImplementationInstance() method to get it.");
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(USGS_API_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(new OkHttpClient.Builder()
                        // Time to establish a connection to the API server
                        .connectTimeout(10, TimeUnit.SECONDS)
                        // Time between two bytes read from the API server
                        .readTimeout(120, TimeUnit.SECONDS)
                        // Time between two bytes send to the API server
                        .writeTimeout(15, TimeUnit.SECONDS)
                        .build())
                .build();

        // Create an instance of the RetrofitService
        retrofitService = retrofit.create(RetrofitService.class);
    }


    public static RetrofitImplementation getRetrofitImplementationInstance() {

        if (retrofitImplementation == null) {
            synchronized (RetrofitImplementation.class) {
                if (retrofitImplementation == null)
                    retrofitImplementation = new RetrofitImplementation();
            }
        }

        return retrofitImplementation;
    }


    /**
     * Asynchronous request by implement a Callback
     *
     * @param retrofitCallback RetrofitCallback.java
     */
    public Call<Earthquakes> getListOfEarthquakes(final RetrofitCallback<Earthquakes> retrofitCallback,
                                                  EarthquakesQueryParameters earthquakesQueryParameters) {

        Call<Earthquakes> retrofitServiceCall = retrofitService.getEarthquakesFromUSGS(
                "geojson",
                earthquakesQueryParameters.getStartTime(),
                earthquakesQueryParameters.getEndTime(),
                earthquakesQueryParameters.getLimit(),
                earthquakesQueryParameters.getMinMagnitude(),
                earthquakesQueryParameters.getMaxMagnitude(),
                earthquakesQueryParameters.getOrderBy());

        Log.d("TESTING", retrofitServiceCall.toString());

        retrofitServiceCall.enqueue(new Callback<Earthquakes>() {

            @Override
            public void onResponse(@NonNull Call<Earthquakes> call,
                                   @NonNull Response<Earthquakes> response) {
                retrofitCallback.onResponse(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<Earthquakes> call, @NonNull Throwable t) {
                if (call.isCanceled()) {
                    retrofitCallback.onCancel();
                } else {
                    retrofitCallback.onResponse(null);
                }
            }
        });

        return retrofitServiceCall;
    }

}

