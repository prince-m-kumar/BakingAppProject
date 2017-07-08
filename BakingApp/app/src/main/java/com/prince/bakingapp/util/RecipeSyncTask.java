package com.prince.bakingapp.util;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.os.RemoteException;

import com.prince.bakingapp.R;
import com.prince.bakingapp.data.BakeAppContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.prince.bakingapp.data.BakeAppContract.AUTHORITY;


public class RecipeSyncTask {
    static int getRecipes(Context context) {
        HttpURLConnection urlConnection;
        BufferedReader reader;

        URL url;
        try {
            String RECIPE_API_URL = context.getResources().getString(R.string.bake_app_url).toString();
            url = new URL(RECIPE_API_URL);

            // Create the request to API, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (null == inputStream) {
                // Nothing to do.
                return Utilities.ApiResponseStatus.NONE;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // add newline to make debugging easier
                buffer.append(line).append("\n");
            }

            if (buffer.length() > 0) {
                syncDatabase(context, buffer.toString());
            }
            return Utilities.ApiResponseStatus.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return Utilities.ApiResponseStatus.ERROR;
        }
    }

    private static void syncDatabase(Context context, String jsonData) {
        try {
            ArrayList<ContentProviderOperation> contentProviderOperations = new ArrayList<>();
            JSONArray recipeJSONArray = new JSONArray(jsonData);
            for (int i = 0; i < recipeJSONArray.length(); i++) {
                ContentValues recipeValues = new ContentValues();
                JSONObject recipeJSONObject = recipeJSONArray.getJSONObject(i);
                recipeValues.put(BakeAppContract.RecipeEntry._ID, recipeJSONObject.getString("id"));
                recipeValues.put(BakeAppContract.RecipeEntry.COLUMN_NAME, recipeJSONObject.getString("name"));
                recipeValues.put(BakeAppContract.RecipeEntry.COLUMN_SERVINGS, recipeJSONObject.getString("servings"));
                recipeValues.put(BakeAppContract.RecipeEntry.COLUMN_IMAGE, recipeJSONObject.getString("image"));

                //delete old ingredient and step values
                contentProviderOperations.add(ContentProviderOperation
                        .newDelete(BakeAppContract.IngredientEntry.buildDirUri(recipeJSONObject.getLong("id")))
                        .build());

                contentProviderOperations.add(ContentProviderOperation
                        .newDelete(BakeAppContract.StepEntry.buildDirUri(recipeJSONObject.getLong("id")))
                        .build());

                contentProviderOperations.add(ContentProviderOperation
                        .newInsert(BakeAppContract.RecipeEntry.buildDirUri())
                        .withValues(recipeValues)
                        .build());


                //insert new step values
                JSONArray ingredientJSONArray = recipeJSONObject.getJSONArray("ingredients");
                for (int j = 0; j < ingredientJSONArray.length(); j++) {
                    ContentValues ingredientValues = new ContentValues();
                    JSONObject ingredientJSONObject = ingredientJSONArray.getJSONObject(j);
                    ingredientValues.put(BakeAppContract.IngredientEntry.COLUMN_QUANTITY, ingredientJSONObject.getString("quantity"));
                    ingredientValues.put(BakeAppContract.IngredientEntry.COLUMN_MEASURE, ingredientJSONObject.getString("measure"));
                    ingredientValues.put(BakeAppContract.IngredientEntry.COLUMN_INGREDIENT, ingredientJSONObject.getString("ingredient"));
                    ingredientValues.put(BakeAppContract.IngredientEntry.COLUMN_RECIPE_ID, recipeJSONObject.getLong("id"));

                    contentProviderOperations.add(ContentProviderOperation
                            .newInsert(BakeAppContract.IngredientEntry.buildDirUri(recipeJSONObject.getLong("id")))
                            .withValues(ingredientValues)
                            .build());
                }

                //insert new step values
                JSONArray stepJSONArray = recipeJSONObject.getJSONArray("steps");
                for (int k = 0; k < stepJSONArray.length(); k++) {
                    ContentValues stepValues = new ContentValues();
                    JSONObject stepJSONObject = stepJSONArray.getJSONObject(k);
                    stepValues.put(BakeAppContract.StepEntry._ID, stepJSONObject.getString("id"));
                    stepValues.put(BakeAppContract.StepEntry.COLUMN_SHORT_DESCRIPTION, stepJSONObject.getString("shortDescription"));
                    stepValues.put(BakeAppContract.StepEntry.COLUMN_DESCRIPTION, stepJSONObject.getString("description"));
                    stepValues.put(BakeAppContract.StepEntry.COLUMN_VIDEO_URL, stepJSONObject.getString("videoURL"));
                    stepValues.put(BakeAppContract.StepEntry.COLUMN_THUMBNAIL_URL, stepJSONObject.getString("thumbnailURL"));
                    stepValues.put(BakeAppContract.StepEntry.COLUMN_RECIPE_ID, recipeJSONObject.getLong("id"));

                    contentProviderOperations.add(ContentProviderOperation
                            .newInsert(BakeAppContract.StepEntry.buildDirUri(recipeJSONObject.getLong("id")))
                            .withValues(stepValues)
                            .build());
                }
            }

            try {
                context.getContentResolver().applyBatch(AUTHORITY, contentProviderOperations);

                //new data available! tell everyone ^_^
                Intent dataUpdatedIntent = new Intent(Utilities.ACTION_DATA_UPDATED);
                context.sendBroadcast(dataUpdatedIntent);
            } catch (RemoteException | OperationApplicationException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
