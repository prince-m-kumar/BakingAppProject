package com.prince.bakingapp.util;

import android.app.IntentService;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class RecipeIntentService extends IntentService {
    //SwipeRefreshLayout: parameters for broadcast receiver
    public static final String BROADCAST_ACTION_STATE_CHANGE
            = "com.prince.bakingapp.intent.action.STATE_CHANGE";
    public static final String EXTRA_REFRESHING
            = "com.prince.bakingapp.intent.extra.REFRESHING";

    public RecipeIntentService() {
        super(RecipeIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {

            return;
        }

        sendBroadcast(
                new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_REFRESHING, true));

        RecipeSyncTask.getRecipes(this);

        sendBroadcast(
                new Intent(BROADCAST_ACTION_STATE_CHANGE).putExtra(EXTRA_REFRESHING, false));
    }
}
