package com.weebly.hectorjorozco.earthquakes.retrofit;

import com.weebly.hectorjorozco.earthquakes.models.retrofit.Earthquakes;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Interface used by RetrofitImplementation.java class
 */
interface RetrofitService {
    @GET("query")
    Call<Earthquakes> getEarthquakes(@Query("format") String format,
                                     @Query("starttime") String startTime,
                                     @Query("endtime") String endTime,
                                     @Query("limit") String limit,
                                     @Query("minmagnitude") String minMagnitude,
                                     @Query("maxmagnitude") String maxMagnitude,
                                     @Query("orderby") String orderBy);

    Call<Earthquakes> getEarthquakesWithinMaximumDistance(@Query("format") String format,
                                             @Query("starttime") String startTime,
                                             @Query("endtime") String endTime,
                                             @Query("limit") String limit,
                                             @Query("minmagnitude") String minMagnitude,
                                             @Query("maxmagnitude") String maxMagnitude,
                                             @Query("orderby") String orderBy);
}
