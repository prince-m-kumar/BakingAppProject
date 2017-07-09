package com.prince.bakingapp.utils;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.os.RemoteException;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
    static int getRecipes(final Context context) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = context.getResources().getString(R.string.bake_app_url).toString();
        final int[] status = {0};
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        syncDatabase(context, response);
                        status[0] =Utilities.ApiResponseStatus.SUCCESS;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                status[0] =Utilities.ApiResponseStatus.ERROR;
            }
        });
        queue.add(stringRequest);
        return status[0];
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
