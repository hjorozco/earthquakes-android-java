package com.weebly.hectorjorozco.earthquakes.executors;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Global executor pools for the whole application.
 *
 * Grouping tasks like this avoids the effects of task starvation (e.g. disk reads don't wait behind
 * webservice requests).
 */

public class AppExecutors {

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static AppExecutors sExecutorsInstance;
    private final ExecutorService diskIO;
    private final Executor mainThread;
    private final Executor networkIO;

    private AppExecutors(ExecutorService diskIO, Executor networkIO, Executor mainThread) {
        this.diskIO = diskIO;
        this.networkIO = networkIO;
        this.mainThread = mainThread;
    }

    public static AppExecutors getInstance() {
        if (sExecutorsInstance == null) {
            synchronized (LOCK) {
                sExecutorsInstance = new AppExecutors(Executors.newSingleThreadExecutor(),
                        Executors.newFixedThreadPool(3),
                        new MainThreadExecutor());
            }
        }
        return sExecutorsInstance;
    }

    public ExecutorService diskIO() {
        return diskIO;
    }

    @SuppressWarnings("unused")
    public Executor mainThread() {
        return mainThread;
    }

    @SuppressWarnings("unused")
    public Executor networkIO() {
        return networkIO;
    }

    private static class MainThreadExecutor implements Executor {
        private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
