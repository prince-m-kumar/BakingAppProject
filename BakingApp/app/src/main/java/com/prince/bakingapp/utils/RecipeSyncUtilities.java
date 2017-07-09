package com.prince.bakingapp.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

public class RecipeSyncUtilities {
    private static final int SYNC_INTERVAL_MINUTES = 720;
    private static final int SYNC_INTERVAL_SECONDS = (int) (TimeUnit.MINUTES.toSeconds(SYNC_INTERVAL_MINUTES));
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS;

    private static final String SYNC_JOB_TAG = "recipe_sync_tag";

    private static boolean sInitialized;

    synchronized public static void scheduleSyncTask(@NonNull final Context context) {
        if (sInitialized) return;
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher firebaseJobDispatcher = new FirebaseJobDispatcher(driver);
        Job job = firebaseJobDispatcher.newJobBuilder()
                .setService(RecipeJobService.class)
                .setTag(SYNC_JOB_TAG)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();
        firebaseJobDispatcher.schedule(job);
        sInitialized = true;
    }
}
