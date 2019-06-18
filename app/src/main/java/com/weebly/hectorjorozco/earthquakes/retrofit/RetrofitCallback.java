package com.weebly.hectorjorozco.earthquakes.retrofit;

/**
 * Interface used by RetrofitImplementation.java class
 */
@SuppressWarnings("EmptyMethod")
public interface RetrofitCallback<T> {
    void onResponse(T result);
    void onCancel();
}
