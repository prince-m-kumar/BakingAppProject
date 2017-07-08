package com.prince.bakingapp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class Utilities {
    public static final String ACTION_DATA_UPDATED = "com.prince.bakingapp.ACTION_DATA_UPDATED";

    public class ApiResponseStatus {
        public static final int NONE = 0;
        public static final int ERROR = 1;
        public static final int SUCCESS = 2;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public static boolean isFavoriteEmpty(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return (prefs.getLong(IngredientWidgetProvider.PREF_KEY_RECIPE_ID, 0) == 0);
    }

    public static void setAsFavoriteRecipe(Context context, long recipeId, String recipeName, String recipeImage) {
        SharedPreferences.Editor prefs = PreferenceManager
                .getDefaultSharedPreferences(context).edit();
        prefs.putLong(IngredientWidgetProvider.PREF_KEY_RECIPE_ID, recipeId);
        prefs.putString(IngredientWidgetProvider.PREF_KEY_RECIPE_NAME, recipeName);
        prefs.putString(IngredientWidgetProvider.PREF_KEY_RECIPE_IMAGE, recipeImage);
        prefs.apply();

    }
}
